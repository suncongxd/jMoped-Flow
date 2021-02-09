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


/* this class builds 8 bytes certificates. Initialy, the size was 16
   bytes but the apdu buffer of the gemXpresso simulator was not
   sufficient to receive all the bytes of the echange currency command
   of the purse in a single reading. */

public class PacapCertificate extends Object {
    
    ////////////////      ATTRIBUTES       ////////////////
    
 
    
    ///////////////     CONSTRUCTOR     ////////////////
    
    // temporarily removed by Rodolphe Muller on 16/06/2000
    /*
      private PacapCertificate(byte transformation) {
      super();
      }
    */
    
    ////////////////       METHODS      ///////////////
    
    
    /**     
     *	@return the size of the certificate
     */
    // modified by Rodolphe Muller on 04/04/2000
    // public static short sign(byte [] bArray, short sOffset, short sLength,
    //                            PacapKey key, byte [] dest, short dOffset){
    /*@
      requires key != null & key.instance != null ;
      requires bArray != null;
      requires ivNull != null ;
      requires dest != null;
      requires dOffset >= 0;
      ensures true ;
      exsures (CryptoException e) true;
     */
    public static short sign(
			     byte[] bArray, short sOffset, short sLength,
			     PacapKey key, byte[] dest, short dOffset,
			     byte ivNull[]
			     ) 
	throws CryptoException {
	/* short aux = dOffset;
	   short size = instance.sign(bArray,sOffset, sLength, key, dest, aux);
	   aux += size;        
	   key.reverseKey();
	   size += instance.sign(bArray, sOffset, sLength, key, dest, aux,
	   dest, dOffset,size);
	   key.reverseKey();
	   return size;
		*/
	// modified by Rodolphe Muller on 04/04/2000
	//        return PacapSignature.sign(bArray, sOffset, sLength,
	//                                   key, dest, dOffset);
        //@ assert ivNull.length >= 0;
        //@ assume (short)ivNull.length >= 0;
	return PacapSignature.sign(	bArray, sOffset, sLength,
					key, dest, dOffset,
					ivNull, (short)0, (short)ivNull.length);
    }
    
    /** @return the verification test */
    // modified by Rodolphe Muller on 04/04/2000
    //public static boolean verify(byte[] inBuff, short inOffset, short inLength,
    //                       PacapKey key,
    //                       byte[] sigBuff, short  sigOffset){
    /*@
      requires key != null & key.instance != null ;
      requires inBuff != null;
      requires ivNull != null ;
      requires sigBuff != null;
      requires sigOffset >= 0;
      ensures true ;
      exsures (CryptoException e) true;
     */
    public static boolean verify(byte[] inBuff, short inOffset, short inLength,
				 PacapKey key,
				 byte[] sigBuff, short  sigOffset,
				 byte ivNull[]
				 ) 
	throws CryptoException {
	/* if ( instance == null ) createInstance();       
	   boolean resu = true;
	   resu = resu && instance.verifyInstance(inBuff, inOffset, inLength, key,
	   sigBuff, sigOffset);
        if ( ! resu ) return resu;
        key.reverseKey();
        resu = resu && instance.verify(inBuff, inOffset, inLength, key,
	sigBuff,(short) (sigOffset + 8),
	sigBuff, sigOffset, (short) 8);
        key.reverseKey();
        */
	// modified by Rodolphe Muller on 04/04/2000
	//        return PacapSignature.verify(inBuff, inOffset, inLength,
	//                                     key, sigBuff, sigOffset);
        //@ assert ivNull.length >= 0;
        //@ assume (short)ivNull.length >= 0;
	return PacapSignature.verify(	inBuff, inOffset, inLength,
					key, sigBuff, sigOffset,
					ivNull, (short)0, (short)ivNull.length);
    }
    

}

