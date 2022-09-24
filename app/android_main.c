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
#include <stdio.h>

#include "android_structs.h"

void android_main(struct android_app *app) {
  printf("android_main\n");
}
