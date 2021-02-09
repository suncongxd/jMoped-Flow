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

// Code modified by MH, 29/10/01
//import instruction is needed by ESC/Java to refer TransactionException
import javacard.framework.TransactionException;
// import instruction is needed by ESC/Java to refer _transactionDepth
import javacard.framework.JCSystem;
 
 /* this interface is used to unify the saler and the credit operator identifiers of the purse
 *	@see SalersID
 *	@see CreditorID*/
interface PartnerID {
    
    //@ ghost public static byte _length;
    //@ ghost public byte [] _data;

    /* return the partner identifier in a byte table*/
    /*@ 
      ensures \result != null && _length == \result.length;
      ensures (\forall int i; 0 <= i & i < _length ==>
                              \result[i] == _data[i]);
      //exsures (RuntimeException) false ;    
    */
    byte[] getBytes();
    
    /* modify the partner identifier
     *	@param id : the partner identifier*/

    /*@    
      modifies _data[*] ;
      requires id != null && _length <= id.length ;
      ensures (\forall int i; 0 <= i & i < _length ==> _data[i] == id[i]);
      exsures (TransactionException e) 
                       e._reason == TransactionException.BUFFER_FULL 
                       && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
    */
    // Code modified by MH, 29/10/01
    // inclusion of throws clause
    public void setBytes(byte[] id) 
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException;
}

