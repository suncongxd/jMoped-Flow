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

// temporarily removed by Rodolphe Muller on 05/07/2000
/*
import com.gemplus.pacap.utils.Date;
import com.gemplus.pacap.utils.DateException;
import com.gemplus.pacap.utils.Heure;
import com.gemplus.pacap.utils.HeureException;
*/

import javacard.framework.Util;

// Code modified by Nestor CATANO 21/05/2001
//import instruction is needed by ESC/Java to refer TransactionException
import javacard.framework.TransactionException;
// import instruction is needed by ESC/Java to refer _transactionDepth
import javacard.framework.JCSystem;

/* the currency exchange session are stored in objects of this type*/
public class ExchangeSession {
    /*@
      invariant heure != null ;
      invariant date != null ;
      invariant terminalSN != null ;
      invariant terminalTC != null ;
      invariant id != null ;
      invariant TTC_LENGTH == terminalTC.length ;
      invariant TSN_LENGTH == terminalSN.length ;
      invariant ID_LENGTH == id.length ;
      invariant id != terminalSN && id != terminalTC && 
                terminalSN != terminalTC
    */

	////////////////      ATTRIBUTES       ////////////////

	public static final byte ID_LENGTH			= (byte)4;
	public static final byte TTC_LENGTH			= (byte)4;
	public static final byte TSN_LENGTH			= (byte)4;
	public static final byte WELL_TERMINATED		= (byte)0x01;
	public static final byte ABORTED			= (byte)0x02;

/* number of the transaction that identifies it among other purse transactions*/
/*@ spec_public */ private short sessionNumber;

/* date of the transaction*/
/*@ spec_public */ private Date date = new Date();

/* time of th transaction*/
/*@ spec_public */ private Heure heure = new Heure();

/* exchange operator identifier*/
/*@ spec_public */ private byte[] id = new byte[ID_LENGTH];

/* currency before the exchange*/
/*@ spec_public */ private byte ancienneDevise;

/* the new currency*/
/*@ spec_public */ private byte nouvelleDevise;

/* terminal transaction counter*/
/*@ spec_public */ private byte[] terminalTC = new byte[TTC_LENGTH];

/* terminal serial number*/
/*@ spec_public */ private byte[] terminalSN = new byte[TSN_LENGTH];    

    /*@ invariant status == ABORTED ||
                  status == WELL_TERMINATED
    */
/* status of the transaction*/
/*@ spec_public */ private byte status = ABORTED;
/* boolean indicating whether this object is valid or not*/
/*@ spec_public */ boolean isValid = false;

	///////////////     CONSTRUCTOR     ////////////////

/*
	ExchangeSession() {
		super();
		date = new Date();*
		heure = new Heure();*
		id = new byte [ID_LENGTH];*
		terminalTC = new byte[TTC_LENGTH];*
		terminalSN = new byte[TSN_LENGTH];*
	}
*/

	////////////////       METHODS      ///////////////

    /*@
      modifies sessionNumber, ancienneDevise, nouvelleDevise, isValid,
               terminalTC[*], terminalSN[*], date.jour, date.mois, date.annee,
               heure.heure, heure.minute;
      requires true; 
      ensures sessionNumber == (short)-1;
      ensures ancienneDevise == 0;
      ensures nouvelleDevise == 0;
      ensures isValid == false;
      ensures (\forall int i; 0 <= i & i < TTC_LENGTH ==> terminalTC[i] == 0);
      ensures (\forall int i; 0 <= i & i < TSN_LENGTH ==> terminalSN[i] == 0);
      //exures (RuntimeException)false;
     */
	void reset() {
		sessionNumber = (short)-1;
		try {
			date.setDate((byte)1, (byte)1, (byte)99);
			heure.setHeure((byte)0, (byte)0);
		} catch(DateException de) {//@ assert false;
		} catch(HeureException ee) {//@ assert false;
		}
                ancienneDevise = (byte)0;
		nouvelleDevise = (byte)0;
		Util.arrayFillNonAtomic(terminalTC, (short)0, TTC_LENGTH, (byte)0);
		Util.arrayFillNonAtomic(terminalSN, (short)0, TSN_LENGTH, (byte)0);
		isValid = false;
	}

