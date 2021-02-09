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

//import com.gemplus.pacap.utils.Decimal;
//import com.gemplus.pacap.utils.DecimalException;

// temporarily remmoved by Rodolphe Muller on 05/07/2000
/*
  import com.gemplus.pacap.utils.Date;
  import com.gemplus.pacap.utils.DateException;
  import com.gemplus.pacap.utils.Heure;
  import com.gemplus.pacap.utils.HeureException;
*/
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;

//Code added by Nestor CATANO 23/5/01
import javacard.framework.TransactionException ;

/* a transaction peformed by the purse is stored in objects of this type*/
class Transaction extends Object {
    /*@
      invariant certificat != null ;
      invariant id != null ;
      invariant terminalSN != null ; 
      invariant terminalTC != null ;
      invariant date != null ;
      invariant heure != null ;
      invariant taux != null ;
      invariant montant != null ;
      invariant CERTIFICATE_LENGTH == certificat.length ;
      invariant PARTNER_ID_LENGTH == id.length ;
      invariant 4 == terminalSN.length ;
      invariant 4 == terminalTC.length ;
      invariant terminalTC != terminalSN;

      invariant status == WELL_TERMINATED ||
                status == ABORTED ||
                status == CERTIFICATE_ERROR ||
                status == CERTIFICATE_MISSING ||
		status == INDETERMINE;

      invariant type == INDETERMINE ||
                type == TYPE_CREDIT || 
                type == TYPE_DEBIT;
    */
    
    ////////////////      ATTRIBUTES       ////////////////
    
    /* the transaction status*/
    public static final byte INDETERMINE		= (byte)0;
    /* the transaction status*/
    public static final byte WELL_TERMINATED		= (byte)1;
    /* the transaction status*/
    public static final byte ABORTED			= (byte)2;
    /* the transaction status*/
    public static final byte CERTIFICATE_ERROR		= (byte)3;
    /* the transaction status*/
    public static final byte CERTIFICATE_MISSING	= (byte)4;

	/* the transaction status*/
    public static final byte TYPE_CREDIT		= (byte)50;
    /* the transaction status*/
    public static final byte TYPE_DEBIT			= (byte)51;
    
    
    /* the size of the salers and the credit operator identifier in bytes*/
    public static final short PARTNER_ID_LENGTH = (short)4;

    /* the size of the transaction certificate in bytes*/
    public static final short CERTIFICATE_LENGTH = (short)8;

    /* the transaction number that identifies this transaction among
       the others*/
    /*@ spec_public*/ private short number;   

    /* the terminal transaction counter: 4 bytes*/
    /*@ spec_public*/ private byte[] terminalTC = new byte[4];

    /* the transaction type: debit or credit*/
    /*@ spec_public*/ private byte type;

    /* the saler or the credit operator identifier: 4 bytes*/
    /*@ spec_public*/ private byte[] id = new byte[PARTNER_ID_LENGTH];

    /* the product type. It is used by the loyalty when it computes
       the number of points related with this trasaction*/
    /*@ spec_public*/ private short typeProduit;

    /* the transaction currency*/
    /*@ spec_public*/ private byte devise;

    /* the rate between the reference currency of the purse and the
       current currency*/
    /*@ spec_public*/ private Decimal taux = new Decimal();

    /* the transaction amount*/
    /*@ spec_public*/ private Decimal montant = new Decimal();

    /* the transaction date*/
    /*@ spec_public*/ private Date date = new Date();

    /* the transaction time*/
    /*@ spec_public*/ private Heure heure = new Heure();

    /* the transaction certificate: 8 bytes*/
    /*@ spec_public*/ private byte[] certificat = new byte[CERTIFICATE_LENGTH];

    /* the mutual authentication boolean*/
    /*@ spec_public*/ private boolean mutualAuthentification = false;

    /* the bank authorization boolean*/
    /*@ spec_public*/ private boolean bankAutorization = false;

    /* the terminal serial number*/
    /*@ spec_public*/ private byte[] terminalSN = new byte[4];

    /* this boolean indeicates whether this transaction has been given
       to a loyalty or not*/
    /*@ spec_public*/ private boolean given = false;

    /*transaction status*/
    /*@ spec_public*/ private byte status = INDETERMINE;

