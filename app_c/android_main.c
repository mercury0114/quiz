#include <EGL/egl.h>
#include <GLES3/gl3.h>
#include <dirent.h>
#include <stdio.h>
#include <sys/stat.h>
#include <unistd.h>

#include "CNFGFunctions.h"
#include "android_structs.h"

static const char STORAGE_PATH[] =
    "/storage/emulated/0/Android/data/" PACKAGENAME "/vocabulary/";

static const int32_t CONFIG_ATTRIBUTE_LIST[] = {
    EGL_RED_SIZE,    8,  EGL_GREEN_SIZE,      8,
    EGL_BLUE_SIZE,   8,  EGL_ALPHA_SIZE,      8,
    EGL_BUFFER_SIZE, 32, EGL_STENCIL_SIZE,    0,
    EGL_DEPTH_SIZE,  16, EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT,
    EGL_NONE};
static const int32_t WINDOW_ATTRIBUTE_LIST[] = {EGL_NONE};
static const int32_t CONTEXT_ATTRIBUTE_LIST[] = {EGL_CONTEXT_CLIENT_VERSION, 2,
                                                 EGL_NONE};

volatile int suspended;

static int OGLESStarted;
static int android_width, android_height;
static struct android_app *gapp;
static EGLDisplay egl_display;
static EGLSurface egl_surface;

static int SetupScreen() {
  int32_t egl_major, egl_minor;
  EGLConfig config;
  int32_t num_config;
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

  eglChooseConfig(egl_display, CONFIG_ATTRIBUTE_LIST, &config, 1, &num_config);
  printf("Config: %d\n", num_config);

  printf("Creating Context\n");
  context = eglCreateContext(egl_display, config, EGL_NO_CONTEXT,
                             CONTEXT_ATTRIBUTE_LIST);
  if (context == EGL_NO_CONTEXT) {
    printf("Error: eglCreateContext failed: 0x%08X\n", eglGetError());
    return -1;
  }
  printf("Context Created %p\n", context);

  if (!gapp->window) {
    printf("FAULT: Cannot get window\n");
    return -5;
  }
  android_width = ANativeWindow_getWidth(gapp->window);
  android_height = ANativeWindow_getHeight(gapp->window);
  printf("Width/Height: %dx%d\n", android_width, android_height);
  egl_surface = eglCreateWindowSurface(egl_display, config, gapp->window,
                                       WINDOW_ATTRIBUTE_LIST);
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

  CNFGSetupBatchInternal(android_width, android_height);
  return 0;
}

static void sleep_while_suspended(void) {
  while (suspended) {
    usleep(2000);
  }
}

static void draw_row(uint16_t row_number) {
  if ((row_number + 1) * 200 >= android_height)
    return;
  for (int y = 0; y < 100; y++) {
    for (int x = 0; x < android_width; x++) {
      CNFGColor(0xffffffff);
      CNFGTackPixel(x, row_number * 200 + y);
      CNFGColor(0x00003200);
      CNFGTackPixel(x, row_number * 200 + 100 + y);
    }
  }
}

static void display_image() {
  sleep_while_suspended();
  DIR *dir = opendir(STORAGE_PATH);
  if (dir) {
    struct dirent *file;
    uint16_t count = 1;
    while ((file = readdir(dir))) {
      if (file->d_type == DT_REG) {
        draw_row(count);
        count++;
      }
    }
    closedir(dir);
  }
  eglSwapBuffers(egl_display, egl_surface);
}

static void handle_cmd(struct android_app *app, int32_t cmd) {
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
  SetupScreen();

  mkdir(STORAGE_PATH, 511);
  display_image();
}
