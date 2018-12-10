package cn.lanyj.cehelper;

import org.bytedeco.javacpp.Pointer;

public class CEAddressPointer extends Pointer {

	public CEAddressPointer(long address) {
		this.address = address;
	}
	
	public CEAddressPointer address(long address) {
		this.address = address;
		return this;
	}
	
}
