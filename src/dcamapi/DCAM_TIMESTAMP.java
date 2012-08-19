package dcamapi;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.CLong;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * <i>native declaration : lib/dcam/inc/dcamapi.h:379</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("dcamapi") 
public class DCAM_TIMESTAMP extends StructObject {
	public DCAM_TIMESTAMP() {
		super();
	}
	/// [out]
	@CLong 
	@Field(0) 
	public long sec() {
		return this.io.getCLongField(this, 0);
	}
	/// [out]
	@CLong 
	@Field(0) 
	public DCAM_TIMESTAMP sec(long sec) {
		this.io.setCLongField(this, 0, sec);
		return this;
	}
	/// [out]
	@CLong 
	@Field(1) 
	public long microsec() {
		return this.io.getCLongField(this, 1);
	}
	/// [out]
	@CLong 
	@Field(1) 
	public DCAM_TIMESTAMP microsec(long microsec) {
		this.io.setCLongField(this, 1, microsec);
		return this;
	}
	public DCAM_TIMESTAMP(Pointer pointer) {
		super(pointer);
	}
}
