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

public class DateException extends PacapException{
    
    ////////////////      ATTRIBUTES       ////////////////
   
         
    public static final byte ERREUR_JOUR    = (byte) 1;
    public static final byte ERREUR_MOIS    = (byte) 2;
    public static final byte ERREUR_ANNEE   = (byte) 3;
    
    private static DateException instance;
  
    
    ///////////////     CONSTRUCTOR     ////////////////
       
    /*@
      requires true ;
      ensures super.type == code ;
      ensures \fresh(this) ;
      //Exsures(RuntimeException) false ;
    */
    DateException (byte code){
        super(code);            		
    		//{{INIT_CONTROLS
		//}}
    }
    
    ////////////////       METHODS      ///////////////

    /*@
      modifies instance, ((PacapException)instance).type ;
      requires true ;
      ensures false ;
      exsures (DateException)((PacapException)instance).type == t && 
                             (\old(instance) == null ==> \fresh(instance)) ;
      //Exsures(RuntimeException) false ;
    */ 
    public static void throwIt (byte t) throws DateException {
        if ( instance == null ) {
            instance = new DateException(t);
        }
        else {
            instance.setType(t);
        }
        throw instance;
    }    
	//{{DECLARE_CONTROLS
	//}}
}

