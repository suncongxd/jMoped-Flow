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

class LoyaltiesTableException extends PacapException{
    
    
    /////////////////   ATTRIBUTES       ////////////////////
       
    static final byte NB_LOYALTIES_OVERFLOW = (byte) 0x01;
    
    private static LoyaltiesTableException instance;
    
    
    ///////////////     CONSTRUCTOR     ////////////////
    
    /*@
      requires true ;
      ensures super.type == code ;
      ensures \fresh(this) ;
      //exsures (RuntimeException) false ;
    */
    LoyaltiesTableException (byte code){
        super(code);        
	//{{INIT_CONTROLS
	//}}
    }
    
    ////////////////       METHODS      ///////////////
    
    /*@
      modifies instance, ((PacapException)instance).type ;
      requires true ;
      ensures false ;
      exsures (LoyaltiesTableException)((PacapException)instance).type == t &&
                                (\old(instance) == null ==> \fresh(instance)) ;
      //exsures (RuntimeException) false ;
    */
    public static void throwIt (byte t) throws LoyaltiesTableException {
        if ( instance == null ) {
            instance = new LoyaltiesTableException(t);
        }
        else {
            instance.setType(t);
        }
        throw instance;
    }
    
    //{{DECLARE_CONTROLS
    //}}
}

