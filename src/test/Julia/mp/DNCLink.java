
package test.Julia.mp;

import java.security.*;

public class DNCLink{// implements JifObject[L]{
    private byte[] chainingValue; //chaining value
    private DataField data;
    private int k;
    public byte[] getChainingValue() {
        return this.chainingValue;
    }
    public DataField getData(){
        return data;
    }
    public int getk() {
        return this.k;
    }
    public DNCLink(int k, DataField data, byte[] chainingValue) {
        this.k = k;
        this.data = data;
        this.chainingValue = chainingValue;
    }
    
    public String toString() {
        return "";
    }
	
    public boolean equals(IDComparable o) {
            return false;
    }

    public int hashCode() {
            return 0;
    }
	
}
