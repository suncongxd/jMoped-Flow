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
 *  Original author: Thierry Bressure
 *------------------------------------------------------------------------------
 */


package test.Julia.EPurse;

/**
 * Classe qui mod�lise une ann�e. Les ann�es sont compt�es � partir de 1900.
 * Cette classe n'est utilis�e que pour valider la correction d'une ann�e.
 * Une ann�e est correcte si son ordinal est compris entre
 * <code>Annee.MIN</code> et <code>Annee.MAX</code>.
 */
public abstract class Annee extends Object{

	////////////////      ATTRIBUTS       ////////////////

	/**	le logiciel �tant impl�menter en 1999, cette ann�e sera l'ann�e
	 *	minimum
	 */
    // modified by Nestor CATANO COLLAZOS 07/05/01
    // variable modifier `final' added
    // see 'Formal specification of Gemplus' electronic purse case study',
    // N. Catano and M. Huisman, Section 4
	public static final byte MIN = (byte)99;

	/**	Ann�e maximum qui nous est impos�e par la magnitude de l'octet
	 *	en Java
	 */
    // code modifies by Nestor CATANO COLLAZOS 07/05/01
    // variable modifier `final' added
    // see 'Formal specification of Gemplus' electronic purse case study',
    // N. Catano and M. Huisman, Section 4
	public static final byte MAX = (byte)127;


	///////////////     CONSTRUCTEUR     ////////////////



	////////////////       METHODES      ///////////////


    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == (j >= MIN && j <= MAX) ;
      //exsures (RuntimeException)false ;
    */
	public static boolean check(byte j) {
		return ((j >= Annee.MIN) && (j <= Annee.MAX));
	}

}
