package test.slicing.mp;

//import jif.util.ArrayList;
import java.util.ArrayList;

public class PHIntVector extends java.util.ArrayList{ // implements DataFieldAttribute {
	public PHIntVector() {
		super();
	}
	
	public String toString() {
//        try {
            int n = this.size();
            String s = "{";
            for (int i = 0; i < n; i++ ){
                PHInteger ph_i = this.getPHI(i);
                s+=ph_i.toString();
                if (i < n- 1) 
                    s+= ",";
            }
            s += "}";
            return s;
//        } catch (NullPointerException ignored) {
//        } catch (IndexOutOfBoundsException ignored ) { }
//        return "";
	}
/*
     public byte[] toByteArray() {
		// TODO this implementation is not optimized
		// should be better to avoid redundant array copying
//        try {
            int size = this.byteLength();
            int n = this.size();
            byte [] retval = new byte[size];
            int i = 0;
            for (int j = 0; j < n; j++ ) {
                byte[] ph_j = ((PHInteger) this.get(j)).toByteArray();
                System.arraycopy(ph_j, 0, retval, i, ph_j.length);
                i += ph_j.length;
            }
            return retval;
//        } catch (NullPointerException ignored) {
//        } catch (IndexOutOfBoundsException ignored) {
//        } catch (ClassCastException ignored) {
//        } catch (ArrayStoreException ignored){ }
//        return null;
	}
*/
/*
    public int byteLength() {
//        try {
            int size = 0;
            int n = this.size();
            for (int j = 0; j < n; j++) {
                PHInteger v_j = this.getPHI(j);
                size += v_j.byteLength();
            }
            return size;
//        } catch (NullPointerException ignored) {
//        } catch (IndexOutOfBoundsException ignored) {}
//        return 0;
	}
*/
	public PHInteger getPHI (int i){ // throws (IndexOutOfBoundsException) {
//        try {
            return (PHInteger) this.get(i);
//        } catch (ClassCastException ignored) {
        //} catch (NullPointerException ignored) { }
 //       return null;
	}
}

