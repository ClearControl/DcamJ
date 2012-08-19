package dcamapi;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.CLong;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * <i>native declaration : lib/dcam/inc/dcamapi.h:434</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("dcamapi") 
public class DCAMREC_OPENA extends StructObject {
	public DCAMREC_OPENA() {
		super();
	}
	/// [in]
	@CLong 
	@Field(0) 
	public long size() {
		return this.io.getCLongField(this, 0);
	}
	/// [in]
	@CLong 
	@Field(0) 
	public DCAMREC_OPENA size(long size) {
		this.io.setCLongField(this, 0, size);
		return this;
	}
	/// [in]
	@CLong 
	@Field(1) 
	public long reserved() {
		return this.io.getCLongField(this, 1);
	}
	/// [in]
	@CLong 
	@Field(1) 
	public DCAMREC_OPENA reserved(long reserved) {
		this.io.setCLongField(this, 1, reserved);
		return this;
	}
	/**
	 * [out]<br>
	 * C type : HDCAMREC
	 */
	@Field(2) 
	public Pointer<HDCAMREC_struct > hrec() {
		return this.io.getPointerField(this, 2);
	}
	/**
	 * [out]<br>
	 * C type : HDCAMREC
	 */
	@Field(2) 
	public DCAMREC_OPENA hrec(Pointer<HDCAMREC_struct > hrec) {
		this.io.setPointerField(this, 2, hrec);
		return this;
	}
	/**
	 * [in]<br>
	 * C type : const char*
	 */
	@Field(3) 
	public Pointer<Byte > path() {
		return this.io.getPointerField(this, 3);
	}
	/**
	 * [in]<br>
	 * C type : const char*
	 */
	@Field(3) 
	public DCAMREC_OPENA path(Pointer<Byte > path) {
		this.io.setPointerField(this, 3, path);
		return this;
	}
	/**
	 * [in]<br>
	 * C type : const char*
	 */
	@Field(4) 
	public Pointer<Byte > ext() {
		return this.io.getPointerField(this, 4);
	}
	/**
	 * [in]<br>
	 * C type : const char*
	 */
	@Field(4) 
	public DCAMREC_OPENA ext(Pointer<Byte > ext) {
		this.io.setPointerField(this, 4, ext);
		return this;
	}
	/// [in]
	@CLong 
	@Field(5) 
	public long maxframepersession() {
		return this.io.getCLongField(this, 5);
	}
	/// [in]
	@CLong 
	@Field(5) 
	public DCAMREC_OPENA maxframepersession(long maxframepersession) {
		this.io.setCLongField(this, 5, maxframepersession);
		return this;
	}
	/// [in]
	@CLong 
	@Field(6) 
	public long userdatasize() {
		return this.io.getCLongField(this, 6);
	}
	/// [in]
	@CLong 
	@Field(6) 
	public DCAMREC_OPENA userdatasize(long userdatasize) {
		this.io.setCLongField(this, 6, userdatasize);
		return this;
	}
	/// [in]
	@CLong 
	@Field(7) 
	public long userdatasizeSession() {
		return this.io.getCLongField(this, 7);
	}
	/// [in]
	@CLong 
	@Field(7) 
	public DCAMREC_OPENA userdatasizeSession(long userdatasizeSession) {
		this.io.setCLongField(this, 7, userdatasizeSession);
		return this;
	}
	/// [in]
	@CLong 
	@Field(8) 
	public long userdatasizeFile() {
		return this.io.getCLongField(this, 8);
	}
	/// [in]
	@CLong 
	@Field(8) 
	public DCAMREC_OPENA userdatasizeFile(long userdatasizeFile) {
		this.io.setCLongField(this, 8, userdatasizeFile);
		return this;
	}
	/// [in]
	@CLong 
	@Field(9) 
	public long usertextsize() {
		return this.io.getCLongField(this, 9);
	}
	/// [in]
	@CLong 
	@Field(9) 
	public DCAMREC_OPENA usertextsize(long usertextsize) {
		this.io.setCLongField(this, 9, usertextsize);
		return this;
	}
	/// [in]
	@CLong 
	@Field(10) 
	public long usertextsizeSession() {
		return this.io.getCLongField(this, 10);
	}
	/// [in]
	@CLong 
	@Field(10) 
	public DCAMREC_OPENA usertextsizeSession(long usertextsizeSession) {
		this.io.setCLongField(this, 10, usertextsizeSession);
		return this;
	}
	/// [in]
	@CLong 
	@Field(11) 
	public long usertextsizeFile() {
		return this.io.getCLongField(this, 11);
	}
	/// [in]
	@CLong 
	@Field(11) 
	public DCAMREC_OPENA usertextsizeFile(long usertextsizeFile) {
		this.io.setCLongField(this, 11, usertextsizeFile);
		return this;
	}
	public DCAMREC_OPENA(Pointer pointer) {
		super(pointer);
	}
}
