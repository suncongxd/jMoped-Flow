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

public class  PacapException extends Exception{
    
    ////////////////      ATTRIBUTS       ////////////////
    
    public static final byte INDETERMINE                      = (byte) 0;

    /*@ spec_public */ private byte type = INDETERMINE;
    
    /*@ spec_public */ private static PacapException instance = null;
    
    ///////////////     CONSTRUCTEUR     ////////////////
    
    /*@
      requires true ; 
      ensures this.type == code ;
      ensures \fresh(this) ;
      //exsures (RuntimeException)false ;
    */
    public PacapException (byte code){
        super();
        type = code;
    		//{{INIT_CONTROLS
		//}}
    }
    
    /*@  
      requires true ; 
      ensures this.type == INDETERMINE ;
      ensures \fresh(this) ;
      //exsures (RuntimeException)false ;
    */
    public PacapException(){
        this(INDETERMINE);        
    }
    
    ////////////////       METHODES      ///////////////
     
    /*@  
      requires true ; 
      modifies instance, instance.type ;
      ensures false ;
      exsures (PacapException) instance.type == t && 
                               (\old(instance) == null ==> \fresh(instance)) ;
    */
    public static void throwIt (byte t) throws PacapException {
        if ( instance == null ) {
            instance = new PacapException(t);
        }
        else {
            instance.setType(t);
        }
        throw instance;
    }
 
    /*@
      //modifies \nothing ;
      requires true ; 
      ensures \result == type ;
      //exsures (RuntimeException)false ;
    */
    public byte getType(){
        return type;
    }
    
    /*@  
      requires true ;  
      modifies type ;
      ensures type == code ;
      //exsures (RuntimeException)false ;
    */
    public void setType(byte code){
        type = code;
    }
    
	//{{DECLARE_CONTROLS
	//}}
}

