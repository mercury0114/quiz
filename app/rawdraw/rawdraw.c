//Copyright (c) 2011 <>< Charles Lohr - Under the MIT/x11 or NewBSD License you choose.

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "os_generic.h"


#define CNFG3D
#define CNFG_IMPLEMENTATION

#include "CNFG.h"

unsigned frames = 0;
unsigned long iframeno = 0;

void HandleKey( int keycode, int bDown )
{
	if( keycode == 27 ) exit( 0 );
	printf( "Key: %d -> %d\n", keycode, bDown );
}

void HandleButton( int x, int y, int button, int bDown )
{
	printf( "Button: %d,%d (%d) -> %d\n", x, y, button, bDown );
}

#define HMX 40
#define HMY 40
short screenx, screeny;
float Heightmap[HMX*HMY];

void HandleDestroy()
{
	printf( "Destroying\n" );
	exit(10);
}

uint32_t randomtexturedata[65536];


