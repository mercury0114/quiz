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

#define tdMODELVIEW 0
#define tdPROJECTION 1
#define HMX 162
#define HMY 162

volatile int suspended;

const unsigned char RawdrawFontCharData[1405] = {
    0x00, 0x09, 0x20, 0x29, 0x03, 0x23, 0x14, 0x8b, 0x00, 0x09, 0x20, 0x29,
    0x04, 0x24, 0x13, 0x8c, 0x01, 0x21, 0x23, 0x14, 0x03, 0x09, 0x11, 0x9a,
    0x11, 0x22, 0x23, 0x14, 0x03, 0x02, 0x99, 0x01, 0x21, 0x23, 0x09, 0x03,
    0x29, 0x03, 0x09, 0x12, 0x9c, 0x03, 0x2b, 0x13, 0x1c, 0x23, 0x22, 0x11,
    0x02, 0x8b, 0x9a, 0x1a, 0x01, 0x21, 0x23, 0x03, 0x89, 0x03, 0x21, 0x2a,
    0x21, 0x19, 0x03, 0x14, 0x23, 0x9a, 0x01, 0x10, 0x21, 0x12, 0x09, 0x12,
    0x1c, 0x03, 0xab, 0x02, 0x03, 0x1b, 0x02, 0x1a, 0x13, 0x10, 0xa9, 0x01,
    0x2b, 0x03, 0x29, 0x02, 0x11, 0x22, 0x13, 0x8a, 0x00, 0x22, 0x04, 0x88,
    0x20, 0x02, 0x24, 0xa8, 0x01, 0x10, 0x29, 0x10, 0x14, 0x0b, 0x14, 0xab,
    0x00, 0x0b, 0x0c, 0x20, 0x2b, 0xac, 0x00, 0x28, 0x00, 0x02, 0x2a, 0x10,
    0x1c, 0x20, 0xac, 0x01, 0x21, 0x23, 0x03, 0x09, 0x20, 0x10, 0x14, 0x8c,
    0x03, 0x23, 0x24, 0x04, 0x8b, 0x01, 0x10, 0x29, 0x10, 0x14, 0x0b, 0x14,
    0x2b, 0x04, 0xac, 0x01, 0x18, 0x21, 0x10, 0x9c, 0x03, 0x1c, 0x23, 0x1c,
    0x10, 0x9c, 0x02, 0x22, 0x19, 0x22, 0x9b, 0x02, 0x2a, 0x02, 0x19, 0x02,
    0x9b, 0x01, 0x02, 0xaa, 0x02, 0x22, 0x11, 0x02, 0x13, 0xaa, 0x11, 0x22,
    0x02, 0x99, 0x02, 0x13, 0x22, 0x8a, 0x10, 0x1b, 0x9c, 0x10, 0x09, 0x20,
    0x99, 0x10, 0x1c, 0x20, 0x2c, 0x01, 0x29, 0x03, 0xab, 0x21, 0x10, 0x01,
    0x23, 0x14, 0x0b, 0x10, 0x9c, 0x00, 0x09, 0x23, 0x2c, 0x04, 0x03, 0x21,
    0xa8, 0x21, 0x10, 0x01, 0x12, 0x03, 0x14, 0x2b, 0x02, 0xac, 0x10, 0x99,
    0x10, 0x01, 0x03, 0x9c, 0x10, 0x21, 0x23, 0x9c, 0x01, 0x2b, 0x11, 0x1b,
    0x21, 0x0b, 0x02, 0xaa, 0x02, 0x2a, 0x11, 0x9b, 0x04, 0x9b, 0x02, 0xaa,
    0x9c, 0x03, 0xa9, 0x00, 0x20, 0x24, 0x04, 0x08, 0x9a, 0x01, 0x10, 0x1c,
    0x04, 0xac, 0x01, 0x10, 0x21, 0x22, 0x04, 0xac, 0x00, 0x20, 0x24, 0x0c,
    0x12, 0xaa, 0x00, 0x02, 0x2a, 0x20, 0xac, 0x20, 0x00, 0x02, 0x22, 0x24,
    0x8c, 0x20, 0x02, 0x22, 0x24, 0x04, 0x8a, 0x00, 0x20, 0x21, 0x12, 0x9c,
    0x00, 0x0c, 0x00, 0x20, 0x2c, 0x04, 0x2c, 0x02, 0xaa, 0x00, 0x02, 0x22,
    0x20, 0x08, 0x22, 0x8c, 0x19, 0x9b, 0x19, 0x13, 0x8c, 0x20, 0x02, 0xac,
    0x01, 0x29, 0x03, 0xab, 0x00, 0x22, 0x8c, 0x01, 0x10, 0x21, 0x12, 0x1b,
    0x9c, 0x21, 0x01, 0x04, 0x24, 0x22, 0x12, 0x13, 0xab, 0x04, 0x01, 0x10,
    0x21, 0x2c, 0x02, 0xaa, 0x00, 0x04, 0x14, 0x23, 0x12, 0x0a, 0x12, 0x21,
    0x10, 0x88, 0x23, 0x14, 0x03, 0x01, 0x10, 0xa9, 0x00, 0x10, 0x21, 0x23,
    0x14, 0x04, 0x88, 0x00, 0x04, 0x2c, 0x00, 0x28, 0x02, 0x9a, 0x00, 0x0c,
    0x00, 0x28, 0x02, 0x9a, 0x21, 0x10, 0x01, 0x03, 0x14, 0x23, 0x22, 0x9a,
    0x00, 0x0c, 0x20, 0x2c, 0x02, 0xaa, 0x00, 0x28, 0x10, 0x1c, 0x04, 0xac,
    0x00, 0x20, 0x23, 0x14, 0x8b, 0x00, 0x0c, 0x02, 0x12, 0x21, 0x28, 0x12,
    0x23, 0xac, 0x00, 0x04, 0xac, 0x04, 0x00, 0x11, 0x20, 0xac, 0x04, 0x00,
    0x2a, 0x20, 0xac, 0x01, 0x10, 0x21, 0x23, 0x14, 0x03, 0x89, 0x00, 0x0c,
    0x00, 0x10, 0x21, 0x12, 0x8a, 0x01, 0x10, 0x21, 0x23, 0x14, 0x03, 0x09,
    0x04, 0x9b, 0x00, 0x0c, 0x00, 0x10, 0x21, 0x12, 0x02, 0xac, 0x21, 0x10,
    0x01, 0x23, 0x14, 0x8b, 0x00, 0x28, 0x10, 0x9c, 0x00, 0x04, 0x24, 0xa8,
    0x00, 0x03, 0x14, 0x23, 0xa8, 0x00, 0x04, 0x2c, 0x14, 0x1b, 0x24, 0xa8,
    0x00, 0x01, 0x23, 0x2c, 0x04, 0x03, 0x21, 0xa8, 0x00, 0x01, 0x12, 0x1c,
    0x12, 0x21, 0xa8, 0x00, 0x20, 0x02, 0x04, 0xac, 0x10, 0x00, 0x04, 0x9c,
    0x01, 0xab, 0x10, 0x20, 0x24, 0x9c, 0x01, 0x10, 0xa9, 0x04, 0xac, 0x00,
    0x99, 0x02, 0x04, 0x24, 0x2a, 0x23, 0x12, 0x8a, 0x00, 0x04, 0x24, 0x22,
    0x8a, 0x24, 0x04, 0x03, 0x12, 0xaa, 0x20, 0x24, 0x04, 0x02, 0xaa, 0x24,
    0x04, 0x02, 0x22, 0x23, 0x9b, 0x04, 0x09, 0x02, 0x1a, 0x01, 0x10, 0xa9,
    0x23, 0x12, 0x03, 0x14, 0x23, 0x24, 0x15, 0x8c, 0x00, 0x0c, 0x03, 0x12,
    0x23, 0xac, 0x19, 0x12, 0x9c, 0x2a, 0x23, 0x24, 0x15, 0x8c, 0x00, 0x0c,
    0x03, 0x13, 0x2a, 0x13, 0xac, 0x10, 0x9c, 0x02, 0x0c, 0x02, 0x1b, 0x12,
    0x1c, 0x12, 0x23, 0xac, 0x02, 0x0c, 0x03, 0x12, 0x23, 0xac, 0x02, 0x22,
    0x24, 0x04, 0x8a, 0x02, 0x0d, 0x04, 0x24, 0x22, 0x8a, 0x02, 0x04, 0x2c,
    0x25, 0x22, 0x8a, 0x02, 0x0c, 0x03, 0x12, 0xaa, 0x22, 0x02, 0x03, 0x23,
    0x24, 0x8c, 0x11, 0x1c, 0x02, 0xaa, 0x02, 0x04, 0x14, 0x2b, 0x24, 0xaa,
    0x02, 0x03, 0x14, 0x23, 0xaa, 0x02, 0x03, 0x14, 0x1a, 0x13, 0x24, 0xaa,
    0x02, 0x2c, 0x04, 0xaa, 0x02, 0x03, 0x1c, 0x22, 0x23, 0x8d, 0x02, 0x22,
    0x04, 0xac, 0x20, 0x10, 0x14, 0x2c, 0x12, 0x8a, 0x10, 0x19, 0x13, 0x9c,
    0x00, 0x10, 0x14, 0x0c, 0x12, 0xaa, 0x01, 0x10, 0x11, 0xa8, 0x03, 0x04,
    0x24, 0x23, 0x12, 0x8b, 0x18, 0x11, 0x9c, 0x21, 0x10, 0x01, 0x02, 0x13,
    0x2a, 0x10, 0x9b, 0x11, 0x00, 0x04, 0x24, 0x2b, 0x02, 0x9a, 0x01, 0x0a,
    0x11, 0x29, 0x22, 0x2b, 0x03, 0x1b, 0x02, 0x11, 0x22, 0x13, 0x8a, 0x00,
    0x11, 0x28, 0x11, 0x1c, 0x02, 0x2a, 0x03, 0xab, 0x10, 0x1a, 0x13, 0x9d,
    0x20, 0x00, 0x02, 0x11, 0x2a, 0x02, 0x13, 0x22, 0x24, 0x8c, 0x08, 0xa8,
    0x20, 0x10, 0x11, 0xa9, 0x10, 0x29, 0x20, 0x21, 0x11, 0x98, 0x11, 0x02,
    0x1b, 0x21, 0x12, 0xab, 0x01, 0x21, 0xaa, 0x12, 0xaa, 0x10, 0x20, 0x21,
    0x19, 0x12, 0x18, 0x11, 0xaa, 0x00, 0xa8, 0x01, 0x10, 0x21, 0x12, 0x89,
    0x02, 0x2a, 0x11, 0x1b, 0x03, 0xab, 0x01, 0x10, 0x21, 0x03, 0xab, 0x01,
    0x10, 0x21, 0x12, 0x0a, 0x12, 0x23, 0x8b, 0x11, 0xa8, 0x02, 0x0d, 0x04,
    0x14, 0x2b, 0x22, 0xac, 0x14, 0x10, 0x01, 0x1a, 0x10, 0x20, 0xac, 0x9a,
    0x14, 0x15, 0x8d, 0x20, 0xa9, 0x10, 0x20, 0x21, 0x11, 0x98, 0x01, 0x12,
    0x0b, 0x11, 0x22, 0x9b, 0x00, 0x09, 0x02, 0x28, 0x12, 0x13, 0x2b, 0x22,
    0xac, 0x00, 0x09, 0x02, 0x28, 0x12, 0x22, 0x13, 0x14, 0xac, 0x00, 0x10,
    0x11, 0x09, 0x11, 0x02, 0x28, 0x12, 0x13, 0x2b, 0x22, 0xac, 0x18, 0x11,
    0x12, 0x03, 0x14, 0xab, 0x04, 0x02, 0x11, 0x22, 0x2c, 0x03, 0x2b, 0x10,
    0xa9, 0x04, 0x02, 0x11, 0x22, 0x2c, 0x03, 0x2b, 0x01, 0x98, 0x04, 0x02,
    0x11, 0x22, 0x2c, 0x03, 0x2b, 0x01, 0x10, 0xa9, 0x04, 0x02, 0x11, 0x22,
    0x2c, 0x03, 0x2b, 0x01, 0x10, 0x11, 0xa8, 0x04, 0x02, 0x11, 0x22, 0x2c,
    0x03, 0x2b, 0x08, 0xa8, 0x04, 0x02, 0x11, 0x22, 0x2c, 0x03, 0x2b, 0x00,
    0x20, 0x11, 0x88, 0x00, 0x0c, 0x02, 0x2a, 0x00, 0x19, 0x10, 0x1c, 0x10,
    0x28, 0x14, 0xac, 0x23, 0x14, 0x03, 0x01, 0x10, 0x29, 0x14, 0x15, 0x8d,
    0x02, 0x2a, 0x02, 0x04, 0x2c, 0x03, 0x1b, 0x00, 0x99, 0x02, 0x2a, 0x02,
    0x04, 0x2c, 0x03, 0x1b, 0x11, 0xa8, 0x02, 0x2a, 0x02, 0x04, 0x2c, 0x03,
    0x1b, 0x01, 0x10, 0xa9, 0x02, 0x2a, 0x02, 0x04, 0x2c, 0x03, 0x1b, 0x08,
    0xa8, 0x02, 0x2a, 0x12, 0x1c, 0x04, 0x2c, 0x00, 0x99, 0x02, 0x2a, 0x12,
    0x1c, 0x04, 0x2c, 0x11, 0xa8, 0x02, 0x2a, 0x12, 0x1c, 0x04, 0x2c, 0x01,
    0x10, 0xa9, 0x02, 0x2a, 0x12, 0x1c, 0x04, 0x2c, 0x28, 0x88, 0x00, 0x10,
    0x21, 0x23, 0x14, 0x04, 0x08, 0x02, 0x9a, 0x04, 0x02, 0x24, 0x2a, 0x01,
    0x10, 0x11, 0xa8, 0x02, 0x22, 0x24, 0x04, 0x0a, 0x00, 0x99, 0x02, 0x22,
    0x24, 0x04, 0x0a, 0x11, 0xa8, 0x02, 0x22, 0x24, 0x04, 0x0a, 0x11, 0x28,
    0x00, 0x99, 0x02, 0x22, 0x24, 0x04, 0x0a, 0x01, 0x10, 0x11, 0xa8, 0x01,
    0x21, 0x24, 0x04, 0x09, 0x08, 0xa8, 0x01, 0x2b, 0x03, 0xa9, 0x01, 0x10,
    0x21, 0x23, 0x14, 0x03, 0x09, 0x03, 0xa9, 0x01, 0x04, 0x24, 0x29, 0x11,
    0xa8, 0x01, 0x04, 0x24, 0x29, 0x00, 0x99, 0x02, 0x04, 0x24, 0x2a, 0x01,
    0x10, 0xa9, 0x01, 0x04, 0x24, 0x29, 0x08, 0xa8, 0x01, 0x02, 0x13, 0x1c,
    0x13, 0x22, 0x29, 0x11, 0xa8, 0x00, 0x0c, 0x01, 0x11, 0x22, 0x13, 0x8b,
    0x00, 0x0d, 0x00, 0x10, 0x21, 0x1a, 0x02, 0x22, 0x24, 0x8c, 0x02, 0x04,
    0x24, 0x2a, 0x23, 0x12, 0x0a, 0x00, 0x99, 0x02, 0x04, 0x24, 0x2a, 0x23,
    0x12, 0x0a, 0x11, 0xa8, 0x02, 0x04, 0x24, 0x2a, 0x23, 0x12, 0x0a, 0x01,
    0x10, 0xa9, 0x02, 0x04, 0x24, 0x2a, 0x23, 0x12, 0x0a, 0x01, 0x10, 0x11,
    0xa8, 0x02, 0x04, 0x24, 0x2a, 0x23, 0x12, 0x0a, 0x09, 0xa9, 0x02, 0x04,
    0x24, 0x2a, 0x23, 0x12, 0x0a, 0x01, 0x10, 0x21, 0x89, 0x02, 0x1b, 0x02,
    0x04, 0x2c, 0x12, 0x1c, 0x12, 0x2a, 0x13, 0x2b, 0x22, 0xab, 0x03, 0x04,
    0x2c, 0x03, 0x12, 0x2a, 0x14, 0x15, 0x8d, 0x24, 0x04, 0x02, 0x22, 0x23,
    0x1b, 0x00, 0x99, 0x24, 0x04, 0x02, 0x22, 0x23, 0x1b, 0x11, 0xa8, 0x24,
    0x04, 0x02, 0x22, 0x23, 0x1b, 0x01, 0x10, 0xa9, 0x24, 0x04, 0x02, 0x22,
    0x23, 0x1b, 0x09, 0xa9, 0x12, 0x1c, 0x00, 0x99, 0x12, 0x1c, 0x11, 0xa8,
    0x12, 0x1c, 0x01, 0x10, 0xa9, 0x12, 0x1c, 0x09, 0xa9, 0x00, 0x2a, 0x11,
    0x28, 0x02, 0x22, 0x24, 0x04, 0x8a, 0x02, 0x0c, 0x03, 0x12, 0x23, 0x2c,
    0x01, 0x10, 0x11, 0xa8, 0x02, 0x04, 0x24, 0x22, 0x0a, 0x00, 0x99, 0x02,
    0x04, 0x24, 0x22, 0x0a, 0x11, 0xa8, 0x02, 0x04, 0x24, 0x22, 0x0a, 0x01,
    0x10, 0xa9, 0x02, 0x04, 0x24, 0x22, 0x0a, 0x01, 0x10, 0x11, 0xa8, 0x02,
    0x04, 0x24, 0x22, 0x0a, 0x09, 0xa9, 0x19, 0x02, 0x2a, 0x9b, 0x02, 0x04,
    0x24, 0x22, 0x0a, 0x04, 0xaa, 0x02, 0x04, 0x14, 0x2b, 0x24, 0x2a, 0x00,
    0x99, 0x02, 0x04, 0x14, 0x2b, 0x24, 0x2a, 0x11, 0xa8, 0x02, 0x04, 0x14,
    0x2b, 0x24, 0x2a, 0x01, 0x10, 0xa9, 0x02, 0x04, 0x14, 0x2b, 0x24, 0x2a,
    0x09, 0xa9, 0x02, 0x03, 0x1c, 0x22, 0x23, 0x0d, 0x11, 0xa8, 0x00, 0x0c,
    0x02, 0x11, 0x22, 0x13, 0x8a, 0x02, 0x03, 0x1c, 0x22, 0x23, 0x0d, 0x09,
    0xa9,
};

