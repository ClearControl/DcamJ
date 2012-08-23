// dcimgapi.h

#ifndef _INCLUDE_DCIMGAPI_H_

// ****************************************************************
//  common declaration with dcamapi.h


/* **************************************************************** *

	macros

 * **************************************************************** */

#ifdef __cplusplus

/* C++ */

#define	BEGIN_DCIMG_DECLARE( kind, tag )	kind tag
#define	END_DCIMG_DECLARE( tag  )			;

#else

/* C */

#define	BEGIN_DCIMG_DECLARE( kind, tag )	typedef kind
#define	END_DCIMG_DECLARE( tag  )			tag;

#endif // __cplusplus


/* define - DCIMGAPI */

#ifndef DCIMGAPI
#ifdef WIN32
#define	DCIMGAPI	PASCAL	/* DCAM-API based on PASCAL calling */
#else
#define DCIMGAPI
#endif
#endif /* DCIMGAPI */

/* **************************************************************** *

	constant declaration

 * **************************************************************** */

/*** --- errors --- ***/

BEGIN_DCIMG_DECLARE( enum, DCIMG_ERR )
{
  /* status error */
//	DCIMG_ERR_BUSY					= 0x80000101,/*		API cannot process in busy state.		*/
//	DCIMG_ERR_NOTREADY				= 0x80000103,/*		API requires ready state.				*/
//	DCIMG_ERR_NOTSTABLE				= 0x80000104,/*		API requires stable or unstable state.	*/
//	DCIMG_ERR_UNSTABLE				= 0x80000105,/*		API does not support in unstable state.	*/
//	DCIMG_ERR_NOTBUSY				= 0x80000107,/*		API requires busy state.				*/

//	DCIMG_ERR_EXCLUDED				= 0x80000110,/*		some resource is exclusive and already used	*/

//	DCIMG_ERR_COOLINGTROUBLE		= 0x80000302,/*		something happens near cooler */
//	DCIMG_ERR_NOTRIGGER				= 0x80000303,/*		no trigger when necessary. Some camera supports this error. */

//	DCIMG_ERR_INCONSISTENT_OPTICS	= 0x80000311,/*	3.1:camera detects optics is changed */	/*[[ future ]]*/

  /* wait error */
//	DCIMG_ERR_ABORT					= 0x80000102,/*		abort process			*/
//	DCIMG_ERR_TIMEOUT				= 0x80000106,/*		timeout					*/
//	DCIMG_ERR_LOSTFRAME				= 0x80000301,/*		frame data is lost		*/
//	DCIMG_ERR_MISSINGFRAME_TROUBLE	= 0x80000f06,/*frame is lost but reason is low lever driver's bug */

  /* initialization error */
//	DCIMG_ERR_NORESOURCE			= 0x80000201,/*		not enough resource except memory	*/
	DCIMG_ERR_NOMEMORY				= 0x80000203,/*		not enough memory		*/
//	DCIMG_ERR_NOMODULE				= 0x80000204,/*		no sub module			*/
//	DCIMG_ERR_NODRIVER				= 0x80000205,/*		no driver			    */
//	DCIMG_ERR_NOCAMERA				= 0x80000206,/*		no camera				*/
//	DCIMG_ERR_NOGRABBER				= 0x80000207,/* 2.2:no grabber				*/
//	DCIMG_ERR_NOCOMBINATION			= 0x80000208,/* 2.2:no combination on registry */
	DCIMG_ERR_FAILLOADDCIMGDLL		= 0x80000209,/*		DCIMG.DLL is missing */
	DCIMG_ERR_WRONGDCIMGDLL			= 0x8000020a,/*		dcimg_proc does not exist in DCIMG.DLL */
	DCIMG_ERR_REQUIRE_DCIMGINIT		= 0x8000020b,/*		dcimg_init() is not correctly called. */

//	DCIMG_ERR_FAILOPEN				= 0x80001001,
//	DCIMG_ERR_INVALIDMODULE			= 0x80000211,/* 2.2:dcam_init() found invalid module */
//	DCIMG_ERR_INVALIDCOMMPORT		= 0x80000212,/*		invalid serial port		*/
//	DCIMG_ERR_FAILOPENBUS			= 0x81001001,/*		the bus or driver are not available	*/
//	DCIMG_ERR_FAILOPENCAMERA		= 0x82001001,/*		camera report error during opening	*/

  /* calling error */
//	DCIMG_ERR_INVALIDCAMERA			= 0x80000806,/*		invalid camera		 */
	DCIMG_ERR_INVALIDHANDLE			= 0x80000807,/*		invalid dcimg value	*/
	DCIMG_ERR_INVALIDPARAM			= 0x80000808,/*		invalid parameter, e.g. parameter is NULL	*/
	DCIMG_ERR_INVALIDVALUE			= 0x80000821,/*		invalid parameter value	*/
//	DCIMG_ERR_OUTOFRANGE			= 0x80000822,/*		value is out of range	*/
//	DCIMG_ERR_NOTWRITABLE			= 0x80000823,/* 2.2:the property is not readable	*/
//	DCIMG_ERR_NOTREADABLE			= 0x80000824,/* 2.2:the property is not writable	*/
//	DCIMG_ERR_INVALIDPROPERTYID		= 0x80000825,/*		the property id is invalid	*/
//	DCIMG_ERR_NEWAPIREQUIRED		= 0x80000826,/* 2.2:old API does not support the value because only new API supports the value */
//	DCIMG_ERR_WRONGHANDSHAKE		= 0x80000827,/* 2.2:this error happens DCAM get error code from camera unexpectedly  */
//	DCIMG_ERR_NOPROPERTY			= 0x80000828,/* 2.2:there is no altenative or influence id, or no more property id */
//	DCIMG_ERR_INVALIDCHANNEL		= 0x80000829,/* 2.2:the property id specifies channel but channel is invalid */
//	DCIMG_ERR_INVALIDVIEW			= 0x8000082a,/* 2.2:the property id specifies channel but channel is invalid */
//	DCIMG_ERR_INVALIDSUBARRAY		= 0x8000082b,/* 2.2:the conbination of subarray values are invalid. e.g. DCIMG_IDPROP_SUBARRAYHPOS + DCIMG_IDPROP_SUBARRAYHSIZE is greater than the number of horizontal pixel of sensor. */
//	DCIMG_ERR_ACCESSDENY			= 0x8000082c,/*		the property cannot access during this DCAM STATUS */
//	DCIMG_ERR_NOVALUETEXT			= 0x8000082d,/*		the property does not have value text */
//	DCIMG_ERR_WRONGPROPERTYVALUE	= 0x8000082e,/*	at least one property value is wrong */
//	DCIMG_ERR_DISHARMONY			= 0x80000830,/*	3.0:the paired camera does not have same parameter */
//	DCIMG_ERR_NOCONTROL				= 0x80000831,/* 3.0:the function exists but is not controlable */	/*[[ future ]]*/
//	DCIMG_ERR_FRAMEBUNDLESHOULDBEOFF= 0x80000832,/*	framebundle mode should be OFF under current property settings */
	DCIMG_ERR_INVALIDFRAMEINDEX		= 0x80000833,/*		the frame index is invalid  */
	DCIMG_ERR_INVALIDSESSIONINDEX	= 0x80000834,/*		the session index is invalid */
	DCIMG_ERR_FILENOTOPENED			= 0x80000835,/*		file is not opened at dcimg_open() or dcimg_create() */
	DCIMG_ERR_UNKNOWNFILEFORMAT		= 0x80000836,/*		opened file format is not supported */
	DCIMG_ERR_MISSINGRELATEDFILE	= 0x80000837,/*		File could be opened but necessary file is missing, e.g. DLL, file body, ... */
	DCIMG_ERR_NOTSUPPORT			= 0x80000f03,/*		the function or property are not supportted under current condition */

  /* camera or bus trouble */
  /* include calibration error */						/*[[ future ]]*/
//	DCIMG_ERR_FAILREADCAMERA		= 0x83001002,
//	DCIMG_ERR_FAILWRITECAMERA		= 0x83001003,
//	DCIMG_ERR_CONFLICTCOMMPORT		= 0x83001004,
//	DCIMG_ERR_OPTICS_UNPLUGGED		= 0x83001005,/* 3.1:Optics part is unplugged so please check it. */
//	DCIMG_ERR_FAILCALIBRATION		= 0x83001006,/*	3.1:fail calibration	*/
//	DCIMG_ERR_NOTFOUNDCALIBDATA		= 0x83001007,/* not found the calibration data for C9595 series */	/*[[ reserved ]]*/

//	DCIMG_ERR_FAILEDOPENRECFILE		= 0x84001001,
//	DCIMG_ERR_INVALIDFILEHANDLE		= 0x84001002,
//	DCIMG_ERR_FAILEDWRITEDATA		= 0x84001003,
	DCIMG_ERR_FAILEDREADDATA		= 0x84001004,
//	DCIMG_ERR_NOWRECORDING			= 0x84001005,
//	DCIMG_ERR_WRITEFULL				= 0x84001006,
//	DCIMG_ERR_ALREADYOCCUPIED		= 0x84001007,

  /* calling error for DCAM-API 2.1.3 */
	DCIMG_ERR_UNKNOWNCOMMAND		= 0x80000801,/*		unknown command id		*/
//	DCIMG_ERR_UNKNOWNSTRID			= 0x80000802,/*		unknown string id		*/
	DCIMG_ERR_UNKNOWNPARAMID		= 0x80000803,/*		unkown parameter id		*/
//	DCIMG_ERR_UNKNOWNBITSTYPE		= 0x80000804,/*		unknown bitmap bits type			*/
//	DCIMG_ERR_UNKNOWNDATATYPE		= 0x80000805,/*		unknown frame data type				*/

  /* internal error */
	DCIMG_ERR_SUCCESS				= 1,		/*		no error, general success code		*/

  /* internal error */
//	DCIMG_ERR_NONE					= 0,		/*		no error, nothing to have done		*/
//	DCIMG_ERR_INSTALLATIONINPROGRESS= 0x80000f00,/*installation progress				*/
	DCIMG_ERR_UNREACH				= 0x80000f01,/*		internal error						*/
//	DCIMG_ERR_UNLOADED				= 0x80000f04,/*		calling after process terminated	*/
//	DCIMG_ERR_THRUADAPTER			= 0x80000f05,/*											*/

	DCIMG_ERR_NOTIMPLEMENT			= 0x80000f02,/*		not yet implementation				*/

	_end_of_dcimgerr
}
END_DCIMG_DECLARE( DCIMG_ERR )


