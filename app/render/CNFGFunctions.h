#pragma once

typedef struct {
    short x, y; 
} RDPoint; 


void CNFGGetDimensions(short* x, short* y);

//Note that some backends do not support alpha of any kind.
//Some platforms also support alpha blending.  So, be sure to set alpha to 0xFF
uint32_t CNFGColor( uint32_t RGBA );


void CNFGDrawBox(short x1, short y1, short x2, short y2);

void CNFGGetTextExtents(const char *text, int *w, int *h, int textsize);
void CNFGEmitBackendTriangles(const float *fv, const uint32_t *col, int nr_verts);

void CNFGTackPixel(short x1, short y1);
void CNFGTackSegment(short x1, short y1, short x2, short y2);
void CNFGTackRectangle(short x1, short y1, short x2, short y2);
void CNFGTackPoly(RDPoint *points, int verts);
uint32_t CNFGColor(uint32_t RGB);
void CNFGSetLineWidth(short width);
uint32_t CNFGGLInternalLoadShader(const char *vertex_shader,
                                  const char *fragment_shader);
void CNFGSetupBatchInternal();
void CNFGInternalResize(short x, short y);
void CNFGEmitBackendTriangles(const float *vertices, const uint32_t *colors,
                              int num_vertices);

void CNFGBlitImage(uint32_t *data, int x, int y, int w, int h);
void CNFGUpdateScreenWithBitmap(uint32_t *data, int w, int h);

void CNFGFlushRender();
void CNFGClearFrame();
