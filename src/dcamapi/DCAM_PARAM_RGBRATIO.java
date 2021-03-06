package dcamapi;

import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;

/**
 * <i>native declaration : lib/dcam/inc/dcamapi3.h</i><br>
 * This file was autogenerated by
 * <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that
 * <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a
 * few opensource projects.</a>.<br>
 * For help, please visit
 * <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or
 * <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("dcamapi")
public class DCAM_PARAM_RGBRATIO extends StructObject
{
  public DCAM_PARAM_RGBRATIO()
  {
    super();
  }

  /**
   * id == DCAM_IDPARAM_RGBRATIO<br>
   * C type : DCAM_HDR_PARAM
   */
  @Field(0)
  public DCAM_HDR_PARAM hdr()
  {
    return this.io.getNativeObjectField(this, 0);
  }

  /**
   * id == DCAM_IDPARAM_RGBRATIO<br>
   * C type : DCAM_HDR_PARAM
   */
  @Field(0)
  public DCAM_PARAM_RGBRATIO hdr(final DCAM_HDR_PARAM hdr)
  {
    this.io.setNativeObjectField(this, 0, hdr);
    return this;
  }

  // / C type : dcam_rgbratio
  @Field(1)
  public dcam_rgbratio exposure()
  {
    return this.io.getNativeObjectField(this, 1);
  }

  // / C type : dcam_rgbratio
  @Field(1)
  public DCAM_PARAM_RGBRATIO exposure(final dcam_rgbratio exposure)
  {
    this.io.setNativeObjectField(this, 1, exposure);
    return this;
  }

  // / C type : dcam_rgbratio
  @Field(2)
  public dcam_rgbratio gain()
  {
    return this.io.getNativeObjectField(this, 2);
  }

  // / C type : dcam_rgbratio
  @Field(2)
  public DCAM_PARAM_RGBRATIO gain(final dcam_rgbratio gain)
  {
    this.io.setNativeObjectField(this, 2, gain);
    return this;
  }

  public DCAM_PARAM_RGBRATIO(final Pointer pointer)
  {
    super(pointer);
  }
}
