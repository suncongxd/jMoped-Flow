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

// Code modified by Nestor CATANO 21/05/2001
//import instruction is needed by ESC/Java to refer TransactionException
import javacard.framework.TransactionException;
// import instruction is needed by ESC/Java to refer _transactionDepth
import javacard.framework.JCSystem;

// huge modifications on 31/07/2000
// now, we handle AID objects instead of byte arrays that represents them.
// that way, we gain some space, and code is clearer.

/* the purse stores in this object the loyalties that are allowed to communicate with it*/
class LoyaltiesTable {
    /*@
      invariant \nonnullelements(data) ;
      invariant data.length == NB_MAX ;
      invariant 0 <= nbLoyalties ;
      invariant nbLoyalties <= data.length ;
    */
    
    ////////////////      ATTRIBUTES       ////////////////
    
    // temporarily modified by RM on 26/07/2000 for memory reason
    //	private final static byte NB_MAX   = (byte)20;
    /*@ spec_public */ private final static byte NB_MAX   = (byte)3;
    
    
    /*@ spec_public */ private AllowedLoyalty[] data = new AllowedLoyalty[NB_MAX];
    /*@ spec_public */ private byte nbLoyalties = (byte)0;
    
    ///////////////     CONSTRUCTOR     ////////////////
    /*@
      requires true ;
      ensures \fresh(this) ;
      ensures (\forall int k; (k >= 0 && k<NB_MAX)==> \fresh(data[k])) ;
      ensures nbLoyalties == 0 ;
      //exsures (Exception) false ;
    */
    LoyaltiesTable() {
	super();
	for(byte i = 0;i < NB_MAX;i++) {
	    data[i] = new AllowedLoyalty();
	}
    }
    
    ////////////////       METHODS      ///////////////
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == nbLoyalties ;
      //exsures \nothing ;
    */
    byte getNbLoyalties() {
	return nbLoyalties;
    }
    
    /* add a loyalty in the list of loyalties
     *	@param aid a table of bytes that represents the aid of the loyalty
     *	@param b : a boolean indicating if the loyalty has subscribed to the logfull service
     *	@param com : a table containing up to 5 saler identifier where points can be earned with
     this loyalty program. 1 id is 4 bytes long.
     *	@return l: the index of the new entry in the table
     */
    /*@
      modifies nbLoyalties, data[*] ;
      requires nbLoyalties < NB_MAX ;
      requires aid != null && aid.length >= 16 ;
      requires com != null && com.length >= 24 ;
      ensures (\forall int k; (k >= 0 && k< 16)==> 
                              data[\old(nbLoyalties)].aid[k] == aid[k]);
      ensures data[\old(nbLoyalties)].logfullInformation == b;
      ensures (\forall int i; i >= 0 & i < 5 ==>
                  (\forall int j; 0 >= j & j < SalerID.ID_LENGTH ==> 
                   (data[\old(nbLoyalties)].data[i].data[j] == 
                        com[i * SalerID.ID_LENGTH + j])));
      ensures data[\old(nbLoyalties)].nbSalers == 20/SalerID.ID_LENGTH;
      ensures nbLoyalties == \old(nbLoyalties) + 1 ;
      ensures \result == nbLoyalties ;
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                       && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
      exsures (LoyaltiesTableException) false ;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of the first 3 exceptions in the throws clause
    byte addLoyalty(byte[] aid, boolean b, byte[] com)
	throws  ArrayIndexOutOfBoundsException, 
		NullPointerException, 
		TransactionException,
		LoyaltiesTableException, 
		AllowedLoyaltyException {
	byte resu = 0;
	if(nbLoyalties < NB_MAX) {
	    data[nbLoyalties].reset();
	    data[nbLoyalties].setAID(aid);
	    data[nbLoyalties].toBeInformed(b);
	    for(byte i = 0;i < 20;i += SalerID.ID_LENGTH) {
		data[nbLoyalties].addSaler(com, i);
	    }
	    nbLoyalties++;
	    resu = nbLoyalties;
	} else {
	    byte t = LoyaltiesTableException.NB_LOYALTIES_OVERFLOW;
	    LoyaltiesTableException.throwIt(t);
	}
	return resu;
    }

	/* suppress all the entries of the loyalty specified by aid	 */
    // MH, 29/10/01: see comments DelSaler in AllowedLoyalty
    /*@
      modifies nbLoyalties, data[*] ;
      requires aid != null ;

      // cannot be established by ESC/Java, because of LOOP
      ensures (\forall int i; 0 <= i & i < nbLoyalties ==>
                  (\exists int k; 0 <= k & k < data[i].aid.length &&
                                  (data[i].aid[k] != aid.theAID[k])));
      // cannot be established by ESC/Java, because of LOOP
      ensures (\forall int i; 0 <= i & i < \old(nbLoyalties) ==>
                  (\forall int k; 0 <= k & k < data[i].aid.length ==>
                                  (\old(data[i].aid[k]) == aid.theAID[k])) 
                  ||
                  (\exists int j; 0 <= j & j < nbLoyalties &&
                      (\forall int k; 0 <= k & k < data[i].aid.length ==>
                          \old(data[i].aid[k]) == data[j].aid[k])));
      ensures nbLoyalties <= \old(nbLoyalties);
      exsures (TransactionException e) 
                 e._reason == TransactionException.BUFFER_FULL 
                 && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    void delLoyalty(AID aid)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException  {
	//search for the index
	byte i = 0 ;
	while(i < nbLoyalties) {
	    AllowedLoyalty al = data[i];
	    if(al.getAID().equals(aid)) {
		// suppress the offset
		for(byte j = (byte)(i+1);j < nbLoyalties;j++) {
		    data[(short)(j - 1)].clone(data[j]);
		}
		// update the entries number
		nbLoyalties--;
	    } else
		i++;
	}
    }

    /*@
      modifies nbLoyalties, data[*] ;
      requires index >= 0 && index < nbLoyalties ;
      ensures nbLoyalties == (byte)(\old(nbLoyalties)-1); 
      ensures (\forall byte k; (k>=(byte)(index+1) && k<nbLoyalties)==> 
                               (\forall int p; (p>=0 && p<16)==>data[(short)(k-1)].aid[p]==0) &&
			       data[(short)(k-1)].logfullInformation == \old(data[k]).logfullInformation &&
			       data[(short)(k-1)].nbSalers == \old(data[k]).nbSalers
			       ) ; 
      ensures (index < 0 || index >= nbLoyalties)==>(\forall int k; data[k] == \old(data[k])) ;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause  
    void delLoyalty(byte index)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException {
	if(index >= 0 && index < nbLoyalties) {
	    // suppress the offset
	    for(byte j = (byte)(index+1);j < nbLoyalties;j++) {
		data[(short)(j - 1)].clone(data[j]);
	    }
	    // update the entries number
	    nbLoyalties--;
	}
    }
    
	/*	@return the entry corresponding to the index*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == (index >= 0 && index < nbLoyalties ? 
                          data[index] : 
                          null) ;
      //exsures (RuntimeException) false ;
    */
    AllowedLoyalty getAllowedLoyalty(byte index) {
	AllowedLoyalty resu = null;
	if(index >= 0 && index < nbLoyalties) {
	    resu = data[index];
	}
	return resu;
    }
    
