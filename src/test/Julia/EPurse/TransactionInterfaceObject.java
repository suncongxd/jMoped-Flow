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

// temporarily removed by Rodolphe Muller on 31/05/2000
//import com.gemplus.pacap.utils.Decimal;

// added by RM on 26/07/2000
//import com.gemplus.pacap.pacapinterfaces.TransactionInterface;

/* this class is used to send to the loyalty the information about a transaction related 
   to this loyalty
   *	@see TransactionInterface*/
class TransactionInterfaceObject extends Transaction implements TransactionInterface{ 
    /*@
      invariant reste >= 0 ;
     */
    
    ////////////////      ATTRIBUTES       ////////////////
    
    /* the number of remaining transactions in the history file and that concerns the calling loyalty*/
    /*@ spec_public */private byte reste;
    
    
    ///////////////     CONSTRUCTOR     ////////////////
    
    
    
    ////////////////       METHODS      ///////////////
    
    // temporarily removed by Rodolphe Muller on 16/08/2000
    /*@
      //also_modifies \nothing ;
      also_requires true ;
      also_ensures \result == super.montant.intPart ;
      //exsures (NOException) false ;  
    */
    public short getIntPartMontant() {
	return getMontant().getIntPart();
    }
    
    /*@
      //also_modifies \nothing ;
      also_requires true ;
      also_ensures \result == super.montant.decPart ;
      //exsures (RuntimeException) false ;  
    */
    public short getDecPartMontant() {
	return getMontant().getDecPart();
    }
	
    /*
      // temporarily added by Rodolphe Muller on 16/08/2000
      public byte getMontantDigit(short index) {
      return getMontant().number[index];
      }
    */
    /*@
      //also_modifies \nothing ;
      also_ensures \result == super.devise ;
      //also_exsures (RuntimeException) false ;      
    */
    public byte getDevise() {
	return super.getDevise();
    }
    
    /* return the bytes of the index n that represents the saler id*/
    /*@
      //also_modifies \nothing ;
      also_requires n < super.id.length ;
      also_ensures \result == super.id[n] ;
      //also_exsures (NOException) false ;      
    */
    public byte getId (byte n) {
	return getPartnerID()[n];
    }
    
    /* return the lenght of the saler id*/
    /*@
      //also_modifies \nothing ;
      also_requires true ;
      also_ensures \result == super.id.length ;
      //also_exsures (NOException) false ;      
    */
    public short getIdLength() {
	return (short) getPartnerID().length;
    }

    /*@
      modifies reste ;
      requires n >= 0;
      ensures reste == n ;
      //exsures (RuntimeException) false ;
    */
    void setReste(byte n) {
	reste = n;
    }

    /*@
      //also_modifies \nothing ;
      also_requires true ;
      also_ensures \result == reste ;
      //also_exsures (NOException) false ;
    */
    public byte getReste() {
	return reste;
    }
 
    /*@
      //also_modifies \nothing
      also_ensures true ;
      //also_exsures (NOException) false ;
    */
    public byte getMois() {
	return super.getMois();
    }
    
    /*@
      //also_modifies \nothing
      also_ensures true ;
      //also_exsures (NOException) false ;
    */
    public byte getJour() {
	return super.getJour();
    }
    
    /*@
      //also_modifies \nothing
      also_ensures true ;
      //also_exsures (NOException) false ;
    */
    public byte getAnnee() {
	return super.getAnnee();
    }

    /*@
      //also_modifies \nothing
      also_ensures true ;
      //also_exsures (NOException) false ;
    */
    public short getTypeProduit() {
	return super.getTypeProduit();
    }
    
}