    /* this boolean indeicates whether this transaction is valid or
       not, i.e. the contained informations are correct or not*/
    /*@ spec_public*/ private boolean isValid = false;
    
    ///////////////     CONSTRUCTOR     ////////////////
    
    // temporarily moved by Rodolphe Muller on 15/06/2000
    /*
      Transaction() {
      super();
      //id = new byte [PARTNER_ID_LENGTH];*
      //taux = new Decimal();*
      //montant = new Decimal();*
      //date = new Date();*
      //heure = new Heure();*
      
      // 128 bits de certificat*
      //certificat = new byte [CERTIFICATE_LENGTH];*
      // 4 octets de num�ro de serie
      //terminalSN = new byte [4];*
      // 4 octets de compteur de transaction du terminal
      //terminalTC = new byte [4];*
      }
    */
    
    ////////////////       METHODES      ///////////////
    
    /* clone the attributes of this transaction from the transaction passed as argument
     *	@param t: a transaction used as a model*/
    /*@
      modifies JCSystem._transactionDepth, number, type, devise, typeProduit ;
      modifies taux.intPart, taux.decPart, montant.intPart, montant.decPart ;
      modifies date.jour, date.mois, date.annee, heure.heure, heure.minute ;
      modifies id[*], certificat[*], terminalSN[*], terminalTC[*] ;
      modifies mutualAuthentification, bankAutorization, status, given, isValid ;
      requires t != null ;
      requires t.terminalSN != terminalTC;
      ensures JCSystem._transactionDepth == 0 ;
      ensures number == t.number ;
      ensures type == t.type ;
      ensures devise == t.devise ;
      ensures typeProduit == t.typeProduit ;
      ensures taux.intPart == t.taux.intPart && taux.decPart == t.taux.decPart;
      ensures montant.intPart == t.montant.intPart && 
              montant.decPart == t.montant.decPart;
      ensures date.jour == t.date.jour && date.mois == t.date.mois  && 
              date.annee == t.date.annee ;
      ensures heure.heure == t.heure.heure && heure.minute == t.heure.minute ;
      ensures (\forall int i; 0 <= i & i < CERTIFICATE_LENGTH ==>
                  certificat[i] == t.certificat[i]);
      ensures (\forall int i; 0 <= i & i < 4 ==>
                  terminalSN[i] == t.terminalSN[i]);
      ensures (\forall int i; 0 <= i & i < 4 ==>
                  terminalTC[i] == t.terminalTC[i]);
      ensures mutualAuthentification == t.mutualAuthentification ;
      ensures bankAutorization == t.bankAutorization ;
      ensures status == t.status ;
      ensures given == t.given ;
      ensures isValid == t.isValid ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (NullPointerException) false ;      
      exsures (ISOException) false ;      
      exsures (TransactionException e) 
                 (\old(JCSystem._transactionDepth) == 1 && 
                 e._reason == TransactionException.IN_PROGRESS) || 
		 (\old(JCSystem._transactionDepth) == 0 &&
		 e._reason == TransactionException.NOT_IN_PROGRESS) ;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clauses
    void clone(Transaction t)
 	throws TransactionException,
	       ISOException,
	       ArrayIndexOutOfBoundsException, 
	       NullPointerException{
	
 	JCSystem.beginTransaction();
 	number = t.getNumber();
 	type = t.getType();
 	// twice the same thing???
 	t.getPartnerID(id, (short)0);
 	Util.arrayCopyNonAtomic(
 				t.getPartnerID(), (short)0, id, (short)0, PARTNER_ID_LENGTH
 				);
 	devise = t.getDevise();
 	typeProduit = t.getTypeProduit();
	// added DecimalException 14/11/00
	try{
	    taux.setValue(t.getTaux());
 	    montant.setValue(t.getMontant());
 	}
 	catch(DecimalException e){
            //@ assert false;
 	    //Code modified by Nestor CATANO for effects of ESC/Java compilation
 	    ISOException.throwIt(/*PurseApplet.DECIMAL_OVERFLOW*/(short)0x9F15);			
 	}
	
 	try{
 	    date.setDate(t.getDate());
 	    heure.setHeure(t.getHeure());
 	} catch (DateException de){
 	    //@ assert false;
 	    ISOException.throwIt(ISO7816.SW_UNKNOWN);
 	} catch (HeureException he){
 	    //@ assert false;
 	    ISOException.throwIt(ISO7816.SW_UNKNOWN);
 	}
 	Util.arrayCopyNonAtomic(
 				t.getCertificat(), (short)0, certificat, (short)0, CERTIFICATE_LENGTH
 				);
	Util.arrayCopyNonAtomic(
 				t.getTerminalSN(), (short)0, terminalSN, (short)0, (short)4
 				);        
	Util.arrayCopyNonAtomic(
 				t.getTerminalTC(), (short)0, terminalTC, (short)0, (short)4
 				);
 	mutualAuthentification = t.getMutualAuthentification();
 	// Added by LC and HM 09/01/2001
	bankAutorization = t.getBankAuthorization();
 	status = t.getStatus();
	//Code modified by Nestor CATANO 07/06/2001
	//isGiven(); --> t.isGiven();
	given = t.isGiven();
	isValid = t.isValid();
	JCSystem.commitTransaction();        
    }

    
    /* set this transaction as valid*/
    /*@
      modifies isValid ;
      requires true ;
      ensures isValid == true ;
      //exsures (RuntimeException) false ;
    */
    void valid() {
	isValid = true;
    }
    
    /* set this transaction as not valid, i.e. its informations will not be valid ones*/
    /*@
      modifies isValid ;
      requires true ;
      ensures isValid == false ;
      //exsures (RuntimeException) false ;
    */
    void unvalid() {
	isValid = false;
    }
    
    
    /* set all this transaction attributes to 0*/
    /*@
      modifies isValid, number, type, typeProduit, devise, taux, montant ;
      modifies date.jour, date.mois, date.annee ;
      modifies heure.heure, heure.minute ;
      modifies mutualAuthentification, status, given ;
      ensures isValid == false;
      ensures number == (short)0;
      ensures type == INDETERMINE;
      ensures typeProduit == (short)0;
      ensures (\forall int i; 0 <= i & i < PARTNER_ID_LENGTH ==> id[i] == 0);
      ensures devise == (byte)0;
      ensures taux.intPart == 0 && taux.decPart == 0;
      ensures montant.intPart == 0 && montant.decPart == 0;
      ensures date.jour == (byte)1 && date.mois == (byte)1 && 
              date.annee == (byte)99 ;
      ensures heure.heure == (byte)0 && heure.minute == (byte)0 ;
      ensures (\forall int i; 0 <= i & i < CERTIFICATE_LENGTH ==> 
                                                 certificat[i] == 0);
      ensures (\forall int i; 0 <= i & i < 4 ==> terminalSN[i] == 0);
      ensures (\forall int i; 0 <= i & i < 4 ==> terminalTC[i] == 0);
      ensures mutualAuthentification == false;
      ensures status == INDETERMINE;
      ensures given == false;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (NullPointerException) false ;
      exsures (ISOException) false ;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    void reset()
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException,
	       ISOException {
	isValid = false;
	number = (short)0;
	type = INDETERMINE;
	typeProduit = (short)0;
	Util.arrayFillNonAtomic(id, (short)0, PARTNER_ID_LENGTH, (byte)0);
	devise = (byte)0;
        //Decimal Exception added 14/11/00
        try{
            taux.setValue((short)0);
	    montant.setValue((short)0);
	}
	catch(DecimalException e){
            //@ assert false;
	    //Code modified by Nestor CATANO for effects of ESC/Java compilation
	    ISOException.throwIt(/*PurseApplet.DECIMAL_OVERFLOW*/(short)0x9F15);	
	}
	
	try {
	    date.setDate((byte)1, (byte)1, (byte)99);
	    heure.setHeure((byte)0, (byte)0);
	} catch (DateException de) {//@ assert false;
	} catch (HeureException ee) {//@ assert false;
	}
	Util.arrayFillNonAtomic(certificat, (short)0, CERTIFICATE_LENGTH, (byte)0);
	Util.arrayFillNonAtomic(terminalSN, (short)0, (short)4, (byte)0);
	Util.arrayFillNonAtomic(terminalTC, (short)0, (short)4, (byte)0);
	mutualAuthentification = false;
	status = INDETERMINE;
	given = false;
    }


    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == isValid ;
      //exsures (RuntimeException) false ; 
    */
    boolean isValid(){
	return isValid;
    }

    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == number;
      //exsures (RuntimeException) false ; 
    */
    short getNumber(){
	return number;
    }
    
    /* set the transaction number
     *	@param n the number*/
    /*@
      modifies number ;
      requires true ;
      ensures number == n ;
      //exsures (RuntimeException) false ; 
    */
    void setNumber(short n) {
	// a problem?? RM le 15/06/2000
	// number = n ??
	//  May be not ? HM and LC 08/01/2001
        number = n;
    }
    
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == typeProduit;
      //exsures (RuntimeException) false ; 
    */
    public short getTypeProduit() {
	return typeProduit;
    }
    
    /*@
      modifies typeProduit ;
      requires true ;
      ensures typeProduit == t ;
      //exsures (RuntimeException) false ; 
    */
    void setTypeProduit(short t) {
	typeProduit = t;
	}
    
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == terminalTC ;
      //exsures (RuntimeException) false ; 
    */
    byte[] getTerminalTC() {
	return terminalTC;
    }
    
    /* set the terminal transaction counter in the table bArray at the specified offset 
     *	@param bArray : the table
     *	@param off : the offset in the table*/
    /*@
      modifies bArray[*] ;
      requires bArray != null && off >= 0 && 4+off <= bArray.length ;
      ensures (\forall int i; i >= 0 & i < 4 ==> 
                              terminalTC[i] == bArray[i + off]);
      ensures \result == off + 4;
      exsures (Exception) false ;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    short getTerminalTC(byte[] bArray, short off)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException  {
	short offset = off;
	offset = Util.arrayCopyNonAtomic(
					 terminalTC, (short)0, bArray, offset, (short)4
					 );
	return offset;
    }
    
    /*
     *	@param bArray : the table containing the new transaction counter
     *	@param off: the offset in the table
     *	@return off + 4;
     */
    /*@
      modifies terminalTC[*] ;
      requires bArray != null && off >= 0 && 4+off <= bArray.length ;
      ensures (\forall int i; i >= 0 & i < 4 ==> 
                              bArray[i + off] == terminalTC[i]);
      exsures (Exception) false ;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    short setTerminalTC(byte[] bArray, short off)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException {
	short offset = off;
	offset += Util.arrayCopyNonAtomic(bArray, off, terminalTC, (short)0, (short)4);
	return offset;
    }

	/* read the terminal serial numer*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == terminalSN ;
      //exsures (RuntimeException) false ; 
    */
    byte[] getTerminalSN() {
	return terminalSN;
    }
    
    /*@
      modifies bArray[*] ;
      requires bArray != null && off >= 0 && 4+off <= bArray.length ;
      ensures (\forall int i; i >= 0 & i < 4 ==> 
                              terminalSN[i] == bArray[i + off]);
      ensures \result == off + 4;
      exsures (Exception) false ;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    short getTerminalSN(byte[] bArray, short off)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException { 
	short offset = off;
	offset = Util.arrayCopyNonAtomic(
					 terminalSN, (short)0, bArray, offset, (short)4
					 );
	return offset;
    }

	/* set the terminal serial number
	 *	@param bArray: the table that contains the new serial number
	 *	@param off : the offset in the table
	 *	@return off + 4;
	 */
    /*@ 
      modifies terminalSN[*] ;
      requires bArray != null && off >= 0 && 4+off <= bArray.length ;
      ensures (\forall int i; i >= 0 & i < 4 ==> 
                              bArray[i + off] == terminalSN[i]);
      exsures (Exception) false ;
    */
    short setTerminalSN(byte[] bArray, short off)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException { 
	short offset = off;
	offset += Util.arrayCopyNonAtomic(bArray, off, terminalSN, (short)0, (short)4);
	return offset;
    }    

    /* return the type of the transaction*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == type ;
      //exsures (RuntimeException) false ; 
    */
    byte getType() {
	return type;
    }
    
    /* set the type of the transaction*/
    /*@
      modifies type ; 
      requires t == INDETERMINE || t == TYPE_DEBIT || t == TYPE_CREDIT;
      ensures type == t ;
      //exsures (RuntimeException) false ; 
    */
    void setType(byte t) {
	type = t;
    }
    
    /* return the currency of the transaction*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == devise ;
      //exsures (RuntimeException) false ; 
    */
    public byte getDevise() {
	return devise;
    }
    
    /* set the currency of the transaction*/
    /*@
      modifies devise ;
      requires true ;
      ensures devise == d ;
      //exsures (RuntimeException) false ; 
    */
    void setDevise(byte d) {
	devise = d;
    }

    /* set the date of the transaction*/       
    /*@
      modifies date.jour, date.mois, date.annee ;
      requires d != null ;
      ensures date.jour == d.jour && date.annee == d.annee && 
              date.mois == d.mois ;
      exsures (DateException) false ;
    */ 
    void setDate(Date d) throws DateException {
	date.setDate(d);
    }
    
    /* set the date using the 3 bytes stored in the table  bArray. The bytes have the following meaning:
       day-month-year
       *	@param bArray : the table
       *	@param off the offset
       *	@return off + 3
       */
    // temporarily removed by Rodolphe Muller on 15/06/2000
    /*
      short setDate(byte[] date, short off) throws DateException {
      short offset = off;
      this.date.setDate(date[offset++],date[offset++],date[offset++]);
      return offset;
      }
    */
    
    /**
     *	put the date in the table bArray as a succession of bytes
     *	@param bArray : the table where the date is stored
     *	@param off the offset in the table
     *	@return off + 3
     */
    // temporarily removed by Rodolphe Muller on 15/06/2000
    /*
      short getDate(byte[] bArray, short off) {
      bArray[off++] = date.getJour();
      bArray[off++] = date.getMois();
      bArray[off++] = date.getAnnee();
      return off;
      }
    */
    
    /**
     *	@return the transaction date
     */       
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == date ;
      //exsures (RuntimeException) false ;       
    */ 
    Date getDate() {
	return date;
	}
    
    /**
     *	@return the transaction day
     */
    // temporarily removed by Rodolphe Muller on 15/06/2000
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == date.jour ;
      //exsures (RuntimeException) false ;       
    */
    public byte getJour() {
	return date.getJour();
    }
    
    /**
     *	@return the transaction month
     */
    // temporarily removed by Rodolphe Muller on 15/06/2000
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == date.mois ;
      //exsures (RuntimeException) false ;       
    */
    public byte getMois(){
	return date.getMois();
    }
    
    /**
     *	@return the transaction year
     */
    // temporarily removed by Rodolphe Muller on 15/06/2000
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == date.annee ;
      //exsures (RuntimeException) false ;       
    */
    public byte getAnnee() {
	return date.getAnnee();
    }
    
    /* return the partner identifier*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == id ;
      //exsures (RuntimeException) false ;      
    */
    byte[] getPartnerID() {
	return id;
    }
    
    /* copy the saler or the credit operator identifier in the table bArray
     *	@param bArray : the destination table
     *	@param off : the offset of the destination
     *	@return off + size of the identifier
     */
    /*@
      modifies bArray[*] ;
      requires bArray != null && off >= 0 && 
               off+PARTNER_ID_LENGTH <= bArray.length ;
      ensures (\forall int i; i >= 0 & i < PARTNER_ID_LENGTH ==> 
                              id[i] == bArray[i + off]);
      ensures \result == off + PARTNER_ID_LENGTH;
      exsures (Exception) false ;
    */ 
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    short getPartnerID(byte[] bArray, short off)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException {
	short offset = off;
	offset = Util.arrayCopyNonAtomic(
					 id, (short)0, bArray, offset, PARTNER_ID_LENGTH
					 );
	return offset;
    }
    
    /* modify the saler or the exchange operator identifier
     *	@return off + size of the identifier*/
    /*@
      modifies id[*] ;
      requires bArray != null && off >= 0 && 
               off+PARTNER_ID_LENGTH <= bArray.length ;
      ensures (\forall int i; i >= 0 & i < PARTNER_ID_LENGTH ==> 
                              bArray[i + off] == id[i]);
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
    */ 
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    short setPartnerID(byte[] bArray, short off)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException {
	short offset = off;
	offset += Util.arrayCopy(bArray, offset, id, (short)0, PARTNER_ID_LENGTH);
	return offset;
    }
    
    /* return the transaction exchange rate*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == taux ;
      //exsures (RuntimeException) false ;      
    */
    Decimal getTaux() {
	return taux;
    }
    
    /* set the transaction exchange rate*/ 
    /*@
      modifies taux.intPart, taux.decPart ;
      requires tx != null && tx.intPart >= 0 ;
      ensures taux.intPart == tx.intPart ;
      ensures taux.decPart == tx.decPart ;
      exsures (ISOException) false ;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    void setTaux(Decimal tx) 
	throws ISOException  {
	// added DecimalException 14/11/00
	try{
	    taux.setValue(tx);
	}
	catch(DecimalException e){
            //@ assert false;
	    //Code modified by Nestor CATANO for effects of ESC/Java compilation
	    ISOException.throwIt(/*PurseApplet.DECIMAL_OVERFLOW*/(short)0x9F15);			
	}
    }
    
    /* return the transaction hour*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == heure ;
      //exsures (RuntimeException) false ;      
    */
    Heure getHeure() {
	return heure;
    }

    /* set the transaction hour*/
    /*@
      modifies heure.heure, heure.minute ;
      requires h != null ;
      ensures  heure.heure == heure.heure && heure.minute == heure.minute ;
      exsures (HeureException) false ;      
    */
    void setHeure(Heure h) throws HeureException {
	heure.setHeure(h);
    }
    
    /* return the transaction status*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == status ;
      //exsures (RuntimeException) false ;      
    */
    byte getStatus() {
	return status;
    }
    
    /* set the transaction status*/
    /*@
      modifies status ; 
      requires st == WELL_TERMINATED || st == ABORTED ||
               st == CERTIFICATE_ERROR || st == CERTIFICATE_MISSING ||
	       st == INDETERMINE;
      ensures status == st ;
      //exsures (RuntimeException) false ;      
    */
    void setStatus(byte st) {
	status = st;
    }

    /*	@return TRUE if this transaction has been given to a loyalty*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == given ;
      //exsures (RuntimeException) false ;      
    */
    boolean isGiven() {
	return given;
    }

    /* set this transaction as already given to a loyalty*/
    /*@
      modifies given ;
      requires true ;
      ensures given == true ;
      //exsures (RuntimeException) false ;      
    */
    void markAsGiven() {
	given = true;
    }
    
    /*	@return the amount of the transaction
	Warning: its a reference over an encapsulated object*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == montant ;
      //exsures (RuntimeException) false ;      
    */
    Decimal getMontant() {
	return montant;
    }
    
    /* store in a byte table the transaction amount using the following format:
       integer par- decimal part
       *	@param bArray : the table
       *	@param off : the offset in the table
       *	@return off + 4
       */
    /*@
      modifies bArray[*];
      requires bArray != null && off >= 0 && off+3 < bArray.length ;
      ensures \result == off+4 ;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL && 
                  JCSystem._transactionDepth == 1; 
      exsures (ArrayIndexOutOfBoundsException) false;      
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    short getMontant(byte[] bArray, short off) 
	throws TransactionException,
	       ArrayIndexOutOfBoundsException{
	// temporarily modified by RM on 16/08/2000
	/*
	  short offset = off;
	  offset = Util.setShort(bArray, offset, montant.getIntPart());
	  offset = Util.setShort(bArray, offset, montant.getDecPart());
	  return offset;
	*/
	return montant.getValue(bArray, off);
    }


    /* set the amount of the transaction using the integer part and the decimal part contained 
       in bArray, using the following format:
       "2 bytes signed - 2 bytes signed"
       *	@param bArray the table where the two parts are stored
       *	@param off: the offset in the table
       *	@return off + 4 */
    /*@
      modifies montant.intPart, montant.decPart ;
      requires (short)(( (short)(bArray[off]) << 8 ) +
               ((short)(bArray[(short)(off+1)]) & 0xFF)) >= 0 ;
      requires bArray != null ;
      requires 0 <= off && off < bArray.length; 
      requires 0 <= (short)(off + 1) && off + 1 < bArray.length; 
      requires 0 <= (short)(off + 2) && off + 2 < bArray.length; 
      requires 0 <= (short)(off + 3) && off + 3 < bArray.length; 
      //we have not specified the postcondition of the method 
      // Util.getShort(byte[], short)
      // ensures montant.intPart == bArray[C(off)] && 
      // montant.decPart == bArray[C(off+2)] ;
      ensures \result == (short)(off + (short)4) ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (ISOException) true ;
    */
    // Code modified by Nestor CATANO 23/05/2001
    // inclusion of throws clause
    short setMontant(byte[] bArray, short off)
	throws ISOException,
	       ArrayIndexOutOfBoundsException{
	short offset = off;
        //Decimal Exception added 14/11/00
        try{
            montant.setValue( 
			     Util.getShort(bArray, offset),
			     Util.getShort(bArray, (short)(offset + (short)2))
			     ); 
	}
	catch(DecimalException e){
	    //Code modified by Nestor CATANO for effects of ESC/Java compilation
	    ISOException.throwIt(/*PurseApplet.DECIMAL_OVERFLOW*/(short)0x9F15);			
	}
	
	return (short)(offset + (short)4);
        
        /* Added by rodolphe muller */
	//return montant.setValue(bArray, off);	
    }

    /*@ 
      modifies montant.intPart, montant.decPart ;
      requires m != null && m.intPart >= 0 ;
      ensures montant.intPart == m.intPart && montant.decPart == m.decPart ;
      exsures (ISOException) false ;
    */
    // Code modified by Nestor CATANO 23/05/2001
    // inclusion of throws clause
    void setMontant(Decimal m)
	throws ISOException {
	// added DecimalException 14/11/00
	try{
	    montant.setValue(m);
	}
	catch(DecimalException e){
            //@ assert false;
	    //Code modified by Nestor CATANO for effects of ESC/Java compilation
	    ISOException.throwIt(/*PurseApplet.DECIMAL_OVERFLOW*/(short)0x9F15);			
	}
    }
    
    
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == certificat ;
      //exsures (RuntimeException) false ;
    */
    byte[] getCertificat() {
	return certificat;
    }
    
    /* copy the certificate in a byte table bArray
     *	@param bArray : the table where the ertificate will be stored
     *	@param off : the offset in the table
     *	@return <code>off + the size of the certificate</code>
     */
    // temporarily removed by Rodolphe Muller on 15/06/2000
    /*
      short getCertificat(byte[] bArray, short off) {
      short resu = (short)(off + (short)certificat.length);
      Util.arrayCopyNonAtomic(
      certificat, (short)0, bArray, off, (short)certificat.length
		);
		return resu;
		}
    */
    
    /* write the transactionc certificate
     *	@param bArray : the certificate
     *	@param off : the offset
     *	@return off + size of the certificate
     */
    /*@
      modifies certificat[*] ;
      requires bArray != null && off >= 0 && 
               off+CERTIFICATE_LENGTH <= bArray.length ;
      ensures (\forall int i; i >= 0 & i < CERTIFICATE_LENGTH ==> 
                              bArray[i + off] == certificat[i]);
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
    */ 
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    short setCertificat(byte[] bArray, short off)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException {
	short offset = off;
	offset += Util.arrayCopyNonAtomic(
					  bArray, off, certificat, (short)0, CERTIFICATE_LENGTH
					  );
	return offset;
    }
    
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == mutualAuthentification ;
      //exsures (RuntimeException) false ;
    */
    boolean getMutualAuthentification() {
	return mutualAuthentification;
    }

    /*@
      modifies mutualAuthentification ;
      requires true ;
      ensures mutualAuthentification == b ;
      //exsures (RuntimeException) false ;
    */
    void setMutualAuthentification(boolean b) {
	mutualAuthentification = b;
    }
    
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == bankAutorization ;
      //exsures (RuntimeException) false ;
    */
    boolean getBankAuthorization() {
	return bankAutorization;
    }
    
    /*@
      modifies bankAutorization ;
      requires true ;
      ensures  bankAutorization == b ;
      //exsures (RuntimeException) false ;
    */
    void setBankAuthorization(boolean b) {
	bankAutorization = b;
    }
    
}

