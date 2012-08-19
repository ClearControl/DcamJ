// console/misc/common.cpp
//

#include	"console.h"
#include	"common.h"

#include	<stdarg.h>
#include	<iostream>
#include	<stdio.h>
using namespace std;

#ifndef ASSERT
#define	ASSERT(c)
#endif

// ----------------------------------------------------------------

void dcamcon_show_dcamerr( DCAMERR err, const char* apiname, const char* fmt, ...  )
{
	printf( "DCAMERR=0x%08lX @ %s\n", (long)err, apiname );	
	if( fmt != NULL )
	{
		va_list	arg;
		va_start(arg,fmt);
		vprintf( fmt, arg );
		va_end(arg);
	}
}

// ----------------------------------------------------------------

inline const int my_dcamdev_string( HDCAM hdcam, long idStr, char* text, long textbytes )
{
	DCAMDEV_STRING	param;
	memset( &param, 0, sizeof(param) );
	param.size		= sizeof(param);
	param.text		= text;
	param.textbytes	= textbytes;
	param.iString	= idStr;
	
	DCAMERR	err;
	err = dcamdev_getstring( hdcam, &param );
	if( failed( err ) )
	{
		cout << "DCAMERR: " << err << "\n";
		return false;
	}

	return true;
}

void dcamcon_cout_dcamdev_info( HDCAM hdcam )
{
	char	model[ 256 ];
	char	cameraid[ 64 ];
	char	bus[ 64 ];

	if( my_dcamdev_string( hdcam, DCAM_IDSTR_MODEL,    model,    sizeof(model)   )
	 && my_dcamdev_string( hdcam, DCAM_IDSTR_CAMERAID, cameraid, sizeof(cameraid))
	 && my_dcamdev_string( hdcam, DCAM_IDSTR_BUS,      bus,      sizeof(bus)     ) )
	{
		cout << model << "(" << cameraid << ")" << " on " << bus << "\n";
	}
}

// show HDCAM camera information by text.
void dcamcon_cout_dcamdev_info_detail( HDCAM hdcam )
{
	char	buf[ 256 ];
	
	my_dcamdev_string( hdcam, DCAM_IDSTR_VENDOR,			buf, sizeof( buf ) );
	printf( "DCAM_IDSTR_VENDOR         = %s\n", buf );

	my_dcamdev_string( hdcam, DCAM_IDSTR_MODEL,				buf, sizeof( buf ) );
	printf( "DCAM_IDSTR_MODEL          = %s\n", buf );

	my_dcamdev_string( hdcam, DCAM_IDSTR_CAMERAID,			buf, sizeof( buf ) );
	printf( "DCAM_IDSTR_CAMERAID       = %s\n", buf );

	my_dcamdev_string( hdcam, DCAM_IDSTR_BUS,				buf, sizeof( buf ) );
	printf( "DCAM_IDSTR_BUS            = %s\n", buf );

	my_dcamdev_string( hdcam, DCAM_IDSTR_CAMERAVERSION,		buf, sizeof( buf ) );
	printf( "DCAM_IDSTR_CAMERAVERSION  = %s\n", buf );

	my_dcamdev_string( hdcam, DCAM_IDSTR_DRIVERVERSION,		buf, sizeof( buf ) );
	printf( "DCAM_IDSTR_DRIVERVERSION  = %s\n", buf );

	my_dcamdev_string( hdcam, DCAM_IDSTR_MODULEVERSION,		buf, sizeof( buf ) );
	printf( "DCAM_IDSTR_MODULEVERSION  = %s\n", buf );

	my_dcamdev_string( hdcam, DCAM_IDSTR_DCAMAPIVERSION,	buf, sizeof( buf ) );
	printf( "DCAM_IDSTR_DCAMAPIVERSION = %s\n", buf );
}

// ----------------------------------------------------------------
// initialize DCAM-API and get HDCAM camera handle.

HDCAM dcamcon_init_open()
{
	// Initialize DCAM-API ver 4.0
	DCAMAPI_INIT	paraminit;
	memset( &paraminit, 0, sizeof(paraminit) );
	paraminit.size	= sizeof(paraminit);
	
	DCAMERR	err;
	err = dcamapi_init( &paraminit );
	if( failed( err ) )
	{
		// failure
		dcamcon_show_dcamerr( err, "dcamapi_init()" );
		return NULL;
	}
	
	ASSERT( paraminit.iDeviceCount > 0 );	// nDevice must be larger than 0
	
	long	nDevice = paraminit.iDeviceCount;
	long	iDevice;
	
	// show all camera information by text
	for( iDevice = 0; iDevice < nDevice; iDevice++ )
	{
		//dcamcon_cout_dcamdev_info( (HDCAM)iDevice );
	}
	
	if( nDevice > 1 )
	{
		// choose one camera from the list if there are two or more cameras.
				cout << "choose one of camera from above list by index (0-" << nDevice-1 << ") >";

		iDevice = -1;
		for( ;; )
		{
			char	buf[256];
			memset( buf, 0, sizeof(buf) );

			cin >> buf;
			if( strcmpi( buf, "exit" ) == 0 )
			{
				iDevice = -1;
				break;
			}

			iDevice = atoi( buf );
			if( 0 <= iDevice && iDevice < nDevice )
				break;
		}
	}
	else
	{
		iDevice = 0;
	}
	
	if( 0 <= iDevice && iDevice < nDevice )
	{
		// open specified camera
		DCAMDEV_OPEN	paramopen;
		memset( &paramopen, 0, sizeof(paramopen) );
		paramopen.size	= sizeof(paramopen);
		paramopen.index	= iDevice;
		err = dcamdev_open( &paramopen );
		if( ! failed(err) )
		{
			HDCAM	hdcam = paramopen.hdcam;
			
			dcamcon_cout_dcamdev_info_detail( hdcam );

			// success
			return hdcam;
		}
		
		dcamcon_show_dcamerr( err, "dcamdev_open()", "index is %d\n", iDevice );
	}
	
	// uninitialize DCAM-API
	dcamapi_uninit();
	
	// failure
	return NULL;
}

