#include <jni.h>
#include <errno.h>
#include <stdlib.h>
#include <string.h>
#include <sys/resource.h>
#include <unistd.h>
#include <android/log.h>

#include "android_native_app_glue.h"
#include "android_main.h"

static int pfd[2];
pthread_t debug_capture_thread;
static void *debug_capture_thread_fn(void *v) {
  ssize_t readSize;
  char buf[2048];

  while ((readSize = read(pfd[0], buf, sizeof buf - 1)) > 0) {
    if (buf[readSize - 1] == '\n') {
      --readSize;
    }
    buf[readSize] = 0; // add null-terminator
    __android_log_write(ANDROID_LOG_DEBUG, APPNAME,
                        buf); // Set any log level you want
  }
  return 0;
}

static void free_saved_state(struct android_app *android_app) {
  pthread_mutex_lock(&android_app->mutex);
  if (android_app->savedState != NULL) {
    free(android_app->savedState);
    android_app->savedState = NULL;
    android_app->savedStateSize = 0;
  }
  pthread_mutex_unlock(&android_app->mutex);
}

int8_t android_app_read_cmd(struct android_app *android_app) {
  int8_t cmd;
  if (read(android_app->msgread, &cmd, sizeof(cmd)) == sizeof(cmd)) {
    switch (cmd) {
    case APP_CMD_SAVE_STATE:
      free_saved_state(android_app);
      break;
    }
    return cmd;
  } else {
    printf("No data on command pipe!");
  }
  return -1;
}

void android_app_pre_exec_cmd(struct android_app *android_app, int8_t cmd) {
  switch (cmd) {
  case APP_CMD_INPUT_CHANGED:
    printf("APP_CMD_INPUT_CHANGED\n");
    pthread_mutex_lock(&android_app->mutex);
    if (android_app->inputQueue != NULL) {
      AInputQueue_detachLooper(android_app->inputQueue);
    }
    android_app->inputQueue = android_app->pendingInputQueue;
    if (android_app->inputQueue != NULL) {
      printf("Attaching input queue to looper\n");
      AInputQueue_attachLooper(android_app->inputQueue, android_app->looper,
                               LOOPER_ID_INPUT, NULL,
                               &android_app->inputPollSource);
    }
    pthread_cond_broadcast(&android_app->cond);
    pthread_mutex_unlock(&android_app->mutex);
    break;

  case APP_CMD_INIT_WINDOW:
    printf("APP_CMD_INIT_WINDOW\n");
    pthread_mutex_lock(&android_app->mutex);
    android_app->window = android_app->pendingWindow;
    pthread_cond_broadcast(&android_app->cond);
    pthread_mutex_unlock(&android_app->mutex);
    break;

  case APP_CMD_TERM_WINDOW:
    printf("APP_CMD_TERM_WINDOW\n");
    pthread_cond_broadcast(&android_app->cond);
    break;

  case APP_CMD_RESUME:
  case APP_CMD_START:
  case APP_CMD_PAUSE:
  case APP_CMD_STOP:
    printf("activityState=%d\n", cmd);
    pthread_mutex_lock(&android_app->mutex);
    android_app->activityState = cmd;
    pthread_cond_broadcast(&android_app->cond);
    pthread_mutex_unlock(&android_app->mutex);
    break;

  case APP_CMD_CONFIG_CHANGED:
    printf("APP_CMD_CONFIG_CHANGED\n");
    AConfiguration_fromAssetManager(android_app->config,
                                    android_app->activity->assetManager);
    break;

  case APP_CMD_DESTROY:
    printf("APP_CMD_DESTROY\n");
    android_app->destroyRequested = 1;
    break;
  }
}

