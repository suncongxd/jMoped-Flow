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
import javacard.security.CryptoException;
import javacardx.crypto.*;


/* a class really usefull to reduce the code*/
public class PacapCipher {

    ////////////////      ATTRIBUTES       ////////////////
    
    private static Cipher instance = null;
    
    // temporarily removed by Rodolphe Muller on 04/05/2000
    /*
      public static byte [] ivNull = new byte[] {
      (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x00
      };
    */
    ///////////////     CONSTRUCTOR     ////////////////
    
    
    ////////////////       METHODS      ///////////////
    
    /*@
      modifies instance ;
      requires true ;
      ensures instance != null;
      //exsures (Exception) false ;
     */
    private static void createInstance() {
	instance = Cipher.getInstance(Cipher.ALG_DES_CBC_NOPAD, false);
    }
    
    /* cypher the table bArray and adding pmadding if necessary
       @return the number of bytes writen in dOffset
       @exception CryptoException if the padding is necessary but not possible
    */
    // temporarily modified by Rodolphe Muller on 04/05/2000
    //	public static short crypte(	byte [] bArray, short sOffset, short sLength,
    //								PacapKey key, byte [] dest, short dOffset){

    // code modified by Nestor CATANO 23/09/01
    // inclusion of throws clause
    /*@
      modifies instance ;
      requires key != null ;
      requires ivNull != null ;
      requires bArray != null;
      ensures true ;
      exsures (CryptoException e) true ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (NullPointerException) false ;
    */
    public static short crypte(	byte bArray[], short sOffset, short sLength,
				PacapKey key, byte dest[], short dOffset,
				byte ivNull[]) 
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException,
	       CryptoException{
	
	if(instance == null)
	    createInstance();
	// modified by Rodolphe Muller on 05/05/2000
	/*
	  instance.init(
	  key, Cipher.MODE_ENCRYPT,
	  ivNull, (short)0, (short)ivNull.length
	  );
	*/
	instance.init(
		      key.instance(), Cipher.MODE_ENCRYPT,
		      ivNull, (short)0, (short)ivNull.length
		      );
	// on padde !!
	short rest = (short)(sLength % (short)8);
	if (rest != 0) {
	    short newLength = (short)(sLength / (short)8);
	    newLength++;
	    newLength *= (short)8;
	    if ((short)(sOffset + newLength) > bArray.length)
		CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
            // assert (short)(newLength - sLength) >= 0;
	    Util.arrayFillNonAtomic(
				    bArray, (short)(sOffset + sLength), (short)(newLength - sLength),
				    (byte)0
				    );
	    sLength = newLength;
	}
	return instance.doFinal(bArray, sOffset, sLength, dest, dOffset);
    }
    
    /* @return the number of bytes written in dOffset*/
    // temporarily modified by Rodolphe Muller on 04/05/2000
    //	public static short  decrypte(	byte [] bArray, short sOffset, short sLength,
    //									PacapKey key, byte [] dest, short dOffset) {
    /*@ 
      modifies instance ;
      requires key != null ;
      requires ivNull != null ;
      ensures true ;
      //exsures (Exception) false ;
    */
    public static short decrypte(	byte bArray[], short sOffset, short sLength,
					PacapKey key, byte dest[], short dOffset,
					byte ivNull[]) {
	
	if(instance == null)
	    createInstance();
	// modified by Rodolphe Muller on 05/05/2000
	/*
	  instance.init(
	  key, Cipher.MODE_DECRYPT,
	  ivNull, (short)0, (short)ivNull.length
	  );
	*/
	instance.init(
		      key.instance(), Cipher.MODE_DECRYPT,
		      ivNull, (short)0, (short)ivNull.length
		      );
	return instance.doFinal(bArray, sOffset, sLength, dest, dOffset);
    }
}

