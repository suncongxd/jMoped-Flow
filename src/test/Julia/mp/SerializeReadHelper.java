
package test.Julia.mp;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;


public class SerializeReadHelper{
    private final BufferedReader reader;

    public SerializeReadHelper( BufferedReader reader) throws IllegalArgumentException {     
        this.reader = reader;
        if (reader == null)
           throw new IllegalArgumentException("reader is null");
    }

    private String line = null;
    private int pos = 0;

    private String readString() throws MPException, IOException{
        String line = this.line;
        if (line != null) line = line.trim();        
        BufferedReader reader = this.reader;
        if (reader == null) 
            throw new MPException("readString: reader is null");
        if (line == null || line.length() == 0) {
            line = reader.readLine();
            pos = 0;
            if (line == null)
                return null;
            else 
                line = line.trim();
        }
        try {
            int j = pos;
            while (j < line.length() && !Character.isWhitespace(line.charAt(j))) {
                j++;
            }
            String v  = line.substring(pos, j);
            this.line = line.substring(j).trim();
            return v;
        } catch (NullPointerException e) {
            throw new MPException("readString: NPE");
        } catch (StringIndexOutOfBoundsException e) {
            throw new MPException("readString: IOB");
        }
    }
    
    private int readInt() throws MPException, NumberFormatException, IOException{
        String v = readString();
        return Integer.parseInt(v);                           
    }
    
    private byte readByte() throws MPException, NumberFormatException, IOException {
        int ival = readInt();
        return (byte) ival;
    }
    
    public DABigInteger readBigInt() throws MPException, IOException {
        String type = readString();
        if (type == null)
            throw new MPException("readBigInt: null type input");
        if (0 != type.compareTo("###BIGINT"))
             throw new MPException("readBigInt: invalid format"); 
        String value = readString();
        if (value == null)
            throw new MPException("readBigInt: null value input");        
        if (value.compareTo("NULL") == 0)
             return null;
        try {
            DABigInteger x = new DABigInteger(value);
            return x;
        } catch (NumberFormatException e) {
            throw new MPException("readBigInt: NFE");
        }        
    }

    public BigIntPair readBigIntPair() throws MPException, IOException{
        String type = readString();
        if (type == null)
            throw new MPException("readBigIntPair: null type input");
        if (0 != type.compareTo("###BIGINTPAIR"))
            throw new MPException("readBigIntPair: invalid format");
        String value = readString();
        if (value == null)
            throw new MPException("readBigIntPair: null value input"); 
        if (value.compareTo("NULL") == 0)
            return null;
        DABigInteger x = readBigInt();
        DABigInteger y = readBigInt();
        return new BigIntPair(x,y);
    }
        
    public PHInteger readPHInt() throws MPException, IOException{
        String type = readString();
        if (type == null)
            throw new MPException("readPHInt: null type input");
        if (0 != type.compareTo("###PHINT"))
           throw new MPException("readPHInt: invalid format");
        ArrayList y = new ArrayList();
        try {
            int t = readInt();
            y.ensureCapacity(t);
            for (int i = 0; i < t; i++)
                y.add (readBigIntPair());
        } catch (NumberFormatException e) {
            return null;
        }
        return new PHInteger(y);
    }

    public PHIntVector readPHIntVector() throws MPException, IOException{
        String type = readString();
        if (type == null)
            throw new MPException("readPHIntVector: null type input");
        if (0 != type.compareTo("###PHINTVECTOR"))
            throw new MPException("readPHIntVector: invalid format");
        PHIntVector y = new PHIntVector();
        try {
            int t = readInt();
            y.ensureCapacity(t);
            for (int i = 0; i < t; i++)
                y.add(readPHInt());
        } catch (NumberFormatException e) {
            return null;
        }            
        return y;
    }

    public BigIntVector readBigIntVector() throws MPException, IOException{
        String type = readString();
         if (type == null)
            throw new MPException("readBigIntVector: null type input");
        if (0 != type.compareTo("###BIGINTVECTOR") )
            throw new MPException("readBigIntVector: invalid format");
        BigIntVector y = new BigIntVector();
        try {
            int t = readInt();
            y.ensureCapacity(t);
            for (int i = 0; i < t; i++)
                y.add(readBigInt());
        } catch (NumberFormatException e) {
            return null;
        }
        return y;
    }

