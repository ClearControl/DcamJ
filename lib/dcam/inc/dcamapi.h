/* **************************************************************** *

	dcamapi.h:	Feb. 27, 2012

 * **************************************************************** */

#ifndef _INCLUDE_DCAMAPI_H_

#ifndef DCAMAPI_VER
#define	DCAMAPI_VER		4000
#endif

/* **************************************************************** *

	language absorber

 * **************************************************************** */

#ifdef __cplusplus

/* C++ */

#define	DCAM_DECLARE_BEGIN( kind, tag )	kind tag
#define	DCAM_DECLARE_END( tag )		;

#define	DCAM_DEFAULT_ARG				= 0
#define	DCAMINIT_DEFAULT_ARG			= DCAMINIT_DEFAULT

extern "C" {

#else

/* C */

#define	DCAM_DECLARE_BEGIN( kind, tag )	typedef kind
#define	DCAM_DECLARE_END( tag )		tag;

#define	DCAM_DEFAULT_ARG
#define	DCAMINIT_DEFAULT_ARG

#endif

/* **************************************************************** *

	defines

 * **************************************************************** */

/* define - HDCAM */

typedef struct { long data1; long data2;}* HDCAM;

/* define - DCAMAPI */

#ifndef DCAMAPI
#ifdef PASCAL
#define	DCAMAPI	PASCAL	/* DCAM-API based on PASCAL calling */
#else
#define DCAMAPI
#endif
#endif /* DCAMAPI */

/* **************************************************************** *

	constant declaration

 * **************************************************************** */

/*** --- errors --- ***/

DCAM_DECLARE_BEGIN( enum, DCAMERR )
{
  /* status error */
	DCAMERR_BUSY				= 0x80000101,/*		API cannot process in busy state.		*/
	DCAMERR_NOTREADY			= 0x80000103,/*		API requires ready state.				*/
	DCAMERR_NOTSTABLE			= 0x80000104,/*		API requires stable or unstable state.	*/
	DCAMERR_UNSTABLE			= 0x80000105,/*		API does not support in unstable state.	*/
	DCAMERR_NOTBUSY				= 0x80000107,/*		API requires busy state.				*/

	DCAMERR_EXCLUDED			= 0x80000110,/*		some resource is exclusive and already used	*/

	DCAMERR_COOLINGTROUBLE		= 0x80000302,/*		something happens near cooler */
	DCAMERR_NOTRIGGER			= 0x80000303,/*		no trigger when necessary. Some camera supports this error. */

  /* wait error */
	DCAMERR_ABORT				= 0x80000102,/*		abort process			*/
	DCAMERR_TIMEOUT				= 0x80000106,/*		timeout					*/
	DCAMERR_LOSTFRAME			= 0x80000301,/*		frame data is lost		*/
	DCAMERR_MISSINGFRAME_TROUBLE= 0x80000f06,/*frame is lost but reason is low lever driver's bug */

  /* initialization error */
	DCAMERR_NORESOURCE			= 0x80000201,/*		not enough resource except memory	*/
	DCAMERR_NOMEMORY			= 0x80000203,/*		not enough memory		*/
	DCAMERR_NOMODULE			= 0x80000204,/*		no sub module			*/
	DCAMERR_NODRIVER			= 0x80000205,/*		no driver				*/
	DCAMERR_NOCAMERA			= 0x80000206,/*		no camera				*/
	DCAMERR_NOGRABBER			= 0x80000207,/* 2.2:no grabber				*/
	DCAMERR_NOCOMBINATION		= 0x80000208,/* 2.2:no combination on registry */

	DCAMERR_FAILOPEN			= 0x80001001,
	DCAMERR_INVALIDMODULE		= 0x80000211,/* 2.2:dcam_init() found invalid module */
	DCAMERR_INVALIDCOMMPORT		= 0x80000212,/*		invalid serial port		*/
	DCAMERR_FAILOPENBUS			= 0x81001001,/*		the bus or driver are not available	*/
	DCAMERR_FAILOPENCAMERA		= 0x82001001,/*		camera report error during opening	*/

  /* calling error */
	DCAMERR_INVALIDCAMERA		= 0x80000806,/*		invalid camera		 */
	DCAMERR_INVALIDHANDLE		= 0x80000807,/*		invalid camera handle	*/
	DCAMERR_INVALIDPARAM		= 0x80000808,/*		invalid parameter		*/
	DCAMERR_INVALIDVALUE		= 0x80000821,/*		invalid property value	*/
	DCAMERR_OUTOFRANGE			= 0x80000822,/*		value is out of range	*/
	DCAMERR_NOTWRITABLE			= 0x80000823,/* 2.2:the property is not readable	*/
	DCAMERR_NOTREADABLE			= 0x80000824,/* 2.2:the property is not writable	*/
	DCAMERR_INVALIDPROPERTYID	= 0x80000825,/*		the property id is invalid	*/
	DCAMERR_NEWAPIREQUIRED		= 0x80000826,/* 2.2:old API does not support the value because only new API supports the value */
	DCAMERR_WRONGHANDSHAKE		= 0x80000827,/* 2.2:this error happens DCAM get error code from camera unexpectedly */
	DCAMERR_NOPROPERTY			= 0x80000828,/* 2.2:there is no altenative or influence id, or no more property id */
	DCAMERR_INVALIDCHANNEL		= 0x80000829,/* 2.2:the property id specifies channel but channel is invalid */
	DCAMERR_INVALIDVIEW			= 0x8000082a,/* 2.2:the property id specifies channel but channel is invalid */
	DCAMERR_INVALIDSUBARRAY		= 0x8000082b,/* 2.2:the conbination of subarray values are invalid. e.g. DCAM_IDPROP_SUBARRAYHPOS + DCAM_IDPROP_SUBARRAYHSIZE is greater than the number of horizontal pixel of sensor. */
	DCAMERR_ACCESSDENY			= 0x8000082c,/*		the property cannot access during this DCAM STATUS */
	DCAMERR_NOVALUETEXT			= 0x8000082d,/*		the property does not have value text */
	DCAMERR_WRONGPROPERTYVALUE	= 0x8000082e,/*	at least one property value is wrong */
	DCAMERR_DISHARMONY			= 0x80000830,/*	3.0:the paired camera does not have same parameter */
	DCAMERR_FRAMEBUNDLESHOULDBEOFF=0x80000832,/*	framebundle mode should be OFF under current property settings */
	DCAMERR_INVALIDFRAMEINDEX	= 0x80000833,/*		the frame index is invalid  */
	DCAMERR_INVALIDSESSIONINDEX	= 0x80000834,/*		the session index is invalid */
//	DCAMERR_UNKNOWNFILEFORMAT	= 0x80000836,/*		opened file format is not supported */
//	DCAMERR_MISSINGRELATEDFILE	= 0x80000837,/*		File could be opened but necessary file is missing, e.g. DLL, file body, ... */
	DCAMERR_NOTSUPPORT			= 0x80000f03,/*		camera does not support the function or property with current settings */

  /* camera or bus trouble */
	DCAMERR_FAILREADCAMERA		= 0x83001002,
	DCAMERR_FAILWRITECAMERA		= 0x83001003,
	DCAMERR_CONFLICTCOMMPORT	= 0x83001004,
	DCAMERR_OPTICS_UNPLUGGED	= 0x83001005,/* 3.1:Optics part is unplugged so please check it. */
	DCAMERR_FAILCALIBRATION		= 0x83001006,/*	3.1:fail calibration	*/

	DCAMERR_FAILEDOPENRECFILE	= 0x84001001,
	DCAMERR_INVALIDRECHANDLE	= 0x84001002,
	DCAMERR_FAILEDWRITEDATA		= 0x84001003,
	DCAMERR_FAILEDREADDATA		= 0x84001004,
	DCAMERR_NOWRECORDING		= 0x84001005,
	DCAMERR_WRITEFULL			= 0x84001006,
	DCAMERR_ALREADYOCCUPIED		= 0x84001007,
	DCAMERR_TOOLARGEUSERDATASIZE= 0x84001008,
	DCAMERR_INVALIDWAITHANDLE	= 0x84002001,

  /* calling error for DCAM-API 2.1.3 */
	DCAMERR_UNKNOWNMSGID		= 0x80000801,/*		unknown message id		*/
	DCAMERR_UNKNOWNSTRID		= 0x80000802,/*		unknown string id		*/
	DCAMERR_UNKNOWNPARAMID		= 0x80000803,/*		unkown parameter id		*/
	DCAMERR_UNKNOWNBITSTYPE		= 0x80000804,/*		unknown bitmap bits type			*/
	DCAMERR_UNKNOWNDATATYPE		= 0x80000805,/*		unknown frame data type				*/

  /* internal error */
	DCAMERR_NONE				= 0,		/*		no error, nothing to have done		*/
	DCAMERR_INSTALLATIONINPROGRESS=0x80000f00,/*installation progress				*/
	DCAMERR_UNREACH				= 0x80000f01,/*		internal error						*/
	DCAMERR_UNLOADED			= 0x80000f04,/*		calling after process terminated	*/
	DCAMERR_THRUADAPTER			= 0x80000f05,/*											*/
	DCAMERR_NOCONNECTION		= 0x80000f07,/*		HDCAM lost connection to camera		*/

	DCAMERR_NOTIMPLEMENT		= 0x80000f02,/*		not yet implementation				*/

  /* success */
	DCAMERR_SUCCESS				= 1			/*		no error, general success code, app should check the value is positive	*/
}
DCAM_DECLARE_END( DCAMERR )

DCAM_DECLARE_BEGIN( enum, DCAM_USERDATAKIND )
{
	DCAM_USERDATAKIND_FRAME		= 0x00000000,
	DCAM_USERDATAKIND_FILE		= 0x01000000,
	DCAM_USERDATAKIND_SESSION	= 0x02000000
}
DCAM_DECLARE_END( DCAM_USERDATAKIND )

DCAM_DECLARE_BEGIN( enum, DCAM_PIXELTYPE )
{

	DCAM_PIXELTYPE_NONE			= 0x00000000,

	DCAM_PIXELTYPE_MONO8		= 0x00000001,
	DCAM_PIXELTYPE_MONO16		= 0x00000002,

	DCAM_PIXELTYPE_YUV411		= 0x00000015,
	DCAM_PIXELTYPE_YUV422		= 0x00000016,
	DCAM_PIXELTYPE_YUV444		= 0x00000017,

	DCAM_PIXELTYPE_RGB24		= 0x00000021,
	DCAM_PIXELTYPE_RGB48		= 0x00000022,
	DCAM_PIXELTYPE_BGR24		= 0x00000029,
	DCAM_PIXELTYPE_BGR48		= 0x0000002a
}
DCAM_DECLARE_END( DCAM_PIXELTYPE )

DCAM_DECLARE_BEGIN( enum, DCAMBUF_ATTACHKIND )
{
	DCAMBUF_ATTACHKIND_TIMESTAMP	= 1,
	DCAMBUF_ATTACHKIND_FRAME		= 0
}
DCAM_DECLARE_END( DCAM_ATTACHKIND )

/*** --- status --- ***/
DCAM_DECLARE_BEGIN( enum, DCAMCAP_STATUS )
{
	DCAMCAP_STATUS_ERROR				= 0x0000,
	DCAMCAP_STATUS_BUSY					= 0x0001,
	DCAMCAP_STATUS_READY				= 0x0002,
	DCAMCAP_STATUS_STABLE				= 0x0003,
	DCAMCAP_STATUS_UNSTABLE				= 0x0004,

#if ! defined(DCAMAPI_VERMIN) || DCAMAPI_VERMIN <= 3200
	DCAM_STATUS_ERROR					= DCAMCAP_STATUS_ERROR,
	DCAM_STATUS_BUSY					= DCAMCAP_STATUS_BUSY,
	DCAM_STATUS_READY					= DCAMCAP_STATUS_READY,
	DCAM_STATUS_STABLE					= DCAMCAP_STATUS_STABLE,
	DCAM_STATUS_UNSTABLE				= DCAMCAP_STATUS_UNSTABLE,
#endif

	end_of_dcamcap_status
}
DCAM_DECLARE_END( DCAMCAP_STATUS )

DCAM_DECLARE_BEGIN( enum, DCAMWAIT_EVENT )
{
	DCAMCAP_EVENT_TRANSFERRED			= 0x0001,
	DCAMCAP_EVENT_FRAMEREADY			= 0x0002,	/* all modules support	*/
	DCAMCAP_EVENT_CYCLEEND				= 0x0004,	/* all modules support	*/
	DCAMCAP_EVENT_EXPOSUREEND			= 0x0008,
	DCAMCAP_EVENT_STOPPED				= 0x0010,

	DCAMREC_EVENT_STOPPED				= 0x0100,
	DCAMREC_EVENT_WARNING				= 0x0200,
	DCAMREC_EVENT_MISSED				= 0x0400,
//	DCAMREC_EVENT_FULLBUF				= 0x0800,	/* *cancel* */
	DCAMREC_EVENT_DISKFULL				= 0x1000,
	DCAMREC_EVENT_WRITEFAULT			= 0x2000,

#if ! defined(DCAMAPI_VERMIN) || DCAMAPI_VERMIN <= 3200
	DCAM_EVENT_FRAMESTART				= DCAMCAP_EVENT_TRANSFERRED,
	DCAM_EVENT_FRAMEEND					= DCAMCAP_EVENT_FRAMEREADY,
	DCAM_EVENT_CYCLEEND					= DCAMCAP_EVENT_CYCLEEND,
	DCAM_EVENT_EXPOSUREEND				= DCAMCAP_EVENT_EXPOSUREEND,	/* old name was VVALIDBEGIN */
	DCAM_EVENT_CAPTUREEND				= DCAMCAP_EVENT_STOPPED,
#endif

	end_of_dcamwait_event
}
DCAM_DECLARE_END( DCAMWAIT_EVENT )

/*** --- dcamcap_start --- ***/
DCAM_DECLARE_BEGIN( enum, DCAMCAP_START )
{
	DCAMCAP_START_SEQUENCE				= -1,
	DCAMCAP_START_SNAP					= 0
}
DCAM_DECLARE_END( DCAMCAP_START )

/*** --- string id --- ***/
DCAM_DECLARE_BEGIN( enum, DCAM_IDSTR )
{

	DCAM_IDSTR_BUS						= 0x04000101,
	DCAM_IDSTR_CAMERAID					= 0x04000102,
	DCAM_IDSTR_VENDOR					= 0x04000103,
	DCAM_IDSTR_MODEL					= 0x04000104,
	DCAM_IDSTR_CAMERAVERSION			= 0x04000105,
	DCAM_IDSTR_DRIVERVERSION			= 0x04000106,
	DCAM_IDSTR_MODULEVERSION			= 0x04000107,
	DCAM_IDSTR_DCAMAPIVERSION			= 0x04000108,

	DCAM_IDSTR_OPTICALBLOCK_MODEL		= 0x04001101,
	DCAM_IDSTR_OPTICALBLOCK_ID			= 0x04001102,
	DCAM_IDSTR_OPTICALBLOCK_DESCRIPTION	= 0x04001103,
	DCAM_IDSTR_OPTICALBLOCK_CHANNEL_1	= 0x04001104,
	DCAM_IDSTR_OPTICALBLOCK_CHANNEL_2	= 0x04001105
}
DCAM_DECLARE_END( DCAM_IDSTR )

/*** --- wait timeout --- ***/
DCAM_DECLARE_BEGIN( enum, DCAMWAIT_TIMEOUT )
{
	DCAMWAIT_TIMEOUT_INFINITE			= 0x80000000,

#if ! defined(DCAMAPI_VERMIN) || DCAMAPI_VERMIN <= 3200
	DCAM_WAIT_INFINITE					= DCAMWAIT_TIMEOUT_INFINITE,
#endif

	end_of_dcamwait_timeout
}
DCAM_DECLARE_END( DCAMWAIT_TIMEOUT  )

#if DCAMAPI_VER >= 4000

/*** --- initialize parameter --- ***/
#define	DCAMAPI_INITOPTION_QUICKLOCK		0x00FF0081

DCAM_DECLARE_BEGIN( enum, DCAM_METADATAKIND )
{
	DCAM_METADATAKIND_USERDATATEXT			= 0x00000001,
	DCAM_METADATAKIND_USERDATABIN			= 0x00000002,
	DCAM_METADATAKIND_TIMESTAMPS			= 0x00010000,
}
DCAM_DECLARE_END( DCAM_METADATAKIND )

DCAM_DECLARE_BEGIN( enum, DCAMREC_STATUSFLAG )
{
	DCAMREC_STATUSFLAG_NONE					= 0x00000000,
	DCAMREC_STATUSFLAG_RECORDING			= 0x00000001
}
DCAM_DECLARE_END( DCAMREC_STATUSFLAG )

/* **************************************************************** *

	structures (ver 4.x)

 * **************************************************************** */

typedef struct { long data1; long data2;}*	HDCAMWAIT;
typedef struct { long data1; long data2;}*	HDCAMREC;

DCAM_DECLARE_BEGIN( struct, DCAM_GUID )
{
	unsigned long	Data1;
	unsigned short	Data2;
	unsigned short	Data3;
	unsigned char	Data4[ 8 ];
}
DCAM_DECLARE_END( DCAM_GUID )

DCAM_DECLARE_BEGIN( struct, DCAMAPI_INIT )
{
	long				size;			// [in]
	long				iDeviceCount;	// [out]
	long				apiver;			// [in]
	long				reserved;		//	0
	const long*			initoption;		// [in ptr] 0 terminated long pointer.
	const DCAM_GUID*	guid;			// [in ptr]
}
DCAM_DECLARE_END( DCAMAPI_INIT )

DCAM_DECLARE_BEGIN( struct, DCAMDEV_OPEN )
{
	long			size;				// [in]
	long			index;				// [in]
	HDCAM			hdcam;				// [out]
}
DCAM_DECLARE_END( DCAMDEV_OPEN )

DCAM_DECLARE_BEGIN( struct, DCAMDEV_CAPABILITY )
{
	long			size;				// [in]
	long			reserved;			// [in]
	long			capflag1;			// [out]
	long			capflag2;			// [out]
}
DCAM_DECLARE_END( DCAMDEV_CAPABILITY )

DCAM_DECLARE_BEGIN( struct, DCAMDEV_STRING )
{
	long			size;				// [in]
	long			iString;			// [in]
	char*			text;				// [in,obuf]
	long			textbytes;			// [in]
}
DCAM_DECLARE_END( DCAMDEV_STRING )

DCAM_DECLARE_BEGIN( struct, DCAMBUF_ATTACH )
{
	long			size;				// [in]
	long			iKind;				// [in] DCAMBUF_ATTACHKIND
	void**			buffer;				// [in,ptr]
	long			buffercount;		// [in]
}
DCAM_DECLARE_END( DCAMBUF_ATTACH )

DCAM_DECLARE_BEGIN( struct, DCAM_TIMESTAMP )
{
	long			sec;				// [out]
	long			microsec;			// [out]
}
DCAM_DECLARE_END( DCAM_TIMESTAMP )

DCAM_DECLARE_BEGIN( struct, DCAM_FRAME )
{
	long			size;
	long			iKind;				// [in] 0 reserved
	long			option;				// [i or o] reserved
	long			iFrame;				// [in] frame index
	void*			buf;				// [i or o] pointer for top-left image
	long			rowbytes;			// [i or o] byte size for next line.
	DCAM_PIXELTYPE	type;				// [i or o] DCAM_PIXELTYPE_XXX
	long			width;				// [i or o] horizontal pixel count
	long			height;				// [i or o] vertical line count
	long			left;				// [i or o] (option) horizontal start pixel
	long			top;				// [i or o] (option) vertical start line
	DCAM_TIMESTAMP	timestamp;			// [i or o] timestamp
	long			framestamp;			// [i or o] framestamp
	long			reserved;			// [in] 0 reserved
}
DCAM_DECLARE_END( DCAM_FRAME )

DCAM_DECLARE_BEGIN( struct, DCAMWAIT_OPEN )
{
	long			size;
	long			supportevent;		// [out];
	HDCAMWAIT		hwait;				// [out];
	HDCAM			hdcam;				// [in];
}
DCAM_DECLARE_END( DCAMWAIT_OPEN )

DCAM_DECLARE_BEGIN( struct, DCAMWAIT_START)
{
	long			size;
	long			eventhappened;		// [out];
	long			eventmask;			// [in]
	long			timeout;			// [in];
}
DCAM_DECLARE_END( DCAMWAIT_START )

DCAM_DECLARE_BEGIN( struct, DCAMCAP_TRANSFERINFO )
{
	long			size;				// [in]
	long			reserved;			// [in]
	long			nNewestFrameIndex;	// [out]
	long			nFrameCount;		// [out]
}
DCAM_DECLARE_END( DCAMCAP_TRANSFERINFO )

#ifdef _WIN32

DCAM_DECLARE_BEGIN( struct, DCAMREC_OPENA )
{
	long			size;				// [in]
	long			reserved;			// [in]
	HDCAMREC		hrec;				// [out]
	const char*		path;				// [in]
	const char*		ext;				// [in]
	long			maxframepersession;	// [in]
	long			userdatasize;		// [in]
	long			userdatasize_session;//[in]
	long			userdatasize_file;	// [in]
	long			usertextsize;		// [in]
	long			usertextsize_session;//[in]
	long			usertextsize_file;	// [in]
}
DCAM_DECLARE_END( DCAMREC_OPENA )

DCAM_DECLARE_BEGIN( struct, DCAMREC_OPENW )
{
	long			size;				// [in]
	long			reserved;			// [in]
	HDCAMREC		hrec;				// [out]
	const wchar_t*	path;				// [in]
	const wchar_t*	ext;				// [in]
	long			maxframepersession;	// [in]
	long			userdatasize;		// [in]
	long			userdatasize_session;//[in]
	long			userdatasize_file;	// [in]
	long			usertextsize;		// [in]
	long			usertextsize_session;//[in]
	long			usertextsize_file;	// [in]
}
DCAM_DECLARE_END( DCAMREC_OPENW )

#else

DCAM_DECLARE_BEGIN( struct, DCAMREC_OPEN )
{
	long			size;				// [in]
	long			reserved;			// [in]
	HDCAMREC		hrec;				// [out]
	const char*		path;				// [in]
	const char*		ext;				// [in]
	long			maxframepersession;	// [in]
	long			userdatasize;		// [in]
	long			userdatasize_session;//[in]
	long			userdatasize_file;	// [in]
	long			usertextsize;		// [in]
	long			usertextsize_session;//[in]
	long			usertextsize_file;	// [in]
}
DCAM_DECLARE_END( DCAMREC_OPEN )

#endif

DCAM_DECLARE_BEGIN( struct, DCAM_METADATAHDR )
{
	long			size;
	long			iKind;						// [in] DCAM_METADATAKIND
	long			option;						// [in] 0 reserved
	long			iFrame;						// [in] start frame index
}
DCAM_DECLARE_END( DCAM_METADATAHDR )

DCAM_DECLARE_BEGIN( struct, DCAM_METADATABLOCKHDR )
{
	long			size;
	long			iKind;						// [in] DCAM_METADATAKIND
	long			option;						// [in] 0 reserved
	long			iFrame;						// [in] start frame index
	long			in_count;					// [in] max count of meta data
	long			outcount;					// [out] count of got meta data.
}
DCAM_DECLARE_END( DCAM_METADATABLOCKHDR )

DCAM_DECLARE_BEGIN( struct, DCAM_USERDATATEXT )
{
	DCAM_METADATAHDR	hdr;

	char*			text;
	long			text_len;
	long			text_kind;		// character encoding scheme, DCAM_CES_UTF8...
}
DCAM_DECLARE_END( DCAM_USERDATATEXT )

DCAM_DECLARE_BEGIN( struct, DCAM_USERDATABIN )
{
	DCAM_METADATAHDR	hdr;

	void*			bin;
	long			bin_len;
	long			reserved;
}
DCAM_DECLARE_END( DCAM_USERDATABIN )

DCAM_DECLARE_BEGIN( struct, DCAM_TIMESTAMPBLOCK )
{
	DCAM_METADATABLOCKHDR	hdr;

	DCAM_TIMESTAMP*		timestamps;				// [in] pointer for TIMESTAMP block
	long				timestampsize;			// [in] sizeof(DCAM_TIMESTRAMP)
	long				timestampvaildsize;		// [o] return the written data size of DCAM_TIMESTRAMP.
	long				timestampkind;			// [o] return timestamp kind(Hardware, Driver, DCAM etc..)
	long				reserved;
}
DCAM_DECLARE_END( DCAM_TIMESTAMPBLOCK )

DCAM_DECLARE_BEGIN( struct, DCAM_METADATABLOCK )
{
	DCAM_METADATABLOCKHDR	hdr;

	void*				buf;					// [i/o] see below.
	long*				unitsizes;				// [i/o] see below.
	long				bytesperunit;			// [i/o] see below.
	long				userdata_kind;			// [in] choose userdata kind(File, Session, Frame)

// Note
	// dcamrec_copymetadatablock()
	//	buf										// [in] pointer for filling userdata block
	//	unitsizes								// [in] pointer for filling each unit size of METADATA
	//	bytesperunit							// [in] max bytes per unit for filling each METADATA

	// dcamrec_lockmetadatablock()
	//	buf										// [out] return DCAM internal pointer of userdata block
	//	unitsizes								// [out] return DCAM internal array pointer of each size
	//	bytesperunit							// [out] max bytes per unit which is set at DCAMREC_OPEN
}
DCAM_DECLARE_END( DCAM_METADATABLOCK )

DCAM_DECLARE_BEGIN( struct, DCAMREC_STATUS )
{
	long			size;
	long			currentsession_index;
	long			maxframecount_per_session;
	long			currentframe_index;
	long			missingframe_count;
	long			flags;
	long			totalframecount;
	long			reserved;
}
DCAM_DECLARE_END( DCAMREC_STATUS )

/* **************************************************************** *

	functions (ver 4.x)

 * **************************************************************** */

// Initialize, uninitialize and misc.
DCAMERR DCAMAPI dcamapi_init			( DCAMAPI_INIT* param DCAM_DEFAULT_ARG );
DCAMERR DCAMAPI dcamapi_uninit			();
DCAMERR DCAMAPI dcamdev_open			( DCAMDEV_OPEN* param );
DCAMERR DCAMAPI dcamdev_close			( HDCAM h );
DCAMERR DCAMAPI dcamdev_showpanel		( HDCAM h, long iKind );		//	( HDCAM h, HWND hWnd, _DWORD reserved DCAM_DEFAULT_ARG );
DCAMERR DCAMAPI dcamdev_getcapability	( HDCAM h, DCAMDEV_CAPABILITY* param );
DCAMERR DCAMAPI dcamdev_getstring		( HDCAM h, DCAMDEV_STRING* param );

// Buffer control
DCAMERR DCAMAPI dcambuf_alloc			( HDCAM h, long framecount );	// call dcambuf_release() to free.
DCAMERR DCAMAPI dcambuf_attach			( HDCAM h, const DCAMBUF_ATTACH* param );
DCAMERR DCAMAPI dcambuf_release			( HDCAM h, long iKind DCAM_DEFAULT_ARG );
DCAMERR DCAMAPI dcambuf_lockframe		( HDCAM h, DCAM_FRAME* pFrame );
DCAMERR DCAMAPI dcambuf_copyframe		( HDCAM h, DCAM_FRAME* pFrame );
DCAMERR DCAMAPI dcambuf_copymetadata	( HDCAM h, DCAM_METADATAHDR* hdr );

// Capturing
DCAMERR DCAMAPI dcamcap_start			( HDCAM h, long mode );
DCAMERR DCAMAPI dcamcap_stop			( HDCAM h );
DCAMERR DCAMAPI dcamcap_status			( HDCAM h, DCAMCAP_STATUS* pStatus ); //Loic: was long*
DCAMERR DCAMAPI dcamcap_transferinfo	( HDCAM h, DCAMCAP_TRANSFERINFO* param );
DCAMERR DCAMAPI dcamcap_firetrigger		( HDCAM h, long iKind DCAM_DEFAULT_ARG );
DCAMERR DCAMAPI dcamcap_record			( HDCAM h, HDCAMREC hrec );

// Wait abort handle control
DCAMERR DCAMAPI dcamwait_open			( DCAMWAIT_OPEN* param );
DCAMERR DCAMAPI dcamwait_close			( HDCAMWAIT hWait );
DCAMERR DCAMAPI dcamwait_start			( HDCAMWAIT hWait, DCAMWAIT_START* param );
DCAMERR DCAMAPI dcamwait_abort			( HDCAMWAIT hWait );

// Recording
#ifdef _WIN32
DCAMERR DCAMAPI dcamrec_openA			( DCAMREC_OPENA* param );
DCAMERR DCAMAPI dcamrec_openW			( DCAMREC_OPENW* param );

#ifdef _UNICODE
#define	DCAMREC_OPEN	DCAMREC_OPENW
#define	dcamrec_open	dcamrec_openW
#else
#define	DCAMREC_OPEN	DCAMREC_OPENA
#define	dcamrec_open	dcamrec_openA
#endif

#else
DCAMERR DCAMAPI dcamrec_open			( DCAMREC_OPEN* param );
#endif

DCAMERR DCAMAPI dcamrec_close			( HDCAMREC hrec );
DCAMERR DCAMAPI dcamrec_lockframe		( HDCAMREC hrec, DCAM_FRAME* pFrame );
DCAMERR DCAMAPI dcamrec_copyframe		( HDCAMREC hrec, DCAM_FRAME* pFrame );
DCAMERR	DCAMAPI dcamrec_writemetadata	( HDCAMREC hrec, const DCAM_METADATAHDR* hdr );
DCAMERR DCAMAPI	dcamrec_lockmetadata	( HDCAMREC hrec, DCAM_METADATAHDR* hdr );
DCAMERR DCAMAPI dcamrec_copymetadata	( HDCAMREC hrec, DCAM_METADATAHDR* hdr );
DCAMERR DCAMAPI	dcamrec_lockmetadatablock( HDCAMREC hrec, DCAM_METADATABLOCKHDR* hdr );
DCAMERR DCAMAPI dcamrec_copymetadatablock( HDCAMREC hrec, DCAM_METADATABLOCKHDR* hdr );

DCAMERR DCAMAPI dcamrec_pause			( HDCAMREC hrec );
DCAMERR DCAMAPI dcamrec_resume			( HDCAMREC hrec );
DCAMERR DCAMAPI dcamrec_status			( HDCAMREC hrec, DCAMREC_STATUS* pStatus );

#endif // DCAMAPI_VER >= 4000

/* **************************************************************** */

//#ifdef __cplusplus

/* end of extern "C" */
};

/*** C++ utility ***/

//inline int failed( DCAMERR err )
//{
//	return int(err) < 0;
//}

//#endif

#if ! defined(DCAMAPI_VERMIN) || DCAMAPI_VERMIN <= 3200

#include "dcamapi3.h"

#endif // ! defined(DCAMAPI_VERMIN) || DCAMAPI_VERMIN <= 3200

#if (defined(_MSC_VER)&&defined(_LINK_DCAMAPI_LIB))
#pragma comment(lib, "dcamapi.lib")
#endif

#define	_INCLUDE_DCAMAPI_H_
#endif
