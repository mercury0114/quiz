#if !defined( _CHEW_H ) || defined( TABLEONLY )
#define _CHEW_H

#ifndef TABLEONLY

#if defined( WINDOWS ) || defined( _WINDOWS ) || defined( WIN32 ) || defined( WIN64 )
#include <stdint.h>
#else
//Include OpenGL or something maybe?
#endif
#endif

#if defined( WIN32 ) || defined( WINDOWS )
#define STDCALL __stdcall
#else
#define STDCALL
#endif

#ifdef __cplusplus
#ifndef TABLEONLY
extern "C" {
#endif
#endif

#ifndef TABLEONLY

#ifdef EGL_LEAN_AND_MEAN
#include <GLES/gl.h>
#include <GLES3/gl3.h>
#else
#include <GL/gl.h>
#endif
#include "chewtypes.h"

#ifndef chew_FUN_EXPORT
#define chew_FUN_EXPORT extern
#endif

#define CHEWTYPEDEF( ret, name, retcmd, parameters, ... ) \
typedef ret (STDCALL *name##_t)( __VA_ARGS__ );	\
chew_FUN_EXPORT name##_t	name##fnptr; \
chew_FUN_EXPORT ret name( __VA_ARGS__ );

#define CHEWTYPEDEF2( ret, name, usename, retcmd, parameters, ... ) \
typedef ret (STDCALL *usename##_t)( __VA_ARGS__ );	\
chew_FUN_EXPORT usename##_t	usename##fnptr; \
chew_FUN_EXPORT ret usename( __VA_ARGS__ );


void chewInit();
void * chewGetProcAddress( const char *name );

#endif

// Add the things you want here; DO NOT put ; at end of line.

CHEWTYPEDEF( void, glGenVertexArrays, , (n,arrays), uint32_t n, uint32_t *arrays ) 
CHEWTYPEDEF( void, glBindVertexArray, , (array), uint32_t array )
CHEWTYPEDEF( void, glGenBuffers, , (n,buffers), uint32_t n, uint32_t * buffers )
CHEWTYPEDEF( void, glBindBuffer, , (target,buffer), GLenum target, uint32_t buffer )
CHEWTYPEDEF( void, glBufferData, , (target,size,data,usage), GLenum target, intptr_t size, const GLvoid * data, GLenum usage )
CHEWTYPEDEF( void, glNamedBufferData, , (buffer,size,data,usage) , uint32_t buffer, intptr_t size, const void *data, GLenum usage )
CHEWTYPEDEF( void, glEnableVertexAttribArray, , (index), uint32_t index )
CHEWTYPEDEF( void, glDisableVertexAttribArray, , (index), uint32_t index )
CHEWTYPEDEF( void, glEnableVertexArrayAttrib, , (vaobj,index), uint32_t vaobj, uint32_t index )
CHEWTYPEDEF( void, glDisableVertexArrayAttrib, , (vaobj,index), uint32_t vaobj, uint32_t index )
CHEWTYPEDEF( void, glVertexAttribPointer, , (index,size,type,normalized,stride,pointer), uint32_t index, int size, GLenum type, GLboolean normalized, uint32_t stride, const GLvoid * pointer )
CHEWTYPEDEF( void, glVertexAttribIPointer, , (index,size,type,stride,pointer), uint32_t index, int size, GLenum type, uint32_t stride, const GLvoid * pointer )
CHEWTYPEDEF( void, glVertexAttribLPointer, , (index,size,type,stride,pointer), uint32_t index, int size, GLenum type, uint32_t stride, const GLvoid * pointer )
CHEWTYPEDEF( void, glBindAttribLocation, , (program,index,name), uint32_t program, uint32_t index, const GLchar *name )

CHEWTYPEDEF( void, glDeleteVertexArrays, , (n,arrays), uint32_t n, const uint32_t *arrays )
CHEWTYPEDEF( void, glDeleteBuffers, , (n,buffers), uint32_t n, const uint32_t * buffers )
CHEWTYPEDEF( void, glBufferSubData, , (target,offset,size,data), GLenum target, intptr_t offset, intptr_t size, const GLvoid * data )
CHEWTYPEDEF( void, glNamedBufferSubData, , (buffer,offset,size,data), uint32_t buffer, intptr_t offset, intptr_t size, const void *data )

//Already covered in SDL_opengl.h
CHEWTYPEDEF2( void, glActiveTexture, glActiveTextureCHEW, , (texture) , GLenum texture )
CHEWTYPEDEF2( void, glSampleCoverage, glSampleCoverageCHEW, , (value,invert), GLfloat value, GLboolean invert )

#ifndef EGL_LEAN_AND_MEAN
CHEWTYPEDEF( void, glDebugMessageCallback, , (callback,userParam), GLDEBUGPROC callback, const void * userParam )
CHEWTYPEDEF( void, glDebugMessageControl, , (source,type,severity,count,ids,enabled), GLenum source, GLenum type, GLenum severity, uint32_t count, const uint32_t *ids, GLboolean enabled )
#endif

CHEWTYPEDEF2( void, glGenerateMipmap, glGenerateMipmapCHEW, , (index), uint32_t index )

CHEWTYPEDEF( void, glGenFramebuffers, , (n,framebuffers), uint32_t n, uint32_t * framebuffers )
CHEWTYPEDEF( void, glGenRenderbuffers, , (n,renderbuffers), uint32_t n, uint32_t * renderbuffers )
CHEWTYPEDEF( void, glBindFramebuffer, , (target,framebuffer), GLenum target, uint32_t framebuffer )
CHEWTYPEDEF( void, glBindRenderbuffer, , (target,renderbuffer), GLenum target, uint32_t renderbuffer )
CHEWTYPEDEF( void, glRenderbufferStorage, , (target,internalformat,width,height), GLenum target, GLenum internalformat, uint32_t width, uint32_t height )
CHEWTYPEDEF( void, glRenderbufferStorageMultisample, , (target,samples,internalformat,width,height), GLenum target, uint32_t samples, GLenum internalformat, uint32_t width, uint32_t height )
CHEWTYPEDEF( void, glNamedRenderbufferStorageMultisample, , (renderbuffer,samples,internalformat,width,height), uint32_t renderbuffer, uint32_t samples, GLenum internalformat, uint32_t width, uint32_t height )
CHEWTYPEDEF( void, glFramebufferRenderbuffer, ,(target,attachment,renderbuffertarget,renderbuffer), GLenum target, GLenum attachment, GLenum renderbuffertarget, uint32_t renderbuffer )
CHEWTYPEDEF( void, glTexImage2DMultisample, ,(target,samples,internalformat, width, height, fixedsamplelocations),GLenum target, uint32_t samples, GLenum internalformat, uint32_t width, uint32_t height, GLboolean fixedsamplelocations )
CHEWTYPEDEF( void, glFramebufferTexture2D, ,(target,attachment, textarget, texture, level) , GLenum target, GLenum attachment, GLenum textarget, uint32_t texture, int level )
CHEWTYPEDEF( void, glBlitFramebuffer, , (srcX0, srcY0, srcX1, srcY1,dstX0, dstY0,dstX1, dstY1,mask, filter) , int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, GLbitfield mask, GLenum filter )
CHEWTYPEDEF( void, glBlitNamedFramebuffer, , (readFramebuffer, drawFramebuffer, srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter), uint32_t readFramebuffer, uint32_t drawFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, GLbitfield mask, GLenum filter )
CHEWTYPEDEF( void, glDeleteFramebuffers, , (n,framebuffers), uint32_t n, const uint32_t * framebuffers )
CHEWTYPEDEF( void, glDeleteRenderbuffers, , (n,renderbuffers), uint32_t n, const uint32_t * renderbuffers )
CHEWTYPEDEF( GLenum, glCheckFramebufferStatus, return, (target) , GLenum target )
CHEWTYPEDEF( GLenum, glCheckNamedFramebufferStatus, return, (framebuffer,target), uint32_t framebuffer, GLenum target )
CHEWTYPEDEF( void, glFramebufferTexture, , (target,attachment,texture,level), GLenum target, GLenum attachment, uint32_t texture, int level )



CHEWTYPEDEF( uint32_t, glCreateProgram, return, () , void )
CHEWTYPEDEF( uint32_t, glCreateShader, return, (e), GLenum e )
#ifndef EGL_LEAN_AND_MEAN
CHEWTYPEDEF( void, glShaderSource, , (shader,count,string,length), uint32_t shader, uint32_t count, const GLchar **string, const int *length )
#endif
CHEWTYPEDEF( void, glCompileShader, ,(shader), uint32_t shader )
CHEWTYPEDEF( void, glGetShaderiv, , (shader,pname,params), uint32_t shader, GLenum pname, int *params )
CHEWTYPEDEF( void, glGetShaderInfoLog , , (shader,maxLength, length, infoLog), uint32_t shader, uint32_t maxLength, uint32_t *length, GLchar *infoLog )
CHEWTYPEDEF( void, glDeleteProgram, , (program), uint32_t program )
CHEWTYPEDEF( void, glDeleteShader, , (shader), uint32_t shader )
CHEWTYPEDEF( void, glAttachShader, , (program,shader), uint32_t program, uint32_t shader )
CHEWTYPEDEF( void, glLinkProgram, , (program), uint32_t program )
CHEWTYPEDEF( void, glGetProgramiv, , (program,pname,params), uint32_t program, GLenum pname, int *params )
CHEWTYPEDEF( void, glUseProgram, , (program), uint32_t program )
CHEWTYPEDEF( void, glUniform1f, , (location,v0), int location, GLfloat v0 )
CHEWTYPEDEF( void, glUniform2f, , (location,v0,v1), int location, GLfloat v0, GLfloat v1 )
CHEWTYPEDEF( void, glUniform3f, , (location,v0,v1,v2), int location, GLfloat v0, GLfloat v1, GLfloat v2 )
CHEWTYPEDEF( void, glUniform4f, , (location,v0,v1,v2,v3), int location, GLfloat v0, GLfloat v1, GLfloat v2, GLfloat v3 )
CHEWTYPEDEF( void, glUniform1i, , (location,v0), int location, int v0 )
CHEWTYPEDEF( void, glUniform2i, , (location,v0,v1), int location, int v0, int v1 )
CHEWTYPEDEF( void, glUniform3i, , (location,v0,v1,v2), int location, int v0, int v1, int v2 )
CHEWTYPEDEF( void, glUniform4i, , (location,v0,v1,v2,v3), int location, int v0, int v1, int v2, int v3 )
CHEWTYPEDEF( void, glUniform1fv, , (location,count,value), int location, uint32_t count, const GLfloat *value )
CHEWTYPEDEF( void, glUniform2fv, , (location,count,value), int location, uint32_t count, const GLfloat *value )
CHEWTYPEDEF( void, glUniform3fv, , (location,count,value), int location, uint32_t count, const GLfloat *value )
CHEWTYPEDEF( void, glUniform4fv, , (location,count,value), int location, uint32_t count, const GLfloat *value )
CHEWTYPEDEF2( void, glUniform4fv, glUniform4fvCHEW, , (location,count,value), int location, uint32_t count, const GLfloat *value )
CHEWTYPEDEF( void, glUniform1iv, , (location,count,value), int location, uint32_t count, const int *value )
CHEWTYPEDEF( void, glUniform2iv, , (location,count,value), int location, uint32_t count, const int *value )
CHEWTYPEDEF( void, glUniform3iv, , (location,count,value), int location, uint32_t count, const int *value )
CHEWTYPEDEF( void, glUniform4iv, , (location,count,value), int location, uint32_t count, const int *value )
CHEWTYPEDEF( void, glUniformMatrix2fv, ,(location,count,transpose,value) , int location, uint32_t count, GLboolean transpose, const GLfloat *value )
CHEWTYPEDEF( void, glUniformMatrix3fv, ,(location,count,transpose,value) , int location, uint32_t count, GLboolean transpose, const GLfloat *value )
CHEWTYPEDEF( void, glUniformMatrix4fv, ,(location,count,transpose,value) , int location, uint32_t count, GLboolean transpose, const GLfloat *value )
CHEWTYPEDEF( void, glGetProgramInfoLog, , (program,maxLength, length, infoLog), uint32_t program, uint32_t maxLength, uint32_t *length, GLchar *infoLog )
CHEWTYPEDEF( int, glGetUniformLocation, return, (program,name), uint32_t program, const GLchar *name )

CHEWTYPEDEF( void *, glMapBuffer, return, (target,access), GLenum target, GLenum access )
CHEWTYPEDEF( void *, glMapNamedBuffer, return, (buffer,access), uint32_t buffer, GLenum access )
CHEWTYPEDEF( void *, glMapBufferRange, return, (buffer,offset,length,access), uint32_t buffer, intptr_t offset, intptr_t length, GLbitfield access )

CHEWTYPEDEF( GLboolean, glUnmapBuffer, return, (target), GLenum target )

#ifdef __cplusplus
#ifndef TABLEONLY
};
#endif
#endif

#endif // _CHEW_H