BEGIN_DCIMG_DECLARE( enum, DCIMG_USERDATAKIND )
{
	DCIMG_USERDATAKIND_FRAME	= 0x00000000,
	DCIMG_USERDATAKIND_FILE		= 0x01000000,
	DCIMG_USERDATAKIND_SESSION	= 0x02000000,

	end_of_dcimg_userdatakind
}
END_DCIMG_DECLARE( DCIMG_USERDATAKIND )


BEGIN_DCIMG_DECLARE( enum, DCIMG_PIXELTYPE )
{
	DCIMG_PIXELTYPE_NONE		= 0x00000000,

	DCIMG_PIXELTYPE_MONO8		= 0x00000001,
	DCIMG_PIXELTYPE_MONO16		= 0x00000002,

	DCIMG_PIXELTYPE_YUV411		= 0x00000015,
	DCIMG_PIXELTYPE_YUV422		= 0x00000016,
	DCIMG_PIXELTYPE_YUV444		= 0x00000017,

	DCIMG_PIXELTYPE_RGB24		= 0x00000021,
	DCIMG_PIXELTYPE_RGB48		= 0x00000022,
	DCIMG_PIXELTYPE_BGR24		= 0x00000029,
	DCIMG_PIXELTYPE_BGR48		= 0x0000002a,

	DCIMG_PIXELTYPE_RAW8_BGGR	= 0x00001001,
	DCIMG_PIXELTYPE_RAW16_BGGR	= 0x00001002,

	end_of_dcimg_pixeltype
}
END_DCIMG_DECLARE( DCIMG_PIXELTYPE )