void android_app_post_exec_cmd(struct android_app *android_app, int8_t cmd) {
  switch (cmd) {
  case APP_CMD_TERM_WINDOW:
    printf("APP_CMD_TERM_WINDOW\n");
    pthread_mutex_lock(&android_app->mutex);
    android_app->window = NULL;
    pthread_cond_broadcast(&android_app->cond);
    pthread_mutex_unlock(&android_app->mutex);
    break;

  case APP_CMD_SAVE_STATE:
    printf("APP_CMD_SAVE_STATE\n");
    pthread_mutex_lock(&android_app->mutex);
    android_app->stateSaved = 1;
    pthread_cond_broadcast(&android_app->cond);
    pthread_mutex_unlock(&android_app->mutex);
    break;

  case APP_CMD_RESUME:
    free_saved_state(android_app);
    break;
  }
}

static void android_app_destroy(struct android_app *android_app) {
  printf("android_app_destroy!");
  free_saved_state(android_app);
  pthread_mutex_lock(&android_app->mutex);
  if (android_app->inputQueue != NULL) {
    AInputQueue_detachLooper(android_app->inputQueue);
  }
  AConfiguration_delete(android_app->config);
  android_app->destroyed = 1;
  pthread_cond_broadcast(&android_app->cond);
  pthread_mutex_unlock(&android_app->mutex);
  // Can't touch android_app object after this.
}

static void process_input(struct android_app *app,
                          struct android_poll_source *source) {
  AInputEvent *event = NULL;
  while (AInputQueue_getEvent(app->inputQueue, &event) >= 0) {
    // printf("New input event: type=%d\n", AInputEvent_getType(event));
    if (AInputQueue_preDispatchEvent(app->inputQueue, event)) {
      continue;
    }
    int32_t handled = 0;
    if (app->onInputEvent != NULL)
      handled = app->onInputEvent(app, event);
    AInputQueue_finishEvent(app->inputQueue, event, handled);
  }
}

static void process_cmd(struct android_app *app,
                        struct android_poll_source *source) {
  int8_t cmd = android_app_read_cmd(app);
  android_app_pre_exec_cmd(app, cmd);
  if (app->onAppCmd != NULL)
    app->onAppCmd(app, cmd);
  android_app_post_exec_cmd(app, cmd);
}

static void *android_app_entry(void *param) {
  struct android_app *android_app = (struct android_app *)param;

  android_app->config = AConfiguration_new();
  AConfiguration_fromAssetManager(android_app->config,
                                  android_app->activity->assetManager);

  android_app->cmdPollSource.id = LOOPER_ID_MAIN;
  android_app->cmdPollSource.app = android_app;
  android_app->cmdPollSource.process = process_cmd;
  android_app->inputPollSource.id = LOOPER_ID_INPUT;
  android_app->inputPollSource.app = android_app;
  android_app->inputPollSource.process = process_input;

  ALooper *looper = ALooper_prepare(ALOOPER_PREPARE_ALLOW_NON_CALLBACKS);
  ALooper_addFd(looper, android_app->msgread, LOOPER_ID_MAIN,
                ALOOPER_EVENT_INPUT, NULL, &android_app->cmdPollSource);
  android_app->looper = looper;

  pthread_mutex_lock(&android_app->mutex);
  android_app->running = 1;
  pthread_cond_broadcast(&android_app->cond);
  pthread_mutex_unlock(&android_app->mutex);

  android_main(android_app);

  android_app_destroy(android_app);
  return NULL;
}

// --------------------------------------------------------------------
// Native activity interaction (called from main thread)
// --------------------------------------------------------------------