// The following two arrays are generated by Fonter/fonter.cpp
const unsigned short RawdrawFontCharMap[256] = {
    65535, 0,     8,     16,    24,    31,    41,    50,    51,    65535, 65535,
    57,    66,    65535, 75,    83,    92,    96,    100,   108,   114,   123,
    132,   137,   147,   152,   158,   163,   169,   172,   178,   182,   65535,
    186,   189,   193,   201,   209,   217,   226,   228,   232,   236,   244,
    248,   250,   252,   253,   255,   261,   266,   272,   278,   283,   289,
    295,   300,   309,   316,   318,   321,   324,   328,   331,   337,   345,
    352,   362,   368,   375,   382,   388,   396,   402,   408,   413,   422,
    425,   430,   435,   442,   449,   458,   466,   472,   476,   480,   485,
    492,   500,   507,   512,   516,   518,   522,   525,   527,   529,   536,
    541,   546,   551,   557,   564,   572,   578,   581,   586,   593,   595,
    604,   610,   615,   621,   627,   632,   638,   642,   648,   653,   660,
    664,   670,   674,   680,   684,   690,   694,   65535, 65535, 65535, 65535,
    65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535,
    65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535,
    65535, 65535, 65535, 65535, 65535, 65535, 700,   703,   711,   718,   731,
    740,   744,   754,   756,   760,   766,   772,   775,   777,   785,   787,
    792,   798,   803,   811,   813,   820,   827,   828,   831,   833,   838,
    844,   853,   862,   874,   880,   889,   898,   908,   919,   928,   939,
    951,   960,   969,   978,   988,   997,   1005,  1013,  1022,  1030,  1039,
    1047,  1054,  1061,  1070,  1079,  1086,  1090,  1099,  1105,  1111,  1118,
    1124,  1133,  1140,  1150,  1159,  1168,  1178,  1189,  1198,  1209,  1222,
    1231,  1239,  1247,  1256,  1264,  1268,  1272,  1277,  1281,  1290,  1300,
    1307,  1314,  1322,  1331,  1338,  1342,  1349,  1357,  1365,  1374,  1382,
    1390,  1397,  65535,
};

