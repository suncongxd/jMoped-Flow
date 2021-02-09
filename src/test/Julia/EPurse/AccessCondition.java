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


/*model the access condition of a method*/

public class AccessCondition extends Object{

    /*@ invariant condition == FREE ||
                  condition == LOCKED ||
                  condition == SECRET_CODE ||
                  condition == SECURE_MESSAGING ||
                  condition == (SECRET_CODE | SECURE_MESSAGING)
    */    
    
    ////////////////      ATTRIBUTES       ////////////////
    
    public static final byte FREE		= (byte)1;
    public static final byte LOCKED		= (byte)2;
    public static final byte SECRET_CODE	= (byte)4;
    public static final byte SECURE_MESSAGING	= (byte)8; 
    /*@ spec_public */ private byte condition = FREE;
    
    ///////////////     CONSTRUCTOR     ////////////////
    
    /*@ 
      requires true ;
      ensures condition == FREE;
      ensures \fresh(this) ;
      //exsures (RuntimeException) false ;
    */
    public AccessCondition() {
	super();
	// commented by Rodolphe Muller on 16/06/2000
	// strange to set the condition here to 0 and to FREE above
        // MH, commented out instruction, as it breaks invariant
	// setCondition((byte)0);
    }

    ////////////////       METHODS      ///////////////
    
    /*@
      modifies condition ;
      requires true ;
      ensures condition == FREE ;
      //exsures (RuntimeException) false ;
    */
    public void reset() {
        condition = FREE;
    }
    
    /*@ 
      modifies condition ;
      requires c == FREE || 
               c == LOCKED || 
               c == SECRET_CODE ||
               c == SECURE_MESSAGING ||
               c == (SECRET_CODE | SECURE_MESSAGING);
      ensures condition == c ;
      //exsures (RuntimeException) false ;
    */
    public void setCondition (byte c){
        condition = c;
    }
    
    /*@
      modifies condition ;
      requires c == FREE || 
               c == LOCKED || 
               c == SECRET_CODE ||
               c == SECURE_MESSAGING ||
               c == (SECRET_CODE | SECURE_MESSAGING);
      ensures (c == SECURE_MESSAGING || c == SECRET_CODE ) && 
              (\old(condition) == SECRET_CODE || 
                 \old(condition) == SECURE_MESSAGING) ? 
	      (condition == (byte)(\old(condition)|c)) : 
	      condition == c;
      //exsures (RuntimeException) false ;
      //ESC/Java can not establish that this method preserves the invariant
    */  
    public void addCondition(byte c) {
        byte x;
        if ( ( c == SECURE_MESSAGING || c == SECRET_CODE )
	     && ( condition == SECRET_CODE || condition == SECURE_MESSAGING )){
    	    condition |= c;
	}
                   
        else condition = c;
    }

    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == condition ;
      //exsures (RuntimeException) false ;
    */   
    public byte getCondition(){
        return condition;
    }
    
    /*check if the condition c meets the access condition*/
    
    /*@
      //modifies \nothing ;
      requires c != null ;
      ensures \result ==  
       (condition == FREE) ||
       ((condition == SECRET_CODE) && 
           (byte)(c.condition & SECRET_CODE) == SECRET_CODE) ||
       ((condition == SECURE_MESSAGING) && 
           (byte)(c.condition & SECURE_MESSAGING) == SECURE_MESSAGING) ||
       (condition == (SECRET_CODE | SECURE_MESSAGING) &&
  	   ((byte)(c.condition & (SECRET_CODE | SECURE_MESSAGING)) ==
                                (SECRET_CODE|SECURE_MESSAGING)));
      exsures (AccessConditionException) false;
    */
    public final boolean verify(AccessCondition  c) throws AccessConditionException {
	return verify(c.getCondition());
    }

    /* check if the given security state is sufficient for this access condition
     *	@param c : the security state
     *	@return boolean: true if the security state is correct for this access condition*/
    
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result ==  
       (condition == FREE) ||
       ((condition == SECRET_CODE) && 
           (byte)(c & SECRET_CODE) == SECRET_CODE) ||
       ((condition == SECURE_MESSAGING) && 
           (byte)(c & SECURE_MESSAGING) == SECURE_MESSAGING) ||
       (condition == (SECRET_CODE | SECURE_MESSAGING) &&
  	   ((byte)(c & (SECRET_CODE | SECURE_MESSAGING)) ==
                       (SECRET_CODE|SECURE_MESSAGING)));
      exsures (AccessConditionException) false;
    */
    private final boolean verify(byte c) throws AccessConditionException {
	byte t = (byte)0;
	switch(condition) {
	case FREE:
	    // no condition required
	    return true;
	case SECRET_CODE:
	    // secret code required
	    t = (byte)(c & SECRET_CODE);
	    return(t == SECRET_CODE);
	case SECURE_MESSAGING:
	    // secure messaging required
	    t = (byte)(c & SECURE_MESSAGING);
	    return (t == SECURE_MESSAGING);
	case SECRET_CODE | SECURE_MESSAGING:
	    // secret code and secure messaging
	    t = (byte)(c & (SECRET_CODE | SECURE_MESSAGING));
	    return (t == (SECRET_CODE | SECURE_MESSAGING));
	case LOCKED:
	    // we never get ther
	    return false;
	default:
            //@ unreachable;
	    t = AccessConditionException.CONDITION_COURANTE_INVALIDE;
	    AccessConditionException.throwIt(t);
	    return false;
	}
    }
    
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == ((condition & SECRET_CODE) == SECRET_CODE) ;
      //exsures (RuntimeException) false ;
    */
    public boolean secretCodeNeeded(){
        byte t = (byte) ( condition & SECRET_CODE );
        return ( t == SECRET_CODE );
    }
    
    
  
	
	//{{DECLARE_CONTROLS
	//}}
}