    /*	@return the first entry corresponding to aid*/
    // missing in specification: that it is the first element
    /*@
      //modifies \nothing ;
      requires appletAID != null ;

      // cannot be established by ESC/Java, because of LOOP
      ensures (\forall int i; 0 <= i & i < nbLoyalties ==>
                  (\exists int k; 0 <= k & k < data[i].aid.length &&
                      data[i].aid[k] != appletAID.theAID[k])) ==> 
                        \result == null;
      // cannot be established by ESC/Java, because of LOOP
      ensures (\exists int i; 0 <= i & i < nbLoyalties && 
                  (\forall int k; 0 <= k & k < data[i].aid.length ==>
                      data[i].aid[k] == appletAID.theAID[k])) ==> 
                      \result != null;
      //exsures (RuntimeException) false ;      
    */
    AllowedLoyalty getAllowedLoyalty(AID appletAID) {
	AllowedLoyalty resu = null;
	byte i = 0;
	boolean found = false;
	while(i < nbLoyalties && !found) {
	    AllowedLoyalty al = data[i];
	    if(appletAID.equals(al.getAID(), (short) 0, (byte) 16)) {
		found = true;
		resu = al;
	    }
	    else i++;
	}
	return resu;
    }
    
    /*@
      modifies data[*] ; 
      requires aid != null ;

      // cannot be established by ESC/Java, because of LOOP
      ensures (\forall int i; 0 <= i & i < nbLoyalties ==>
                  (\forall int k; 0 <= k & k < data[i].aid.length ==>
                      data[i].aid[k] == aid.theAID[k]) ==> 
                        !data[i].logfullInformation);
      //exsures (RuntimeException) false ;
    */
    void removeNotification(AID aid) {
	byte i = 0;        
	while(i < nbLoyalties) {
	    AllowedLoyalty al = data[i];
	    if(al.getAID().equals(aid)) {
		al.dontKeepInformed();
	    }
	    else
		i++;
	}
    }
    
    /* check if the aid provided in argument is one of a loyalty of our list*/
    /*@
      //modifies \nothing ;
      requires aid != null ;
      ensures \result ==
                  (\exists int i; 0 <= i & i < nbLoyalties &&
                     (\forall int k; 0 <= k & k < data[i].aid.length ==>
                         data[i].aid[k] == aid.theAID[k]));
      //exsures (RuntimeException) false ;
    */
    boolean contents(AID aid) {
	return (getAllowedLoyalty(aid) != null);
    }
    
}