    /*@
      modifies sessionNumber, date.jour, date.mois, date.annee, 
               heure.heure, heure.minute;
      modifies ancienneDevise, nouvelleDevise, isValid, status ;
      modifies id[*], terminalTC[*], terminalSN[*] ;
      requires es != null ;
      requires es.id != terminalTC & es.id != terminalSN &
               es.terminalTC != terminalSN;
      ensures sessionNumber == es.sessionNumber ;
      ensures ancienneDevise == es.ancienneDevise ;
      ensures nouvelleDevise == es.nouvelleDevise ;
      ensures isValid == es.isValid ;
      ensures status == es.status ;
      ensures (\forall int i; 0 <= i & i < ID_LENGTH ==> es.id[i] == id[i]);
      ensures (\forall int i; 0 <= i & i < TTC_LENGTH ==> 
                                           es.terminalTC[i] == terminalTC[i]);
      ensures (\forall int i; 0 <= i & i < TSN_LENGTH ==> 
                                           es.terminalSN[i] == terminalSN[i]);
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    void clone(ExchangeSession es)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException {
	sessionNumber = es.getSessionNumber();
	try {
	    date.setDate(es.getDate());
	    heure.setHeure(es.getHeure());
	} catch(DateException de) {//@ assert false;
	} catch(HeureException ee) {//@ assert false;
        }
	Util.arrayCopy(es.getId(), (short)0, id, (short)0, ID_LENGTH);
	Util.arrayCopy(es.getTerminalTC(), (short)0, terminalTC, (short)0, TTC_LENGTH);
	Util.arrayCopy(es.getTerminalSN(), (short)0, terminalSN, (short)0, TSN_LENGTH);
	ancienneDevise = es.getAncienneDevise();
	nouvelleDevise = es.getNouvelleDevise();
	isValid = es.isValid();
	// added by LC and HM 11/01/2001
	status = es.getStatus();
    } 

    /*@
      //modifies \nothing
      requires true ;
      ensures \result == id ;
      //exsures (RuntimeException) false ;
    */
	byte[] getId() {
		return id;
	}

    /*@
      //modifies \nothing
      requires true ;
      ensures \result == terminalTC ;
      //exsures (RuntimeException) false ;
    */
	byte[] getTerminalTC() {
		return terminalTC;
	}

    /*@
      //modifies \nothing
      requires true ;
      ensures \result == terminalSN ;
      //exsures (RuntimeException) false ;
    */
	byte[] getTerminalSN() {
		return terminalSN;
	}

    /*@
      modifies id[*] ;
      requires bArray != null && off >= 0 && off + ID_LENGTH <= bArray.length;
      ensures (\forall int i; 0 <= i & i < ID_LENGTH ==> 
                                           id[i] == bArray[i + off]);
      //exsures (RuntimeException)false ;
    */
	void setId(byte[] bArray, short off) {
		Util.arrayCopyNonAtomic(bArray, off, id, (short)0, ID_LENGTH);
	}

    /*@
      modifies terminalTC[*] ;
      requires bArray != null & off >= 0 & off + TTC_LENGTH <= bArray.length ;
      ensures (\forall int i; 0 <= i & i < TTC_LENGTH ==> 
                                           terminalTC[i] == bArray[i + off]);
      //exsures (RuntimeException)false ;
    */
	void setTerminalTC(byte[] bArray, short off) {
		Util.arrayCopyNonAtomic(bArray, off, terminalTC, (short)0, TTC_LENGTH);
	}

