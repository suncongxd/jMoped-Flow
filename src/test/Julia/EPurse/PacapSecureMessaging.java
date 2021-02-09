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


//code adde by Nestor CATANO
import javacard.security.CryptoException ;


/* we implement the secure messaging with a triple DES signature*/

public class PacapSecureMessaging extends Object {
    
    ////////////////      ATTRIBUTES       ////////////////
    
    
    ///////////////     CONSTRUCTOR     ////////////////
    
    
    ////////////////       METHODS      ///////////////
    
    
    /* @return the size of the secure messaging*/
    //code modified by Nestor CATANO 23/09/01
    // inclusion of throws clause
    /*@
      requires key != null & key.instance != null ;
      requires bArray != null;
      requires ivNull != null ;
      requires dest != null;
      requires dOffset >= 0;
      ensures true ;
      exsures (CryptoException e) true;
     */
    public static short generate(	byte bArray[], short sOffset, short sLength,
					PacapKey key, byte dest[], short dOffset,
					byte ivNull[])
	throws CryptoException  {
        //@ assert ivNull.length >= 0;
        //@ assume (short)ivNull.length >= 0;
	return PacapSignature.sign(bArray, sOffset, sLength, key, dest, dOffset,
				   ivNull, (short)0, (short)ivNull.length);
    }
    
    /* @return the verification tests*/
    //code modified by Nestor CATANO 23/09/01
    // inclusion of throws clause
    /*@
      requires key != null & key.instance != null ;
      requires bArray != null;
      requires ivNull != null ;
      requires SM != null;
      requires SMOffset >= 0;
      ensures true ;
      exsures (CryptoException e) true;
     */
    public static boolean verify(	byte bArray[], short sOffset, short sLength,
					PacapKey key, byte SM[], short SMOffset,
					byte ivNull[])
	throws CryptoException {
        //@ assert ivNull.length >= 0;
        //@ assume (short)ivNull.length >= 0;
	return PacapSignature.verify(bArray, sOffset, sLength,key,SM,SMOffset,
				     ivNull, (short)0, (short)ivNull.length);
	}
    
}

