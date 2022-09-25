#include <stdio.h>
#include <sys/stat.h>

#include "android_structs.h"

const char STORAGE_PATH[] = "/storage/emulated/0/Documents/vocabulary/";

void android_main(struct android_app *app) {
    printf("android_main\n");
    mkdir(STORAGE_PATH, 511);
}
