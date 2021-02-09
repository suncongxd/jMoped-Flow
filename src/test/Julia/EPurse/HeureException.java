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

public class HeureException extends PacapException{
    
    ////////////////      ATTRIBUTES       ////////////////
    
    
    public static final byte ERREUR_HEURE    = (byte) 1;
    public static final byte ERREUR_MINUTE    = (byte) 2;
   
    
    /*@ spec_public */ private static HeureException instance;
  
    
    ///////////////     CONSTRUCTOR     ////////////////
    
    /*@
      requires true ;
      ensures super.type == code ;
      ensures \fresh(this) ;
      //exsures (RuntimeException) false ;
    */
    HeureException (byte code){
        super(code);            		
    
		//{{INIT_CONTROLS
		//}}
    }
    
    ////////////////       METHODS      ///////////////

    /*@
      modifies instance, ((PacapException)instance).type ;
      requires true ;
      ensures false ;
      exsures (HeureException)((PacapException)instance).type == t && 
                              (\old(instance) == null ==> \fresh(instance)) ;
      //exsures (RuntimeException) false ;
     */
    public static void throwIt (byte t) throws HeureException {
        if ( instance == null ) {
            instance = new HeureException(t);
        }
        else {
            instance.setType(t);
        }
        throw instance;
    }    
    //
    //{{DECLARE_CONTROLS
    //}}
}
