
package test.Julia.mp;
import java.io.PrintStream;

public class SerializeWriteHelper {
    private final PrintStream stream;
    public SerializeWriteHelper(PrintStream stream) throws IllegalArgumentException{
        if (stream == null)
            throw new IllegalArgumentException("stream is null");
        this.stream = stream;
    }
    
    public void writeBigInt(DABigInteger x) throws SecurityException{
        PrintStream stream = this.stream;
        if (stream == null) return;
        stream.print("###BIGINT\t");
        stream.println(x==null? "NULL" : x.toString());
    }
    

    public void writeBigIntPair(BigIntPair x) throws SecurityException{
        PrintStream stream = this.stream;
        if (stream == null) return;
        stream.print("###BIGINTPAIR\t");
        if (x == null) {
            stream.println("NULL");
            return;
        } else {
            stream.println("2");
        }
        writeBigInt(x.getX());
        writeBigInt(x.getY());
    }
    
    
    public void writePHInt(PHInteger x) throws SecurityException {
        PrintStream stream = this.stream;
        if (stream  == null) return;
        stream.print("###PHINT\t");
        if (x == null) {
            stream.println("NULL");
            return;
        }
        int t = x.size();
        stream.println(t);
        try {
            for (int i = 0; i < t; i++)
                writeBigIntPair(x.getBigIntPair(i));
        } catch (IndexOutOfBoundsException ignored) {}
    }
    
    public void writePHIntVector(PHIntVector x) throws SecurityException {
        PrintStream stream = this.stream;
        if (stream  == null) return;
        stream.print("###PHINTVECTOR\t");
        if (x == null) {
            stream.println("NULL");
            return;
        }
        int t = x.size();
        stream.println(t);
        try {
            for (int i = 0; i < t; i++)
                writePHInt(x.getPHI(i));
        } catch (IndexOutOfBoundsException ignored) {}
    }
    
    
    public void writeBigIntVector(BigIntVector x) throws SecurityException {
        PrintStream stream = this.stream;
        if (stream  == null) return;
        stream.print("###BIGINTVECTOR\t");
        if (x == null) {
            stream.println("NULL");
            return;
        }
        int t = x.size();
        stream.println(t);
        try {
            for (int i = 0; i < t; i++)
                writeBigInt(x.getBI(i));
        } catch (IndexOutOfBoundsException ignored) {}
    }
    
    public void writeCardVector(CardVector x) throws SecurityException {
        PrintStream stream = this.stream;
        if (stream  == null) return;
        stream.print("###CARDVECTOR\t");
        if (x == null) {
            stream.println("NULL");
            return;
        }
        stream.println("VALUE");
        writeBigIntVector(x.getValueVector());        
    }
    
    
    public void writeEncCardVector(EncryptedCardVector x) throws SecurityException {
        PrintStream stream = this.stream;
        if (stream  == null) return;
        stream.print("###ENCCARDVECTOR\t");
        if (x == null) {
            stream.println("NULL");
            return;
        }
        stream.println("VALUE");
        writePHIntVector(x.getValueVector());        
    }
    
    public void writeByteArray(byte[] x) throws SecurityException {
        PrintStream stream = this.stream;
        if (stream  == null) return;
        stream.print("###BYTEARRAY\t");
        if (x == null) {
            stream.println("NULL");
            return;
        }
        int t = x.length;
        stream.println(t);
        stream.print("###BYTES\t");
        try {
            for (int i = 0; i < t; i++) { 
                 stream.print(x[i]);
                 stream.print(" ");
            }                
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        stream.println();
    }
    
    public void writeDigest(Digest x) throws SecurityException{
        PrintStream stream = this.stream;
        if (stream  == null) return;
        stream.print("###DIGEST\t");
        if (x == null) {
            stream.println("NULL");
            return;
        }
        stream.println("VALUE");
        writeByteArray(x.toByteArray());
    }
    
    
        
    public void writeAttribute(DataFieldAttribute attrib) throws SecurityException {       
        try {
            if (attrib instanceof EncryptedCardVector) {
                 writeEncCardVector((EncryptedCardVector) attrib);
            } else if (attrib instanceof CardVector ) {
                 writeCardVector((CardVector) attrib);
            } else if (attrib instanceof BigIntVector) {
                 writeBigIntVector((BigIntVector) attrib);
            } else if (attrib instanceof PHIntVector) {
                 writePHIntVector((PHIntVector) attrib);
            } else if (attrib instanceof PHInteger) {
                 writePHInt((PHInteger) attrib);
            } else if (attrib instanceof DABigInteger) {
                 writeBigInt((DABigInteger) attrib);
            } else if (attrib instanceof Digest) {
                 writeDigest((Digest) attrib);
            } else if (attrib instanceof DAVector) {
                writeDAVector((DAVector) attrib);
            }
        } catch (ClassCastException ignored) {}                 
    }
    
    public void writeDAVector(DAVector x) throws SecurityException{
        PrintStream stream = this.stream;
        if (stream  == null) return;
        stream.print("###DAVECTOR\t");
        if (x == null) {
            stream.println("NULL");
            return;
        }
        int t = x.size();
        stream.println(t);
        try {
            for (int i = 0; i < t; i++)
                writeAttribute((DataFieldAttribute)x.get(i));
        } catch (ClassCastException e) {
        } catch (IndexOutOfBoundsException e) {
        }
    }
    public void writeDNCLink(DNCLink link) throws SecurityException{
        PrintStream stream = this.stream;
        if (stream  == null) return;
        stream.print("###DNCLINK\t");
        if (link == null) {
            stream.println("NULL");
            return;
        }
        stream.println(link.getk());
        writeDataField(link.getData());
        stream.println("###CHAINVALUE\t");
        writeByteArray(link.getChainingValue());
    }
    
    
    public void writeDataField(DataField data) throws SecurityException{
        PrintStream stream = this.stream;
        if (stream  == null) return;
        stream.print("###DATAFIELD\t");
        if (data == null) {
            stream.println("NULL");
            return;
        }
        stream.print(data.getPlayerName());
        stream.print(" ");
        stream.print(data.getTimestamp());
        stream.print(" ");
        stream.println(data.getInfo());
        writeAttribute(data.getAttrib());
    }
    
    public void writeMatrix(PermutationMatrix matrix) throws SecurityException{
        PrintStream stream = this.stream;
        if (stream == null) return;
        stream.print("###MATRIX\t");
        if (matrix == null) {
            stream.println("NULL");
            return;
        }
        java.util.ArrayList m = matrix.getMatrix();
        if (m == null) {
            stream.println("NULL");
            return;
        }
        int t = m.size();
        stream.println(t);
        for (int i = 0; i <t ;i++) {
            try {
                BigIntVector rowi = (BigIntVector)m.get(i);
                writeBigIntVector(rowi);
            } catch (IndexOutOfBoundsException ignored){
            } catch (ClassCastException ignored) {
            }
        }
    }
}

