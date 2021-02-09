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
import javacard.framework.Util;
import javacard.framework.JCSystem;

// Code modified by Nestor CATANO 21/05/2001
//import instruction is needed by ESC/Java to refer TransactionException
import javacard.framework.TransactionException;
// import instruction is needed by ESC/Java to refer _transactionDepth
import javacard.framework.JCSystem;

/*This record allow to keep information about the loyalty program. It
  contains: 
  aid: the identifier of the loyalty
  logfullInformation: a boolean indicating whether the loyalty has
  subscribe or not to the logfull service proposed by the purse
  SalerId: the identifier of the different salers concerned by this
  loyalty program
  nbSalers: the number of salers in the loyalty program*/
class AllowedLoyalty {
    /*@
      invariant aid != null ;
      invariant \nonnullelements(data) ;
      invariant data.length == MAX_SALERS ;
      invariant 16 == aid.length ;
      invariant nbSalers >= 0 ;
    */

    ////////////////      ATTRIBUTES       ////////////////
    
    static final byte MAX_SALERS = (byte)5;
    
    /*@ spec_public */ private byte[] aid = new byte[16];
    /*@ spec_public */ private boolean logfullInformation = false;
    /*@ spec_public */ private SalerID[] data = new SalerID[MAX_SALERS];
    /*@ spec_public */ private byte nbSalers = (byte)0;

    ///////////////     CONSTRUCTOR     ////////////////
    /*@
      ensures \fresh(this) ;
      ensures (\forall int k; (k >= 0 && k < MAX_SALERS)==>\fresh(data[k])) ;
      ensures logfullInformation == false ;
      ensures nbSalers == (byte)0 ;      
      //exsures (Exception) false ;
    */
    AllowedLoyalty() {
	super();
	for(byte i = 0;i < MAX_SALERS;i++) {
	    data [i] = new SalerID();
	}
    }
    
    /*@
      ensures \fresh(this) ;
      ensures (\forall int k; (k >= 0 && k < MAX_SALERS)==>\fresh(data[k])) ;
      ensures this.logfullInformation == toBeInformed ;
      ensures nbSalers == (byte)0 ;
      //exsures (Exception) false ;
    */
    AllowedLoyalty(boolean toBeInformed) {
	this();
	logfullInformation = toBeInformed;
    }


    ////////////////       METHODS      ///////////////
    
    /* initialize the attribute values*/
    /*@
      modifies aid[*], data[*], logfullInformation, nbSalers ;
      requires true ;
      ensures (\forall int k; (k >= 0 & k < 16)==>(aid[k] == 0));
      ensures logfullInformation == false ;
      ensures (\forall int i, j; 0 <= i & i < MAX_SALERS ==>
                                 0 <= j & j < SalerID.ID_LENGTH ==> 
                                 data[i].data[j] == 0);
      ensures nbSalers == 0 ;
      //exsures (Exception) false ;
    */
    void reset() {
	for(byte i = 0;i < 16;i++) {
	    aid[i] = (byte) 0;
	}
	logfullInformation = false;
	for(byte i = 0;i < MAX_SALERS;i++) {
	    data[i].reset();
	}
	nbSalers = 0;
    }
    
    /* clone another instance*/
    /*@
      modifies aid[*], data[*], logfullInformation, nbSalers ;
      requires al != null ;
      ensures (\forall int i; 
                  0 <= i & i < aid.length ==> aid[i] == al.aid[i]);
      ensures this.logfullInformation == al.logfullInformation ;
      ensures (\forall int i, j; (0 <= i & i < MAX_SALERS &
                                  0 <= j & j < SalerID.ID_LENGTH) ==> 
                                       data[i].data[j] == al.data[i].data[j]);
      ensures this.nbSalers == al.nbSalers ;    
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    void clone(AllowedLoyalty al) 
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException {
	this.reset();
	Util.arrayCopy(al.getAID(), (short)0, this.aid, (short)0, (short)16);
	this.logfullInformation = al.isToBeInformed();
	for(byte i = 0;i < MAX_SALERS;i++) {
	    data[i].clone(al.getSaler(i));
	}
	this.nbSalers = al.getNbSalers();
    }
    
    /* test whether the argument is the aid of an authorised loyalty represented by this instance
     *	@param aid: the aid being checked
     *	@return: true if the aid is the correct one*/
    /*@
      //modifies \nothing ;
      requires aid != null ;
      ensures true; // To specify this (and have it checked by ESC/Java), 
                    // we need a fully specified version of aid.equals.
      //exsures (Exception) false ;
    */
    boolean isThis(AID aid) {
	return aid.equals(this.aid);
    }
    
    /* check if the loyalty must be informed if the purse record file is full*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == logfullInformation ;
      //exsures (Exception) false ;
    */    
    boolean isToBeInformed(){
	return logfullInformation;
    }
    
    /* set to true the notification boolean. The loyalty must be informed if the record file is full*/
    /*@
      modifies logfullInformation ;
      requires true ;
      ensures logfullInformation == true ;
      //exsures (Exception) false ;
    */    
    void keepInformed() {
	toBeInformed(true);
    }
    
