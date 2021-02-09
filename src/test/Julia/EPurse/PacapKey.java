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


//package com.gemplus.pacap.utils;
package test.Julia.EPurse;

import javacard.framework.Util;
import javacard.security.KeyBuilder;
import javacard.security.DESKey;

// added by Rodolphe Muller
import javacard.framework.JCSystem;

//added by Nestor CATANO
import javacard.security.CryptoException ;
import javacard.framework.ISOException ;


/* a key of   2*8 bytes for the triple DES*/
public class PacapKey implements DESKey {
    
    ////////////////      ATTRIBUTES       ////////////////
    
    // javadoc comment modified by Rodolphe on 05/05/2000
    // temporary variable for the permutation of the 2 blocks of the key
    // @see PacapKey#reverseKey()
    /**
     *	temporary array used to store the key value when doing
     *	operations on it.
     *	@see PacapKey#reverseKey()
     *	@see PacapKey#encrypte()
     *	@see PacapKey#decrypte()
     */
    private /*@ spec_public */ byte temp[] = null;

    
    // added by Rodolphe Muller on 04/2000
    private /*@ spec_public */ DESKey instance;

    //@ invariant temp != null & temp.length == 16;
    //@ invariant instance != null;

     
    ///////////////     CONSTRUCTOR     ////////////////
    
    // modified by Rodolphe Muller on 04/2000

    // code modified by Nestor CATANO 23/09/01
    // inclusion of throws clause
    /*@
      // ESC/Java cannot establish that downcast of key to DESKey is okay
      // ESC/Java cannot establish that constructor establishes invariant
      // instance != null
      // This is because we do not have accurate specification of method
      // buildKey
      //modifies \nothing ;
      requires event == JCSystem.NOT_A_TRANSIENT_OBJECT ||
               event == JCSystem.CLEAR_ON_DESELECT;
      ensures \fresh(this) ;
      exsures (ISOException e) e._reason == javacard.framework.ISO7816.SW_UNKNOWN ;
      exsures (CryptoException) true ;
    */
    public PacapKey(byte event) 
    throws javacard.framework.ISOException, CryptoException {
	switch(event) {
	case JCSystem.NOT_A_TRANSIENT_OBJECT:
	    instance = (DESKey)KeyBuilder.buildKey(
						   KeyBuilder.TYPE_DES,
						   KeyBuilder.LENGTH_DES3_2KEY,
						   false
						   );
	    break;
	case JCSystem.CLEAR_ON_DESELECT:
	    instance = (DESKey)KeyBuilder.buildKey(
						   KeyBuilder.TYPE_DES_TRANSIENT_DESELECT,
						   KeyBuilder.LENGTH_DES3_2KEY,
						   false
						   );
	    break;
	default:
            //@ assert false;
				// we sould never get here !!!
	    javacard.framework.ISOException.throwIt(javacard.framework.ISO7816.SW_UNKNOWN);
	    break;
	}
	temp = new byte[(byte)16];
    }
    
    ////////////////       METHODS      ///////////////
    
    /* the key is made of two blocks of 8 bytes K1-K2
       this method inverses the two blocks K2-K1*/

    // code modified by Nestor CATANO 23/09/01
    // inclusion of throws clause
    /*@
      modifies temp[*] ;
      ensures true ;
      exsures (CryptoException) true ;  
    */
    public void reverseKey()  
	throws CryptoException {
	// modified by Rodolphe Muller on 04/05/2000
	/*
	  Util.arrayCopyNonAtomic(value,(short) 0,temp, (short) 0, (short) 8);
	  Util.arrayCopy(value,(short) 8,value, (short) 0, (short) 8);
	  Util.arrayCopy(temp,(short) 0, value, (short) 8, (short) 8);
	*/
	getKey(temp, (short)0);
	for(byte i=0;i<8;i++) {
	    byte swap;
	    swap = temp[i];
	    temp[i] = temp[(byte)(i+8)];
	    temp[(byte)(i+8)] = swap;
	}
	setKey(temp, (short)0);
    }

    
    // modified by Rodolphe Muller on 04/05/2000
    //	public void encrypte(PacapKey key){

    // code modified by Nestor CATANO 23/09/01
    // inclusion of throws clause
    /*@
      //modifies \nothing ;
      requires key != null;
      requires ivNull != null;
      ensures true ;
      exsures (CryptoException) true ;      
     */
    public void encrypte(PacapKey key, byte ivNull[]) 
	throws CryptoException {
	getKey(temp, (short)0);
	// modified by Rodolphe Muller on 04/05/2000
	//		PacapCipher.crypte(temp,(short) 0, (short) 16,key,temp, (short) 0);
	PacapCipher.crypte(temp, (short)0, (short)16, key, temp, (short)0, ivNull);
	setKey(temp, (short)0);
    }
    
    // modified by Rodolphe Muller on 04/05/2000
    //	public void decrypte(PacapKey key){

    // code modified by Nestor CATANO 23/09/01
    // inclusion of throws clause
    /*@
      //modifies \nothing ;
      requires key != null;
      requires ivNull != null;     
      ensures true ;
      exsures (CryptoException) true ;
     */
    public void decrypte(PacapKey key, byte ivNull[])
	throws CryptoException {
	getKey(temp, (short)0);
	// modified by Rodolphe Muller on 04/05/2000
	//		PacapCipher.decrypte(temp,(short) 0, (short) 16,key,temp, (short) 0);
	PacapCipher.decrypte(temp, (short)0, (short)16, key, temp, (short)0, ivNull);
	setKey(temp, (short)0);
    }
    
    // functions added by Rodolphe Muller on 04/2000
    // in order to implement DESKey interface
    /*@
      //also_modifies \nothing ;
      also_ensures true;
      //aslo_exsures (Exception) false ;
    */
    public byte getKey(byte[] keyData, short kOff) {
	return instance.getKey(keyData, kOff);
    }
    
    // code modified by Nestor CATANO 23/09/01
    // inclusion of throws clause
    /*@
      // also_modifies \nothing ;
       also_ensures true;
       also_exsures (CryptoException) true;
    */
    public void setKey(byte[] keyData, short kOff) 
	throws CryptoException {
	instance.setKey(keyData, kOff);
    }
    
    /*@
      // also_modifies \nothing ;
       also_ensures true;
      // also_exsures (Exception) false ;
    */
    public void clearKey() {
	instance.clearKey();
    }
    
    /*@
      // also_modifies \nothing ;
       also_ensures true;
      // also_exsures (Exception) false ;
    */
    public short getSize() {
	return instance.getSize();
    }
    
    /*@
      // also_modifies \nothing ;
      also_ensures true;
      // also_exsures (Exception) false ;
    */
    public byte getType() {
	return instance.getType();
    }

    /*@
      //also_modifies \nohting 
      also_ensures true ;
      //also_exsures (Exception) false ;
    */
    public boolean isInitialized() {
	return instance.isInitialized();
    }
    
    // functions added by Rodolphe Muller on 05/05/2000
    // in order to implement do some kind of DESKey cast
    /*@
      //modifies \nohting 
      requires true ;
      ensures \result == instance ;
      //exsures (Exception) false ;
     */
    public DESKey instance() {
	return instance;
    }
}