static short iLastInternalW, iLastInternalH;
uint32_t randomtexturedata[256 * 256];
static float mountainangle;
static float mountainoffsetx;
static float mountainoffsety;
int CNFGPenX, CNFGPenY;

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

static bool no_sensor_for_gyro = false;
static ASensorEventQueue *aeq;

int lastbuttonx = 0;
int lastbuttony = 0;
int lastmotionx = 0;
int lastmotiony = 0;
int lastbid = 0;
int lastmask = 0;
int lastkey, lastkeydown;

struct android_app *gapp;
static int OGLESStarted;
int android_width, android_height;
int android_sdk_version;

short screenx, screeny;
float Heightmap[HMX * HMY];

static int keyboard_up;

unsigned long iframeno = 0;
float accx, accy, accz;
int accs;

float translateX;
float translateY;
float scaleX;
float scaleY;

float *gSMatrix;
float gsMatricies[2][32][16];

void CNFGSwapBuffers();
void CNFGSetupFullscreen(const char* WindowName, int screen_number);
void CNFGHandleInput();
int AndroidGetUnicodeChar( int keyCode, int metaState );
void AndroidSendToBack( int param );

void tdFinalPoint(float *pin, float *pout) {
  float tdin[4] = {pin[0], pin[1], pin[2], 1.};
  float tmp[4];
  td4Transform(tdin, gsMatricies[0][0], tmp);
  td4Transform(tmp, gsMatricies[1][0], tmp);
  pout[0] = (tmp[0] / tmp[3] - translateX) * scaleX;
  pout[1] = (tmp[1] / tmp[3] - translateY) * scaleY;
  pout[2] = tmp[2] / tmp[3];
}

