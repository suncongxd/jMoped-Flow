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
import javacard.security.RandomData;

//code added by Nestor CATANO 23/09/01
import javacard.security.CryptoException;

public class PacapRandom {

	////////////////      ATTRIBUTES       ////////////////

    private /*@ spec_public */ static RandomData instance = null;


	///////////////     CONSTRUCTOR     ////////////////


	////////////////       METHODS      ///////////////

    // code modified by Nestor CATANO 23/09/01
    // inclusion of throws clause
    /*@
      modifies instance ;
      requires true;
      ensures instance != null;
      exsures (CryptoException) true;
    */
    private static void createInstance()
    throws CryptoException {
	instance = RandomData.getInstance(RandomData.ALG_SECURE_RANDOM);
    }

    // code modified by Nestor CATANO 23/09/01
    // inclusion of throws clause
    /*@
      modifies instance ;
      requires bArray !=null && sOffset >=0 && sLength >=0 &&
               sOffset + sLength <= bArray.length;
      ensures true;
      exsures (CryptoException) true;
    */
    public static void next(byte [] bArray, short sOffset, short sLength)
	throws CryptoException {
	if(instance == null)
	    createInstance();
        //@ assert instance != null;
	instance.generateData(bArray, sOffset, sLength);
    }

}

