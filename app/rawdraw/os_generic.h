#pragma once

typedef void* og_thread_t;
typedef void* og_mutex_t;
typedef void* og_sema_t;
typedef void* og_tls_t;

static inline void OGSleep( int is );
static inline void OGUSleep( int ius );
static inline double OGGetAbsoluteTime();
static inline double OGGetFileTime( const char * file );
static inline og_thread_t OGCreateThread( void * (routine)( void * ), void * parameter );
static inline void * OGJoinThread( og_thread_t ot );
static inline void OGCancelThread( og_thread_t ot );
static inline og_mutex_t OGCreateMutex();
static inline void OGLockMutex( og_mutex_t om );
static inline void OGUnlockMutex( og_mutex_t om );
static inline void OGDeleteMutex( og_mutex_t om );
static inline og_sema_t OGCreateSema();
static inline int OGGetSema( og_sema_t os );
static inline void OGLockSema( og_sema_t os );
static inline void OGUnlockSema( og_sema_t os );
static inline void OGDeleteSema( og_sema_t os );
static inline og_tls_t OGCreateTLS();
static inline void OGDeleteTLS( og_tls_t key );
static inline void * OGGetTLS( og_tls_t key );
static inline void OGSetTLS( og_tls_t key, void * data );


#ifndef OSG_NO_IMPLEMENTATION





#ifdef USE_WINDOWS

#include <windows.h>
#include <stdint.h>

static inline void OGSleep( int is )
{
	Sleep( is*1000 );
}

static inline void OGUSleep( int ius )
{
	Sleep( ius/1000 );
}

static inline double OGGetAbsoluteTime()
{
	static LARGE_INTEGER lpf;
	LARGE_INTEGER li;

	if( !lpf.QuadPart )
	{
		QueryPerformanceFrequency( &lpf );
	}

	QueryPerformanceCounter( &li );
	return (double)li.QuadPart / (double)lpf.QuadPart;
}


static inline double OGGetFileTime( const char * file )
{
	FILETIME ft;

	HANDLE h = CreateFile(file, GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, 0, NULL);

	if( h==INVALID_HANDLE_VALUE )
		return -1;

	GetFileTime( h, 0, 0, &ft );

	CloseHandle( h );

	return ft.dwHighDateTime + ft.dwLowDateTime;
}


static inline og_thread_t OGCreateThread( void * (routine)( void * ), void * parameter )
{
	return (og_thread_t)CreateThread( 0, 0, (LPTHREAD_START_ROUTINE)routine, parameter, 0, 0 );
}

static inline void * OGJoinThread( og_thread_t ot )
{
	WaitForSingleObject( ot, INFINITE );
	CloseHandle( ot );
	return 0;
}

static inline void OGCancelThread( og_thread_t ot )
{
	TerminateThread( ot, 0);
	CloseHandle( ot );	
}

static inline og_mutex_t OGCreateMutex()
{
	return CreateMutex( 0, 0, 0 );
}

static inline void OGLockMutex( og_mutex_t om )
{
	WaitForSingleObject(om, INFINITE);
}

static inline void OGUnlockMutex( og_mutex_t om )
{
	ReleaseMutex(om);
}

static inline void OGDeleteMutex( og_mutex_t om )
{
	CloseHandle( om );
}



static inline og_sema_t OGCreateSema()
{
	HANDLE sem = CreateSemaphore( 0, 0, 32767, 0 );
	return (og_sema_t)sem;
}

static inline int OGGetSema( og_sema_t os )
{
	typedef LONG NTSTATUS;
	HANDLE sem = (HANDLE)os;
	typedef NTSTATUS (NTAPI *_NtQuerySemaphore)(
		HANDLE SemaphoreHandle, 
		DWORD SemaphoreInformationClass, /* Would be SEMAPHORE_INFORMATION_CLASS */
		PVOID SemaphoreInformation,      /* but this is to much to dump here     */
		ULONG SemaphoreInformationLength, 
		PULONG ReturnLength OPTIONAL
	);

	typedef struct _SEMAPHORE_BASIC_INFORMATION {   
		ULONG CurrentCount; 
		ULONG MaximumCount;
	} SEMAPHORE_BASIC_INFORMATION;


	static _NtQuerySemaphore NtQuerySemaphore;
	SEMAPHORE_BASIC_INFORMATION BasicInfo;
	NTSTATUS Status;

	if( !NtQuerySemaphore )
	{	
	    NtQuerySemaphore = (_NtQuerySemaphore)GetProcAddress (GetModuleHandle ("ntdll.dll"), "NtQuerySemaphore");
		if( !NtQuerySemaphore )
		{
			return -1;
		}
	}

	
    Status = NtQuerySemaphore (sem, 0 /*SemaphoreBasicInformation*/, 
        &BasicInfo, sizeof (SEMAPHORE_BASIC_INFORMATION), NULL);

    if (Status == ERROR_SUCCESS)
    {       
        return BasicInfo.CurrentCount;
    }

	return -2;
}