void tdSetViewport(float leftx, float topy, float rightx, float bottomy,
                   float pixx, float pixy) {
  translateX = leftx;
  translateY = bottomy;
  scaleX = pixx / (rightx - leftx);
  scaleY = pixy / (topy - bottomy);
}

void tdMode(int mode) {
  if (mode < 0 || mode > 1)
    return;
  gSMatrix = gsMatricies[mode][0];
}

void CNFGDrawText(const char *text, short scale) {
  const unsigned char *lmap;
  float iox = (float)CNFGPenX; // x offset
  float ioy = (float)CNFGPenY; // y offset

  int place = 0;
  unsigned short index;
  int bQuit = 0;
  while (text[place]) {
    unsigned char c = text[place];
    switch (c) {
    case 9: // tab
      iox += 12 * scale;
      break;
    case 10: // linefeed
      iox = (float)CNFGPenX;
      ioy += 6 * scale;
      break;
    default:
      index = RawdrawFontCharMap[c];
      if (index == 65535) {
        iox += 3 * scale;
        break;
      }

      lmap = &RawdrawFontCharData[index];
      short penx, peny;
      unsigned char start_seg = 1;
      do {
        unsigned char data = (*(lmap++));
        short x1 = (short)(((data >> 4) & 0x07) * scale + iox);
        short y1 = (short)((data & 0x07) * scale + ioy);
        if (start_seg) {
          penx = x1;
          peny = y1;
          start_seg = 0;
          if (data & 0x08)
            CNFGTackPixel(x1, y1);
        } else {
          CNFGTackSegment(penx, peny, x1, y1);
          penx = x1;
          peny = y1;
        }
        if (data & 0x08)
          start_seg = 1;
        bQuit = data & 0x80;
      } while (!bQuit);

      iox += 3 * scale;
    }
    place++;
  }
}

static inline double OGGetAbsoluteTime() {
  struct timeval tv;
  gettimeofday(&tv, 0);
  return ((double)tv.tv_usec) / 1000000. + (tv.tv_sec);
}

void tdPSubtract(float *x, float *y, float *z) {
  z[0] = x[0] - y[0];
  z[1] = x[1] - y[1];
  z[2] = x[2] - y[2];
}

void SetupIMU() {
  static ASensorManager *sm;
  static const ASensor *as;
  static ALooper *alooper;

  sm = ASensorManager_getInstance();
  as = ASensorManager_getDefaultSensor(sm, ASENSOR_TYPE_GYROSCOPE);
  no_sensor_for_gyro = as == NULL;
  alooper = ALooper_prepare(ALOOPER_PREPARE_ALLOW_NON_CALLBACKS);
  aeq = ASensorManager_createEventQueue(sm, (ALooper *)&alooper, 0, 0, 0);
  if (!no_sensor_for_gyro) {
    ASensorEventQueue_enableSensor(aeq, as);
    printf("setEvent Rate: %d\n",
           ASensorEventQueue_setEventRate(aeq, as, 10000));
  }
}

