package dcamapi;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Array;
import org.bridj.ann.CLong;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * <i>native declaration : lib/dcam/inc/dcamapi3.h</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("dcamapi") 
public class DCAM_PARAM_FEATURE_INQ extends StructObject {
	public DCAM_PARAM_FEATURE_INQ() {
		super();
	}
	/**
	 * id == DCAM_IDPARAM_FEATURE_INQ<br>
	 * C type : DCAM_HDR_PARAM
	 */
	@Field(0) 
	public DCAM_HDR_PARAM hdr() {
		return this.io.getNativeObjectField(this, 0);
	}
	/**
	 * id == DCAM_IDPARAM_FEATURE_INQ<br>
	 * C type : DCAM_HDR_PARAM
	 */
	@Field(0) 
	public DCAM_PARAM_FEATURE_INQ hdr(DCAM_HDR_PARAM hdr) {
		this.io.setNativeObjectField(this, 0, hdr);
		return this;
	}
	/**
	 * [in]<br>
	 * C type : _DWORD
	 */
	@CLong 
	@Field(1) 
	public long featureid() {
		return this.io.getCLongField(this, 1);
	}
	/**
	 * [in]<br>
	 * C type : _DWORD
	 */
	@CLong 
	@Field(1) 
	public DCAM_PARAM_FEATURE_INQ featureid(long featureid) {
		this.io.setCLongField(this, 1, featureid);
		return this;
	}
	/**
	 * [out]<br>
	 * C type : _DWORD
	 */
	@CLong 
	@Field(2) 
	public long capflags() {
		return this.io.getCLongField(this, 2);
	}
	/**
	 * [out]<br>
	 * C type : _DWORD
	 */
	@CLong 
	@Field(2) 
	public DCAM_PARAM_FEATURE_INQ capflags(long capflags) {
		this.io.setCLongField(this, 2, capflags);
		return this;
	}
	/// [out]
	@Field(3) 
	public float min() {
		return this.io.getFloatField(this, 3);
	}
	/// [out]
	@Field(3) 
	public DCAM_PARAM_FEATURE_INQ min(float min) {
		this.io.setFloatField(this, 3, min);
		return this;
	}
	/// [out]
	@Field(4) 
	public float max() {
		return this.io.getFloatField(this, 4);
	}
	/// [out]
	@Field(4) 
	public DCAM_PARAM_FEATURE_INQ max(float max) {
		this.io.setFloatField(this, 4, max);
		return this;
	}
	/// [out]
	@Field(5) 
	public float step() {
		return this.io.getFloatField(this, 5);
	}
	/// [out]
	@Field(5) 
	public DCAM_PARAM_FEATURE_INQ step(float step) {
		this.io.setFloatField(this, 5, step);
		return this;
	}
	/// [out]
	@Field(6) 
	public float defaultvalue() {
		return this.io.getFloatField(this, 6);
	}
	/// [out]
	@Field(6) 
	public DCAM_PARAM_FEATURE_INQ defaultvalue(float defaultvalue) {
		this.io.setFloatField(this, 6, defaultvalue);
		return this;
	}
	/**
	 * [out]<br>
	 * C type : char[16]
	 */
	@Array({16}) 
	@Field(7) 
	public Pointer<Byte > units() {
		return this.io.getPointerField(this, 7);
	}
	public DCAM_PARAM_FEATURE_INQ(Pointer pointer) {
		super(pointer);
	}
}
