
package test.Julia.mp;

public class EncryptedCardVector implements DataFieldAttribute{
	private PHIntVector value;

	public EncryptedCardVector(PHIntVector  v) 
    throws IllegalArgumentException{
        if (v == null) 
            throw new IllegalArgumentException();  
		this.value = v;
	}

	public PHIntVector getValueVector() {
		return this.value;
	}

     public byte[] toByteArray() {
        PHIntVector value = this.value;        
        return value == null ? null: value.toByteArray();
	}

     public int byteLength() {
        PHIntVector value = this.value;        
        return value == null ? 0: value.byteLength();
	}
	
	public EncryptedCardVector multMatrix(PHEPermutationMatrix pi)
    throws IllegalArgumentException, MPException {
            if (pi == null)
                throw new IllegalArgumentException();
        try {    
            int t = this.value.size();
            PHIntVector w = new PHIntVector();       
            w.ensureCapacity(t);
            for (int i = 0; i < t; i++) {
                PHIntVector rowi = (PHIntVector)pi.getMatrix().get(i);
                PHInteger coli = (PHInteger)this.value.get(i);
                if (i == 0)
                    for (int j = 0; j < t; j++) {
                        PHInteger p_ij = (PHInteger)rowi.get(j);
                        w.add(p_ij.mult(coli));
                    }
                else 
                    for (int j = 0; j < t; j++) {
                        PHInteger p_ij = (PHInteger)rowi.get(j);
                        PHInteger w_j = (PHInteger) w.get(j);
                        w.set(j, w_j.add(p_ij.mult(coli)));
                    }			
            }
            return new EncryptedCardVector(w);
        } catch (NullPointerException ignored) { throw new MPException("NPE");
        } catch (IndexOutOfBoundsException ignored) { throw new MPException("IOB");
        } catch (ClassCastException ignored) { throw new MPException("CCE");
        }
	}
	
	
	public String toString() {
            PHIntVector value = this.value;
            return value == null ? "": value.toString();
	}

    public boolean equals (IDComparable o) {
        if (o == null)
            return false;
        if (!(o instanceof EncryptedCardVector))
            return false;
        try {
            EncryptedCardVector that = (EncryptedCardVector) o;
            PHIntVector thisvalue = this.value;
            PHIntVector thatvalue = that.value;
            if (thisvalue == null || thatvalue == null)
                return false;
            return thisvalue.equals(thatvalue);
        } catch (ClassCastException e) {
        }
        return false;
    }
    
    
    public int hashCode() {
        PHIntVector value = this.value;        
        return value == null ? 0: value.hashCode();
    }
}
