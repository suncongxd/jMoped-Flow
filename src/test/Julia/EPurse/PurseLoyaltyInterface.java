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


// modified by Rodolphe Muller on 05/05/2000
//import com.gemplus.pacap.utils.PacapShareable;
import javacard.framework.Shareable;
import javacard.framework.AID;


/**
 *	Interface que le purse met � disposition des loyalties.
 *	Elle est utilis� pour l'envoie de la commande "getTransaction" lorsque le loylaty
 *	veut obtenir les transactions de l'historique
 */
//public interface PurseLoyaltyInterface extends PacapShareable { 
public interface PurseLoyaltyInterface extends Shareable { 
    
    
    ///////////////     CONSTRUCTEUR     ////////////////
    
    
    ////////////////       METHODES      ///////////////
    
    /**     
     *	m�thode invoqu�e par le loyalty qui d�sire obtenir les transactions qui
     *	le concerne.      
     *	@param aid les octets de l'aid du loyalty
     *	@return une objet partageable qui repr�sente une transaction
     */
    // modified by RM on 27/07/2000
    // only simple type values, and Shareable object can be transmitted through
    // the firewall.
    //	public TransactionInterface getTransaction(byte[] aid);
    
    // Modified by H. MARTIN 17/01/01
    //	public Shareable getTransaction();
    /*@
      requires loyaltyAID != null;
      ensures true ;
      //exsures (Exception) false ;
    */
    public TransactionInterface getTransaction(AID loyaltyAID);
    
    /**
     *	Test si il y a dans l'historique des transactions qui concernent
     *	le loyalty appelant.
     *	@param aid les octets de l'aid du loyalty
     */
    // modified by RM on 27/07/2000
    // only simple type values, and Shareable object can be transmitted through
    // the firewall.
    //	public boolean isThereTransaction(byte[] aid);
    
    // Modified by H. MARTIN and L. CASSET 18/01/01
    //	public boolean isThereTransaction();
    /*@
      requires loyaltyAID != null;
      ensures true ;
      //exsures (Exception) false ;
    */
    public boolean isThereTransaction(AID loyaltyAID);
    
    /**
     *	Demande la partie enti�re de l'inverse du taux de change actuel
     */
    /*@
      requires true ;
      ensures true ;
      //exsures (Exception) false ;
    */
    public short getInvExchangeRateIntPart();
    
    /** 
     *	Demande la partie d�cimale de l'inverse du taux de change actuel
     */
    /*@
      requires true ;
      ensures true ;
      //exsures (Exception) false ;
    */
    public short getInvExchangeRateDecPart();
    
    
    /*
      // temporarily added by Rodolphe Muller on 16/08/2000
      public byte getInvExchangeRateDigit(short index);
    */
}
