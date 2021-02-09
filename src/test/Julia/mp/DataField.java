
package test.Julia.mp;

public class DataField{ // implements JifObject[L] {
	private int timestamp;
	private String info;
	private DataFieldAttribute attrib;
	private String playerName;
	
	public DataField (String playerName, int ts, String inf, DataFieldAttribute attr) {
		this.playerName = playerName;
		this.timestamp 	= ts;
		this.info 		= inf;
		this.attrib 	= attr;
	}

	public byte[] toByteArray() {
		try {	
                byte[] a = Integer.toString(this.timestamp).getBytes();
				byte[] b = info.getBytes();
				byte[] c = attrib.toByteArray();
				int n = a.length + b.length + c.length;
				byte[] r = new byte[n];
				int i = 0 ;
				for (int j = 0; j < a.length; j++, i++)
					r[i] = a[j];
				for (int j = 0; j < b.length; j++, i++) 
					r[i] = b[j];
				for (int j = 0; j < c.length; j++, i++) 
					r[i] = c[j];
				return r;
		} catch (NullPointerException ignored) {
		} catch (ArrayIndexOutOfBoundsException ignored) {
		} 
		return null;
	}

     public DataFieldAttribute getAttrib() {
		return attrib;
	}

	public String getInfo() {
		return info;
	}

	public String getPlayerName() {
		return playerName;
	}

     public int getTimestamp() {
		return timestamp;
	}
    
    public String toString() {
        return "mp.DataField";
    }
    
    public boolean equals(IDComparable o) {
        if (o == null)
            return false;
        if (!(o instanceof DataField))
            return false;
        try {
            DataField that = (DataField)o;
            return ((this.timestamp == that.timestamp) &&
                (this.info.equals(that.info)) && 
                (this.playerName.equals(that.playerName)) &&
                (this.attrib.equals(that.attrib)) );
        } catch (ClassCastException impossible) {
        } catch (NullPointerException ignored) {
        }
        return false;
    }
    
    public int hashCode() {
        try {
            int h = this.timestamp;
            h += this.info == null? 0 : 31*h*this.info.hashCode();
            h += this.playerName == null ? 0: 31*h*playerName.hashCode();
            h += h*this.attrib.hashCode();
            return h;
        } catch (NullPointerException ignored) {
        }
        return 0;
    }
}