static struct android_app *android_app_create(ANativeActivity *activity,
                                              void *savedState,
                                              size_t savedStateSize) {
  struct android_app *android_app =
      (struct android_app *)malloc(sizeof(struct android_app));
  memset(android_app, 0, sizeof(struct android_app));
  android_app->activity = activity;

  pthread_mutex_init(&android_app->mutex, NULL);
  pthread_cond_init(&android_app->cond, NULL);

  pthread_attr_t attr;
  pthread_attr_init(&attr);
  pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);

  // Capture input
  setvbuf(stdout, 0, _IOLBF, 0); // make stdout line-buffered
  setvbuf(stderr, 0, _IONBF, 0); // make stderr unbuffered
  pipe(pfd);
  dup2(pfd[1], 1);
  dup2(pfd[1], 2);
  pthread_create(&debug_capture_thread, &attr, debug_capture_thread_fn,
                 android_app);

  if (savedState != NULL) {
    android_app->savedState = malloc(savedStateSize);
    android_app->savedStateSize = savedStateSize;
    memcpy(android_app->savedState, savedState, savedStateSize);
  }

  int msgpipe[2];
  if (pipe(msgpipe)) {
    printf("could not create pipe: %s", strerror(errno));
    return NULL;
  }
  android_app->msgread = msgpipe[0];
  android_app->msgwrite = msgpipe[1];

  pthread_attr_init(&attr);
  pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
  pthread_create(&android_app->thread, &attr, android_app_entry, android_app);

  // Wait for thread to start.
  pthread_mutex_lock(&android_app->mutex);
  while (!android_app->running) {
    pthread_cond_wait(&android_app->cond, &android_app->mutex);
  }
  pthread_mutex_unlock(&android_app->mutex);

  return android_app;
}

static void android_app_write_cmd(struct android_app *android_app, int8_t cmd) {
  if (write(android_app->msgwrite, &cmd, sizeof(cmd)) != sizeof(cmd)) {
    printf("Failure writing android_app cmd: %s\n", strerror(errno));
  }
}

static void android_app_set_input(struct android_app *android_app,
                                  AInputQueue *inputQueue) {
  pthread_mutex_lock(&android_app->mutex);
  android_app->pendingInputQueue = inputQueue;
  android_app_write_cmd(android_app, APP_CMD_INPUT_CHANGED);
  while (android_app->inputQueue != android_app->pendingInputQueue) {
    pthread_cond_wait(&android_app->cond, &android_app->mutex);
  }
  pthread_mutex_unlock(&android_app->mutex);
}

static void android_app_set_window(struct android_app *android_app,
                                   ANativeWindow *window) {
  pthread_mutex_lock(&android_app->mutex);
  if (android_app->pendingWindow != NULL) {
    android_app_write_cmd(android_app, APP_CMD_TERM_WINDOW);
  }
  android_app->pendingWindow = window;
  if (window != NULL) {
    android_app_write_cmd(android_app, APP_CMD_INIT_WINDOW);
  }
  while (android_app->window != android_app->pendingWindow) {
    pthread_cond_wait(&android_app->cond, &android_app->mutex);
  }
  pthread_mutex_unlock(&android_app->mutex);
}

static void android_app_set_activity_state(struct android_app *android_app,
                                           int8_t cmd) {
  pthread_mutex_lock(&android_app->mutex);
  android_app_write_cmd(android_app, cmd);
  while (android_app->activityState != cmd) {
    pthread_cond_wait(&android_app->cond, &android_app->mutex);
  }
  pthread_mutex_unlock(&android_app->mutex);
}

static void android_app_free(struct android_app *android_app) {
  pthread_mutex_lock(&android_app->mutex);
  android_app_write_cmd(android_app, APP_CMD_DESTROY);
  while (!android_app->destroyed) {
    pthread_cond_wait(&android_app->cond, &android_app->mutex);
  }
  pthread_mutex_unlock(&android_app->mutex);

  close(android_app->msgread);
  close(android_app->msgwrite);
  pthread_cond_destroy(&android_app->cond);
  pthread_mutex_destroy(&android_app->mutex);
  free(android_app);
}

static void onDestroy(ANativeActivity *activity) {
  printf("Destroy: %p\n", activity);
  android_app_free((struct android_app *)activity->instance);
}

static void onStart(ANativeActivity *activity) {
  printf("Start: %p\n", activity);
  android_app_set_activity_state((struct android_app *)activity->instance,
                                 APP_CMD_START);
}

static void onResume(ANativeActivity *activity) {
  printf("Resume: %p\n", activity);
  android_app_set_activity_state((struct android_app *)activity->instance,
                                 APP_CMD_RESUME);
}

