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

public class AccessConditionException extends PacapException{
    
    
    ////////////////      ATTRIBUTES       ////////////////
   
    
    public static final byte CONDITION_COURANTE_INVALIDE     = (byte) 1;
    
    private static AccessConditionException instance;
    
    ///////////////     CONSTRUCTOR     ////////////////
    
    /*@
      requires true ;
      ensures super.type == code ;
      ensures \fresh(this) ;
      //exsures (RuntimeException) false ;
    */
    AccessConditionException (byte code){
        super(code);        
	//{{INIT_CONTROLS
	//}}
    }
      
    
    ////////////////       METHODS      ///////////////
    
    /*@ 
      modifies instance, ((PacapException)instance).type ;
      requires true ;
      ensures ((PacapException)instance).type == t ;
      exsures (AccessConditionException)
                 ((PacapException)instance).type == t && 
                 (\old(instance) == null ==> \fresh(instance)) ;
      //exsures (Exception) false ;
    */
    public static void throwIt (byte t) throws AccessConditionException {
	if ( instance == null ) {
            instance = new AccessConditionException(t);
	    throw instance;
        }
        else {
            instance.setType(t);
        }
	return;
    }
    
    //{{DECLARE_CONTROLS
    //}}
}

