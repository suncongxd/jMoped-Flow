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

// added by Rodolphe Muller on 06/07/2000
//import com.gemplus.pacap.utils.*;

/* model the date to the date-month-year format using the constants of
   the abstract classes Jour, Mois, Annee of this package*/
public class Date extends Object {
    
    /*@
      invariant jour >= Jour.MIN && jour <= Jour.MAX ;
      invariant mois >= Mois.MIN && mois <= Mois.MAX ;
      invariant annee >= Annee.MIN && annee <= Annee.MAX ;
    */
    
    ////////////////      ATTRIBUTES       ////////////////
    
    /*@ spec_public */ private byte jour	= Jour.MIN;
    /*@ spec_public */ private byte mois	= Mois.MIN;    
    /*@ spec_public */ private byte annee	= Annee.MIN;
    
    
    ///////////////     CONSTRUCTOR     ////////////////
    
    /* the default constructor initialize the date to january, 1rst 1999*/
    /*@    
      requires true ;
      ensures jour == Jour.MIN && mois == Mois.MIN && annee == Annee.MIN ;
      ensures \fresh(this) ;
      //exsures (RuntimeException) false ;      
    */
    public Date() {
	super();
    }
    
    
    /**
     *	@param j the day of the month
     *	@param m the month of the year
     *	@param a the year counted from 1900
     *	@exception DateException  is thrown if the arguments provide a date not coherent or before
     January, 1rst 1999*/
    
    /*@ 
      requires j >= Jour.MIN && j <= Jour.MAX &&
               m >= Mois.MIN && m <= Mois.MAX &&
               a >= Annee.MIN && a <= Annee.MAX;
      ensures jour == j && annee == a && mois == m ;
      exsures (DateException) false;
    */
    public Date(byte j, byte m, byte a) throws DateException{
	super();
	// check the day     
	if(!Jour.check(j)) {
	    DateException.throwIt(DateException.ERREUR_JOUR);
	} else {
	    // check the months
	    if(!Mois.check(m)) {
		DateException.throwIt(DateException.ERREUR_MOIS);
	    } else {
				// check the year
		if(!Annee.check(a)) {
		    DateException.throwIt(DateException.ERREUR_ANNEE);
                } else {
		    // all is good
		    jour = j;
		    mois = m;
		    annee = a;
		}
	    }
	}
    }
    

    ////////////////       METHODS      ///////////////
    
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == jour ;
      //exsures (RuntimeException)false ;
    */
    public byte getJour() {
	return jour;
    }

    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == mois ;
      //exsures (RuntimeException)false ;
    */
    public byte getMois() {
	return mois;
    }
    
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == annee ;
      //exsures (RuntimeException)false ;
    */
    public byte getAnnee() {
	return annee;
    }
    
    /* store the date in the given table
     *	@param bArray : the destination tabme
     *	@param offset the position in the tabme
     *	@return offset + 3; 
     */
    /*@
      modifies bArray[*] ;
      requires bArray != null && offset >= 0 && offset <= bArray.length - 3 ;
      ensures \result == offset + 3 ;
      //exsures (RuntimeException)false ;
    */
    public short getDate(byte [] bArray, short offset) {
	short aux = offset;
	//@ assume aux >= 0 && aux <= 256 ;
	bArray[aux++] = jour;
	bArray[aux++] = mois;
	bArray[aux++] = annee;
	return aux;
    }
    
    /*@ 
      modifies jour, mois, annee ;
      requires j >= Jour.MIN && j <= Jour.MAX &&
               m >= Mois.MIN && m <= Mois.MAX &&
               a >= Annee.MIN && a <= Annee.MAX;
      ensures jour == j && annee == a && mois == m ;
      exsures (DateException) false;
    */
    public void setDate(byte j, byte m, byte a) throws DateException {
	// check the day         
	if(!Jour.check(j)) {
	    DateException.throwIt(DateException.ERREUR_JOUR);
	} else {
	    // check the month
	    if(!Mois.check(m)) {
		DateException.throwIt(DateException.ERREUR_MOIS);
	    } else {
		// check the year
		if(!Annee.check(a)) {
		    DateException.throwIt(DateException.ERREUR_ANNEE);
		} else {
		    // all is good
		    jour = j;
		    mois = m;
		    annee = a;
		}
	    }
	}
    }
    
    /*@ 
      modifies jour, mois, annee ;
      requires d != null;
      ensures jour == d.jour && annee == d.annee && mois == d.mois ;
      exsures (DateException) false ;
    */
    public void setDate(Date d) throws DateException {
	setDate(d.getJour(), d.getMois(), d.getAnnee());
    }
    
    /* check if the date is before (strictly) to the one provided
     *	@param d : date used to compare the date of thos class*/

    /*@ 
      //modifies \nothing ;
      requires d != null ;
      ensures \result == ((d.annee > annee) ||
                          (d.annee == annee && d.mois > mois) ||
			  (d.annee == annee && d.mois == mois && d.jour > jour)
			  ) ;
      //exsures (RuntimeException) false ;
    */
    public boolean before(Date d) {
	if(d.getAnnee() < annee) return false;
	else if(d.getAnnee() > annee) return true;
	else if(d.getMois() < mois) return false;
	else if(d.getMois() > mois) return true;
	else if(d.getJour() <= jour) return false;
	else return true;
    }


    /* check if the date is after the one provided in the parameter
     *	@param d the date used to comapre the current date
     */
    /*@
      //modifies \nothing
      requires d != null ;
      ensures \result == ((d.annee < annee) || 
                          (d.annee == annee && d.mois < mois) || 
			  (d.annee == annee && d.mois == mois && d.jour <= jour)
			  ) ;
      //exsures (RuntimeException) false ;
    */
    public boolean after(Date d) {
	if(d.getAnnee() > annee) return false;
	else if(d.getAnnee() < annee) return true;
	else if(d.getMois() > mois) return false;
	else if(d.getMois() < mois) return true;
	else if(d.getJour() > jour) return false;
	else return true;
    }
}
