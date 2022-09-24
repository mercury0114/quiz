#pragma once

#define CNFG_BATCH 8192

typedef struct {
    short x, y; 
} RDPoint; 

void CNFGGetDimensions( short * x, short * y );
void 	CNFGSwapBuffers();
void CNFGSetupFullscreen( const char * WindowName, int screen_number );
void CNFGHandleInput();




