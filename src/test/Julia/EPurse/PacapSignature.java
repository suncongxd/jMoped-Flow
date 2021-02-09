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
import javacard.security.Signature;


public class PacapSignature {
    /*@
      invariant instance != null ;
     */

    ////////////////      ATTRIBUTES       ////////////////
    

    private /*@ spec_public */ static Signature instance = null;
    
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

    
    //code modified by Nestor CATANO 23/09/01
    // inclusion of throws clause
    /*@
      modifies instance ;
      requires false ;
      ensures instance == \old(instance) ;
      exsures (CryptoException e) e._reason == CryptoException.NO_SUCH_ALGORITHM ;
    */
    private static void createInstance()
	throws CryptoException {
	instance = Signature.getInstance(Signature.ALG_DES_MAC8_NOPAD, false);
    }
    
    /* @return the size of the signature */
    
    // temporarily removed by Rodolphe Muller on 04/05/2000
    /*
      public static short sign(byte [] bArray, short sOffset, short sLength,
      PacapKey key, byte [] dest, short dOffset){       
      if ( instance == null ) createInstance();
      instance.init(key.instance, Signature.MODE_SIGN,ivNull,(short) 0,(short) ivNull.length);
      // on padde !!
      short rest = ( short ) ( sLength % (short) 8 );
      if (  rest != 0 ) {
      short newLength = ( short ) ( sLength / (short) 8 );
      newLength++;
      newLength *= (short) 8;
      if ( (short)(sOffset + newLength) > bArray.length )
      CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
      Util.arrayFillNonAtomic(bArray,(short) (sOffset + sLength),
      (short) (newLength - sLength),
      (byte) 0);
      sLength = newLength;
      }
      return instance.sign(bArray, sOffset, sLength, dest, dOffset);
      }
    */
    
    /* @return the size of the signature*/
    //code modified by Nestor CATANO 23/09/01
    // inclusion of throws clause
    /*@
      requires key != null & key.instance != null ;
      requires bArray != null;
      requires iv != null;
      requires dest != null;
      requires ivOffset >= 0 & ivLen >= 0 & ivOffset + ivLen <= iv.length;
      requires dOffset >= 0;
      ensures true ;
      exsures (CryptoException e) true;
     */
    public static short sign(	byte bArray[], short sOffset, short sLength,
				PacapKey key, byte dest[], short dOffset,
				byte iv[], short ivOffset, short ivLen)
	throws CryptoException {
	if(instance == null)
	    createInstance();
	instance.init(key.instance(), Signature.MODE_SIGN, iv, ivOffset, ivLen);
	// on padde !!
	short rest = (short)(sLength % (short)8);
	if (rest != 0) {
	    short newLength = (short)(sLength / (short)8);
	    newLength++;
	    newLength *= (short)8;
	    if((short)(sOffset + newLength) > bArray.length)
		CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
	    Util.arrayFillNonAtomic(
				    bArray, (short)(sOffset + sLength), (short)(newLength - sLength),
				    (byte)0
				    );
	    sLength = newLength;
	}
	return instance.sign(bArray, sOffset, sLength, dest, dOffset);
    }
    
    /** @return the verification test
     * @exception CryptoException with the code ILLEGAL_VALUE */
    
    // temporarily removed by Rodolphe Muller on 04/05/2000
    /*
      public static boolean verify(byte[] inBuff, short inOffset, short inLength,
      PacapKey key,
      byte[] sigBuff, short  sigOffset){
      if ( instance == null ) createInstance();
      instance.init(key.instance, Signature.MODE_VERIFY,ivNull,(short) 0,(short) ivNull.length);
      // on padde !!
      short rest = ( short ) ( inLength % (short) 8 );
      if (  rest != 0 ) {
      short newLength = ( short ) ( inLength / (short) 8 );
      newLength++;
      newLength *= ( short) 8;
      if ( (short)(inOffset + newLength) > inBuff.length )
      CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);            
      Util.arrayFillNonAtomic(inBuff,(short) (inOffset + inLength),
      (short) (newLength - inLength),
      (byte) 0);
      inLength = newLength;            
      }
      return instance.verify(inBuff, inOffset, inLength,
      sigBuff,sigOffset,(short) 8);       
      }
    */
    
    /*@
      requires key != null & key.instance != null ;
      requires inBuff != null;
      requires iv != null;
      requires sigBuff != null;
      requires ivOffset >= 0 & ivLen >= 0 & ivOffset + ivLen <= iv.length;
      requires sigOffset >= 0;
      ensures true ;
      exsures (CryptoException e) true;
     */
    public static boolean verify(	byte inBuff[], short inOffset, short inLength,
					PacapKey key,
					byte sigBuff[], short  sigOffset,
					byte iv[], short ivOffset, short ivLen)
	throws CryptoException {
	if ( instance == null )
	    createInstance();
	instance.init(key.instance(), Signature.MODE_VERIFY, iv, ivOffset, ivLen);
	// on padde !!
	short rest = (short)(inLength % (short)8);
	if (rest != 0) {
	    short newLength = (short)(inLength / (short)8);
	    newLength++;
	    newLength *= (short)8;
	    if((short)(inOffset + newLength) > inBuff.length)
		CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
	    Util.arrayFillNonAtomic(
				    inBuff, (short)(inOffset + inLength), (short)(newLength - inLength),
				    (byte)0
				    );
	    inLength = newLength;
	}
	return instance.verify(inBuff, inOffset, inLength,sigBuff, sigOffset, (short)8);
    }
}

