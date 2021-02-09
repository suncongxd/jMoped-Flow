
package test.slicing.mp;

public class DAVector extends java.util.ArrayList{// implements DataFieldAttribute {
	public DAVector() {
		super();
	}
/*
	public byte[] toByteArray() {
		int n = this.size();
		int size = this.byteLength();
		byte[] retval = new byte[size];
		int j = 0;
//		try {
				for (int i = 0; i < n; i++) {
					byte[] v_i = ((DataFieldAttribute) this.get(i)).toByteArray();
					System.arraycopy(v_i, 0, retval, j, v_i.length);
					j += v_i.length;
				}
				return retval;
//		} catch (IndexOutOfBoundsException e) {
//		} catch (ClassCastException e) {
//		} catch (NullPointerException e) {
//		} catch (ArrayStoreException e) {}
//		return null;
	}
	

	public int byteLength() {
		int n = this.size();
		int byteLength = 0;
//		try {
				for (int i = 0; i < n; i++ ) {
					byteLength += ((DataFieldAttribute)this.get(i)).byteLength();
				}
//		} catch (IndexOutOfBoundsException e) {
//		} catch (ClassCastException e) {
//		} catch (NullPointerException e) {}
		return byteLength;
	}
	
	public void permute() {
		int t = this.size();
//		try {
				for (int i = 0; i < 100; i++) {
					int u = (int) java.lang.Math.round((t - 1) * java.lang.Math.random());
					int v = (int) java.lang.Math.round((t - 1) * java.lang.Math.random());
					Object a =  this.get(u);
					this.set(u, this.get(v));
					this.set(v, a);
				}
//		} catch (IndexOutOfBoundsException e) {}
	}*/
}
