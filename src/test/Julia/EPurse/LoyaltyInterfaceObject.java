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

import javacard.framework.AID;
import javacard.framework.JCSystem;
import javacard.framework.Shareable;

//import com.gemplus.pacap.pacapinterfaces.PurseLoyaltyInterface;
//import com.gemplus.pacap.pacapinterfaces.TransactionInterface;

/* shareable object that realises the interface between the purse and the loyalties*/
public class LoyaltyInterfaceObject extends Object implements PurseLoyaltyInterface {
    //@invariant purse != null ;
    
    ////////////////      ATTRIBUTES       ////////////////
    
    /* constants define in this interface. The client applet must send this identifier as parameter
       when calling getAppletShareableInterfaceObject(byte)to specify that this is the requested
       interface
    */
    public static byte interfaceID = (byte)0x01;
    /* link this shared object to the shared data using the methods of the cardIssuer*/
    private static Purse purse = null;
    
    
    ///////////////     CONSTRUCTOR     ////////////////
    
    /*@
      requires p != null ;
      ensures \fresh(this) ;
      // exsures (RuntimeException) false ;
    */
    LoyaltyInterfaceObject (Purse p){
	super();
	purse = p;
    }
    
    
    ////////////////       METHODS      ///////////////
    
    
    
    /* implement the command that allows to obtain transactions
     *	@param aid : the loyalty aid
     *	@return the transaction
     */
    // modified by Rodolphe Muller on 27/07/2000
    //	public TransactionInterface getTransaction(byte[] aid) {
    
    // Modified by H. MARTIN 17/01/01
    //	public Shareable getTransaction() {
    // Modified by H. MARTIN and L. CASSET 18/01/01
    //	public TransactionInterface getTransaction() {
    /*@
      // also_exsures (RuntimeException) false ;
    */
    public TransactionInterface getTransaction(AID loyaltyAID) {
	TransactionInterface resu = null;
	// GemXpresso v1.0 renvoie toujours null !!!
	// uncommented by Rodolphe Muller on 27/07/2000
	// Modified by H. MARTIN and L. CASSET 18/01/01
	//		AID aid = JCSystem.getPreviousContextAID();
	//		resu = purse.getTransaction(aid);
	resu = purse.getTransaction(loyaltyAID);
	return resu;
    }
    
    /* check if there is a transaction for the calling loyalty
     *	@param aid the loyalty aid*/
    // modified by Rodolphe Muller on 27/07/2000
    //	public boolean isThereTransaction(byte [] aid) {
    // Modified by H. MARTIN and L. CASSET 18/01/01
    //	public boolean isThereTransaction() {
    /*@
      // also_exsures (RuntimeException) false ;
    */
    public boolean isThereTransaction(AID loyaltyAID) {
	// GemXpresso v1.0 always sends null
	// uncommented by Rodolphe Muller on 27/07/2000
	// Modified by H. MARTIN and L. CASSET
	//		AID aid = JCSystem.getPreviousContextAID();
	//		return purse.isThereTransaction(aid);
	return purse.isThereTransaction(loyaltyAID);
    }
    
    
    /*@
      also_requires true ;
      also_ensures true ;
      // also_exsures (RuntimeException) false ;
    */
    public short getInvExchangeRateDecPart() {
	return purse.getInvExchangeRate().getDecPart(); 
    }
    
    /*@
      also_requires true ;
      also_ensures true ;
      // also_exsures (RuntimeException) false ;
    */
    public short getInvExchangeRateIntPart() {
	return purse.getInvExchangeRate().getIntPart(); 
    }
    
    /*
      // temporarily added by Rodolphe Muller on 16/08/2000
      public byte getInvExchangeRateDigit(short index) {
      return purse.getInvExchangeRate().number[index];
      }
    */
}