    /*@
      modifies terminalSN[*] ;
      requires bArray != null & off >= 0 & off + TSN_LENGTH <= bArray.length ;
      ensures (\forall int i; 0 <= i & i < TSN_LENGTH ==> 
                                           terminalSN[i] == bArray[i + off]);
      //exsures (RuntimeException)false ;
    */
	void setTerminalSN(byte[] bArray, short off) {
		Util.arrayCopyNonAtomic(bArray, off, terminalSN, (short)0, TSN_LENGTH);
	}


/* copy the saler id or the credit operator in the given table
 *	@param bArray : the destination table
 *	@param off : the destination offset
 *	@return the size of the identifier and the offset*/
    /*@
      modifies bArray[*] ;
      requires bArray != null && off >= 0 && off + ID_LENGTH <= bArray.length ;
      ensures (\forall int i; 0 <= i & i < ID_LENGTH ==> 
                                           bArray[i + off] == id[i]);
      ensures \result == off + ID_LENGTH;
      //exsures (RuntimeException)false ;
    */
	short getId(byte[] bArray, short off) {
		short offset = off;
		offset = Util.arrayCopyNonAtomic(id, (short)0, bArray, offset, ID_LENGTH);
        return offset;
	}

/* copy the terminal transaction counter into this transaction
 *	@param bArray : the destination table
 *	@param off : the destination offset
 *	@return:  off + identifier size*/
    /*@
      modifies bArray[*] ;
      requires bArray != null & off >= 0 & off + TTC_LENGTH <= bArray.length;
      ensures (\forall int i; 0 <= i & i < TTC_LENGTH ==> 
                                           bArray[i + off] == terminalTC[i]);
      ensures \result == off + TTC_LENGTH;
      //exsures (RuntimeException)false ;
    */
	short getTerminalTC(byte[] bArray, short off) {
		short offset = off;
		offset = Util.arrayCopyNonAtomic(
			terminalTC, (short)0, bArray, offset, TTC_LENGTH
		);
		return offset;
	}

/* copy the terminal serial number into this transaction
 *	@param bArray : the destination table
 *	@param off : the destination offset
 *	@return off + identifier size	 */
    /*@
      modifies bArray[*] ;
      requires bArray != null & off >= 0 & off + TSN_LENGTH <= bArray.length;
      ensures (\forall int i; 0 <= i & i < TSN_LENGTH ==> 
                                           bArray[i + off] == terminalSN[i]);
      ensures \result == off + TSN_LENGTH;
      //exsures (RuntimeException)false ;
    */
	short getTerminalSN(byte[] bArray, short off) {
		short offset = off;
		offset = Util.arrayCopyNonAtomic(
			terminalSN, (short)0, bArray, offset, TSN_LENGTH
		);
		return offset;
	}

    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == sessionNumber ;
      //exsures (RuntimeException)false ;
     */
	short getSessionNumber() {
		return sessionNumber;
	}

    /*@
      modifies sessionNumber ;
      requires true ;
      ensures sessionNumber == n ;
      //exsures (RuntimeException)false
     */
	void setSessionNumber(short n) {
		sessionNumber = n;
	}

    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == date ;
      //exsures (RuntimeException)false
    */
	Date getDate() {
		return date;
	}

    /*@
      modifies date.jour, date.mois, date.annee ;
      requires d != null ;
      ensures date.jour == d.jour && date.annee == d.annee && 
              date.mois == d.mois ;
      exsures (DateException)false
    */
	void setDate(Date d) throws DateException {
		date.setDate(d);
	}

    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == heure ;
      //exsures (RuntimeException)false
     */
	Heure getHeure() {
		return heure;
	}

    /*@
      modifies heure, heure.heure, heure.minute ;
      requires h != null ;
      ensures heure.heure == h.heure && heure.minute == h.minute ;
      exsures (Exception)false
    */
	void setHeure(Heure h) throws HeureException {
		heure.setHeure(h);
	}

    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == ancienneDevise ;
      //exsures (RuntimeException)false
    */
	byte getAncienneDevise() {
		return ancienneDevise;
	}

    /*@
      modifies ancienneDevise ;
      requires true ;
      ensures ancienneDevise == d ;
      //exsures (RuntimeException)false
    */
	void setAncienneDevise(byte d) {
		ancienneDevise = d;
	}
    
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == nouvelleDevise ;
      //exsures (RuntimeException)false
    */
	byte getNouvelleDevise() {
		return nouvelleDevise;
	}

    /*@
      modifies nouvelleDevise ;
      requires true ;
      ensures nouvelleDevise == d ;
      //exsures (RuntimeException)false
    */
	void setNouvelleDevise(byte d) {
		nouvelleDevise = d;
	}

    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == isValid ;
      //exsures (RuntimeException)false
    */
	boolean isValid() {
		return isValid;
	}

    /*@
      modifies isValid ;
      requires true ;
      ensures isValid == true ;
      //exsures (RuntimeException)false
    */
	void valid() {
		isValid = true;
	}

    /*@
      modifies isValid ;
      requires true ;
      ensures isValid == false ;
      //exsures (RuntimeException)false
    */
	void unvalid() {
		isValid = false;
	}

    /*@
      modifies status ;
      requires s == ABORTED || s == WELL_TERMINATED;
      ensures status == s ;
      //exsures (RuntimeException)false
    */
	void setStatus(byte s) {
		status = s;
	}
    
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == status ;
      //exsures (RuntimeException)false
    */
	byte getStatus() {
		return status;
	}

}

