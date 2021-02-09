package test.Julia.EPurse ;

public class OPSystem{
    public static final byte APPLET_SELECTABLE = (byte) 0 ;
    public static final byte APPLET_PERSONALIZED = (byte) 1 ;
    public static final byte APPLET_BLOCKED = (byte) 2 ;

    public static byte getCardContentState(){
        return (byte)0;
    }
    public static boolean setCardContentState(byte state){
        return true;
    }
    public static boolean verifyPin  (javacard.framework.APDU apdu , byte pin){
        return false;
    }
    public static byte getTriesRemaining(){
        return (byte)0;
    }
}

