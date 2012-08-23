// paramC4742_95.h
// [Oct.3,2001]

#ifndef	_INCLUDE_PARAMC4742_95_H_
#define	_INCLUDE_PARAMC4742_95_H_

struct DCAM_PARAM_C4742_95 {
	DCAM_HDR_PARAM	hdr;		// id == DCAM_PARAMID_C4742_95

	long	AMD;	// char;	Acquire mode              N/E
	long	NMD;	// char;	Normal mode               N/S/F
	long	EMD;	// char;	External mode             E/L
	long	ATP;	// char;	Active Trigger Polarity   N/P 

	long	SHT;	// long;	Shutter
	long	FBL;	// long;	Frame Blanking
	long	EST;	// long;	External Shutter

	long	SFD;	// char;	Optical Black             O/F
	long	SMD;	// char;	Scam mode                 N/S
	long	SPX;	// long;	Super Pixel               2/4/8

	long	ADS;	// long;	A/D Select                8/10/12
	
	long	CEO;	// long;	Contrast Enhance Offset   0-255
	long	CEG;	// long;	Contrast Enhance Gain     0-255

	long	ESC;	// char;	External trigger Sourece Connector
					//			B/D/I	BNC or D-SUB or I/F

	long	SHA;	// char;	read Scan Horizontal Area
					//			F/K  Full(1280) Killo(1024)
};


enum {
	dcamparam_c4742_95_AMD	= 0x00000001,
	dcamparam_c4742_95_NMD	= 0x00000002,
	dcamparam_c4742_95_EMD	= 0x00000004,

	dcamparam_c4742_95_ATP	= 0x00000008,

	dcamparam_c4742_95_SHT	= 0x00000100,
	dcamparam_c4742_95_FBL	= 0x00000200,
	dcamparam_c4742_95_EST	= 0x00000400,

	dcamparam_c4742_95_SFD	= 0x00001000,
	dcamparam_c4742_95_SMD	= 0x00002000,
	dcamparam_c4742_95_SPX	= 0x00004000,
	dcamparam_c4742_95_ADS	= 0x00008000,
	
	dcamparam_c4742_95_CEO	= 0x00010000,
	dcamparam_c4742_95_CEG	= 0x00020000,

	dcamparam_c4742_95_ESC	= 0x00040000,

	dcamparam_c4742_95_SHA	= 0x00080000
};

/*
enum {

	c4742_95_kATP_Negative		= 0,
	c4742_95_kATP_Positive,

	c4742_95_kUNIT_Shutter		= 0,
	c4742_95_kUNIT_USec,
	c4742_95_kUNIT_MSec,
	c4742_95_kUNIT_Sec,
	c4742_95_kUNIT_Min,

	c4742_95_kSPX_2				= 0,
	c4742_95_kSPX_4,
	c4742_95_kSPX_8,

	c4742_95_kSFD_On			= 0,
	c4742_95_kSFD_Off,

	c4742_95_kESC_BNC			= 0,
	c4742_95_kESC_DSUB,
	c4742_95_kESC_IF,
};
*/

#endif // _INCLUDE_PARAMC4742_95_H_