BEGIN_DCIMG_DECLARE( enum, DCIMG_METADATAKIND )
{
	DCIMG_METADATAKIND_USERDATATEXT			= 0x00000001,
	DCIMG_METADATAKIND_USERDATABIN			= 0x00000002,
	DCIMG_METADATAKIND_TIMESTAMPS			= 0x00010000,
}
END_DCIMG_DECLARE( DCIMG_METADATAKIND )

BEGIN_DCIMG_DECLARE( struct, DCIMG_TIMESTAMP )
{
	long			sec;				// [out]
	long			microsec;			// [out]
}
END_DCIMG_DECLARE( DCIMG_TIMESTAMP )

BEGIN_DCIMG_DECLARE( struct, DCIMG_METADATAHDR )
{
	long				size;
	long				iKind;				// [in] DCIMG_METADATAKIND
	long				option;				// [in] 0 reserved
	long				iFrame;				// [in] start frame index
}
END_DCIMG_DECLARE( DCIMG_METADATAHDR )

BEGIN_DCIMG_DECLARE( struct, DCIMG_USERDATATEXT )
{
	DCIMG_METADATAHDR	hdr;

	char*			text;
	long			text_len;
	long			text_kind;		// character encoding scheme, DCAM_CES_UTF8...
}
END_DCIMG_DECLARE( DCIMG_USERDATATEXT )

