#include <GLES3/gl3.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <math.h>

#include "CNFGFunctions.h"

#define CNFG_BATCH 8192
#define GL_FRAGMENT_SHADER 0x8B30
#define GL_VERTEX_SHADER 0x8B31
#define GL_COMPILE_STATUS 0x8B81
#define GL_INFO_LOG_LENGTH 0x8B84
#define GL_LINK_STATUS 0x8B82
#define GL_TEXTURE_2D 0x0DE1
#define GL_CLAMP_TO_EDGE 0x812F

uint32_t CNFGBGColor = 0x00000000;
static uint32_t CNFGLastColor;
uint32_t CNFGDialogColor;
static float CNFGVertDataV[CNFG_BATCH * 3];
uint32_t CNFGVertDataC[CNFG_BATCH];
int CNFGVertPlace;
static float wgl_last_width_over_2 = .5;
uint32_t gRDShaderProg = -1;
uint32_t gRDBlitProg = -1;
uint32_t gRDShaderProgUX = -1;
uint32_t gRDBlitProgUX = -1;
uint32_t gRDBlitProgUT = -1;
uint32_t gRDBlitProgTex = -1;
uint32_t gRDLastResizeW;
uint32_t gRDLastResizeH;


void CNFGDrawBox(short x1, short y1, short x2, short y2) {
  uint32_t lc = CNFGLastColor;
  CNFGColor(CNFGDialogColor);
  CNFGTackRectangle(x1, y1, x2, y2);
  CNFGColor(lc);
  CNFGTackSegment(x1, y1, x2, y1);
  CNFGTackSegment(x2, y1, x2, y2);
  CNFGTackSegment(x2, y2, x1, y2);
  CNFGTackSegment(x1, y2, x1, y1);
}

void CNFGGetTextExtents(const char *text, int *w, int *h, int textsize) {
  int charsx = 0;
  int charsy = 1;
  int charsline = 0;
  const char *s;

  for (s = text; *s; s++) {
    if (*s == '\n') {
      charsline = 0;
      if (*(s + 1))
        charsy++;
    } else {
      charsline++;
      if (charsline > charsx)
        charsx = charsline;
    }
  }

  *w = charsx * textsize * 3 - 1 * textsize;
  *h = charsy * textsize * 6;
}

void CNFGEmitBackendTriangles(const float *fv, const uint32_t *col,
                              int nr_verts);

static void EmitQuad(float cx0, float cy0, float cx1, float cy1, float cx2,
                     float cy2, float cx3, float cy3) {
  if (CNFGVertPlace >= CNFG_BATCH - 6)
    CNFGFlushRender();
  float *fv = &CNFGVertDataV[CNFGVertPlace * 3];
  fv[0] = cx0;
  fv[1] = cy0;
  fv[3] = cx1;
  fv[4] = cy1;
  fv[6] = cx2;
  fv[7] = cy2;
  fv[9] = cx2;
  fv[10] = cy2;
  fv[12] = cx1;
  fv[13] = cy1;
  fv[15] = cx3;
  fv[16] = cy3;
  uint32_t *col = &CNFGVertDataC[CNFGVertPlace];
  uint32_t color = CNFGLastColor;
  col[0] = color;
  col[1] = color;
  col[2] = color;
  col[3] = color;
  col[4] = color;
  col[5] = color;
  CNFGVertPlace += 6;
}

void CNFGTackPixel(short x1, short y1) {
  x1++;
  y1++;
  const short l2 = wgl_last_width_over_2;
  const short l2u = wgl_last_width_over_2 + 0.5;
  EmitQuad(x1 - l2u, y1 - l2u, x1 + l2, y1 - l2u, x1 - l2u, y1 + l2, x1 + l2,
           y1 + l2);
}

void CNFGTackSegment(short x1, short y1, short x2, short y2) {
  float ix1 = x1;
  float iy1 = y1;
  float ix2 = x2;
  float iy2 = y2;

  float dx = ix2 - ix1;
  float dy = iy2 - iy1;
  float imag = 1. / sqrtf(dx * dx + dy * dy);
  dx *= imag;
  dy *= imag;
  float orthox = dy * wgl_last_width_over_2;
  float orthoy = -dx * wgl_last_width_over_2;

  ix2 += dx / 2 + 0.5;
  iy2 += dy / 2 + 0.5;
  ix1 -= dx / 2 - 0.5;
  iy1 -= dy / 2 - 0.5;

  // This logic is incorrect. XXX FIXME.
  EmitQuad((ix1 - orthox), (iy1 - orthoy), (ix1 + orthox), (iy1 + orthoy),
           (ix2 - orthox), (iy2 - orthoy), (ix2 + orthox), (iy2 + orthoy));
}

