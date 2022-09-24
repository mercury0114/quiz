#pragma once
//This file contains the additional functions that are available on the Android platform.
//In order to build rawdraw for Android, please compile CNFGEGLDriver.c with -DANDROID

int AndroidHasPermissions(const char* perm_name);
void AndroidRequestAppPermissions(const char * perm);
void AndroidDisplayKeyboard(int pShow);
int AndroidGetUnicodeChar( int keyCode, int metaState );
void AndroidSendToBack( int param );

extern int android_sdk_version; //Derived at start from property ro.build.version.sdk
extern int android_width, android_height;
extern int UpdateScreenWithBitmapOffsetX;
extern int UpdateScreenWithBitmapOffsetY;

//You must implement these.
void HandleResume();
void HandleSuspend();
