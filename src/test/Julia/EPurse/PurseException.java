package test.Julia.EPurse;

public class PurseException extends Exception
{
    public static byte ADMINISTRATIVE_MODE_ERR=1;
    
    public PurseException()
    {
        super("Purse Error");
    }
    
    public PurseException(String reason)
    {
        super(reason);
    }
    public static void throwIt(byte b){
    }
}