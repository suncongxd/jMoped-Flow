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
 
 
import javacard.framework.Util;

// Code modified by Nestor CATANO 21/05/2001
//import instruction is needed by ESC/Java to refer TransactionException
import javacard.framework.TransactionException;
// import instruction is needed by ESC/Java to refer _transactionDepth
import javacard.framework.JCSystem;
 
/**
 *	identit� d'un commer�ant
 */
public class SalerID extends Object implements PartnerID {
    /*@
      invariant data != null ;
      invariant data.length == ID_LENGTH ;
    */

    /*@ invariant data == _data;
        invariant ID_LENGTH == _length;
    */
    
    ///////////////////   ATTRIBUTS   /////////////////
    
    /**
     *	la taille de l'identifiant en octet
     */
    public static final byte ID_LENGTH = (byte)4;
    //	private byte[] data = null;
    /*@ spec_public*/ private /* non_null */ byte[] data = new byte[SalerID.ID_LENGTH];
    
    
    /////////////////////// CONSTRUCTEUR ////////////////
    
    // temporarily moved by Rodolphe Muller on 15/06/2000
    public SalerID() { 
	//@ set _length = ID_LENGTH;
	//@ set _data = data;
    }
    /*
      super();
      data = new byte [SalerID.ID_LENGTH];
      }
    */
    
    ////////////////////   METHODES   ////////////////////
    
    
    /**
     *	initialise les attributs de cette instance
     */
    /*@
      modifies data[*] ;
      requires true ;
      ensures (\forall int i; 0 <= i & i < ID_LENGTH ==> data[i] == 0);
      //exsures (RuntimeException) false ;
    */
    void reset() {
	Util.arrayFillNonAtomic(data, (short)0, ID_LENGTH, (byte)0);
    }
    
    
    /**
     *	@see PartnerID
     */
    // inherits specification from PartnerID
    //@ also_ensures \result == data;
    public byte[] getBytes() {
	// Ca c'est un truc hyper dangereux !!!!!!
	// On retourne une ref sur une donn�e priv�e
	// heureusement que la classe n'est accessible que dans le package
	return data;
    }
    
    /*@ 
      modifies dest[*] ;
      requires dest != null && off >= 0 && off + ID_LENGTH <= dest.length ;
      ensures \result == (short)(off + ID_LENGTH);
      ensures (\forall int i; 0 <= i & i < dest.length ==>
                              (off <= i & i < off + ID_LENGTH)?
                              dest[i] == \old(data[(short)0 + i - off]) :
                              dest[i] == \old(dest[i]));
      //exsures (RuntimeException) false ;    
    */
    public short getBytes(byte[] dest, short off) {
	Util.arrayCopyNonAtomic(data, (short)0, dest, off, (short)ID_LENGTH);
	return (short)(off + ID_LENGTH);
    }
    
    /** 
     *	@param id un tableau d'octet qui repr�dente l'identit� du commer�ant
     *	@see PartnerID
     */
    // inherits specification from PartnerID
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    public void setBytes(byte[] id) 
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException {
	setBytes(id, (short)0);
    }

    /*@
      modifies data[*] ;
      requires id != null && off >= 0 && off + ID_LENGTH <= id.length ;

      ensures (\forall int i; 
                  0 <= i & i < ID_LENGTH ==> data[i] == id[off + i]);
      exsures (TransactionException e) 
                 e._reason == TransactionException.BUFFER_FULL 
                 && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    public void setBytes(byte[] id, short off) 
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException{
	Util.arrayCopy(id, off, data, (short)0, (short)ID_LENGTH);
    }

    /*@
      modifies data[*] ;
      requires s != null ;
      ensures (\forall int i; 
                  0 <= i & i < ID_LENGTH ==> data[i] == s.data[i]);
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    public void clone(SalerID s) 
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException {
	setBytes(s.getBytes());        
    }
}