void CNFGTackRectangle(short x1, short y1, short x2, short y2) {
  printf("Tacking Rectangle %d %d %d %d\n", x1, y1, x2, y2);
  EmitQuad(x1, y1, x2, y1, x1, y2, x2, y2);
}

void CNFGTackPoly(RDPoint *points, int verts) {
  int tris = verts - 2;
  if (CNFGVertPlace >= CNFG_BATCH - tris * 3)
    CNFGFlushRender();

  uint32_t color = CNFGLastColor;
  short *ptrsrc = (short *)points;

  for (int i = 0; i < tris; i++) {
    float *fv = &CNFGVertDataV[CNFGVertPlace * 3];
    fv[0] = ptrsrc[0];
    fv[1] = ptrsrc[1];
    fv[3] = ptrsrc[i * 2 + 2];
    fv[4] = ptrsrc[i * 2 + 3];
    fv[6] = ptrsrc[i * 2 + 4];
    fv[7] = ptrsrc[i * 2 + 5];

    uint32_t *col = &CNFGVertDataC[CNFGVertPlace];
    col[0] = color;
    col[1] = color;
    col[2] = color;

    CNFGVertPlace += 3;
  }
}

uint32_t CNFGColor(uint32_t RGB) { return CNFGLastColor = RGB; }

void CNFGSetLineWidth(short width) {
  wgl_last_width_over_2 = width / 2.0; // + 0.5;
}

uint32_t CNFGGLInternalLoadShader(const char *vertex_shader,
                                  const char *fragment_shader) {
  uint32_t fragment_shader_object = 0;
  uint32_t vertex_shader_object = 0;
  uint32_t program = 0;
  int ret;

  vertex_shader_object = glCreateShader(GL_VERTEX_SHADER);
  if (!vertex_shader_object) {
    fprintf(stderr,
            "Error: glCreateShader(GL_VERTEX_SHADER) "
            "failed: 0x%08X\n",
            glGetError());
    goto fail;
  }

  glShaderSource(vertex_shader_object, 1, &vertex_shader, NULL);
  glCompileShader(vertex_shader_object);

  glGetShaderiv(vertex_shader_object, GL_COMPILE_STATUS, &ret);
  if (!ret) {
    fprintf(stderr, "Error: vertex shader compilation failed!\n");
    glGetShaderiv(vertex_shader_object, GL_INFO_LOG_LENGTH, &ret);

    if (ret > 1) {
      char *log = alloca(ret);
      glGetShaderInfoLog(vertex_shader_object, ret, NULL, log);
      fprintf(stderr, "%s", log);
    }
    goto fail;
  }

  fragment_shader_object = glCreateShader(GL_FRAGMENT_SHADER);
  if (!fragment_shader_object) {
    fprintf(stderr,
            "Error: glCreateShader(GL_FRAGMENT_SHADER) "
            "failed: 0x%08X\n",
            glGetError());
    goto fail;
  }

  glShaderSource(fragment_shader_object, 1, &fragment_shader, NULL);
  glCompileShader(fragment_shader_object);

  glGetShaderiv(fragment_shader_object, GL_COMPILE_STATUS, &ret);
  if (!ret) {
    fprintf(stderr, "Error: fragment shader compilation failed!\n");
    glGetShaderiv(fragment_shader_object, GL_INFO_LOG_LENGTH, &ret);

    if (ret > 1) {
      char *log = malloc(ret);
      glGetShaderInfoLog(fragment_shader_object, ret, NULL, log);
      fprintf(stderr, "%s", log);
    }
    goto fail;
  }

  program = glCreateProgram();
  if (!program) {
    fprintf(stderr, "Error: failed to create program!\n");
    goto fail;
  }

  glAttachShader(program, vertex_shader_object);
  glAttachShader(program, fragment_shader_object);

  glBindAttribLocation(program, 0, "a0");
  glBindAttribLocation(program, 1, "a1");

  glLinkProgram(program);

  glGetProgramiv(program, GL_LINK_STATUS, &ret);
  if (!ret) {
    fprintf(stderr, "Error: program linking failed!\n");
    glGetProgramiv(program, GL_INFO_LOG_LENGTH, &ret);

    if (ret > 1) {
      char *log = alloca(ret);
      glGetProgramInfoLog(program, ret, NULL, log);
      fprintf(stderr, "%s", log);
    }
    goto fail;
  }
  return program;
fail:
  if (!vertex_shader_object)
    glDeleteShader(vertex_shader_object);
  if (!fragment_shader_object)
    glDeleteShader(fragment_shader_object);
  if (!program)
    glDeleteShader(program);
  return -1;
}

