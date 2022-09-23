#include <math.h>
#include <stdio.h>
#include <string.h>
//#include "CNFG.h"


#define tdQ_PI 3.141592653589
#define tdDEGRAD (tdQ_PI/180.)
#define tdRADDEG (180./tdQ_PI)
#define tdMATRIXMAXDEPTH 32
#define tdMATCOPY(x,y) memcpy( x, y, 16*sizeof(float))


#define m00 0
#define m01 1
#define m02 2
#define m03 3
#define m10 4
#define m11 5
#define m12 6
#define m13 7
#define m20 8
#define m21 9
#define m22 10
#define m23 11
#define m30 12
#define m31 13
#define m32 14
#define m33 15

static void tdMultiply( float * fin1, float * fin2, float * fout )
{
	float fotmp[16];
	int i, k;
	for( i = 0; i < 16; i++ )
	{
		int xp = i & 0x03;
		int yp = i & 0x0c;
		fotmp[i] = 0;
		for( k = 0; k < 4; k++ )
		{
			fotmp[i] += fin1[yp+k] * fin2[(k<<2)|xp];
		}
	}
	memcpy(fout, fotmp, 16*sizeof(float));
}

void tdCross( float * va, float * vb, float * vout )
{
	float vtmp[2];
	vtmp[0] = va[1] * vb[2] - va[2] * vb[1];
	vtmp[1] = va[2] * vb[0] - va[0] * vb[2];
	vout[2] = va[0] * vb[1] - va[1] * vb[0];
	vout[0] = vtmp[0];
	vout[1] = vtmp[1];
}

void tdIdentity( float * f )
{
	f[m00] = 1; f[m01] = 0; f[m02] = 0; f[m03] = 0;
	f[m10] = 0; f[m11] = 1; f[m12] = 0; f[m13] = 0;
	f[m20] = 0; f[m21] = 0; f[m22] = 1; f[m23] = 0;
	f[m30] = 0; f[m31] = 0; f[m32] = 0; f[m33] = 1;
}

void tdZero( float * f )
{
	f[m00] = 0; f[m01] = 0; f[m02] = 0; f[m03] = 0;
	f[m10] = 0; f[m11] = 0; f[m12] = 0; f[m13] = 0;
	f[m20] = 0; f[m21] = 0; f[m22] = 0; f[m23] = 0;
	f[m30] = 0; f[m31] = 0; f[m32] = 0; f[m33] = 0;
}

void tdTranslate( float * f, float x, float y, float z )
{
	float ftmp[16];
	tdIdentity(ftmp);
	ftmp[m03] += x;
	ftmp[m13] += y;
	ftmp[m23] += z;
	tdMultiply( f, ftmp, f );
}

void tdScale( float * f, float x, float y, float z )
{
	float ftmp[16];
	tdIdentity(ftmp);
	ftmp[m00] *= x;
	ftmp[m11] *= y;
	ftmp[m22] *= z;

	tdMultiply( f, ftmp, f );

}

void tdTransposeSelf( float * f )
{
	float fout[16];
	fout[m00] = f[m00]; fout[m01] = f[m10]; fout[m02] = f[m20]; fout[m03] = f[m30];
	fout[m10] = f[m01]; fout[m11] = f[m11]; fout[m12] = f[m21]; fout[m13] = f[m31];
	fout[m20] = f[m02]; fout[m21] = f[m12]; fout[m22] = f[m22]; fout[m23] = f[m32];
	fout[m30] = f[m03]; fout[m31] = f[m13]; fout[m32] = f[m23]; fout[m33] = f[m33];
	tdMATCOPY( f, fout );
}


void tdPerspective( float fovy, float aspect, float zNear, float zFar, float * out )
{
	float f = 1./tanf(fovy * tdQ_PI / 360.0);
	out[m00] = f/aspect; out[m01] = 0; out[m02] = 0; out[m03] = 0;
	out[m10] = 0; out[m11] = f; out[m12] = 0; out[m13] = 0;
	out[m20] = 0; out[m21] = 0;
	out[m22] = (zFar + zNear)/(zNear - zFar);
	out[m23] = 2*zFar*zNear  /(zNear - zFar);
	out[m30] = 0; out[m31] = 0; out[m32] = -1; out[m33] = 0;
}

