#include <EGL/egl.h>
#include <GLES3/gl3.h>
#include <android/sensor.h>
#include <asset_manager.h>
#include <asset_manager_jni.h>
#include <jni.h>
#include <math.h>
#include <native_activity.h>
#include <pthread.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <unistd.h>

#include "CNFG3D.h"
#include "CNFGFunctions.h"
#include "android_structs.h"

volatile int suspended;

static short iLastInternalW, iLastInternalH;

static EGLNativeWindowType native_window;

static EGLint const config_attribute_list[] = {
    EGL_RED_SIZE,    8,  EGL_GREEN_SIZE,      8,
    EGL_BLUE_SIZE,   8,  EGL_ALPHA_SIZE,      8,
    EGL_BUFFER_SIZE, 32, EGL_STENCIL_SIZE,    0,
    EGL_DEPTH_SIZE,  16, EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT,
    EGL_NONE};

static EGLint window_attribute_list[] = {EGL_NONE};

static const EGLint context_attribute_list[] = {EGL_CONTEXT_CLIENT_VERSION, 2,
                                                EGL_NONE};

static EGLDisplay egl_display;
static EGLSurface egl_surface;

static ASensorEventQueue *aeq;

struct android_app *gapp;
static int OGLESStarted;
static int android_width, android_height;

float *gSMatrix;
void CNFGSetupFullscreen(const char* WindowName, int screen_number);

static void display_image() {
  CNFGSetupFullscreen("Test Bench", 0);
  int time = 0;
  while (1) {
    time++;
    if (suspended) {
      usleep(50000);
      continue;
    }
    CNFGClearFrame();
    CNFGColor(0xffffffff);
    for (int x = 10 * time; x < android_width/2; x++) {
        for (int y = x; y < android_height/2; y++) {
            CNFGTackPixel(x, y);
        }
    }
    eglSwapBuffers(egl_display, egl_surface);
    usleep(2000);
  }
}

static void AndroidMakeFullscreen() {
  const struct JNINativeInterface *env = 0;
  const struct JNINativeInterface **envptr = &env;
  const struct JNIInvokeInterface **jniiptr = gapp->activity->vm;
  const struct JNIInvokeInterface *jnii = *jniiptr;

  jnii->AttachCurrentThread(jniiptr, &envptr, NULL);
  env = (*envptr);

  // Get android.app.NativeActivity, then get getWindow method handle, returns
  // view.Window type
  jclass activityClass = env->FindClass(envptr, "android/app/NativeActivity");
  jmethodID getWindow = env->GetMethodID(envptr, activityClass, "getWindow",
                                         "()Landroid/view/Window;");
  jobject window =
      env->CallObjectMethod(envptr, gapp->activity->clazz, getWindow);

  // Get android.view.Window class, then get getDecorView method handle, returns
  // view.View type
  jclass windowClass = env->FindClass(envptr, "android/view/Window");
  jmethodID getDecorView = env->GetMethodID(envptr, windowClass, "getDecorView",
                                            "()Landroid/view/View;");
  jobject decorView = env->CallObjectMethod(envptr, window, getDecorView);

  // Get the flag values associated with systemuivisibility
  jclass viewClass = env->FindClass(envptr, "android/view/View");
  const int flagLayoutHideNavigation = env->GetStaticIntField(
      envptr, viewClass,
      env->GetStaticFieldID(envptr, viewClass,
                            "SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION", "I"));
  const int flagLayoutFullscreen = env->GetStaticIntField(
      envptr, viewClass,
      env->GetStaticFieldID(envptr, viewClass,
                            "SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN", "I"));
  const int flagLowProfile = env->GetStaticIntField(
      envptr, viewClass,
      env->GetStaticFieldID(envptr, viewClass, "SYSTEM_UI_FLAG_LOW_PROFILE",
                            "I"));
  const int flagHideNavigation = env->GetStaticIntField(
      envptr, viewClass,
      env->GetStaticFieldID(envptr, viewClass, "SYSTEM_UI_FLAG_HIDE_NAVIGATION",
                            "I"));
  const int flagFullscreen = env->GetStaticIntField(
      envptr, viewClass,
      env->GetStaticFieldID(envptr, viewClass, "SYSTEM_UI_FLAG_FULLSCREEN",
                            "I"));
  const int flagImmersiveSticky = env->GetStaticIntField(
      envptr, viewClass,
      env->GetStaticFieldID(envptr, viewClass,
                            "SYSTEM_UI_FLAG_IMMERSIVE_STICKY", "I"));

  jmethodID setSystemUiVisibility =
      env->GetMethodID(envptr, viewClass, "setSystemUiVisibility", "(I)V");

  env->CallVoidMethod(envptr, decorView, setSystemUiVisibility,
                      (flagLayoutHideNavigation | flagLayoutFullscreen |
                       flagLowProfile | flagHideNavigation | flagFullscreen |
                       flagImmersiveSticky));

  jclass layoutManagerClass =
      env->FindClass(envptr, "android/view/WindowManager$LayoutParams");
  const int flag_WinMan_Fullscreen =
      env->GetStaticIntField(envptr, layoutManagerClass,
                             (env->GetStaticFieldID(envptr, layoutManagerClass,
                                                    "FLAG_FULLSCREEN", "I")));
  const int flag_WinMan_KeepScreenOn = env->GetStaticIntField(
      envptr, layoutManagerClass,
      (env->GetStaticFieldID(envptr, layoutManagerClass, "FLAG_KEEP_SCREEN_ON",
                             "I")));
  const int flag_WinMan_hw_acc = env->GetStaticIntField(
      envptr, layoutManagerClass,
      (env->GetStaticFieldID(envptr, layoutManagerClass,
                             "FLAG_HARDWARE_ACCELERATED", "I")));
  env->CallVoidMethod(
      envptr, window,
      (env->GetMethodID(envptr, windowClass, "addFlags", "(I)V")),
      (flag_WinMan_Fullscreen | flag_WinMan_KeepScreenOn | flag_WinMan_hw_acc));

  jnii->DetachCurrentThread(jniiptr);
}