void AccCheck() {
  if (no_sensor_for_gyro) {
    return;
  }

  ASensorEvent evt;
  do {
    ssize_t s = ASensorEventQueue_getEvents(aeq, &evt, 1);
    if (s <= 0)
      break;
    accx = evt.vector.v[0];
    accy = evt.vector.v[1];
    accz = evt.vector.v[2];
    mountainangle -= accz;
    mountainoffsety += accy;
    mountainoffsetx += accx;
    accs++;
  } while (1);
}

void AndroidDisplayKeyboard(int pShow);

void HandleKey(int keycode, int bDown) {
  lastkey = keycode;
  lastkeydown = bDown;
  if (keycode == 10 && !bDown) {
    keyboard_up = 0;
    AndroidDisplayKeyboard(keyboard_up);
  }

  if (keycode == 4) {
    AndroidSendToBack(1);
  }
}

void HandleButton(int x, int y, int button, int bDown) {
  lastbid = button;
  lastbuttonx = x;
  lastbuttony = y;

  if (bDown) {
    keyboard_up = !keyboard_up;
    AndroidDisplayKeyboard(keyboard_up);
  }
}

void HandleMotion(int x, int y, int mask) {
  lastmask = mask;
  lastmotionx = x;
  lastmotiony = y;
}

void DrawHeightmap() {
  int x, y;
  mountainangle += .2;
  if (mountainangle < 0)
    mountainangle += 360;
  if (mountainangle > 360)
    mountainangle -= 360;

  mountainoffsety = mountainoffsety - ((mountainoffsety - 100) * .1);

  float eye[3] = {(float)sin(mountainangle * (3.14159 / 180.0)) * 30 *
                      sin(mountainoffsety / 100.),
                  (float)cos(mountainangle * (3.14159 / 180.0)) * 30 *
                      sin(mountainoffsety / 100.),
                  30 * cos(mountainoffsety / 100.)};
  float at[3] = {0, 0, 0};
  float up[3] = {0, 0, 1};

  tdSetViewport(-1, -1, 1, 1, screenx, screeny);

  tdMode(tdPROJECTION);
  tdIdentity(gSMatrix);
  tdPerspective(30, ((float)screenx) / ((float)screeny), .1, 200., gSMatrix);

  tdMode(tdMODELVIEW);
  tdIdentity(gSMatrix);
  tdTranslate(gSMatrix, 0, 0, -40);
  tdLookAt(gSMatrix, eye, at, up);

  float scale = 60. / HMX;

  for (x = 0; x < HMX - 1; x++)
    for (y = 0; y < HMY - 1; y++) {
      float tx = x - HMX / 2;
      float ty = y - HMY / 2;
      float pta[3];
      float ptb[3];
      float ptc[3];
      float ptd[3];

      float normal[3];
      float lightdir[3] = {.6, -.6, 1};
      float tmp1[3];
      float tmp2[3];

      RDPoint pto[6];

      pta[0] = (tx + 0) * scale;
      pta[1] = (ty + 0) * scale;
      pta[2] = Heightmap[(x + 0) + (y + 0) * HMX] * scale;
      ptb[0] = (tx + 1) * scale;
      ptb[1] = (ty + 0) * scale;
      ptb[2] = Heightmap[(x + 1) + (y + 0) * HMX] * scale;
      ptc[0] = (tx + 0) * scale;
      ptc[1] = (ty + 1) * scale;
      ptc[2] = Heightmap[(x + 0) + (y + 1) * HMX] * scale;
      ptd[0] = (tx + 1) * scale;
      ptd[1] = (ty + 1) * scale;
      ptd[2] = Heightmap[(x + 1) + (y + 1) * HMX] * scale;

      tdPSubtract(pta, ptb, tmp2);
      tdPSubtract(ptc, ptb, tmp1);
      tdCross(tmp1, tmp2, normal);
      tdNormalizeSelf(normal);

      tdFinalPoint(pta, pta);
      tdFinalPoint(ptb, ptb);
      tdFinalPoint(ptc, ptc);
      tdFinalPoint(ptd, ptd);

      if (pta[2] >= 1.0)
        continue;
      if (ptb[2] >= 1.0)
        continue;
      if (ptc[2] >= 1.0)
        continue;
      if (ptd[2] >= 1.0)
        continue;

      if (pta[2] < 0)
        continue;
      if (ptb[2] < 0)
        continue;
      if (ptc[2] < 0)
        continue;
      if (ptd[2] < 0)
        continue;

      pto[0].x = pta[0];
      pto[0].y = pta[1];
      pto[1].x = ptb[0];
      pto[1].y = ptb[1];
      pto[2].x = ptd[0];
      pto[2].y = ptd[1];

      pto[3].x = ptc[0];
      pto[3].y = ptc[1];
      pto[4].x = ptd[0];
      pto[4].y = ptd[1];
      pto[5].x = pta[0];
      pto[5].y = pta[1];

      float bright = tdDot(normal, lightdir);
      if (bright < 0)
        bright = 0;
      CNFGColor(0xff | (((int)(bright * 90)) << 24));
      CNFGTackSegment(pta[0], pta[1], ptb[0], ptb[1]);
      CNFGTackSegment(pta[0], pta[1], ptc[0], ptc[1]);
      CNFGTackSegment(ptb[0], ptb[1], ptc[0], ptc[1]);
    }
}

void HandleDestroy() {
  printf("Destroying\n");
  exit(10);
}

void HandleSuspend() { suspended = 1; }

void HandleResume() { suspended = 0; }