void CNFGSetupBatchInternal() {
  CNFGGetDimensions();

  gRDShaderProg =
      CNFGGLInternalLoadShader("uniform vec4 xfrm;"
                               "attribute vec3 a0;"
                               "attribute vec4 a1;"
                               "varying lowp vec4 vc;"
                               "void main() { gl_Position = vec4( "
                               "a0.xy*xfrm.xy+xfrm.zw, a0.z, 0.5 ); vc = a1; }",

                               "varying lowp vec4 vc;"
                               "void main() { gl_FragColor = vec4(vc.abgr); }");

  glUseProgram(gRDShaderProg);
  gRDShaderProgUX = glGetUniformLocation(gRDShaderProg, "xfrm");

  gRDBlitProg = CNFGGLInternalLoadShader(
      "uniform vec4 xfrm;"
      "attribute vec3 a0;"
      "attribute vec4 a1;"
      "varying mediump vec2 tc;"
      "void main() { gl_Position = vec4( a0.xy*xfrm.xy+xfrm.zw, a0.z, 0.5 ); "
      "tc = a1.xy; }",

      "varying mediump vec2 tc;"
      "uniform sampler2D tex;"
      "void main() { gl_FragColor = texture2D(tex,tc)."

      "wzyx"
      ";}");

  glUseProgram(gRDBlitProg);
  gRDBlitProgUX = glGetUniformLocation(gRDBlitProg, "xfrm");
  gRDBlitProgUT = glGetUniformLocation(gRDBlitProg, "tex");
  glGenTextures(1, &gRDBlitProgTex);

  glEnableVertexAttribArray(0);
  glEnableVertexAttribArray(1);

  glDisable(GL_DEPTH_TEST);
  glDepthMask(false);
  glEnable(GL_BLEND);
  glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

  CNFGVertPlace = 0;
}

void CNFGInternalResize(short x, short y)
{
  glViewport(0, 0, x, y);
  gRDLastResizeW = x;
  gRDLastResizeH = y;
  if (gRDShaderProg == 0xFFFFFFFF) {
    return;
  } // Prevent trying to set uniform if the shader isn't ready yet.
  glUseProgram(gRDShaderProg);
  glUniform4f(gRDShaderProgUX, 1.f / x, -1.f / y, -0.5f, 0.5f);
}

void CNFGEmitBackendTriangles(const float *vertices, const uint32_t *colors,
                              int num_vertices) {
  glUseProgram(gRDShaderProg);
  glUniform4f(gRDShaderProgUX, 1.f / gRDLastResizeW, -1.f / gRDLastResizeH,
                  -0.5f, 0.5f);
  glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, vertices);
  glVertexAttribPointer(1, 4, GL_UNSIGNED_BYTE, true, 0, colors);
  glDrawArrays(GL_TRIANGLES, 0, num_vertices);
}

void CNFGBlitImage(uint32_t *data, int x, int y, int w, int h)
{
  if (w <= 0 || h <= 0)
    return;

  CNFGFlushRender();

  glUseProgram(gRDBlitProg);
  glUniform4f(gRDBlitProgUX, 1.f / gRDLastResizeW, -1.f / gRDLastResizeH,
                  -0.5f + x / (float)gRDLastResizeW,
                  0.5f - y / (float)gRDLastResizeH);
  glUniform1i(gRDBlitProgUT, 0);

  glEnable(GL_TEXTURE_2D);
  glActiveTexture(0);
  glBindTexture(GL_TEXTURE_2D, gRDBlitProgTex);

  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

  glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE,
               data);

  const float verts[] = {
      0, 0, w, 0, w, h, 0, 0, w, h, 0, h,
  };
  static const uint8_t colors[] = {0, 0, 255, 0,   255, 255,
                                   0, 0, 255, 255, 0,   255};

  glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, verts);
  glVertexAttribPointer(1, 2, GL_UNSIGNED_BYTE, true, 0, colors);
  glDrawArrays(GL_TRIANGLES, 0, 6);
}

void CNFGUpdateScreenWithBitmap(uint32_t *data, int w, int h) {
  CNFGBlitImage(data, 0, 0, w, h);
}


void CNFGFlushRender() {
  if (!CNFGVertPlace)
    return;
  CNFGEmitBackendTriangles(CNFGVertDataV, CNFGVertDataC, CNFGVertPlace);
  CNFGVertPlace = 0;
}

void CNFGClearFrame() {
  glClearColor(((CNFGBGColor & 0xff000000) >> 24) / 255.0,
               ((CNFGBGColor & 0xff0000) >> 16) / 255.0,
               (CNFGBGColor & 0xff00) / 65280.0, (CNFGBGColor & 0xff) / 255.0);
  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}