void CNFGGetDimensions(void) {
  CNFGInternalResize(android_width, android_height);
}

int CNFGSetup(const char *WindowName, int w, int h) {
  EGLint egl_major, egl_minor;
  EGLConfig config;
  EGLint num_config;
  EGLContext context;

  // This MUST be called before doing any initialization.
  int events;
  while (!OGLESStarted) {
    struct android_poll_source *source;
    if (ALooper_pollAll(0, 0, &events, (void **)&source) >= 0) {
      if (source != NULL)
        source->process(gapp, source);
    }
  }

  egl_display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
  if (egl_display == EGL_NO_DISPLAY) {
    printf("Error: No display found!\n");
    return -1;
  }

  if (!eglInitialize(egl_display, &egl_major, &egl_minor)) {
    printf("Error: eglInitialise failed!\n");
    return -1;
  }

  eglChooseConfig(egl_display, config_attribute_list, &config, 1, &num_config);
  printf("Config: %d\n", num_config);

  printf("Creating Context\n");
  context = eglCreateContext(egl_display, config, EGL_NO_CONTEXT,
                             context_attribute_list);
  if (context == EGL_NO_CONTEXT) {
    printf("Error: eglCreateContext failed: 0x%08X\n", eglGetError());
    return -1;
  }
  printf("Context Created %p\n", context);

  if (native_window && !gapp->window) {
    printf("WARNING: App restarted without a window.  Cannot progress.\n");
    exit(0);
  }

  printf("Getting Surface %p\n", native_window = gapp->window);

  if (!native_window) {
    printf("FAULT: Cannot get window\n");
    return -5;
  }
  android_width = ANativeWindow_getWidth(native_window);
  android_height = ANativeWindow_getHeight(native_window);
  printf("Width/Height: %dx%d\n", android_width, android_height);
  egl_surface = eglCreateWindowSurface(egl_display, config, gapp->window,
                                       window_attribute_list);
  printf("Got Surface: %p\n", egl_surface);

  if (egl_surface == EGL_NO_SURFACE) {
    printf("Error: eglCreateWindowSurface failed: "
           "0x%08X\n",
           eglGetError());
    return -1;
  }

  if (!eglMakeCurrent(egl_display, egl_surface, egl_surface, context)) {
    printf("Error: eglMakeCurrent() failed: 0x%08X\n", eglGetError());
    return -1;
  }

  CNFGSetupBatchInternal();
  return 0;
}

void CNFGSetupFullscreen(const char *WindowName, int screen_number) {
  AndroidMakeFullscreen();
  CNFGSetup(WindowName, -1, -1);
}

void handle_cmd(struct android_app *app, int32_t cmd) {
  printf("handle_cmd with cmd=%d\n", cmd);
  if (cmd == APP_CMD_INIT_WINDOW) {
    OGLESStarted = 1;
    suspended = 0;
  }
}

void android_main(struct android_app *app) {
  printf("android_main\n");
  gapp = app;
  app->onAppCmd = handle_cmd;
  display_image();
}