static void display_image() {
  int x, y;
  double ThisTime;
  double LastFPSTime = OGGetAbsoluteTime();

  CNFGSetupFullscreen("Test Bench", 0);

  for (x = 0; x < HMX; x++)
    for (y = 0; y < HMY; y++) {
      Heightmap[x + y * HMX] = tdPerlin2D(x, y) * 8.;
    }

  const char *assettext = "Not Found";
  AAsset *file = AAssetManager_open(gapp->activity->assetManager, "asset.txt",
                                    AASSET_MODE_BUFFER);
  if (file) {
    size_t fileLength = AAsset_getLength(file);
    char *temp = malloc(fileLength + 1);
    memcpy(temp, AAsset_getBuffer(file), fileLength);
    temp[fileLength] = 0;
    assettext = temp;
  }
  SetupIMU();

  while (1) {
    int i;
    iframeno++;

    CNFGHandleInput();
    AccCheck();

    if (suspended) {
      usleep(50000);
      continue;
    }

    CNFGClearFrame();
    CNFGColor(0xFFFFFFFF);
    CNFGGetDimensions(&screenx, &screeny);

    // Mesh in background
    CNFGSetLineWidth(9);
    DrawHeightmap();
    CNFGPenX = 0;
    CNFGPenY = 400;
    CNFGColor(0xffffffff);
    CNFGDrawText(assettext, 15);
    CNFGFlushRender();

    CNFGPenX = 0;
    CNFGPenY = 480;
    char st[50];
    sprintf(st, "%dx%d %d %d %d %d %d %d\n%d %d\n%5.2f %5.2f %5.2f %d", screenx,
            screeny, lastbuttonx, lastbuttony, lastmotionx, lastmotiony,
            lastkey, lastkeydown, lastbid, lastmask, accx, accy, accz, accs);
    CNFGDrawText(st, 10);
    CNFGSetLineWidth(2);

    // Square behind text
    CNFGColor(0x303030ff);
    CNFGTackRectangle(600, 0, 950, 350);

    CNFGPenX = 10;
    CNFGPenY = 10;

    // Text
    CNFGColor(0xffffffff);
    for (i = 0; i < 1; i++) {
      int c;
      char tw[2] = {0, 0};
      for (c = 0; c < 256; c++) {
        tw[0] = c;

        CNFGPenX = (c % 16) * 20 + 606;
        CNFGPenY = (c / 16) * 20 + 5;
        CNFGDrawText(tw, 4);
      }
    }

    // Green triangles
    CNFGPenX = 0;
    CNFGPenY = 0;

    for (i = 0; i < 400; i++) {
      RDPoint pp[3];
      CNFGColor(0x00FF00FF);
      pp[0].x = (short)(50 * sin((float)(i + iframeno) * .01) + (i % 20) * 30);
      pp[0].y =
          (short)(50 * cos((float)(i + iframeno) * .01) + (i / 20) * 20) + 700;
      pp[1].x = (short)(20 * sin((float)(i + iframeno) * .01) + (i % 20) * 30);
      pp[1].y =
          (short)(50 * cos((float)(i + iframeno) * .01) + (i / 20) * 20) + 700;
      pp[2].x = (short)(10 * sin((float)(i + iframeno) * .01) + (i % 20) * 30);
      pp[2].y =
          (short)(30 * cos((float)(i + iframeno) * .01) + (i / 20) * 20) + 700;
      CNFGTackPoly(pp, 3);
    }

    int x, y;
    for (y = 0; y < 256; y++)
      for (x = 0; x < 256; x++)
        randomtexturedata[x + y * 256] =
            x | ((x * 394543L + y * 355 + iframeno) << 8);
    CNFGBlitImage(randomtexturedata, 100, 600, 256, 256);

    // On Android, CNFGSwapBuffers must be called, and
    // CNFGUpdateScreenWithBitmap does not have an implied framebuffer swap.
    CNFGSwapBuffers();

    ThisTime = OGGetAbsoluteTime();
    if (ThisTime > LastFPSTime + 1) {
      LastFPSTime += 1;
    }
  }
}

typedef enum {
  FBDEV_PIXMAP_DEFAULT = 0,
  FBDEV_PIXMAP_SUPPORTS_UMP = (1 << 0),
  FBDEV_PIXMAP_ALPHA_FORMAT_PRE = (1 << 1),
  FBDEV_PIXMAP_COLORSPACE_sRGB = (1 << 2),
  FBDEV_PIXMAP_EGL_MEMORY = (1 << 3) /* EGL allocates/frees this memory */
} fbdev_pixmap_flags;

typedef struct fbdev_window {
  unsigned short width;
  unsigned short height;
} fbdev_window;

typedef struct fbdev_pixmap {
  unsigned int height;
  unsigned int width;
  unsigned int bytes_per_pixel;
  unsigned char buffer_size;
  unsigned char red_size;
  unsigned char green_size;
  unsigned char blue_size;
  unsigned char alpha_size;
  unsigned char luminance_size;
  fbdev_pixmap_flags flags;
  unsigned short *data;
  unsigned int format;
} fbdev_pixmap;

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

void CNFGSwapBuffers() {
  CNFGFlushRender();
  eglSwapBuffers(egl_display, egl_surface);
  android_width = ANativeWindow_getWidth(native_window);
  android_height = ANativeWindow_getHeight(native_window);
  glViewport(0, 0, android_width, android_height);
  if (iLastInternalW != android_width || iLastInternalH != android_height)
    CNFGInternalResize(iLastInternalW = android_width,
                       iLastInternalH = android_height);
}

void CNFGGetDimensions(short *x, short *y) {
  *x = android_width;
  *y = android_height;
  if (*x != iLastInternalW || *y != iLastInternalH)
    CNFGInternalResize(iLastInternalW = *x, iLastInternalH = *y);
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
                             //				NULL );
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

  {
    short dummyx, dummyy;
    CNFGGetDimensions(&dummyx, &dummyy);
  }

  return 0;
}

void CNFGSetupFullscreen(const char *WindowName, int screen_number) {
  // Removes decoration, must be called before setup.
  AndroidMakeFullscreen();

  CNFGSetup(WindowName, -1, -1);
}