BEGIN_DCIMG_DECLARE( struct, DCIMG_USERDATABIN )
{
	DCIMG_METADATAHDR	hdr;

	void*			bin;
	long			bin_len;
	long			reserved;
}
END_DCIMG_DECLARE( DCIMG_USERDATABIN )

BEGIN_DCIMG_DECLARE( struct, DCIMG_TIMESTAMPS )	// obsolete
{
	long				size;
	long				iKind;				// [in] 0 reserved
	long				option;				// [in] control filling field
	long				iFrame;				// [i or o] frame index
	DCIMG_TIMESTAMP*	timestamps;			// [i] pointer for the buffer to receive timestamp values
	long				timestampcount;		// [i or o] number of timestamp to receive. IN: receive buffer size by count. OUT: filled buffer count
	long				timestampkind;		// [o] reserved; return timestamp kind
}
END_DCIMG_DECLARE( DCIMG_TIMESTAMPS )

BEGIN_DCIMG_DECLARE( struct, DCIMG_TIMESTAMPBLOCK )
{
	DCIMG_METADATAHDR	hdr;

	DCIMG_TIMESTAMP*	timestamps;			// [i] pointer for TIMESTAMP block
	long				timestampmax;		// [i or o] number of timestamp to receive. IN: receive buffer size by count. OUT: filled buffer count (when timestampvalidsize isn't supported)
	long				timestampkind;		// [o] return timestamp kind(Hardware, Driver, DCAM etc..)
	long				timestampsize;		// [i] sizeof(DCIMG_TIMESTAMP)	//additional 20120224
	long				timestampvalidsize;	// [o] return the written data size of DCAM_TIMESTRAMP.
	long				timestampcount;		// [o] return how many timestamps are filled
	long				reserved;
}
END_DCIMG_DECLARE( DCIMG_TIMESTAMPBLOCK )

BEGIN_DCIMG_DECLARE( struct, DCIMG_USERDATABLOCK )
{
	DCIMG_METADATAHDR	hdr;

	void*				userdata;				// [in] pointer for userdata block
	long				userdatasize;			// [in] size of one userdata
	long*				userdatavalidsize;		// [o] return the written data size of ...
	long				userdatamax;			// [in] maximum number of userdata which can receive. userdata pointer should have userdata * userdatamax
	long				userdatacount;			// [o] return how many userdata are filled
	long				userdata_kind;			// [in] choose userdata kind(File, Session, Frame)
}
END_DCIMG_DECLARE( DCIMG_USERDATABLOCK )