static void *onSaveInstanceState(ANativeActivity *activity, size_t *outLen) {
  struct android_app *android_app = (struct android_app *)activity->instance;
  void *savedState = NULL;

  printf("SaveInstanceState: %p\n", activity);
  pthread_mutex_lock(&android_app->mutex);
  android_app->stateSaved = 0;
  android_app_write_cmd(android_app, APP_CMD_SAVE_STATE);
  while (!android_app->stateSaved) {
    pthread_cond_wait(&android_app->cond, &android_app->mutex);
  }

  if (android_app->savedState != NULL) {
    savedState = android_app->savedState;
    *outLen = android_app->savedStateSize;
    android_app->savedState = NULL;
    android_app->savedStateSize = 0;
  }

  pthread_mutex_unlock(&android_app->mutex);

  return savedState;
}

static void onPause(ANativeActivity *activity) {
  printf("Pause: %p\n", activity);
  android_app_set_activity_state((struct android_app *)activity->instance,
                                 APP_CMD_PAUSE);
}

static void onStop(ANativeActivity *activity) {
  printf("Stop: %p\n", activity);
  android_app_set_activity_state((struct android_app *)activity->instance,
                                 APP_CMD_STOP);
}

static void onConfigurationChanged(ANativeActivity *activity) {
  struct android_app *android_app = (struct android_app *)activity->instance;
  printf("ConfigurationChanged: %p\n", activity);
  android_app_write_cmd(android_app, APP_CMD_CONFIG_CHANGED);
}

static void onLowMemory(ANativeActivity *activity) {
  struct android_app *android_app = (struct android_app *)activity->instance;
  printf("LowMemory: %p\n", activity);
  android_app_write_cmd(android_app, APP_CMD_LOW_MEMORY);
}

static void onWindowFocusChanged(ANativeActivity *activity, int focused) {
  printf("WindowFocusChanged: %p -- %d\n", activity, focused);
  android_app_write_cmd((struct android_app *)activity->instance,
                        focused ? APP_CMD_GAINED_FOCUS : APP_CMD_LOST_FOCUS);
}

static void onNativeWindowCreated(ANativeActivity *activity,
                                  ANativeWindow *window) {
  printf("NativeWindowCreated: %p -- %p\n", activity, window);
  android_app_set_window((struct android_app *)activity->instance, window);
}

static void onNativeWindowDestroyed(ANativeActivity *activity,
                                    ANativeWindow *window) {
  printf("NativeWindowDestroyed: %p -- %p\n", activity, window);
  android_app_set_window((struct android_app *)activity->instance, NULL);
}

static void onInputQueueCreated(ANativeActivity *activity, AInputQueue *queue) {
  printf("InputQueueCreated: %p -- %p\n", activity, queue);
  android_app_set_input((struct android_app *)activity->instance, queue);
}

static void onInputQueueDestroyed(ANativeActivity *activity,
                                  AInputQueue *queue) {
  printf("InputQueueDestroyed: %p -- %p\n", activity, queue);
  android_app_set_input((struct android_app *)activity->instance, NULL);
}

JNIEXPORT
void ANativeActivity_onCreate(ANativeActivity *activity, void *savedState,
                              size_t savedStateSize) {
  printf("Creating: %p\n", activity);
  activity->callbacks->onDestroy = onDestroy;
  activity->callbacks->onStart = onStart;
  activity->callbacks->onResume = onResume;
  activity->callbacks->onSaveInstanceState = onSaveInstanceState;
  activity->callbacks->onPause = onPause;
  activity->callbacks->onStop = onStop;
  activity->callbacks->onConfigurationChanged = onConfigurationChanged;
  activity->callbacks->onLowMemory = onLowMemory;
  activity->callbacks->onWindowFocusChanged = onWindowFocusChanged;
  activity->callbacks->onNativeWindowCreated = onNativeWindowCreated;
  activity->callbacks->onNativeWindowDestroyed = onNativeWindowDestroyed;
  activity->callbacks->onInputQueueCreated = onInputQueueCreated;
  activity->callbacks->onInputQueueDestroyed = onInputQueueDestroyed;

  activity->instance = android_app_create(activity, savedState, savedStateSize);
}
