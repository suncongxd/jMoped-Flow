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
 *  @author: Thierry Bressure
 *  @version 1
 *------------------------------------------------------------------------------
 */


package test.Julia.EPurse;

//import com.gemplus.pacap.utils.PacapException;

class AllowedLoyaltyException extends PacapException{
    
    
    /////////////////  Class ATTRIBUTES       ////////////////////
    
    static final byte NB_SALERS_OVERFLOW = (byte) 0x01;
    
    private static AllowedLoyaltyException instance;
    
    
    ///////////////     CONSTRUCTOR     ////////////////
          
    /*@
      requires true ;
      ensures super.type == code ;
      ensures \fresh(this) ;
      //exsures (RuntimeException) false ;
    */
    AllowedLoyaltyException (byte code){
        super(code);        
	//{{INIT_CONTROLS
	//}}
    }
    
    ////////////////       METHODS      ///////////////

    /*@
      modifies instance, ((PacapException)instance).type ;
      requires true ;
      ensures false ;
      exsures (AllowedLoyaltyException)
                 ((PacapException)instance).type == t && 
                 (\old(instance) == null ==> \fresh(instance)) ;
      //exsures (RuntimeException) false ;
    */
    public static void throwIt (byte t) throws AllowedLoyaltyException {
        if ( instance == null ) {
            instance = new AllowedLoyaltyException(t);
        }
        else {
            instance.setType(t);
        }
        throw instance;
    }
    
    //{{DECLARE_CONTROLS
    //}}
}