    public CardVector readCardVector() throws MPException, IOException{
        String type = readString();
        if (type == null)
            throw new MPException("readCardVector: null type input");
        if (0 != type.compareTo("###CARDVECTOR"))
            throw new MPException("readCardVector: invalid format");
        String value = readString();
         if (value == null)
            throw new MPException("readPHIntVector: null value input");
        if (value.compareTo("NULL") == 0)
            return null;
        BigIntVector biv = readBigIntVector();
        try {
            return new CardVector(biv);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    public EncryptedCardVector readEncCardVector() throws MPException, IOException{
        String type = readString();
        if (type == null)
            throw new MPException("readEncCardVector: null type input");
        if (0 != type.compareTo("###ENCCARDVECTOR"))
            throw new MPException("readEncCardVector: invalid format");
        String value = readString();
        if (value == null)
            throw new MPException("readEncCardVector; null value input");
        if (value.compareTo("NULL") == 0)
            return null;
        PHIntVector phv = readPHIntVector();
        try {
            return new EncryptedCardVector(phv);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public byte[] readByteArray() throws MPException, IOException{
        String type = readString();
        if (type == null)
            throw new MPException("readByteArray: null type input");
        if (0 != type.compareTo("###BYTEARRAY"))
            throw new MPException("readByteArray: invalid format");
        int t ;
        try {
            t = readInt();
        } catch (NumberFormatException e) {
            return null;
        }
        String value = readString();
        if (value == null)
            throw new MPException("readByteArray: null value input");
        if (0 != value.compareTo("###BYTES"))
            throw new MPException("readByteArray: invalid format " + value);
        byte[] y = new byte[t];
        try {
            for (int i = 0; i < t; i++)
                y[i] = readByte( );
        } catch (ArrayIndexOutOfBoundsException ignored) {
        } catch (NumberFormatException e) {
            throw new MPException("readByteArray: invalid format");
        }
        return y;
    }

    public Digest readDigest() throws MPException, IOException{
        String type = readString();
        if (type == null)
            throw new MPException("readDigest: null type input");
        if (0 != type.compareTo("###DIGEST"))
            throw new MPException("readDigest: invalid format");
        String value = readString();
        if (value == null)
            throw new MPException("readDigest: null value input");
        if (value.compareTo("NULL") == 0)
            return null;
        byte[] ba = readByteArray();
        return new Digest(ba);       
    }

    public DataFieldAttribute readAttribute() throws MPException, IOException {
        String type = readString();
        if (type == null)
            throw new MPException("readAttribute: null type input");
        this.line = type +" " + line;
        if (type.compareTo ( "###ENCCARDVECTOR") == 0)             
            return readEncCardVector();
        else if (type.compareTo( "###CARDVECTOR") == 0) 
            return readCardVector();
        else if (type.compareTo("###BIGINTVECTOR") == 0) 
            return readBigIntVector();
        else if (type.compareTo ("###PHINTVECTOR") == 0)
            return readPHIntVector();
        else if (type.compareTo( "###BIGINT") == 0 )
            return readBigInt();       
        else if (type.compareTo ("###DIGEST") ==0 )
            return readDigest();
        else if (type.compareTo ( "###DAVECTOR") == 0)
            return readDAVector();
        throw new MPException("readAttribute: unknown type" + type);
    }

    public DAVector readDAVector() throws MPException, IOException{
        String type = readString();
        if (type == null)
            throw new MPException("readDAVector: null type input");
        if (0 != type.compareTo("###DAVECTOR"))
            throw new MPException("readDAVector: invalid format");
        DAVector y = new DAVector();
        try {
            int t = readInt();
            y.ensureCapacity(t);
            for (int i =0 ; i < t; i++)
                y.add(readAttribute());
        } catch (NumberFormatException e) {
            return null;
        }
        return y;
    }

    
    public DNCLink readDNCLink() throws MPException, IOException {
        String type = readString();
        if (type == null)
            throw new MPException("readDNCLink: null input");
        if (type.compareTo("###DNCLINK") != 0) 
            throw new MPException("readDNCLink: unknown format " + type);
        int k;
        try {            
            k = readInt();
        } catch (NumberFormatException e) {
            return null;
        }
        DataField data = readDataField();
        String value = readString();
        if (value == null)
            throw new MPException("readDNCLink: null value input");
        if (value.compareTo( "###CHAINVALUE") != 0)
            throw new MPException("readDNCLink: invalid format");
        byte[] chval = readByteArray();
        return new DNCLink(k, data, chval);
    }
    
    
    public DataField readDataField() throws MPException, IOException {
        String type = readString();
        if (type == null)
            throw new MPException("readDataField: null type input");
        if (0 != type.compareTo("###DATAFIELD") )
            throw new MPException("readDataField: invalid type format " + type);
        String value = readString();
        if (value == null)
            throw new MPException("readDataField: null value input");
        if (value.compareTo("NULL") == 0)
            return null;
        String playerName = value;
        int tmstamp;
        try {
            tmstamp = readInt();
        } catch (NumberFormatException e) {
            throw new MPException("readDataField: invalid int format");
        }            
        String info  = readString();
        if (info == null)
            throw new MPException("readDataField: null info input");
        DataFieldAttribute attrib = readAttribute();
        return new DataField(playerName,tmstamp,info,attrib);
    }
    
    
    public PermutationMatrix readMatrix() throws MPException, IOException {
        String type = readString();
        if (type == null)
            throw new MPException("readMatrix: null type input");
        if (0 != type.compareTo("###MATRIX"))
            throw new MPException("readMatrix: invalid type format " + type);
        ArrayList y = new ArrayList();
        int t ;
        try {
            t = readInt();
        } catch (NumberFormatException e ) {
            return null;
        }
        y.ensureCapacity(t);
        for (int i = 0; i < t; i++)
            y.add(readBigIntVector());
        try {
            return new PermutationMatrix(y);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