int32_t handle_input(struct android_app *app, AInputEvent *event) {
  // Potentially do other things here.

  if (AInputEvent_getType(event) == AINPUT_EVENT_TYPE_MOTION) {
    int action = AMotionEvent_getAction(event);
    int whichsource = action >> 8;
    action &= AMOTION_EVENT_ACTION_MASK;
    size_t pointerCount = AMotionEvent_getPointerCount(event);

    for (size_t i = 0; i < pointerCount; ++i) {
      int x, y, index;
      x = AMotionEvent_getX(event, i);
      y = AMotionEvent_getY(event, i);
      index = AMotionEvent_getPointerId(event, i);

      if (action == AMOTION_EVENT_ACTION_POINTER_DOWN ||
          action == AMOTION_EVENT_ACTION_DOWN) {
        int id = index;
        if (action == AMOTION_EVENT_ACTION_POINTER_DOWN && id != whichsource)
          continue;
        HandleButton(x, y, id, 1);
        ANativeActivity_showSoftInput(gapp->activity,
                                      ANATIVEACTIVITY_SHOW_SOFT_INPUT_FORCED);
      } else if (action == AMOTION_EVENT_ACTION_POINTER_UP ||
                 action == AMOTION_EVENT_ACTION_UP ||
                 action == AMOTION_EVENT_ACTION_CANCEL) {
        int id = index;
        if (action == AMOTION_EVENT_ACTION_POINTER_UP && id != whichsource)
          continue;
        HandleButton(x, y, id, 0);
      } else if (action == AMOTION_EVENT_ACTION_MOVE) {
        HandleMotion(x, y, index);
      }
    }
    return 1;
  } else if (AInputEvent_getType(event) == AINPUT_EVENT_TYPE_KEY) {
    int code = AKeyEvent_getKeyCode(event);
    int unicode = AndroidGetUnicodeChar(code, AMotionEvent_getMetaState(event));
    if (unicode)
      HandleKey(unicode, AKeyEvent_getAction(event));
    else {
      HandleKey(code, !AKeyEvent_getAction(event));
      return (code == 4) ? 1 : 0; // don't override functionality.
    }

    return 1;
  }
  return 0;
}

void CNFGHandleInput() {

  int events;
  struct android_poll_source *source;
  while (ALooper_pollAll(0, 0, &events, (void **)&source) >= 0) {
    if (source != NULL) {
      source->process(gapp, source);
    }
  }
}

void handle_cmd(struct android_app *app, int32_t cmd) {
  switch (cmd) {
  case APP_CMD_DESTROY:
    // This gets called initially after back.
    HandleDestroy();
    ANativeActivity_finish(gapp->activity);
    break;
  case APP_CMD_INIT_WINDOW:
    // When returning from a back button suspension, this isn't called.
    if (!OGLESStarted) {
      OGLESStarted = 1;
      printf("Got start event\n");
    } else {
      CNFGSetup("", -1, -1);
      HandleResume();
    }
    break;
  // case APP_CMD_TERM_WINDOW:
  // This gets called initially when you click "back"
  // This also gets called when you are brought into standby.
  // Not sure why - callbacks here seem to break stuff.
  //	break;
  default:
    printf("event not handled: %d\n", cmd);
  }
}

int __system_property_get(const char *name, char *value);

void AndroidDisplayKeyboard(int pShow) {
  jint lFlags = 0;
  const struct JNINativeInterface *env = 0;
  const struct JNINativeInterface **envptr = &env;
  const struct JNIInvokeInterface **jniiptr = gapp->activity->vm;
  const struct JNIInvokeInterface *jnii = *jniiptr;

  jnii->AttachCurrentThread(jniiptr, &envptr, NULL);
  env = (*envptr);
  jclass activityClass = env->FindClass(envptr, "android/app/NativeActivity");

  // Retrieves NativeActivity.
  jobject lNativeActivity = gapp->activity->clazz;

  // Retrieves Context.INPUT_METHOD_SERVICE.
  jclass ClassContext = env->FindClass(envptr, "android/content/Context");
  jfieldID FieldINPUT_METHOD_SERVICE = env->GetStaticFieldID(
      envptr, ClassContext, "INPUT_METHOD_SERVICE", "Ljava/lang/String;");
  jobject INPUT_METHOD_SERVICE = env->GetStaticObjectField(
      envptr, ClassContext, FieldINPUT_METHOD_SERVICE);

  // Runs getSystemService(Context.INPUT_METHOD_SERVICE).
  jclass ClassInputMethodManager =
      env->FindClass(envptr, "android/view/inputmethod/InputMethodManager");
  jmethodID MethodGetSystemService =
      env->GetMethodID(envptr, activityClass, "getSystemService",
                       "(Ljava/lang/String;)Ljava/lang/Object;");
  jobject lInputMethodManager = env->CallObjectMethod(
      envptr, lNativeActivity, MethodGetSystemService, INPUT_METHOD_SERVICE);

  // Runs getWindow().getDecorView().
  jmethodID MethodGetWindow = env->GetMethodID(
      envptr, activityClass, "getWindow", "()Landroid/view/Window;");
  jobject lWindow =
      env->CallObjectMethod(envptr, lNativeActivity, MethodGetWindow);
  jclass ClassWindow = env->FindClass(envptr, "android/view/Window");
  jmethodID MethodGetDecorView = env->GetMethodID(
      envptr, ClassWindow, "getDecorView", "()Landroid/view/View;");
  jobject lDecorView =
      env->CallObjectMethod(envptr, lWindow, MethodGetDecorView);

  if (pShow) {
    // Runs lInputMethodManager.showSoftInput(...).
    jmethodID MethodShowSoftInput =
        env->GetMethodID(envptr, ClassInputMethodManager, "showSoftInput",
                         "(Landroid/view/View;I)Z");
    /*jboolean lResult = */ env->CallBooleanMethod(
        envptr, lInputMethodManager, MethodShowSoftInput, lDecorView, lFlags);
  } else {
    // Runs lWindow.getViewToken()
    jclass ClassView = env->FindClass(envptr, "android/view/View");
    jmethodID MethodGetWindowToken = env->GetMethodID(
        envptr, ClassView, "getWindowToken", "()Landroid/os/IBinder;");
    jobject lBinder =
        env->CallObjectMethod(envptr, lDecorView, MethodGetWindowToken);

    // lInputMethodManager.hideSoftInput(...).
    jmethodID MethodHideSoftInput =
        env->GetMethodID(envptr, ClassInputMethodManager,
                         "hideSoftInputFromWindow", "(Landroid/os/IBinder;I)Z");
    /*jboolean lRes = */ env->CallBooleanMethod(
        envptr, lInputMethodManager, MethodHideSoftInput, lBinder, lFlags);
  }

  // Finished with the JVM.
  jnii->DetachCurrentThread(jniiptr);
}

