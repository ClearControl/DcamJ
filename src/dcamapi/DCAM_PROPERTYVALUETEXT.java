package dcamapi;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.CLong;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * <i>native declaration : lib\dcam\inc\dcamprop.h</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("dcamapi") 
public class DCAM_PROPERTYVALUETEXT extends StructObject {
	public DCAM_PROPERTYVALUETEXT() {
		super();
	}
	/// [in] of this structure
	@CLong 
	@Field(0) 
	public long cbSize() {
		return this.io.getCLongField(this, 0);
	}
	/// [in] of this structure
	@CLong 
	@Field(0) 
	public DCAM_PROPERTYVALUETEXT cbSize(long cbSize) {
		this.io.setCLongField(this, 0, cbSize);
		return this;
	}
	/// [in] DCAMIDPROP
	@CLong 
	@Field(1) 
	public long iProp() {
		return this.io.getCLongField(this, 1);
	}
	/// [in] DCAMIDPROP
	@CLong 
	@Field(1) 
	public DCAM_PROPERTYVALUETEXT iProp(long iProp) {
		this.io.setCLongField(this, 1, iProp);
		return this;
	}
	/// [in] value of property
	@Field(2) 
	public double value() {
		return this.io.getDoubleField(this, 2);
	}
	/// [in] value of property
	@Field(2) 
	public DCAM_PROPERTYVALUETEXT value(double value) {
		this.io.setDoubleField(this, 2, value);
		return this;
	}
	/**
	 * [in,obuf] text of the value<br>
	 * C type : char*
	 */
	@Field(3) 
	public Pointer<Byte > text() {
		return this.io.getPointerField(this, 3);
	}
	/**
	 * [in,obuf] text of the value<br>
	 * C type : char*
	 */
	@Field(3) 
	public DCAM_PROPERTYVALUETEXT text(Pointer<Byte > text) {
		this.io.setPointerField(this, 3, text);
		return this;
	}
	/// [in] text buf size
	@CLong 
	@Field(4) 
	public long textbytes() {
		return this.io.getCLongField(this, 4);
	}
	/// [in] text buf size
	@CLong 
	@Field(4) 
	public DCAM_PROPERTYVALUETEXT textbytes(long textbytes) {
		this.io.setCLongField(this, 4, textbytes);
		return this;
	}
	public DCAM_PROPERTYVALUETEXT(Pointer pointer) {
		super(pointer);
	}
}
