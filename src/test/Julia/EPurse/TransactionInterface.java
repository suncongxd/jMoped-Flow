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


// modified by Rodolphe Muller on 26/07/2000
//package com.gemplus.pacap.purse;

package test.Julia.EPurse;

// temporarily removed by Rodolphe Muller on 31/05/2000
// MH, 29/10/01, uncommented this for ESC/Java
//import com.gemplus.pacap.utils.*;

// modified by Rodolphe Muller on 05/05/2000
//import com.gemplus.pacap.utils.PacapShareable;
import javacard.framework.Shareable;

public interface TransactionInterface extends Shareable {
    
    
    // temporarily modified by RM 26/07/2000
    //	public final short ID_LENGTH = Transaction.PARTNER_ID_LENGTH;
    public final short ID_LENGTH = (short)4;
    
    ///////////////     CONSTRUCTEUR     ////////////////
    
    
    ////////////////       METHODES      ///////////////
    
    
    /**
     *	@return la partie enti�re du montant de la transaction
     */
    /*@
      requires true ;
      ensures \result >= 0 ;
      //exsures (Exception) false ;
     */
    public short getIntPartMontant();
    
    /**
     *	@return la partie d�cimale du montant de la transaction
     */
    /*@
      requires true ;
      ensures \result >= 0 && \result < Decimal.PRECISION;
      //exsures (Exception) false ;
    */
    public short getDecPartMontant();
    
    /*
      // temporarily added by Rodolphe Muller on 16/08/2000	 
      public byte getMontantDigit(short index);
    */
    
    /**
     *	@return la devise du montant
     */
    /*@
      requires true ;
      ensures true ;
      //exsures (Exception) false ;
    */
    public byte getDevise();
    
    /**
     *	retourne l'octet d'index <code>n</code>  de l'identifiant du commer�ant
     *	�vite d'utiliser le buffer APDU
     */
    /*@
      requires n >= 0 & n < ID_LENGTH;
      ensures true ;
      //exsures (Exception) false ;
    */
    public byte getId(byte n);
    
    /**
     *	@return la longueur de l'identifiant du commer�ant
     */
    /*@
      requires true ;
      ensures \result >= 0 ;
      //exsures (Exception) false ;
    */
    public short getIdLength();
    
    /**
     *	@return le nombre de transaction restantes
     */
    /*@
      requires true ;
      ensures \result >= 0 ;
      //exsures (Exception) false ;
    */
    public byte getReste();

    /**
     *	@return le type du produit
     */
    /*@
      requires true ;
      ensures true ;
      //exsures (Exception) false ;
    */
    public short getTypeProduit();
    
    /**
     *	@return le mois d'achat
     */
    /*@
      requires true ;
      ensures \result >= Mois.MIN && \result <= Mois.MAX ;
      //exsures (Exception) false ;
    */
    public byte getMois();
    
    /**
     *	@return le jour du mois d'achat
     */
    /*@
      requires true ;
      ensures \result >= Jour.MIN && \result <= Jour.MAX ;
      //exsures (Exception) false ;
    */
    public byte getJour();
    
    /**
     *	@return l'ann�e de l'achat
     */
    /*@
      requires true ;
      ensures \result >= Annee.MIN && \result <= Annee.MAX ;
      //exsures (Exception) false ;
    */
    public byte getAnnee();
    
}