static inline void OGLockSema( og_sema_t os )
{
	WaitForSingleObject( (HANDLE)os, INFINITE );
}

static inline void OGUnlockSema( og_sema_t os )
{
	ReleaseSemaphore( (HANDLE)os, 1, 0 );
}

static inline void OGDeleteSema( og_sema_t os )
{
	CloseHandle( os );
}

static inline og_tls_t OGCreateTLS()
{
	return (og_tls_t)(intptr_t)TlsAlloc();
}

static inline void OGDeleteTLS( og_tls_t key )
{
	TlsFree( (DWORD)(intptr_t)key );
}

static inline void * OGGetTLS( og_tls_t key )
{
	return TlsGetValue( (DWORD)(intptr_t)key );
}

static inline void OGSetTLS( og_tls_t key, void * data )
{
	TlsSetValue( (DWORD)(intptr_t)key, data );
}

#elif defined( __wasm__ )

//We don't actually have any function defintions here.
//The outside system will handle it.

#else

#ifndef _GNU_SOURCE
#define _GNU_SOURCE
#endif

#include <sys/stat.h>
#include <stdlib.h>
#include <pthread.h>
#include <sys/time.h>
#include <semaphore.h>
#include <unistd.h>

static inline void OGSleep( int is )
{
	sleep( is );
}

static inline void OGUSleep( int ius )
{
	usleep( ius );
}

static inline double OGGetAbsoluteTime()
{
	struct timeval tv;
	gettimeofday( &tv, 0 );
	return ((double)tv.tv_usec)/1000000. + (tv.tv_sec);
}

static inline double OGGetFileTime( const char * file )
{
	struct stat buff; 

	int r = stat( file, &buff );

	if( r < 0 )
	{
		return -1;
	}

	return buff.st_mtime;
}



static inline og_thread_t OGCreateThread( void * (routine)( void * ), void * parameter )
{
	pthread_t * ret = (pthread_t *)malloc( sizeof( pthread_t ) );
	if( !ret ) return 0;
	int r = pthread_create( ret, 0, routine, parameter );
	if( r )
	{
		free( ret );
		return 0;
	}
	return (og_thread_t)ret;
}

static inline void * OGJoinThread( og_thread_t ot )
{
	void * retval;
	if( !ot )
	{
		return 0;
	}
	pthread_join( *(pthread_t*)ot, &retval );
	free( ot );
	return retval;
}

static inline void OGCancelThread( og_thread_t ot )
{
	if( !ot )
	{
		return;
	}
	pthread_kill( *(pthread_t*)ot, SIGTERM );
	free( ot );
}

static inline og_mutex_t OGCreateMutex()
{
	pthread_mutexattr_t   mta;
	og_mutex_t r = malloc( sizeof( pthread_mutex_t ) );
	if( !r ) return 0;

	pthread_mutexattr_init(&mta);
	pthread_mutexattr_settype(&mta, PTHREAD_MUTEX_RECURSIVE);

	pthread_mutex_init( (pthread_mutex_t *)r, &mta );

	return r;
}

static inline void OGLockMutex( og_mutex_t om )
{
	if( !om )
	{
		return;
	}
	pthread_mutex_lock( (pthread_mutex_t*)om );
}

static inline void OGUnlockMutex( og_mutex_t om )
{
	if( !om )
	{
		return;
	}
	pthread_mutex_unlock( (pthread_mutex_t*)om );
}

static inline void OGDeleteMutex( og_mutex_t om )
{
	if( !om )
	{
		return;
	}

	pthread_mutex_destroy( (pthread_mutex_t*)om );
	free( om );
}




static inline og_sema_t OGCreateSema()
{
	sem_t * sem = (sem_t *)malloc( sizeof( sem_t ) );
	if( !sem ) return 0;
	sem_init( sem, 0, 0 );
	return (og_sema_t)sem;
}

static inline int OGGetSema( og_sema_t os )
{
	int valp;
	sem_getvalue( (sem_t*)os, &valp );
	return valp;
}


static inline void OGLockSema( og_sema_t os )
{
	sem_wait( (sem_t*)os );
}

static inline void OGUnlockSema( og_sema_t os )
{
	sem_post( (sem_t*)os );
}

static inline void OGDeleteSema( og_sema_t os )
{
	sem_destroy( (sem_t*)os );
	free(os);
}

static inline og_tls_t OGCreateTLS()
{
	pthread_key_t ret = 0;
	pthread_key_create(&ret, 0);
	return (og_tls_t)(intptr_t)ret;
}

static inline void OGDeleteTLS( og_tls_t key )
{
	pthread_key_delete( (pthread_key_t)(intptr_t)key );
}

static inline void * OGGetTLS( og_tls_t key )
{
	return pthread_getspecific( (pthread_key_t)(intptr_t)key );
}

static inline void OGSetTLS( og_tls_t key, void * data )
{
	pthread_setspecific( (pthread_key_t)(intptr_t)key, data );
}

#endif


#endif //OSG_NO_IMPLEMENTATION

