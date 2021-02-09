/**
 * Copyright (c) 1999 GEMPLUS group. All Rights Reserved.
 *------------------------------------------------------------------------------
 *  Project name:  PACAP  - cas d'�tude -
 *
 *
 *  Platform    :  Java virtual machine
 *  Language    :  JAVA 1.1.x
 *  Devl tool   :  Symantec VisualCafe
 *
 *  @version 1.0.0
 *------------------------------------------------------------------------------
 */


package test.Julia.EPurse;

/**
 * This class allows to detect exception launched by the Decimal class. 
 * Exception catched :  overflow of a short (> 32 767)
 *
*/
public class DecimalException extends PacapException{
    
    ////////////////      ATTRIBUTES       ////////////////
   
   public static final byte DECIMAL_INDETERMINE           = (byte) 0;
   public static final byte DECIMAL_OVERFLOW              = (byte) 0x01;
   public static final short SALERS_TABLE_FULL			  = (short)0x9F14;
    
    
   /*@ spec_public */ private static DecimalException instance;
    
    ///////////////     CONSTRUCTOR     ////////////////

    /*@
      requires true ;
      ensures super.type == DECIMAL_INDETERMINE ;
      ensures \fresh(this) ;
      //exsures (RuntimeException) false ;
    */
    DecimalException (){
        super(DECIMAL_INDETERMINE);        
    }

    /*@
      requires true ;      
      ensures super.type == code ;
      ensures \fresh(this) ;
      //exsures (RuntimeException) false ;
    */
    DecimalException (byte code){
        super(code);        
    }
    
    ////////////////       METHODS      ///////////////

    /*@ 
      modifies instance, ((PacapException)instance).type ;
      requires true ;
      ensures false ;
      exsures (DecimalException)((PacapException)instance).type == t && 
                                (\old(instance) == null ==> \fresh(instance)) ;
      //exsures (RuntimeException) false ;
    */
    public static void throwIt (byte t) throws DecimalException {
        if ( instance == null ) {
            instance = new DecimalException(t);
        }
        else {
            instance.setType(t);
        }
        throw instance;
    }
}