/* **************************************************************** *

	structures (ver 4.x)

 * **************************************************************** */


BEGIN_DCIMG_DECLARE( struct, DCIMG_FRAME )
{
	long			size;				
	long			iKind;				// [in] 0 reserved
	long			option;				// [i or o] reserved
	long			iFrame;				// [in] frame index
	void*			buf;				// [i or o] pointer for the top-left image
	long			rowbytes;			// [i or o] byte size for next line
	DCIMG_PIXELTYPE	type;				// [i or o] DCAM_PIXELTYPE_XXX
	long			width;				// [i or o] horizontal pixel count				i:copyframe, o:getframe
	long			height;				// [i or o] vertical line count					i:copyframe, o:getframe
	long			left;				// [i or o] (option) horizontal start pixel		i:copyframe, o:getframe
	long			top;				// [i or o] (option) vertical start line		i:copyframe, o:getframe
	DCIMG_TIMESTAMP	timestamp;			// [i or o] timestamp
	DWORD			framestamp;			// [i or o] framestamp
	long			reserved;			// [in] 0 reserved
}
END_DCIMG_DECLARE( DCIMG_FRAME )


BEGIN_DCIMG_DECLARE( struct, DCIMG_USERDATA )
{
	long		size;
	long		userdata_kind;
	long		iFrame;

	long		userdatalen;
	void*		userdata;

	long		text_kind;			// character encoding scheme, DCIMG_CES_UTF8...
	long		usertextlen;		// metatexttop + metatextlen <= metadatalen
	char*		usertext;
}
END_DCIMG_DECLARE( DCIMG_USERDATA )




// ****************************************************************
//  declaration for DCIMG API

BEGIN_DCIMG_DECLARE( enum, DCIMG_IDPARAML )
{
	DCIMG_IDPARAML_NUMBEROF_TOTALFRAME,		// number of total frame in the file

	DCIMG_IDPARAML_NUMBEROF_SESSION,			// number of session in the file.
	DCIMG_IDPARAML_NUMBEROF_FRAME,			// number of frame in current session.

	DCIMG_IDPARAML_SIZEOF_USERMETADATA,		// byte size of USER META DATA.
	DCIMG_IDPARAML_SIZEOF_USERMETADATA_SESSION,	// byte size of USER META DATA.
	DCIMG_IDPARAML_SIZEOF_USERMETADATA_FILE,	// byte size of USER META DATA.

	DCIMG_IDPARAML_SIZEOF_USERMETATEXT,		// byte size of USER META TEXT.
	DCIMG_IDPARAML_SIZEOF_USERMETATEXT_SESSION,	// byte size of USER META TEXT.
	DCIMG_IDPARAML_SIZEOF_USERMETATEXT_FILE,	// byte size of USER META TEXT.

	DCIMG_IDPARAML_IMAGE_WIDTH,				// image width in current session.										ver5
	DCIMG_IDPARAML_IMAGE_HEIGHT,			// image height in current session.										ver5
	DCIMG_IDPARAML_IMAGE_ROWBYTES,			// image rowbytes in current session.									ver5
	DCIMG_IDPARAML_IMAGE_PIXELTYPE,			// image pixeltype in current session.									ver5

	DCIMG_IDPARAML_MAXSIZE_USERMETADATA,			// user sets the userdata max of frame with dcamre_open.		ver5			
	DCIMG_IDPARAML_MAXSIZE_USERMETADATA_SESSION,	// user sets the userdata max of session with dcamre_open.		ver5
	DCIMG_IDPARAML_MAXSIZE_USERMETADATA_FILE,		// user sets the userdata max of file with dcamre_open.			ver5

	DCIMG_IDPARAML_MAXSIZE_USERMETATEXT,			// user sets the usertext max of frame with dcamre_open.		ver5
	DCIMG_IDPARAML_MAXSIZE_USERMETATEXT_SESSION,	// user sets the usertext max of session with dcamre_open.		ver5
	DCIMG_IDPARAML_MAXSIZE_USERMETATEXT_FILE,		// user sets the usertext max of file with dcamre_open.			vers

	end_of_dcimg_idparaml
}
END_DCIMG_DECLARE( DCIMG_IDPARAML )


