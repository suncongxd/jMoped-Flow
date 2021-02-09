package jmoped;

import org.gjt.jclasslib.structures.attributes.LocalVariableTableEntry;

public class PDSLocalVar {
	
	LocalVariableTableEntry lvtEntry;
	String name;
	private int bits;			// zero means that it has the default value.
	private boolean external;	// true, if the variable is externally added.
	
	public PDSLocalVar(String name) {
		
		this(name, 0);
	}
	
	public PDSLocalVar(String name, int bits) {
		
		this(null, name, bits, false);
	}
	
	public PDSLocalVar(String name, int bits, boolean external) {
		
		this(null, name, bits, external);
	}
	
	public PDSLocalVar(LocalVariableTableEntry lvtEntry, String name, int bits) {
		
		this(lvtEntry, name, bits, false);
	}
	
	public PDSLocalVar(LocalVariableTableEntry lvtEntry, String name, int bits, 
			boolean external) {
		
		this.lvtEntry = lvtEntry;
		this.name = name;
		this.bits = bits;
		this.external = external;
	}
	
	public void setLocalVariableTableEntry(LocalVariableTableEntry lvtEntry) {
		
		this.lvtEntry = lvtEntry;
	}
	
	public void setName (String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return name;
	}
	
	public void setBits(int bits) {
		
		this.bits = bits;
	}
	
	public int getBits() {
		
		return bits;
	}
	
	public int getDescriptorIndex() {
		
		return lvtEntry.getDescriptorIndex();
	}
	
	public int getIndex() {
		
		return lvtEntry.getIndex();
	}
	
	public int getLength() {
		
		return lvtEntry.getLength();
	}
	
	public int getStartPc() {
		
		return lvtEntry.getStartPc();
	}
	
	public boolean isExternal() {
		
		return external;
	}
	
	public void setExternal() {
		
		external = true;
	}
}
