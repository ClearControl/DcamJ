package dcamapi;

import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.CLong;
import org.bridj.ann.Field;
import org.bridj.ann.Library;

/**
 * <i>native declaration : lib\dcam\inc\dcamapi.h:361</i><br>
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
public class DCAMDEV_STRING extends StructObject
{
  public DCAMDEV_STRING()
  {
    super();
  }

  // / [in]
  @CLong
  @Field(0)
  public long size()
  {
    return this.io.getCLongField(this, 0);
  }

  // / [in]
  @CLong
  @Field(0)
  public DCAMDEV_STRING size(final long size)
  {
    this.io.setCLongField(this, 0, size);
    return this;
  }

  // / [in]
  @CLong
  @Field(1)
  public long iString()
  {
    return this.io.getCLongField(this, 1);
  }

  // / [in]
  @CLong
  @Field(1)
  public DCAMDEV_STRING iString(final long iString)
  {
    this.io.setCLongField(this, 1, iString);
    return this;
  }

  /**
   * [in,obuf]<br>
   * C type : char*
   */
  @Field(2)
  public Pointer<Byte> text()
  {
    return this.io.getPointerField(this, 2);
  }

  /**
   * [in,obuf]<br>
   * C type : char*
   */
  @Field(2)
  public DCAMDEV_STRING text(final Pointer<Byte> text)
  {
    this.io.setPointerField(this, 2, text);
    return this;
  }

  // / [in]
  @CLong
  @Field(3)
  public long textbytes()
  {
    return this.io.getCLongField(this, 3);
  }

  // / [in]
  @CLong
  @Field(3)
  public DCAMDEV_STRING textbytes(final long textbytes)
  {
    this.io.setCLongField(this, 3, textbytes);
    return this;
  }

  public DCAMDEV_STRING(final Pointer pointer)
  {
    super(pointer);
  }
}