BEGIN_DCIMG_DECLARE( enum, DCIMG_CMD )
{
	DCIMG_CMD_INIT				= 0x00000001,	// DCIMG_INIT* pparam;
	DCIMG_CMD_OPENW				= 0x00000004,	// DCIMG_OPENW* pparam;
	DCIMG_CMD_OPENA				= 0x00000005,	// DCIMG_OPENA* pparam;

	DCIMG_CMD_CLOSE				= 0x0000000f,	// HDCIMG hdcimg;

	DCIMG_CMD_GETFRAME			= 0x00000011,
	DCIMG_CMD_COPYFRAME			= 0x00000012,
	DCIMG_CMD_COPYUSERDATA		= 0x00000013,
	DCIMG_CMD_GETPARAML			= 0x00000015,
//	DCIMG_CMD_GETPARAMF			= 0x00000016,

	DCIMG_CMD_WRITEFRAME		= 0x00000021,
	DCIMG_CMD_WRITEUSERDATA		= 0x00000023,
	DCIMG_CMD_WRITEPARAML		= 0x00000025,
//	DCIMG_CMD_WRITEPARAMF		= 0x00000026,

	DCIMG_CMD_GETTIMESTAMP		= 0x00000031,

	DCIMG_CMD_GETMETADATA		= 0x00000032,
	DCIMG_CMD_GETMETADATABLOCK	= 0x00000033,

	DCIMG_CMD_GOTO_FRAME		= 0x00010101,	// lparam = frame index
	DCIMG_CMD_NEXT_FRAME		= 0x00010102,	// no param
	DCIMG_CMD_GOTO_SESSION		= 0x00010201,	// lparam = session index
	DCIMG_CMD_NEXT_SESSION		= 0x00010202,	// no param

	end_of_dcimg_cmd
}
END_DCIMG_DECLARE( DCIMG_CMD )


/* **************************************************************** */

typedef struct tag_DCIMG*	HDCIMG;	// handle for file

BEGIN_DCIMG_DECLARE( struct, DCIMG_GUID )
{
    unsigned long  Data1;
    unsigned short Data2;
    unsigned short Data3;
    unsigned char  Data4[ 8 ];
}
END_DCIMG_DECLARE( DCIMG_GUID )

#define	DCIMG_DEFAULT_ARG	= 0
#define	DCIMG_DEFAULT_PTR	= NULL

// initialize parameter
BEGIN_DCIMG_DECLARE( struct, DCIMG_INIT )
{
	long		size;					// [in]
	long		reserved;				//
	const DCIMG_GUID*	guid;				// [in ptr]
}
END_DCIMG_DECLARE( DCIMG_INIT )

#ifdef _WIN32

// open parameter
BEGIN_DCIMG_DECLARE( struct, DCIMG_OPENW )
{
	long			size;				// [in] size of this structure
	long			reserved;
	HDCIMG			hdcimg;				// [out]
	LPCWSTR			path;				// [in] DCIMG file path
}
END_DCIMG_DECLARE( DCIMG_OPENW )

BEGIN_DCIMG_DECLARE( struct, DCIMG_OPENA )
{
	long			size;				// [in] size of this structure
	long			reserved;
	HDCIMG			hdcimg;				// [out]
	LPCSTR			path;				// [in] DCIMG file path
}
END_DCIMG_DECLARE( DCIMG_OPENA )

#ifdef _UNICODE

#define	DCIMG_OPEN		DCIMG_OPENW
#define	dcimg_open		dcimg_openW

