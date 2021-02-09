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

public abstract class Jour extends Object{

	////////////////      ATTRIBUTS       ////////////////

	public static final byte MIN	= (byte)1;
	public static final byte MAX	= (byte)31;

	///////////////     CONSTRUCTEUR     ////////////////
    


	////////////////       METHODES      ///////////////
    /*@ 
      //modifies \nothing ;
      requires true ;
      ensures \result == (j >= MIN && j <= MAX);
      //exsures (RuntimeException) false;
    */
	public static boolean check(byte j){
		if((j >= Jour.MIN) && (j <= Jour.MAX)) {
			return true;
		} else {
			return false;
		}
	}

}
