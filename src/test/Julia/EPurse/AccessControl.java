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

/* define the access condition associated to this method*/ 
public class AccessControl extends AccessCondition {

    ////////////////      ATTRIBUTES       ////////////////
    
    
    // method identifier
    /*@ spec_public */ private byte methode	= 0x00;
    
    
    ///////////////     CONSTRUCTOR     ////////////////
    /*@
      requires true ;
      ensures super.condition == FREE;
      ensures this.methode == 0x00;
      ensures \fresh(this) ;
      //exsures (RuntimeException) false ;
    */
    AccessControl() {
	super();
    }
    
    /*@
      requires true ;
      ensures super.condition == FREE;
      ensures this.methode == m ;
      ensures \fresh(this) ;
      //exsures (RuntimeException) false ;
    */
    AccessControl(byte m) {
	// temporarily removed by Rodolphe Muller on 16/06/2000
	// not usefull
	//		this();
	methode = m;
    }

    ////////////////       METHODS      ///////////////
    
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == methode ;
      //exsures (RuntimeException) false ;
    */
    byte getMethode() {
	return methode;
    }

    /*@
      modifies methode ;
      ensures methode == m ;
      //exsures (RuntimeException) false ;
    */
    void setMethode(byte m) {
	methode = m;
    }

}

