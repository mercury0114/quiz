#pragma once

void tdCross( float * va, float * vb, float * vout );
void tdIdentity( float * f );
void tdZero( float * f );
void tdTranslate( float * f, float x, float y, float z );
void tdScale( float * f, float x, float y, float z );
void tdTransposeSelf( float * f );
void tdPerspective( float fovy, float aspect, float zNear, float zFar, float * out );
void tdLookAt( float * m, float * eye, float * at, float * up );
void td4Transform( float * pin, float * f, float * pout );
void tdNormalizeSelf( float * vin );
float tdDot( float * va, float * vb );
float tdPerlin2D( float x, float y );