    /* cancel the notification. The loyalty will not be informed if the record file is full*/
    /*@
      modifies logfullInformation ;
      requires true ;
      ensures logfullInformation == false ;
      //exsures (Exception) false ;
    */    
    void dontKeepInformed() {
	toBeInformed(false);
    }
    /* set the notification state: true or false
     *	@param : notification state*/
    /*@
      modifies logfullInformation ;
      requires true ;
      ensures logfullInformation == b ;
      //exsures (Exception) false ;
    */    
    void toBeInformed(boolean b) {
	logfullInformation = b;
    }

    /* set the AID*/
    /*@
      //modifies \nothing ;
      requires aidBytes != null && 16 <= aidBytes.length ;      
      ensures (\forall int i; 
                  0 <= i & i < aid.length ==> aid[i] == aidBytes[i]);
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause  
    void setAID(byte[] aidBytes)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException {
	Util.arrayCopy(aidBytes, (short)0, this.aid, (short)0, (short)16);
    }

    /* read the AID*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == aid ;
      //exsures (Exception) false ;
    */    
    byte[] getAID() {
	return aid;
    }

    /* obtain the number of salers concerned by thos loyalty program*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == nbSalers ;
      //exsures (Exception) false ;
    */    
    byte getNbSalers() {
	return nbSalers;
    }

    /* add a saler id in the list*/
    /*@
      modifies data[nbSalers], nbSalers ;
      requires s != null ;
      requires nbSalers < MAX_SALERS ;
      ensures nbSalers == (short)(\old(nbSalers) + 1) ;
      ensures (\forall int i; 0 <= i & i < SalerID.ID_LENGTH ==> 
                                   data[\old(nbSalers)].data[i] == s.data[i]);
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                       && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
      exsures (AllowedLoyaltyException) false ;
    */  
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of first 3 exceptions in the throws clause  
    void addSaler(SalerID s)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       AllowedLoyaltyException {
	if(nbSalers < MAX_SALERS) {
	    data[nbSalers++].clone(s);
	} else {
	    byte t = AllowedLoyaltyException.NB_SALERS_OVERFLOW;
	    AllowedLoyaltyException.throwIt(t);
	}
    }
    
    /*@
      modifies data[nbSalers], nbSalers ;
      requires s != null && off >=0 && off+SalerID.ID_LENGTH <= s.length ;
      requires nbSalers < MAX_SALERS ;
      ensures nbSalers == (short)(\old(nbSalers) + 1) ;
      ensures (\forall int i; 0 <= i & i < SalerID.ID_LENGTH ==> 
                  data[\old(nbSalers)].data[i] == s[off + i]);
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                       && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (AllowedLoyaltyException) false ;
    */    
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    void addSaler(byte[] s, short off) 
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       AllowedLoyaltyException {
	if(nbSalers < MAX_SALERS){
	    data[nbSalers++].setBytes(s, off);
	} else {
	    byte t = AllowedLoyaltyException.NB_SALERS_OVERFLOW;
	    AllowedLoyaltyException.throwIt(t);
	}
    }

    /* suppress all the entries for a given saler*/
    
    // MH, 29/10/01: all remaining salers are unequal to id
    //               and for every original salers, either it is equal to id,
    //               or it is still there (but nr. of occurences might have
    //               changed according to specification
    /*@
      requires id != null ;
      modifies data[*], nbSalers ;
      ensures (\forall int i; 0 <= i & i < nbSalers ==>
                  (\exists int k; 0 <= k & k < SalerID.ID_LENGTH &&
                                  (data[i].data[k] != id[k])));

      // possibly not established
      ensures (\forall int i; 0 <= i & i < \old(nbSalers) ==>
                 ((\forall int k; 0 <= k & k < SalerID.ID_LENGTH ==>
                                  (\old(data[i].data[k]) == id[k])) 
                  ||
                  (\exists int j; 0 <= j & j < nbSalers &&
                      (\forall int k; 0 <= k & k < SalerID.ID_LENGTH ==>
                          \old(data[i].data[k]) == data[j].data[k]))));
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) SalerID.ID_LENGTH > id.length ;
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                       && JCSystem._transactionDepth == 1; 
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause 
    void delSaler(byte[] id)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException,  
	       TransactionException {
	//look for the index
	byte i = 0;
	while(i < nbSalers) {
	    byte comp = Util.arrayCompare(
					  data[i].getBytes(), (short)0, id, (short)0, SalerID.ID_LENGTH
					  );
	    if(comp == 0) {
		// suppress the offset
		for(byte j = (byte)(i+1);j < nbSalers;j++) {
		    data[(short)(j-1)].clone(data[j]);
		}
		//   update the entries number
		nbSalers--;
	    }
	    else i++;
	}
    }
    
    /* send the saler id of the given index*/
    /*@
      //modifies \nothing ;
      requires index >= 0 && index < data.length ;
      ensures \result == data[index] ;
      //exsures (Exception) false ;
    */
    SalerID getSaler(byte index) {
	return data[index];
    }
    
    /* return the saler list (a refernce list over the SalerID*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == data ;
      //exsures (Exception) false ;
    */
    SalerID[] getSaler() {
	return data;
    }

}

