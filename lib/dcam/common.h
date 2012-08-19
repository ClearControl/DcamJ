// console/misc/common.h
//

void dcamcon_show_dcamerr( DCAMERR err, const char* apiname, const char* fmt=0, ...  );

HDCAM dcamcon_init_open();
void dcamcon_cout_dcamdev_info( HDCAM hdcam );