void tdLookAt( float * m, float * eye, float * at, float * up )
{
	float out[16];
	float F[3] = { at[0] - eye[0], at[1] - eye[1], at[2] - eye[2] };
	float fdiv = 1./sqrtf( F[0]*F[0] + F[1]*F[1] + F[2]*F[2] );
	float f[3] = { F[0]*fdiv, F[1]*fdiv, F[2]*fdiv };
	float udiv = 1./sqrtf( up[0]*up[0] + up[1]*up[1] + up[2]*up[2] );
	float UP[3] = { up[0]*udiv, up[1]*udiv, up[2]*udiv };
	float s[3];
	float u[3];
	tdCross( f, UP, s );
	tdCross( s, f, u );

	out[m00] = s[0]; out[m01] = s[1]; out[m02] = s[2]; out[m03] = 0;
	out[m10] = u[0]; out[m11] = u[1]; out[m12] = u[2]; out[m13] = 0;
	out[m20] = -f[0];out[m21] =-f[1]; out[m22] =-f[2]; out[m23] = 0;
	out[m30] = 0;    out[m31] = 0;    out[m32] = 0;    out[m33] = 1;

	tdMultiply( m, out, m );
	tdTranslate( m, -eye[0], -eye[1], -eye[2] );
}

static void td4Transform( float * pin, float * f, float * pout )
{
	float ptmp[3];
	ptmp[0] = pin[0] * f[m00] + pin[1] * f[m01] + pin[2] * f[m02] + pin[3] * f[m03];
	ptmp[1] = pin[0] * f[m10] + pin[1] * f[m11] + pin[2] * f[m12] + pin[3] * f[m13];
	ptmp[2] = pin[0] * f[m20] + pin[1] * f[m21] + pin[2] * f[m22] + pin[3] * f[m23];
	pout[3] = pin[0] * f[m30] + pin[1] * f[m31] + pin[2] * f[m32] + pin[3] * f[m33];
	pout[0] = ptmp[0];
	pout[1] = ptmp[1];
	pout[2] = ptmp[2];
}

void tdNormalizeSelf( float * vin )
{
	float vsq = 1./sqrtf(vin[0]*vin[0] + vin[1]*vin[1] + vin[2]*vin[2]);
	vin[0] *= vsq;
	vin[1] *= vsq;
	vin[2] *= vsq;
}

float tdDot( float * va, float * vb )
{
	return va[0]*vb[0] + va[1]*vb[1] + va[2]*vb[2];
}

//Stack functionality.
static float gsMatricies[2][tdMATRIXMAXDEPTH][16];
float * gSMatrix = gsMatricies[0][0];
static int gsMMode;
static int gsMPlace[2];


void tdMode( int mode )
{
	if( mode < 0 || mode > 1 )
		return;
	
	gsMMode = mode;

	gSMatrix = gsMatricies[gsMMode][gsMPlace[gsMMode]];

}

static float translateX;
static float translateY;
static float scaleX;
static float scaleY;

void tdSetViewport( float leftx, float topy, float rightx, float bottomy, float pixx, float pixy )
{
	translateX = leftx;
	translateY = bottomy;
	scaleX = pixx/(rightx-leftx);
	scaleY = pixy/(topy-bottomy);

}

void tdFinalPoint( float * pin, float * pout )
{
	float tdin[4] = { pin[0], pin[1], pin[2], 1. };
	float tmp[4];
	td4Transform( tdin, gsMatricies[0][gsMPlace[0]], tmp );
	td4Transform(  tmp, gsMatricies[1][gsMPlace[1]], tmp );
	pout[0] = (tmp[0]/tmp[3] - translateX) * scaleX;
	pout[1] = (tmp[1]/tmp[3] - translateY) * scaleY;
	pout[2] = tmp[2]/tmp[3];
}

static float tdNoiseAt( int x, int y )
{
	return ((x*13241*y + y * 33455927)%9293) / 4646. - 1.0;
}

static inline float tdFade( float f )
{
	float ft3 = f*f*f;
	return ft3 * 10 - ft3 * f * 15 + 6 * ft3 * f * f;
}

static float tdFLerp( float a, float b, float t )
{
	float fr = tdFade( t );
	return a * (1.-fr) + b * fr;
}

static inline float tdFNoiseAt( float x, float y )
{
	int ix = x;
	int iy = y;
	float fx = x - ix;
	float fy = y - iy;

	float a = tdNoiseAt( ix, iy );
	float b = tdNoiseAt( ix+1, iy );
	float c = tdNoiseAt( ix, iy+1 );
	float d = tdNoiseAt( ix+1, iy+1 );

	float top = tdFLerp( a, b, fx );
	float bottom = tdFLerp( c, d, fx );

	return tdFLerp( top, bottom, fy );
}

float tdPerlin2D( float x, float y )
{
	int ndepth = 5;

	int depth;
	float ret = 0;
	for( depth = 0; depth < ndepth; depth++ )
	{
		float nx = x / (1<<(ndepth-depth-1));
		float ny = y / (1<<(ndepth-depth-1));
		ret += tdFNoiseAt( nx, ny ) / (1<<(depth+1));
	}
	return ret;
}