int AndroidGetUnicodeChar(int keyCode, int metaState) {
  int eventType = AKEY_EVENT_ACTION_DOWN;
  const struct JNINativeInterface *env = 0;
  const struct JNINativeInterface **envptr = &env;
  const struct JNIInvokeInterface **jniiptr = gapp->activity->vm;
  const struct JNIInvokeInterface *jnii = *jniiptr;

  jnii->AttachCurrentThread(jniiptr, &envptr, NULL);
  env = (*envptr);
  jclass class_key_event = env->FindClass(envptr, "android/view/KeyEvent");
  int unicodeKey;

  jmethodID method_get_unicode_char =
      env->GetMethodID(envptr, class_key_event, "getUnicodeChar", "(I)I");
  jmethodID eventConstructor =
      env->GetMethodID(envptr, class_key_event, "<init>", "(II)V");
  jobject eventObj = env->NewObject(envptr, class_key_event, eventConstructor,
                                    eventType, keyCode);

  unicodeKey =
      env->CallIntMethod(envptr, eventObj, method_get_unicode_char, metaState);

  // Finished with the JVM.
  jnii->DetachCurrentThread(jniiptr);

  // printf("Unicode key is: %d", unicodeKey);
  return unicodeKey;
}

jstring android_permission_name(const struct JNINativeInterface **envptr,
                                const char *perm_name) {
  // nested class permission in class android.Manifest,
  // hence android 'slash' Manifest 'dollar' permission
  const struct JNINativeInterface *env = *envptr;
  jclass ClassManifestpermission =
      env->FindClass(envptr, "android/Manifest$permission");
  jfieldID lid_PERM = env->GetStaticFieldID(envptr, ClassManifestpermission,
                                            perm_name, "Ljava/lang/String;");
  jstring ls_PERM = (jstring)(
      env->GetStaticObjectField(envptr, ClassManifestpermission, lid_PERM));
  return ls_PERM;
}

/**
 * \brief Tests whether a permission is granted.
 * \param[in] app a pointer to the android app.
 * \param[in] perm_name the name of the permission, e.g.,
 *   "READ_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE".
 * \retval true if the permission is granted.
 * \retval false otherwise.
 * \note Requires Android API level 23 (Marshmallow, May 2015)
 */
int AndroidHasPermissions(const char *perm_name) {
  struct android_app *app = gapp;
  const struct JNINativeInterface *env = 0;
  const struct JNINativeInterface **envptr = &env;
  const struct JNIInvokeInterface **jniiptr = app->activity->vm;
  const struct JNIInvokeInterface *jnii = *jniiptr;

  if (android_sdk_version < 23) {
    printf("Android SDK version %d does not support AndroidHasPermissions\n",
           android_sdk_version);
    return 1;
  }

  jnii->AttachCurrentThread(jniiptr, &envptr, NULL);
  env = (*envptr);

  int result = 0;
  jstring ls_PERM = android_permission_name(envptr, perm_name);

  jint PERMISSION_GRANTED = (-1);

  {
    jclass ClassPackageManager =
        env->FindClass(envptr, "android/content/pm/PackageManager");
    jfieldID lid_PERMISSION_GRANTED = env->GetStaticFieldID(
        envptr, ClassPackageManager, "PERMISSION_GRANTED", "I");
    PERMISSION_GRANTED = env->GetStaticIntField(envptr, ClassPackageManager,
                                                lid_PERMISSION_GRANTED);
  }
  {
    jobject activity = app->activity->clazz;
    jclass ClassContext = env->FindClass(envptr, "android/content/Context");
    jmethodID MethodcheckSelfPermission = env->GetMethodID(
        envptr, ClassContext, "checkSelfPermission", "(Ljava/lang/String;)I");
    jint int_result = env->CallIntMethod(envptr, activity,
                                         MethodcheckSelfPermission, ls_PERM);
    result = (int_result == PERMISSION_GRANTED);
  }

  jnii->DetachCurrentThread(jniiptr);

  return result;
}

/**
 * \brief Query file permissions.
 * \details This opens the system dialog that lets the user
 *  grant (or deny) the permission.
 * \param[in] app a pointer to the android app.
 * \note Requires Android API level 23 (Marshmallow, May 2015)
 */
void AndroidRequestAppPermissions(const char *perm) {
  if (android_sdk_version < 23) {
    printf("Android SDK version %d does not support "
           "AndroidRequestAppPermissions\n",
           android_sdk_version);
    return;
  }

  struct android_app *app = gapp;
  const struct JNINativeInterface *env = 0;
  const struct JNINativeInterface **envptr = &env;
  const struct JNIInvokeInterface **jniiptr = app->activity->vm;
  const struct JNIInvokeInterface *jnii = *jniiptr;
  jnii->AttachCurrentThread(jniiptr, &envptr, NULL);
  env = (*envptr);
  jobject activity = app->activity->clazz;

  jobjectArray perm_array =
      env->NewObjectArray(envptr, 1, env->FindClass(envptr, "java/lang/String"),
                          env->NewStringUTF(envptr, ""));
  env->SetObjectArrayElement(envptr, perm_array, 0,
                             android_permission_name(envptr, perm));
  jclass ClassActivity = env->FindClass(envptr, "android/app/Activity");

  jmethodID MethodrequestPermissions = env->GetMethodID(
      envptr, ClassActivity, "requestPermissions", "([Ljava/lang/String;I)V");

  // Last arg (0) is just for the callback (that I do not use)
  env->CallVoidMethod(envptr, activity, MethodrequestPermissions, perm_array,
                      0);
  jnii->DetachCurrentThread(jniiptr);
}

void AndroidSendToBack(int param) {
  struct android_app *app = gapp;
  const struct JNINativeInterface *env = 0;
  const struct JNINativeInterface **envptr = &env;
  const struct JNIInvokeInterface **jniiptr = app->activity->vm;
  const struct JNIInvokeInterface *jnii = *jniiptr;
  jnii->AttachCurrentThread(jniiptr, &envptr, NULL);
  env = (*envptr);
  jobject activity = app->activity->clazz;

  jclass ClassActivity = env->FindClass(envptr, "android/app/Activity");
  jmethodID MethodmoveTaskToBack =
      env->GetMethodID(envptr, ClassActivity, "moveTaskToBack", "(Z)Z");
  env->CallBooleanMethod(envptr, activity, MethodmoveTaskToBack, param);
  jnii->DetachCurrentThread(jniiptr);
}

void android_main(struct android_app *app) {
  printf("Starting android_main\n");
  {
    char sdk_ver_str[92];
    int len = __system_property_get("ro.build.version.sdk", sdk_ver_str);
    if (len <= 0)
      android_sdk_version = 0;
    else
      android_sdk_version = atoi(sdk_ver_str);
  }

  gapp = app;
  app->onAppCmd = handle_cmd;
  app->onInputEvent = handle_input;

  display_image();
}