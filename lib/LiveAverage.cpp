// console/c44_live_average.cpp
//

#include	"dcam/console.h"
#include	"dcam/common.h"

#include	<iostream>
using namespace std;

// calculate average center point
double calc_average( const void* buf, long rowbytes, DCAM_PIXELTYPE type, long width, long height )
{
	if( type != DCAM_PIXELTYPE_MONO16  )
	{
		// this only supports B/W 16
		return -1;
	}

	long	cy = width  / 10;
	long	cx = height / 10;
	if( cx < 10 )	cx = 10;
	if( cy < 10 )	cy = 10;
	if( cx > width || cy > height )
	{
		// frame is too small
		return -1;
	}

	long	ox = (width-cx) / 2;
	long	oy = (height-cy) / 2;

	const char*	src = (const char*)buf + rowbytes * oy;
	double	total = 0;
	
	long	x, y;
	for( y = 0; y < cy; y++ )
	{
		const unsigned short*	s = (const unsigned short*)src + ox;
		for( x = 0; x < cx; x++ )
		{
			total += *s++;
		}
		src += rowbytes;
	}
	
	return total / cx / cy;
}

void wait_and_calc( HDCAM hdcam, HDCAMWAIT hwait )
{
	DCAMERR	err;

	DCAM_FRAME	frame;
	memset( &frame, 0, sizeof(frame) );
	frame.size	= sizeof(frame);
	frame.iFrame= -1;		// latest frame

	DCAMWAIT_START	paramwait;
	memset( &paramwait, 0, sizeof(paramwait) );
	paramwait.size	= sizeof(paramwait);
	paramwait.eventmask = DCAMCAP_EVENT_FRAMEREADY;
	paramwait.timeout	= 1000;


	long	i, number_of_test = 10000;
	for( i = 0; i < number_of_test; i++ )
	{
		err = dcamwait_start( hwait, &paramwait );
		if( failed( err ) )
			dcamcon_show_dcamerr( err, "dcamwait_start" );
		else
		{
			err = dcambuf_lockframe( hdcam, &frame );
			if( failed( err ) )
				dcamcon_show_dcamerr( err, "dcambuf_lockframe" );
			else
			{
				double	v = calc_average( frame.buf, frame.rowbytes, frame.type, frame.width, frame.height );
				cout << v << "\n";
			}
		}
	}
}	

int main( int argc, char * const argv[])
{
	// Initialize DCAM-API ver 4.0

	DCAMERR	err;
	HDCAM	hdcam;
	hdcam = dcamcon_init_open();
	if( hdcam != NULL )
	{
		dcamcon_cout_dcamdev_info( hdcam );

		DCAMWAIT_OPEN	waitopen;
		memset( &waitopen, 0, sizeof(waitopen) );
		waitopen.size = sizeof(waitopen);
		waitopen.hdcam	= hdcam;
		err = dcamwait_open( &waitopen );
		if( failed( err ) )
			dcamcon_show_dcamerr( err, "dcamwait_open" );
		else
		{
			HDCAMWAIT	hwait = waitopen.hwait;
			long	number_of_buffer = 100;
			err = dcambuf_alloc( hdcam, number_of_buffer );
			if( failed( err ) )
				dcamcon_show_dcamerr( err, "dcambuf_alloc" );
			else
			{
				err = dcamcap_start( hdcam, DCAMCAP_START_SEQUENCE );
				if( failed( err ) )
					dcamcon_show_dcamerr( err, "dcamcap_start" );
				else
				{
					wait_and_calc( hdcam, hwait );
					
					dcamcap_stop( hdcam );
				}
				dcambuf_release( hdcam );
			}
			dcamwait_close( hwait );
		}

		dcamdev_close( hdcam );
		dcamapi_uninit();
	}

    return 0;
}