#else

#define	DCIMG_OPEN		DCIMG_OPENA
#define	dcimg_open		dcimg_openA

#endif // _UNICODE

#else

// open parameter
BEGIN_DCIMG_DECLARE( struct, DCIMG_OPEN )
{
	long			size;				// [in] size of this structure
	long			reserved;
	HDCIMG			hdcimg;				// [out]
	const char*		path;				// [in] DCIMG file path
}
END_DCIMG_DECLARE( DCIMG_OPEN )

#endif


// ****************************************************************
//  helper for C++

#ifdef __cplusplus

/* C++ */

extern "C" {

#endif // __cplusplus

/* **************************************************************** */

	DCIMG_ERR DCIMGAPI dcimg_init		( DCIMG_INIT* param );
#ifdef _WIN32
	DCIMG_ERR DCIMGAPI dcimg_openW		( DCIMG_OPENW* param );
	DCIMG_ERR DCIMGAPI dcimg_openA		( DCIMG_OPENA* param );
#else
	DCIMG_ERR DCIMGAPI dcimg_open		( DCIMG_OPEN* param );
#endif
	DCIMG_ERR DCIMGAPI dcimg_close		( HDCIMG hdcimg ); 

	DCIMG_ERR DCIMGAPI dcimg_command	( HDCIMG hdcimg, DCIMG_CMD cmd, long lparam DCIMG_DEFAULT_ARG, void* pparam DCIMG_DEFAULT_ARG, long pparambytes DCIMG_DEFAULT_ARG );
	DCIMG_ERR DCIMGAPI dcimg_getframe	( HDCIMG hdcimg, DCIMG_FRAME* aFrame );			// obsolete
	DCIMG_ERR DCIMGAPI dcimg_lockframe	( HDCIMG hdcimg, DCIMG_FRAME* aFrame );
	DCIMG_ERR DCIMGAPI dcimg_copyframe	( HDCIMG hdcimg, DCIMG_FRAME* aFrame );
	DCIMG_ERR DCIMGAPI dcimg_copyuserdata(HDCIMG hdcimg, DCIMG_USERDATA* data );		// obsolete
	DCIMG_ERR DCIMGAPI dcimg_copymetadata(HDCIMG hdcimg, DCIMG_METADATAHDR* hdr );
//	DCIMG_ERR DCIMGAPI dcimg_writeframe	( HDCIMG hdcimg, DCIMG_FRAME* aFrame );
//	DCIMG_ERR DCIMGAPI dcimg_writeuserdata(HDCIMG hdcimg,DCIMG_USERDATA* data );
	DCIMG_ERR DCIMGAPI dcimg_copytimestamps(HDCIMG hdcimg, DCIMG_TIMESTAMPS* pStamps );	// obsolete
	DCIMG_ERR DCIMGAPI dcimg_copymetadatablock( HDCIMG hdcimg, DCIMG_METADATAHDR* hdr );

	DCIMG_ERR DCIMGAPI dcimg_getparaml	( HDCIMG hdcimg, DCIMG_IDPARAML index, long* paraml );
//	DCIMG_ERR DCIMGAPI dcimg_getparamf	( HDCIMG hdcimg, DCIMG_IDPARAMF index, double* paramf );

	DCIMG_ERR DCIMGAPI dcimg_writeparaml( HDCIMG hdcimg, DCIMG_IDPARAML index, const long* paraml );
//	DCIMG_ERR DCIMGAPI dcimg_writeparamf( HDCIMG hdcimg, DCIMG_IDPARAMF index, const double* paramf );

/* **************************************************************** */

/* **************************************************************** */

#ifdef __cplusplus

/* end of extern "C" */
};

/*** C++ utility ***/

inline int failed( DCIMG_ERR err )
{
	return int(err) < 0;
}

#endif // __cplusplus

/* **************************************************************** */

#define	_INCLUDE_DCIMGAPI_H_
#endif // _INCLUDE_DCIMGAPI_H_
