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
 *  @modified by Abdellah El Marouani, Ludovic Casset, Jean-Louis Lanet & Hugues Martin
 *  @version 2.0.1
 *------------------------------------------------------------------------------
 */


package test.Julia.EPurse;

/* this class must be re-designed 8/11/00*/
//import com.gemplus.pacap.utils.*;
//import com.gemplus.pacap.pacapinterfaces.*;



import javacard.framework.AID;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.OwnerPIN;
import javacard.framework.Shareable;
import javacard.framework.Util;
import javacard.security.Signature;
import javacardx.crypto.Cipher;
import javacard.framework.APDU;
//import visa.openplatform.OPSystem;

//code added by Nestor CATANO
import javacard.framework.SystemException ;
import javacard.framework.TransactionException ;


/*Functional description of the purse*/
public class Purse {
    /*@ 
      invariant maxTransactionWOPIN   == 3 ;
      invariant appletSN.length       == 4 ;
      invariant maxTransactionCounter == 32000 ;
      invariant transactionWOPIN >= 0 && 
                transactionWOPIN <= maxTransactionWOPIN ;	
      invariant administrativeCounter >= 0 ;
      invariant transactionCounter >= 0;
      
      invariant CARDISSUER_AID != null ;
      invariant appletSN != null ;
      invariant balance != null ;
      invariant creditAmountWOAuthorization != null ;
      invariant currencyTable != null ;
      invariant transactionRecord != null ;
      invariant exchangeRecord != null ;
      invariant exchangeRate != null ;
      invariant loyaltiesTable != null ;
      invariant maxBalance != null ;
      invariant maxDebitWOPIN != null ;
      invariant maxDebitWOExternalAut != null ;
      invariant expirationDate != null ;
      invariant loyaltyInterface != null ;
      invariant invExchangeRate != null ;
      invariant aid != null ;
      invariant t1_4 != null ;
      invariant t2_4 != null ;
      invariant t3_8 != null ;
      invariant t4_5 != null ;
      invariant t5_16 != null ;
      invariant transactionInterface != null;
      invariant temp != null ;
      invariant transaction != null ;
      invariant exchangeSession != null ;
      invariant decimal1 != null ;
      invariant decimal2 != null ;
      invariant securite != null ;
      invariant applet != null;
      invariant applet.purse == this;

    */
	
    
	////////////////////   ATTRIBUTES    ///////////////////////


	//------------- Default constant values-------------------//


/*The cardissuer AID*/
	private static final byte[] CARDISSUER_AID = new byte[] {
		(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x18, (byte)0xFF, (byte)0x00, (byte)0x00,
		(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02
	};    


/*the administrative code for the administrative and the system session*/
	/*private static final byte[] DEFAULT_ADMINISTRATIVE_CODE = new byte[] {
		(byte)0x02, (byte)0x01
	};*/



	//------------- Specification of the attributes ---------------------//


/*The applet Serial number: unique for each applet and different from
  the aid*/
    private /*@ spec_public */ byte[] appletSN = new byte[4];
/*The electronic purse balance*/
    private /*@ spec_public */ Decimal balance = new Decimal();
/*The credit amount without an authorisation*/
    private /*@ spec_public */ Decimal creditAmountWOAuthorization = new Decimal((short)100);
/*The table containing the different currencies supported by the
  purse*/
    private /*@ spec_public */ Currencies currencyTable = new Currencies();
/*The record containing all the transactions performed by the purse*/
    private /*@ spec_public */ TransactionRecord transactionRecord = new TransactionRecord();
/*The record containing all the currency changes*/
    /* spec_public */    private ExchangeRecord exchangeRecord = new ExchangeRecord();
/*The current currency of the purse*/
    private /*@ spec_public */ byte currency = Currencies.REFERENCE;
/*The exchange rate related to the current currency*/
    private /*@ spec_public */ Decimal exchangeRate = new Decimal((short)1);
/*The list of all the loyalties attached with the purse*/
    private /*@ spec_public */  LoyaltiesTable loyaltiesTable = new LoyaltiesTable();
/*The transaction counter*/
    private /*@ spec_public */ short transactionCounter = (short)0;
/*The maximum number of transaction*/
    private /*@ spec_public */ short maxTransactionCounter = (short)32000;
/*The maximum value of the purse balance */
    private /*@ spec_public */ Decimal maxBalance =    new Decimal((short)1000);
/*The maximum debit allowed without entering the PINcode (that is
  being authenticated)*/
    private /*@ spec_public */ Decimal maxDebitWOPIN = new Decimal((short)10);
/*The maximum debit allowed without identifying the terminal (mutual
  authentication)*/
    private /*@ spec_public */ Decimal maxDebitWOExternalAut = new Decimal((short)5);
/*The number of transaction since the last PIN code authentication*/
	private /*@ spec_public */ byte transactionWOPIN = (byte)0;
	
/*The maximum number of transaction allowed since the last Pin code
  authentication*/
    private /*@ spec_public */ byte maxTransactionWOPIN = (byte)3;
	
/*The expiration date of the applet*/
    private /*@ spec_public */ Date expirationDate = new Date();
	
/*The administrative session counter, used to monitor the
  administrative session.*/
    private /*@ spec_public */ short administrativeCounter = (short)0;
	
/*The administrator pin code: the card holder pin code is managed by
the card manager and is the same for all applications on the
card. However, each application can manage is own set of code as the
administrator code for the purse.*/
// WARNING THE NUMBER OF TRIES IS 3 AND THE SIZE OF THE PIN CODE IS 2
// DIGITS
	/*private OwnerPIN administrativeCode = new OwnerPIN((byte)3,
          (byte)2);*/

	

	//---------- Third parts interface links ------------//

/*The loyalties interface*/	
    // ESC/Java warns that at this moment the class invariant for 
    // purse might not yet have been established
    /*@ spec_public */ private LoyaltyInterfaceObject loyaltyInterface = new LoyaltyInterfaceObject(this);


	//------------------ The current session keys ---------------//
/*The current session keys are computed during the init phase of all
the commands of the purse. Their computation depends on a mother key
that is chosen depending on which session is being executed. The goal
of a session key is to be used only once and its computation is based
on different elements that make this key unique.*/

/*The key used to do Secure Messaging. Note that the secure messaging
is performed other an apdu and then it is done in the purseapplet
class. That is this key is sent to the purseapplet class.*/
//	private PacapKey SMKey			= new PacapKey(JCSystem.CLEAR_ON_DESELECT);

	//---------------------- Others attributes -------------------//            

/*Performing a division is not really easy. So the inverse og the
exchange rate is stored in the following variable. Then, it is just
necessary to multiply instead of divide.*/
    private /*@ spec_public */ Decimal invExchangeRate = new Decimal((short)1);
	
	
/*The aid of the appelt*/	
	private byte[] aid;


/*The reference other the object that controls this purse, that is the
  purseapplet*/
	private PurseApplet applet;

/*Temporary tables*/ 
	private byte t1_4[] =
		JCSystem.makeTransientByteArray((short)4, JCSystem.CLEAR_ON_DESELECT);
	private byte t2_4[] =
		JCSystem.makeTransientByteArray((short)4, JCSystem.CLEAR_ON_DESELECT);
	private byte t3_8[] =
		JCSystem.makeTransientByteArray((short)8, JCSystem.CLEAR_ON_DESELECT);
	private byte t4_5[] =
		JCSystem.makeTransientByteArray((short)5, JCSystem.CLEAR_ON_DESELECT);
	private byte t5_16[] =
		JCSystem.makeTransientByteArray((short)16, JCSystem.CLEAR_ON_DESELECT);
	private byte temp[] =
		JCSystem.makeTransientByteArray((short)80, JCSystem.CLEAR_ON_DESELECT);

    /*Temporary Objects*/
    
    /*Data sent during the agreement phase. These variables are used
      to temporary store a transaction or an exchange session that is
      being performed.*/
    Transaction transaction = new Transaction();
    ExchangeSession exchangeSession = new ExchangeSession();
    
    /*A shareable object used to peform a link between the transaction
      history and the loyalties*/
    /* spec_public */ TransactionInterfaceObject transactionInterface = new TransactionInterfaceObject();
    Decimal decimal1 = new Decimal();
    Decimal decimal2 = new Decimal();
    

    //	private byte ivNull[] = {0, 0, 0, 0, 0, 0, 0, 0};
	
    
    /* The security object of the purse: enforce the security policy
       defined for the purse*/
    //    private Security securite;
    protected Security securite;
    

    ///////////////     CONSTRUCTOR ////////////////
    
    /*@
      requires p != null;
      ensures \fresh(this);
      ensures applet == p;
      ensures \fresh(securite);
      //exsures (Exception) false ;
     */
    Purse(PurseApplet p) {
	super ();
	
	/*The adminsitrative code is initialized to its default value*/
	//administrativeCode.update(DEFAULT_ADMINISTRATIVE_CODE,
	//(short)0, (byte)2);
	
	applet = p;
	securite = new Security();
    }
    

    ////////////////       METHODS      ///////////////
    
    /*This method contains actions to perform each time the applet is
      selected*/
    //code modified by Nestor CATANO
    //inclusion of throws clause
    /*@
      modifies securite.terminalSessionType ;
      modifies securite.accessCondition.condition ;
      modifies securite.administrativeCode.flags, 
               securite.administrativeCode.flags[OwnerPIN.VALIDATED],
               securite.administrativeCode.triesLeft[0] ;
      modifies securite.authPin ;
      requires true ;
      ensures securite.terminalSessionType == Security.NOT_INITIALIZED ;
      ensures securite.accessCondition.condition == AccessCondition.FREE ;
      ensures securite.administrativeCode.flags != null ;
      ensures securite.authPin == false ;
      exsures (SystemException e) 
                  e._reason == SystemException.NO_TRANSIENT_SPACE;
     */
    boolean select() 
	throws SystemException {
	//The terminal session type is set to NOT_INITIALIZED. That is there 
	//is no active current session
	securite.setTerminalSessionType(Security.NOT_INITIALIZED);
	//The accessCondition and the adminsitrative code are reset
	securite.resetAccessCondition();
	securite.resetAdministrativeCode();
	// securite.resetAdministrativeCode();
		//The card holder is no longer authenticated
	securite.initAuthPin();
	return true;
    }

    /*This method contains actions to perform each time the applet is
      deselected*/

    //code modified by Nestor CATANO
    //inclusion of throws clause
    /*@
      modifies securite.terminalSessionType ;
      modifies securite.administrativeCode.flags, 
               securite.administrativeCode.flags[OwnerPIN.VALIDATED],
               securite.administrativeCode.triesLeft[0] ;
      requires true ;
      ensures securite.terminalSessionType == Security.NOT_INITIALIZED ; 
      ensures securite.administrativeCode.flags != null ; 
      exsures (SystemException e) 
                  e._reason == SystemException.NO_TRANSIENT_SPACE;
    */
    void deselect() 
    throws SystemException {
	// Simply end administrative mode
	endAdminMode();
    }
 

    //-----------------------------------------------------------
    //
    //              Object sharing methods
    //
    //-----------------------------------------------------------
    
    /**
     *	@param client: The aid of the client applet
     *	@param type: indicates the requested interface
     *	@return returns the interface if the request is accepted
     */
    /*@
      //modifies \nothing ;
      requires client != null ;
      ensures true ;
      //exsures (Exception) false ;
    */
    Shareable getShareableInterfaceObject(AID client, byte type) {
	// There is only one interface
	return getLoyaltyInterface(client);
    }
    
    
    /*returns the interface dedicated to the loyalties, depending on
      the client aid */
    /*@
      //modifies \nothing ;
      requires client != null ;
      ensures true ;
      //exsures (Exception) false ;
    */
    private Shareable getLoyaltyInterface(AID client) {
	Shareable resu = null;
        // check that the loyalty is in the purse list
	AllowedLoyalty al = loyaltiesTable.getAllowedLoyalty(client);
	if(al != null) {
	    resu = loyaltyInterface;
	}
	//if it is the case returns the interface otherwise returns null
	return resu;
    }

    
    //----------------------------------------------------------------------
    //
    //                  Methods with the terminal interface
    //
    //----------------------------------------------------------------------
    

    /* Applicative methods */
    
    /**
     *	Debit initialisation.
     *
     *	@param montant: amount of the transaction
     *	@param monnaie: The currency of the amount
     *	@param idCom: The merchant identifier (4 bytes)
     *	@param randT: The random of the terminal (4 bytes)
     *	@param ttc: the terminal transaction counter (4 bytes)
     *	@param tsn: the terminal serial number (4 bytes)
     *	@param buffer: the apdu buffer, used to check the Secure Messaging if necessary
     *	@exception ISOException with the code
     *		<code>PurseApplet.NOT_CURRENT_CURRENCY</code>
     *		if the current currency is not the one the requested debit
     *	@exception ISOException avec le code
     *		<code>PurseApplet.BAD_VALUE_FOR_AMOUNT</code>
     *		if the amount value (montant) is not correct
     */
    //code modified by Nestor CATANO 24/10/01
    //inclusion of throws clause
    /*@
      //securite.verifyState
      modifies securite.State ;
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth ;
      modifies transactionWOPIN  ;
      requires true;
      ensures true;
      ensures transactionWOPIN == \old(transactionWOPIN) + 1 ;
      exsures (ISOException e )
                  (e._reason == PurseApplet.ACCESS_CONDITION_ERR && 
                    Security.NOT_INITIALIZED!=securite.terminalSessionType) ||
                   e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ||      
                  (e._reason == PurseApplet.TRANSACTION_COUNTER_OVERFLOW &&
		    transactionCounter >= maxTransactionCounter) ||    
                  (e._reason == PurseApplet.NOT_CURRENT_CURRENCY &&
		    monnaie != currency) ||   
		   e._reason == PurseApplet.BAD_VALUE_FOR_AMOUNT || 
		  (e._reason == PurseApplet.TOO_MUCH_TRANSACTION_WO_PIN  &&
		    transactionWOPIN >= maxTransactionWOPIN );
      exsures (TransactionException e) 
                   e._reason == TransactionException.BUFFER_FULL 
                   && JCSystem._transactionDepth == 1 ; 
     */
    void appInitDebit(
		      Decimal montant, byte monnaie, byte[] idCom, short typeProd, byte[] randT,
		      byte[] ttc, byte[] tsn, APDU apdu
		      )
    throws ISOException,TransactionException{//, AccessConditionException {
	//byte buffer[] = apdu.getBuffer();
	securite.verifyState(PurseApplet.APP_INIT_DEBIT);
	try {
	    // Verification of the access condition
	    if( securite.verifyAccessControlTable(PurseApplet.APP_INIT_DEBIT, 
						  Security.NOT_INITIALIZED, apdu)){
		
		// increments the transaction counter of the purse
		// modified by LC 21/11/00. This operation is now performed in appDebit
		// when the transaction is being recorded
		//transactionCounter++;
				
		if(transactionCounter >= maxTransactionCounter) {
		    ISOException.throwIt(PurseApplet.TRANSACTION_COUNTER_OVERFLOW);
		}
		//Check that the current currency and the transaction currency are the same
		if(monnaie != currency) {
		    ISOException.throwIt(PurseApplet.NOT_CURRENT_CURRENCY);
		}
		//check the correctness of the amount value (montant)				
		if((montant.isNull()) || montant.isGreaterThan(balance)) {
		    ISOException.throwIt(PurseApplet.BAD_VALUE_FOR_AMOUNT);
		}
				
		// added by HM and LC 08/01/2001
		// check if the cardholder must be authenticated or not
                //check if the card holder must re-enter its PIN code or not by comparing
                // transactionWOPIN and maxTransactionWOPIN
                if( montant.isGreaterEqualThan(maxDebitWOPIN))
		    verifyDurationOfPin(PurseApplet.APP_DEBIT); 

		//Decide wether or not a terminal authentication is necessary. If montant is 
		//less than maxDebitWOExternalAut, then there is no need to authenticate the 
		//terminal. Otherwise, it is necessary to authenticate the terminal.
		byte authen = (byte)(choixAuthentification(tsn, montant)?1:0);
				
		// The data related to the debit transaction are stores within the 
		//transaction variable. They will be necessary in the 2nd step of the debit. 
		transaction.reset(); 
		transaction.setMontant(montant);
		transaction.setDevise(monnaie);
		transaction.setPartnerID(idCom, (short)0);
		transaction.setNumber(transactionCounter);
		transaction.setTerminalSN(tsn,(short)0);
		transaction.setTerminalTC(ttc, (short)0);
		transaction.setType(Transaction.TYPE_DEBIT);
		transaction.setTypeProduit(typeProd);
		transaction.setMutualAuthentification(authen == 1);
		transaction.setTaux(exchangeRate);
		// This will help to verify the terminal signature
		Util.arrayCopyNonAtomic(randT, (short)0, t2_4, (short)0, (short)4);
		
				
    
		// change the terminal session type to indicate 
		// that a debit is being performed
		securite.setTerminalSessionType(Security.DEBIT_SESSION);
		// generate the session keys
		Util.arrayCopyNonAtomic(securite.generateSessionKeys(tsn, ttc, randT, 
								     appletSN, 
								     transactionCounter),
					(short)0, t1_4, (short) 0, (short) 4);
		
		//obtain the aid
		AID aid = JCSystem.getAID();
		if(aid != null)
		    aid.getBytes(temp, (short)0);
		else
		    Util.arrayCopyNonAtomic(
					    this.aid, (short)0, temp, (short)0, 
					    (short)this.aid.length
					    );
		Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);
		
		
		/* generate the signature for the data that are sent back to the terminal.
		   the signature is stored in t3_8.*/
		
		Util.arrayCopyNonAtomic(securite.initSignature(tsn, ttc, randT, appletSN, 
							       transactionCounter, 
							       expirationDate, authen), 
					(short)0, t3_8, (short)0, (short)8);
			
		// The answer of the appinitdebitcommand
		applet.appInitDebitResp(
					t4_5, appletSN, expirationDate, transactionCounter, t1_4,
					Cipher.ALG_DES_CBC_NOPAD, Signature.ALG_DES_MAC8_NOPAD,
					authen, t3_8, apdu);
		// compare the t3_8 value with the one built in the original purse	
	    } else {
		//the current state does not match the access condition
		ISOException.throwIt(PurseApplet.ACCESS_CONDITION_ERR);
	    }
	    
	} catch(ISOException e) {
	    ISOException.throwIt(e.getReason());
	}
    }

	/**
	 * This command is allowed only if a appinitdebit has been performed before
	 *	@param date: the transaction date sent by the terminal
	 *	@param heure: the transaction time sent by the terminal
	 *	@param externalAuth: The terminal signature
	 *	@param buffer: the apdu buffer if verification of the secure messaging is necessary
	 *	@exception ISOException with the code ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED
	 *		if the terminal authentication fails
	 */
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      //setTerminalSessionType
      modifies securite.terminalSessionType ;
      requires true;
      ensures true;
      //setTerminalSessionType
      ensures securite.terminalSessionType == Security.NOT_INITIALIZED ;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
	void appDebit(Date date, Heure heure, byte[] externalAuth, APDU apdu) 
	    throws ISOException {
		byte buffer[] = apdu.getBuffer();
		
		// check if this command can be invoked regarding the state of the applet
		securite.verifyState(PurseApplet.APP_DEBIT);
		//check if this command can be executed
		securite.verifyAccessCondition(buffer, Security.DEBIT_SESSION, PurseApplet.APP_DEBIT);
		//The debit session is terminated so, the session is now NOT_INITIALIZED
		securite.setTerminalSessionType(Security.NOT_INITIALIZED);      
		
		// Removed by LC and HM 08/01/2001
		// check if the cardholder must be authenticated or not
		//check if the card holder must re-enter its PIN code or not by comparing
		// transactionWOPIN and maxTransactionWOPIN
		//Decimal montant = transaction.getMontant();
		//if( montant.isGreaterEqualThan(maxDebitWOPIN))
		//    verifyDurationOfPin(PurseApplet.APP_DEBIT);
			
		try {
			transaction.setHeure(heure);
			transaction.setDate(date);
			
			        // check if the card must authenticate the terminal 
	        if(transaction.getMutualAuthentification()) {
        		
		        // verification of the signature that authenticates the terminal
		        // generation of the data to be signed
		        AID aid = JCSystem.getAID();
		        if(aid != null)
			        aid.getBytes(temp, (short)0);
		        else
			        Util.arrayCopyNonAtomic(
				        this.aid, (short)0, temp, (short)0, (short)this.aid.length);          
        			
		        // only the first 5 bytes are used
		        Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);
                
                // check the terminal signature
		        if (!securite.authenticateTerminalDebitSignature(transaction, expirationDate, 
	            date, heure, externalAuth, appletSN, transactionCounter))
	            {
	                //added by LC 21/11/00
	                transactionCounter++;
	                transaction.setNumber(transactionCounter);
			        transaction.setStatus(Transaction.CERTIFICATE_ERROR);
			        transactionRecord.addTransaction(transaction);
			        ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
		        }
	        }

			//generation of the debit certificate. This certificate must be signed with 
			//another key as the terminal must not be able to modify it.
			AID aid = JCSystem.getAID();
			if(aid != null)
				aid.getBytes(temp, (short)0);
			else
				Util.arrayCopyNonAtomic(
					this.aid, (short)0, temp, (short)0, (short)this.aid.length
				);
			
			// only the first 5 bytes are used
			Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);
            
            // generate the debit certificate
            Util.arrayCopyNonAtomic(securite.generateDebitCertificate(transaction, 
			                                appletSN, transactionCounter), (short) 0, t3_8,
			                                (short) 0, (short) 8);
			// certifiate the transaction
			transaction.setCertificat(t3_8, (short)0);

			//Now the terminal is authenticated, the debit cerrtificate is generated
			//Then, the debit can be performed: the purse attributes can be changed.
			//in particular, the balance is modified
            
            // Decimal exception added 14/11/00
			try{
			    balance.sub(transaction.getMontant());                  
			}
			catch(DecimalException e){
                ISOException.throwIt(PurseApplet.DECIMAL_OVERFLOW);			
			}
			
			// the transaction is record in the history of all the transactions
			//added by LC 21/11/00
	            transactionCounter++;
	            transaction.setNumber(transactionCounter);
			transaction.setStatus(Transaction.WELL_TERMINATED);            
			transactionRecord.addTransaction(transaction);
			
			
			// Check if the history of all transactions is full or not. If it is full, then
			// the purse will contact all the loyalty in order to send them the transaction
			//in which they are involved. Finally, the transaction history will be deleted.
			processLogFullIfNeeded();
			//The purse response to the terminal
			applet.appDebitResp(balance, t3_8, apdu);
		} catch(DateException de) {
                    //@ assert false;
		    //added by LC 21/11/00
	            transactionCounter++;
	            transaction.setNumber(transactionCounter);
		    // Normally, we never get here because the date is already constructed
			transaction.setStatus(Transaction.ABORTED);
			transactionRecord.addTransaction(transaction);
			ISOException.throwIt(ISO7816.SW_UNKNOWN);
		} catch(HeureException ee) {
                    //@ assert false;
		    //added by LC 21/11/00
	            transactionCounter++;
	            transaction.setNumber(transactionCounter);
			// Normally, we never get here because the date is already constructed
			transaction.setStatus(Transaction.ABORTED);
			transactionRecord.addTransaction(transaction);
			ISOException.throwIt(ISO7816.SW_UNKNOWN);
		}
	}


	/**
	 *	initialisation of a credit transaction.
	 * 
	 *	@param montant: amount of the transaction
	 *	@param monnaie: the currency of montant
	 *	@param idBnk: the credit operator identifier (4 bytes)
	 *	@param randT: a terminal random (4 bytes)
	 *	@param ttc: Terminal transaction counter (4 bytes)
	 *	@param tsn: Terminal serial number (4 bytes)
	 *	@param buffer: the apdu buffer, used to verify the secure messaging if necessary
	 */
    //code modified by Nestor CATANO 24/10/01
    //addition of ISOException exception
    /*@
      //verifyState
      modifies securite.State ; 
      requires true;
      ensures true ;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
	void appInitCredit(
		Decimal montant, byte monnaie, byte[] idBnk, byte[] randT, byte[] ttc,
		byte[] tsn, APDU apdu
	) 
    throws ISOException {
		//byte buffer[] = apdu.getBuffer();
		securite.verifyState(PurseApplet.APP_INIT_CREDIT);
		try {
		    // verification of the access condition
		    if(securite.verifyAccessControlTable(PurseApplet.APP_INIT_CREDIT,
		        Security.NOT_INITIALIZED,apdu))
		        {
		        
		        
				if(transactionCounter >= maxTransactionCounter) {
					ISOException.throwIt(PurseApplet.TRANSACTION_COUNTER_OVERFLOW);
				}
				//check if the currency of the transaction is the same of the current 
				//currency of the purse
				if(monnaie != currency) {
					ISOException.throwIt(PurseApplet.NOT_CURRENT_CURRENCY);
				}
	            //check the correctness of the montant variable
				if(montant.isNull()) {
					ISOException.throwIt(PurseApplet.BAD_VALUE_FOR_AMOUNT);
				}    
					// Decimal exception added 14/11/00
				try{
			        decimal1.setValue(montant);
				    decimal1.add(balance);    				
				    if(decimal1.isGreaterThan(maxBalance)) {
					ISOException.throwIt(PurseApplet.BALANCE_OVERFLOW);
				    }
			    }
			    catch(DecimalException e){
                    ISOException.throwIt(PurseApplet.DECIMAL_OVERFLOW);			
			    }
				// increase the transaction counter
				// modified by LC 21/11/00. It is now performed in appCredit
				//transactionCounter++;
				// Check if a bank authorisation is necessary or not. It depends on the value
				// of montant compared to creditAmountWOAuthorization.
				// 1 : a bank authorization is required
				// 0 : the bank authorization is not required
				byte authen = (byte)0;
				
                // Decimal exception added 14/11/00
			    if(montant.isGreaterThan(creditAmountWOAuthorization))
					authen = (byte)1;
					//data related to the credit transaction are stored for a further used in
                //the credit command.
				transaction.reset();
				transaction.setMontant(montant);
				transaction.setDevise(monnaie);
				transaction.setPartnerID(idBnk, (short)0);
				transaction.setNumber(transactionCounter);
				transaction.setTerminalSN(tsn,(short)0);
				transaction.setTerminalTC(ttc, (short)0);
				transaction.setType(Transaction.TYPE_CREDIT);
				transaction.setMutualAuthentification(true);
				transaction.setBankAuthorization(authen == 1);
				transaction.setTaux(exchangeRate);
				//the terminal random is stored because it will be used to authenticate the terminal
				Util.arrayCopyNonAtomic(randT, (short)0, t2_4, (short)0, (short)4);
				
				//check if the credit can be performed (balance must be less or equal to
				//maxBalance
				
					
				securite.setTerminalSessionType(Security.CREDIT_SESSION);
				//generation of the session key that will be used during this credit session
				// the card random is stored in the local temporary table t1_4
                Util.arrayCopyNonAtomic(securite.generateSessionKeys(tsn, ttc, randT, appletSN,
                                            transactionCounter), (short) 0, t1_4, (short) 0, (short) 4);
                
			    AID aid = JCSystem.getAID();
				if(aid != null)
					aid.getBytes(temp, (short)0);
				else
					Util.arrayCopyNonAtomic(
						this.aid, (short)0, temp, (short)0, (short)this.aid.length);
				// Only the first 5 bytes are used. T4_5 contains the aid of the applet
				Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);
	
				Util.arrayCopyNonAtomic(securite.initSignature(tsn, ttc, randT, appletSN,
					            transactionCounter, expirationDate, authen), (short) 0, t3_8, (short) 0, (short) 8);

                
				//the purse response is the following
				applet.appInitCreditResp(
					t4_5, appletSN, expirationDate,
					transactionCounter, t1_4,
					Cipher.ALG_DES_CBC_NOPAD,
					Signature.ALG_DES_MAC8_NOPAD, authen,
					t3_8, apdu);
			} else {
				// the current security state does not match the access condition
				ISOException.throwIt(PurseApplet.ACCESS_CONDITION_ERR);
			}
		  } //catch(AccessConditionException ase) {
		    //an error has occured during the verificationhe access condition
			// this error is internally treated
			//ISOException.throwIt(PurseApplet.ACCESS_CONDITION_ERR);
			catch(ISOException e) {ISOException.throwIt(e.getReason());
			
		}
		
	}

	/** 
	 *	The CREDIT command. This command can only be peformed if an initCREDIT command
	 *  has been performed just before.
	 *	@param date: the transaction date sent by the terminal
	 *	@param heure: the transaction time ent by the terminal
	 *	@param bankCert: the bank certificate that allow the credit
	 *	@param sign: the signature of the terminal (mutual authentication is mandatory)
	 *	@param buffer; the apdu buffer, used to verify the secure messaging
	 */
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
	  
      //setTerminalSessionType
      modifies securite.terminalSessionType ;
      requires true;
      ensures true; 
      //setTerminalSessionType
      ensures securite.terminalSessionType == Security.NOT_INITIALIZED ;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
	void appCredit(Date date, Heure heure, byte[] bankCert, byte[] sign, APDU apdu) 
	    throws ISOException {
		byte buffer[] = apdu.getBuffer();
        
        // check if this command can be invoked regarding the state of the applet
        securite.verifyState(PurseApplet.APP_CREDIT);
        
        //check the access condition
	securite.verifyAccessCondition(buffer, Security.CREDIT_SESSION, PurseApplet.APP_CREDIT);
	securite.setTerminalSessionType(Security.NOT_INITIALIZED);
	//check if the card holder must re-enter its PIN code or not by comparing
        // transactionWOPIN and maxTransactionWOPIN
	verifyDurationOfPin(PurseApplet.APP_CREDIT);
		
		
		try {
			transaction.setHeure(heure);
			transaction.setDate(date);                        
			
			
			//re-allocated by LC and HM 09/01/2001
			    // verification of the terminal signature
		    // first, the data to be signed are generated, they are stored in the transaction
		    // variable
		    AID aid = JCSystem.getAID();
		    if(aid != null)
			    aid.getBytes(temp, (short)0);
		    else
			    Util.arrayCopyNonAtomic(
				    this.aid, (short)0, temp, (short)0, (short)this.aid.length);
		    // Only the first 5 bytes are used
		    Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);
    		
		    //check the terminal Signature
		    if( !securite.authenticateTerminalCreditSignature(transaction, expirationDate,
		    date, heure, bankCert, sign, appletSN, transactionCounter))
		    {
		        //added, previously, this error was not recorded. However, it must be recorded
		        //added by LC 21/11/00
	            transactionCounter++;
	            transaction.setNumber(transactionCounter);
	            transaction.setStatus(Transaction.CERTIFICATE_ERROR);
			    transactionRecord.addTransaction(transaction);
		        ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);}                                                                               

		    // Check the bank authorisation if required
		    if(transaction.getBankAuthorization()) {
			    // This functionalities is not described in the specification.
			    // Therefore, it is not implemented yet: it has to be done but first specified
		    }

			//generation of the credit certificate. This certificate is signed with the
			// creditSignatureKey that is only known by the purse and the card holder bank.
			// Therefore, the integrity of the certificate is ensured and the terminal cannot
			// modify it.
			AID monAid = JCSystem.getAID();
			if(monAid != null)
				aid.getBytes(temp, (short)0);
			else
				Util.arrayCopyNonAtomic(
					this.aid, (short)0, temp, (short)0, (short)this.aid.length);
			// Only the first 5 bytes are used
			Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);

			// Construction of the data of the certificate to be signed
			Util.arrayCopyNonAtomic(securite.generateCreditCertificate(transaction,appletSN,transactionCounter, balance),
			            (short)0, t3_8, (short) 0, (short) 8);
			            
			transaction.setCertificat(t3_8, (short)0);

			// The transaction can now be stored in the history file
			transaction.setStatus(Transaction.WELL_TERMINATED);

			// internal attributes of the purse can be modified
			// The balance is modified
			
            // Decimal exception added 14/11/00
			try{
			    balance.add(transaction.getMontant()); 
			}
			catch(DecimalException e){
                ISOException.throwIt(PurseApplet.DECIMAL_OVERFLOW);			
			}
			
			//added by LC 21/11/00
	            transactionCounter++;
	            transaction.setNumber(transactionCounter);
			// The transaction is added to the history file of the transactions
			transactionRecord.addTransaction(transaction);
			// Check if the history of all transactions is full or not. If it is full, then
			// the purse will contact all the loyalty in order to send them the transaction
			// in which they are involved. Finally, the transaction history will be deleted.
			processLogFullIfNeeded();
			applet.appCreditResp(balance, t3_8, apdu);
        } catch(DateException de) {
            //added by LC 21/11/00
                    //@ assert false;
	            transactionCounter++;
	            transaction.setNumber(transactionCounter);
			// normally we never get here as the date is already constructed
			transaction.setStatus(Transaction.ABORTED);
			transactionRecord.addTransaction(transaction);
			ISOException.throwIt(ISO7816.SW_UNKNOWN);
        } catch(HeureException ee) {
                    //@ assert false;
            //added by LC 21/11/00
	            transactionCounter++;
	            transaction.setNumber(transactionCounter);
			// normally we never get here as the date is already constructed
			transaction.setStatus(Transaction.ABORTED);
			transactionRecord.addTransaction(transaction);
			ISOException.throwIt(ISO7816.SW_UNKNOWN);
		}
	}



/*initiate an exchange session
	 *	@param monnaieCourante: the current currency
	 *	@param nouvelleMonnaie: the new currency
	 *	@param idBnk: the id of the bank
	 *	@param randT: the terminal random
	 *	@param ttc :the terminal transaction counter
	 *	@param tsn :the terminal serial number
	 *	@param buffer:the buffer used to verify the secure messaging if necessary
	 */
// re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of ISOException exception
    /*@
      //verifyState
      modifies securite.State ; 
      requires true;
      ensures true ;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
	void appInitExchange(
		byte monnaieCourante, byte nouvelleMonnaie, byte[] idBnk, byte[] randT,
		byte[] ttc, byte[] tsn, APDU apdu
	) 
	    throws ISOException {
		byte buffer[] = apdu.getBuffer();
		// check the applet's state
		securite.verifyState(PurseApplet.APP_INIT_EXCHANGE);
		try {
			if(securite.verifyAccessControlTable(PurseApplet.APP_INIT_EXCHANGE, 
			    Security.NOT_INITIALIZED,apdu))
			    {
				if(transactionCounter >= maxTransactionCounter) {
					ISOException.throwIt(PurseApplet.TRANSACTION_COUNTER_OVERFLOW);
				}
				if(monnaieCourante != currency) {
					ISOException.throwIt(PurseApplet.NOT_CURRENT_CURRENCY);
				}
				if(!currencyTable.contens(nouvelleMonnaie)) {
					ISOException.throwIt(PurseApplet.CURRENCY_NOT_SUPPORTED);
				}

				//increase the transaction counter
				// modified by LC 22/11/00. it is now performed in appExchange
				//transactionCounter++;
				

				// the data of the exchange session are temporarly stored in the exchangeSession
				// variable. It will be used later in the appExchange command
				exchangeSession.reset();
				exchangeSession.setAncienneDevise(monnaieCourante);
				exchangeSession.setNouvelleDevise(nouvelleMonnaie);
				exchangeSession.setId(idBnk, (short)0);
				exchangeSession.setSessionNumber(transactionCounter);
				exchangeSession.setTerminalTC(ttc, (short)0);
                exchangeSession.setTerminalSN(tsn, (short)0);
				Util.arrayCopyNonAtomic(randT, (short)0, t2_4, (short)0, (short)4);

                securite.setTerminalSessionType( Security.EXCHANGE_SESSION);

				// generate the session keys and the purse random
				Util.arrayCopyNonAtomic(securite.generateSessionKeys(tsn, ttc, randT, appletSN, 
				transactionCounter), (short) 0, t1_4, (short)0,(short)4);
				
				// get the purse provider aid
				AID aid = JCSystem.getAID();
				if(aid != null)
					aid.getBytes(temp, (short)0);
				else
					Util.arrayCopyNonAtomic(
						this.aid, (short)0, temp, (short)0, (short)this.aid.length);
				// Only the first 5 bytes are used. T4_5 contains the aid of the applet
				Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);

                // WARNING: perhaps a signature is missing to allow the terminal to authenticate
                // the purse
            Util.arrayCopyNonAtomic(securite.initSignature(tsn, ttc, randT, appletSN,
					            transactionCounter, expirationDate, (byte) 0), (short) 0, t3_8, (short) 0, (short) 8);
                // the purse response to the terminal
				applet.appInitExchangeResp(
					t4_5, appletSN, expirationDate, transactionCounter, t1_4,
					Cipher.ALG_DES_CBC_NOPAD, Signature.ALG_DES_MAC8_NOPAD, apdu
				);
			} else {
				// the current state does not match the security requierements
				ISOException.throwIt(PurseApplet.ACCESS_CONDITION_ERR);
			}
		} catch(ISOException e)
		    {ISOException.throwIt(e.getReason());
		}
	}

	/**
	 perform the change of the current currency
	 *	@param date: the transaction date
	 *	@param heure: the transaction time
	 *	@param bankCert: the bank certificate that certifies the exchange rate
	 *	@param termSign :the terminal signature to authenticate the terminal
	 *	@param buffer : the apdu buffer used to verify the secure messaging if necessary
	 */
// re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
	  
      //setTerminalSessionType
      modifies securite.terminalSessionType ;
      requires true;
      ensures true;
      //setTerminalSessionType
      ensures securite.terminalSessionType == Security.NOT_INITIALIZED ;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
    void appExchangeCurrency(
			     Date date, Heure heure, byte[] bankCert, byte[] termSign, APDU apdu
			     )
    throws ISOException {
		byte buffer[] = apdu.getBuffer();
		// check the applet's state
		securite.verifyState(PurseApplet.APP_EXCHANGE_CURRENCY);
		// check the access condition
		securite.verifyAccessCondition(
			buffer, Security.EXCHANGE_SESSION, PurseApplet.APP_EXCHANGE_CURRENCY);
		// now we have check that the session is a EXCHANGE_SESSION, we can set it to NOT_INITIALIZED
		securite.setTerminalSessionType(Security.NOT_INITIALIZED);
		
		try {
			exchangeSession.setHeure(heure);
			exchangeSession.setDate(date);
			
		    // verification of the terminal signature
		    // first, obtain the purse provider aid
		    AID aid = JCSystem.getAID();
		    if(aid != null)
			    aid.getBytes(temp, (short)0);
		    else
			    Util.arrayCopyNonAtomic(
				    this.aid, (short)0, temp, (short)0, (short)this.aid.length
			    );
		    // only the first 5 bytes are used
		    Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);

            // authenticate the terminal
		    if(!securite.authenticateTerminalExchangeSignature(exchangeSession, expirationDate,
		        date, heure, bankCert, termSign, appletSN, transactionCounter)){
		        // added by LC and HM 08/01/01
			transactionCounter++;
			exchangeSession.setSessionNumber(transactionCounter);
			//normally we never get here as the date is already constructed
			exchangeSession.setStatus(ExchangeSession.ABORTED);
			exchangeRecord.addExchangeSession(exchangeSession);
			ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
		    }

		    // verification of the bank certificate
		    try {
		        securite.verifyBankCertificate(bankCert,appletSN,transactionCounter);
		    }
		    catch (ISOException e){
			// Added by LC and HM 09/01/2001
			transactionCounter++;
			exchangeSession.setSessionNumber(transactionCounter);
			// normally we never get here as the time is already constructed
			exchangeSession.setStatus(ExchangeSession.ABORTED);
			exchangeRecord.addExchangeSession(exchangeSession);
			ISOException.throwIt(PurseApplet.BANK_CERTIFICATE_ERR);
		    }
			
			// computation of the new inverse rate and of the new balance
			try{
			decimal1.setValue(bankCert, (short)1);
			decimal1.mul(invExchangeRate);
			decimal2.setValue(balance);
			decimal2.mul(decimal1);}
			catch(DecimalException e){
                ISOException.throwIt(PurseApplet.DECIMAL_OVERFLOW);			
            }
			// generate the signature to authenticate the purse
			Util.arrayCopyNonAtomic(securite.generateExchangeSignature(exchangeSession,decimal2),(short)0,
			                    t3_8,(short)0, (short)8);

			// modification of the purse attributes
			JCSystem.beginTransaction();
			try{
			balance.setValue(decimal2);
			//code modified by Nestor CATANO 12/10/01
			//the method setValue(byte[], short); doesn't exist
			exchangeRate.setValue(bankCert, (short)1);
			//exchangeRate.setValue(bankCert, (short)1);
			invExchangeRate.setValue(bankCert, (short)5);
			maxBalance.mul(decimal1);
			maxDebitWOExternalAut.mul(decimal1);
			maxDebitWOPIN.mul(decimal1);
			creditAmountWOAuthorization.mul(decimal1);
			currency = exchangeSession.getNouvelleDevise();}
			catch(DecimalException e){
                ISOException.throwIt(PurseApplet.DECIMAL_OVERFLOW);			
            }
			JCSystem.commitTransaction();

            // added by LC 22/11/00
            transactionCounter++;
            exchangeSession.setSessionNumber(transactionCounter);
			// the transaction is now stored in the exchange sessions record
			exchangeSession.setStatus(ExchangeSession.WELL_TERMINATED);
			exchangeRecord.addExchangeSession(exchangeSession);
			// the loyalties are informed of the currency change
			informLoyaltiesOfExchangingRate();
			applet.appExchangeCurrencyResp(balance, currency, t3_8, apdu);
		} catch (DateException de) {
		    // added by LC 22/11/00
                    //@ assert false;
            transactionCounter++;
            exchangeSession.setSessionNumber(transactionCounter);
			//normally we never get here as the date is already constructed
			exchangeSession.setStatus(ExchangeSession.ABORTED);
			exchangeRecord.addExchangeSession(exchangeSession);
			
			ISOException.throwIt(ISO7816.SW_UNKNOWN);
		} catch (HeureException ee) {
		    // added by LC 22/11/00
                    //@ assert false;
            transactionCounter++;
            exchangeSession.setSessionNumber(transactionCounter);
			// normally we never get here as the time is already constructed
			exchangeSession.setStatus(ExchangeSession.ABORTED);
			exchangeRecord.addExchangeSession(exchangeSession);
			ISOException.throwIt(ISO7816.SW_UNKNOWN);
		}
	}



	/**
	 *	initilisation of a verify PIN session
	 *	@param tsn: the terminal serial number
	 *	@param randT: the terminal random
	 *	@param buffer: the apdu buffer, used to check the secure messaging if necessary
	 */
    //code modified by Nestor CATANO 24/10/01
    //addition of ISOException exception
    /*@
      //verifyState
      modifies securite.State ; 
      requires true;
      ensures true ;
      //verifyState 
      exsures (ISOException e ) false;
    */	
	void appInitVerifyPin(byte[] tsn, byte[] randT, APDU apdu)
	    throws ISOException {
		//byte buffer[] = apdu.getBuffer();
		// verify the current state of the applet
		securite.verifyState(PurseApplet.APP_INIT_VERIFY_PIN);
		try {
			// verification of the access condition
			if( securite.verifyAccessControlTable(PurseApplet.APP_INIT_VERIFY_PIN, 
			            Security.NOT_INITIALIZED,apdu))
			  {          
			    // The current session is now a VERIFY PIN session
				securite.setTerminalSessionType(Security.VERIFY_PIN_SESSION);  
				// Generation of the session keys
				Util.arrayCopyNonAtomic(securite.generateSessionPINKeys(tsn, randT, appletSN),
				        (short)0, t1_4,(short) 0, (short) 4);
				
				// the purse response to the terminal: The applet serial number, the card
				// random, the encryption algorithm, the signature algorithm and the apdu
				// are sent back to the terminal
				applet.appInitVerifyPinResp(
					appletSN, t1_4,
					Cipher.ALG_DES_CBC_NOPAD, Signature.ALG_DES_MAC8_NOPAD, apdu
				);
			} else {
				// The current state does not match the access condition
				ISOException.throwIt(PurseApplet.ACCESS_CONDITION_ERR);
			}
		} //catch(AccessConditionException ase) {
			// an error has occured during the verification of the access condition
			// This error is internally treated
			//ISOException.throwIt(PurseApplet.ACCESS_CONDITION_ERR);
			catch(ISOException e) {ISOException.throwIt(e.getReason());
		}
	}
	
	/************************************************************************************/
	/***************************** The Verify PIN session *******************************/
	/************************************************************************************/
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
	  
      //verifyAccessCondition     
      requires true;
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
	void appVerifyPin(APDU apdu)
	    throws ISOException {
		byte[] buffer = apdu.getBuffer();
		// check if this command can be invoked regarding the state of the applet
		securite.verifyState(PurseApplet.APP_VERIFY_PIN);
		/* verification of the current access condition*/
		securite.verifyAccessCondition(buffer, Security.VERIFY_PIN_SESSION, PurseApplet.APP_VERIFY_PIN);
		/* the number of tries on the PIN code*/
		byte tries;
		/* To check th PIN code, we use the functionnalities of the Open Platform*/
		if(OPSystem.verifyPin(apdu, ISO7816.OFFSET_CDATA))
		{
		    // reset the transactionWOPIN
			transactionWOPIN = 0;
			// set the number of tries
			tries = -1;
            // indicates that the verification has been performed: the card holder is 
            // authenticated
			securite.setAuthPin(true);
		} else {
		    // indicate the number of remaining tries, we use the Open Platform
		    // functionnalities
			tries = OPSystem.getTriesRemaining();
			// the user is no longer authenticated LC 05/01/01
			securite.setAuthPin(false);
			transactionWOPIN = 3;
		}
		// The verify pin is terminated. The current session is now NOT_INITIALIZED
		securite.setTerminalSessionType(Security.NOT_INITIALIZED);
		// the purse response to the terminal. It sends back the number of tres, the 
		// cryptographic key for the secure messaging and the apdu
		applet.appVerifyPinResp(tries,  apdu);
	}

/* read the current purse balance in its current currency*/
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
	  
      //verifyAccessCondition     
      requires true;
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
	void appGetBalance(APDU apdu)
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		// check the state of the applet
		securite.verifyState(PurseApplet.APP_GET_BALANCE);
		// check if the access condition allows the execution of this command
		securite.verifyAccessCondition(
			apduBuffer, Security.NOT_INITIALIZED, PurseApplet.APP_GET_BALANCE);
		// The purse response to the terminal includes the balance and the currency
		applet.appGetBalanceResp(balance, currency,  apdu);
	}

	
/*****************************************************************************************/
/***********************   Administrative methods      ***********************************/
/*****************************************************************************************/					
    
                    
/* initiate an administrative session. To do so, it requires several parameters:
   @param terminalSN: the terminal serial number
   @param terminalSC: the terminal session counter ????
   @param randT: the terminal random
   @param type: the kind of administrative session: ADMINISTRATIVE or SYSTEM
   @param apdu: the apdu buffer, used to chech the Secure Messaging if necessary */
    //code modified by Nestor CATANO 24/10/01
    //addition of ISOException exception
    /*@
      //verifyState
      modifies securite.State ; 
      requires true;
      ensures true ;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
    void admInitAdministrativeMode(
				   byte[] terminalSN, byte[] terminalSC, byte[] randT, byte type, APDU apdu
				   ) 
	throws PurseException, ISOException {
	//byte apduBuffer[] = apdu.getBuffer();
	// check the applet's state
	securite.verifyState(PurseApplet.ADM_INIT_ADMINISTRATIVE_MODE);
	try {
	    // verification of the access condition
	    
	    if(securite.verifyAccessControlTable(PurseApplet.ADM_INIT_ADMINISTRATIVE_MODE,
						 securite.NOT_INITIALIZED,  apdu))
		{
		    // the behaviour depends on the session type
		    // ADMINISTRATIVE is used to modify some purse parameters
		    // SYSTEM is used to manage the cryptographic keys
		    switch(type) {
		    case Security.ADMINISTRATIVE_SESSION:
			// to access attributes
			securite.setTerminalSessionType(Security.ADMINISTRATIVE_SESSION);
			break;
		    case Security.SYSTEM_SESSION :
			// to access cryptographic keys
			securite.setTerminalSessionType(Security.SYSTEM_SESSION);
			break;
		    default:
			// the session must be one of the following
			byte t = PurseException.ADMINISTRATIVE_MODE_ERR;
			PurseException.throwIt(t);
			break;
		    }
				
		    // increase the counter of adminisitrative session
		    administrativeCounter++;
		    // generate the cryptographic session keys used to secure communication
		    // and copy the card random into t1_4
		    Util.arrayCopyNonAtomic(securite.generateSessionKeys(terminalSN,
									 terminalSC, randT, appletSN, administrativeCounter),
					    (short) 0, t1_4, (short)0,(short) 4);
		    // set the SMKey for this session
		    //SMKey = securite.getSMKey();
			
		    // generate the signature of the data
		    Util.arrayCopyNonAtomic(securite.initAdministrativeSignature(terminalSN, terminalSC,randT,type,appletSN,
										 administrativeCounter),
					    (short)0, t3_8, (short)0,(short)8);
				
				/*// prepare the data to be signed
				short offset = (short)0;
				// add the terminal serial number
				offset = Util.arrayCopyNonAtomic(
					terminalSN, (short)0, temp, offset, (short)terminalSN.length);
				// add the terminal session counter
				offset = Util.arrayCopyNonAtomic(
					terminalSC, (short)0, temp, offset, (short)terminalSC.length);
				// add the terminal random
				offset = Util.arrayCopyNonAtomic(
					randT, (short)0, temp, offset, (short)randT.length);
				// the type of the administrative session SYSTEM or ADMINISTRATIVE
				temp[offset++] = type;
				// add the applet serial number
				offset = Util.arrayCopyNonAtomic(
					appletSN, (short)0, temp, offset, (short)appletSN.length);
				// add the number of administrative session
				offset = Util.setShort(temp,offset, administrativeCounter);
				offset = Util.arrayCopyNonAtomic(
					t1_4, (short)0, temp, offset, (short)t1_4.length);
				// indicate the encryption algorithm used by the card
				temp[offset++] = Cipher.ALG_DES_CBC_NOPAD;
				// indicate the signature algorithm used by the card
				temp[offset++] = Signature.ALG_DES_MAC8_NOPAD;
				// generate the signature
				PacapSignature.sign(
					temp, (short)0, offset,
					signatureKey,
					t3_8, (short)0,
					ivNull, (short)0, (short)ivNull.length);*/
                // the purse response to the terminal
	applet.admInitAdministrativeModeResp(
					     appletSN, administrativeCounter, t1_4,
					     Cipher.ALG_DES_CBC_NOPAD, Signature.ALG_DES_MAC8_NOPAD,
					     t3_8, apdu);
		} else {
		    // the current state does not match the access condition
		    ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
		}
	} //catch(AccessConditionException ase) {
	// an error has occured during the verification of the access condition
	// this error is internaly treated
	//	ISOException.throwIt(PurseApplet.ACCESS_CONDITION_ERR);
	catch(ISOException e){ISOException.throwIt(e.getReason());
	}
    }

/* verification of the administrative code. This code is different from the PIN code. It is
used to authenticate the administrator and it is lmanaged by the purse.
    @param pin: the code to checked
    @apdu: the apdu buffer used to check the secure messaging if necessary */
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
	void admVerifyAdmPin(short pin, APDU apdu)
	    throws ISOException {
	    byte apduBuffer[] = apdu.getBuffer();
	    // check the applet's state
	    securite.verifyState(PurseApplet.ADM_VERIFY_ADM_PIN);
	    // check if the current access condition allow the execution of this command
	    securite.verifyAccessCondition(apduBuffer,Security.ADMINISTRATIVE_SESSION, 
					   PurseApplet.ADM_VERIFY_ADM_PIN);
	    // conditions are accepted, the code can now be checked
	    //Util.setShort(temp, (short)0, pin);
	    //boolean resu = administrativeCode.check(temp, (short)0, (byte)2);
	    boolean resu = securite.checkAdministrativeCode(pin);
	    // if the code is not correct, it ends the adminsitrative session
	    if(!resu) endAdminMode();
	    // the purse responds to the terminal. It indicates the check of the code and the 
	    // number of remaining tries
	    applet.admVerifyAdmPinResp(
				       resu, securite.triesAdministrativeCode(),  apdu);
	}
	
    /* End a session by setting the TypeSession to NOT_INITIALIZED*/
    //code modified by Nestor CATANO
    //inclusion of throws clause
    /*@
      modifies securite.terminalSessionType ;
      modifies securite.administrativeCode.flags, 
               securite.administrativeCode.flags[OwnerPIN.VALIDATED],
               securite.administrativeCode.triesLeft[0] ;
      requires true ;
      ensures securite.terminalSessionType == Security.NOT_INITIALIZED ; 
      ensures securite.administrativeCode.flags != null ; 
      exsures (SystemException e) e._reason == SystemException.NO_TRANSIENT_SPACE;
    */	
	void admEndSession(APDU apdu) 
	    throws SystemException {
	        endAdminMode();
	        applet.admEndSessionResp(apdu);
	}

	/* Read the Applet serial number. This number is unique for each applet.
	    @param apdu: the apdu buffer, used to check the Secure Messaging if necessary */
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
	void admGetAsn(APDU apdu)
	    throws ISOException {
		byte buffer[] = apdu.getBuffer();
		// check the applet's state
		securite.verifyState(PurseApplet.ADM_GET_ASN);
		// check the access condition mandatory to execute this command
		securite.verifyAccessCondition(buffer, Security.ADMINISTRATIVE_SESSION, PurseApplet.ADM_GET_ASN); 
		// the purse response to the terminal includes the applet serial number
		applet.admGetAsnResp(appletSN,  apdu);                             
	}

	
/* This command sets the applet serial number. This command can only be performed in the
    not personalized state.
    @param sn: the new serial number */
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
	void admSetAsn(byte[] sn, APDU apdu) 
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		// check the applet's state
		securite.verifyState(PurseApplet.ADM_SET_ASN);
		// check the applet state using the Open Platform functionalities
		//if(OPSystem.getCardContentState() != OPSystem.APPLET_SELECTABLE)
		//	ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
		// if the state is correct, then check the access condition	
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION, PurseApplet.ADM_SET_ASN);
		// set the new applet serial number
		Util.arrayCopy(sn, (short)0, appletSN, (short)0, (short)4);
		// the purse response to the terminal
		applet.admSetAsnResp( apdu);
	}

/* modify the applet state using the Open Platform functionnalities.
    @param state: the new applet state*/
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
	void admSetState(byte state, APDU apdu)
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		securite.verifyState(PurseApplet.ADM_SET_STATE);
		// check if the current acces condition allows the execution of this command
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION, PurseApplet.ADM_SET_STATE);
		// set the new applet state
		if (!(OPSystem.setCardContentState(state)))
		    ISOException.throwIt(ISO7816.SW_UNKNOWN);
		// the purse response to the terminal
		applet.admSetStateResp( apdu);
	}


/* read the current purse balance in its current currency*/
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
	void admGetBalance(APDU apdu)
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		securite.verifyState(PurseApplet.ADM_GET_BALANCE);
		// check if the access condition allows the execution of this command
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION, PurseApplet.ADM_GET_BALANCE);
		// The purse response to the terminal includes the balance and the currency
		applet.admGetBalanceResp(balance, currency,  apdu);
	}

	
/* read a given transaction that is stored in TransactionRecord.
    @param index: the index of the transaction in the history file TransactionRecord*/
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
	void admGetTransactionRecord(short index, APDU apdu) 
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		// check the applet's state
		securite.verifyState(PurseApplet.ADM_GET_TRANSACTION_RECORD);
		// check if the access condition allows the execution of the command
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION, PurseApplet.ADM_GET_TRANSACTION_RECORD);
		// check the correctness of the index	
		Transaction t = transactionRecord.getTransaction(index);
		if(t == null) {
			short code  = ISO7816.SW_DATA_INVALID;
			ISOException.throwIt(code);
		}
		// indicate the number of remaing entries
		// Modified by HM 08/01/2001
//		byte reste = (byte)(transactionRecord.getNbEntries() - (byte)(index  - (byte)1));
		byte reste = (byte)(transactionRecord.getNbEntries() - (byte)(index) - (byte)1 );
		// extracting the data of the given transaction and sending them back to the
		// terminal
		applet.admGetTransactionRecordResp(
			t.getNumber(), t.getTerminalSN(),
			t.getTerminalTC(),
			t.getType(), t.getPartnerID(),
			t.getTypeProduit(), t.getDevise(),
			t.getTaux(), t.getMontant(),
			t.getDate(), t.getHeure(),
			t.getCertificat(),
			t.getMutualAuthentification(),
			t.getBankAuthorization(),
			t.isGiven(), t.getStatus(),
			t.isValid(), reste,
			 apdu);
	}

/* read the applet's expiration date*/
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */	
    void admGetExpirationDate(APDU apdu) 
	throws ISOException {
	byte apduBuffer[] = apdu.getBuffer();
	securite.verifyState(PurseApplet.ADM_GET_EXPIRATION_DATE);
	// check if the current access condition grants the execution of this command
	securite.verifyAccessCondition(
				       apduBuffer, Security.ADMINISTRATIVE_SESSION, PurseApplet. ADM_GET_EXPIRATION_DATE);
	// the purse response to the terminal includes the expiration date of the purse	
	applet.admGetExpirationdateResp(expirationDate,  apdu);
    }

/* modify the expiration date of the purse.
    @param d: the expiration date of the purse.*/
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */
	void admSetExpirationDate(Date d, APDU apdu) 
	throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		securite.verifyState(PurseApplet.ADM_SET_EXPIRATION_DATE);
		// check if the current access condition grants the execution of this command
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION,
			PurseApplet.ADM_SET_EXPIRATION_DATE);
		// set the expiration date in the purse
		try {
			expirationDate.setDate(d);
		} catch(DateException de) {}
                    //@ assert false;
		// the purse response to the terminal
		applet.admSetExpirationdateResp( apdu);
	}

/* initialize or modify the cryptographic mother key used to secure the DEBIT session.
    @param bArray: a cryptogram containg the key.*/
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 

      //setDebitKey
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
	       ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //setDebitKey
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                       && JCSystem._transactionDepth == 1 ; 

      //verifyState, setDebitKey
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ||
                                e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ; 
    */	
	void admSetDebitKey(byte[] bArray, APDU apdu)
	    throws ISOException, TransactionException {
		//byte apduBuffer[] = apdu.getBuffer();
		securite.verifyState(PurseApplet.ADM_SET_DEBIT_KEY);
		// check if the current access condition grants the execution of this command
		//securite.verifyAccessCondition(
		//	apduBuffer, securite.SYSTEM_SESSION, PurseApplet.ADM_SET_DEBIT_KEY);
		// decrypt the bArray. This key is encrypted by the purse provider because
		// a cryptographic key is confidential. The result is stored in DebitKey
		//decryptKey(bArray, (short)0, (byte)16, debitKey);
		// update the key in the security class
		securite.setDebitKey(bArray, apdu);
		// the purse response to the terminal
		applet.admSetDebitKeyResp( apdu);
	}
	
/* initialize or modify the cryptographic mother key used to secure the CREDIT session.
    @param bArray: a cryptogram containg the key.*/

    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 

      //setCreditKey
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //setCreditKey
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                       && JCSystem._transactionDepth == 1 ; 

      //verifyState, setCreditKey
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ||
                                e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ;
    */	
	void admSetCreditKey(byte[] bArray, APDU apdu)
	    throws ISOException, TransactionException {
		//byte apduBuffer[] = apdu.getBuffer();
		securite.verifyState(PurseApplet.ADM_SET_CREDIT_KEY);
		// check if the current access condition grants the execution of this command
		//securite.verifyAccessCondition(
		//	apduBuffer, securite.SYSTEM_SESSION, PurseApplet.ADM_SET_CREDIT_KEY);
		// decrypt the bArray. This key is encrypted by the purse provider because
		// a cryptographic key is confidential. The result is stored in CreditKey
		//decryptKey(bArray, (short)0, (byte)16, creditKey);
		// update the key in the security class
		securite.setCreditKey(bArray, apdu);
		// the purse response to the terminal
		applet.admSetCreditKeyResp( apdu);
	}

/* initialize or modify the cryptographic mother key used to secure the PIN session.
    @param bArray: a cryptogram containg the key.*/
    //PERHAPS IT IS NO LONGER USEFULL BECAUSE WE USE THE FUNCTIONNALITIES OF THE
    // OPEN PLATFORM. CHECK THE VALIDITY OF THE ATTRIBUTE PINKEY

    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 

      //setPINKey
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //setPINKey
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                       && JCSystem._transactionDepth == 1 ; 
      //verifyState, setPINKey
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ||
                                e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ;
    */
	void admSetPINKey(byte[] bArray, APDU apdu)
	    throws ISOException, TransactionException{
		//byte apduBuffer[] = apdu.getBuffer();
		securite.verifyState(PurseApplet.ADM_SET_PIN_KEY);
    	// check if the current access condition grants the execution of this command
		//securite.verifyAccessCondition(apduBuffer, securite.SYSTEM_SESSION, PurseApplet.ADM_SET_PIN_KEY);
		// decrypt the bArray. This key is encrypted by the purse provider because
		// a cryptographic key is confidential. The result is stored in PINKey
		//decryptKey(bArray, (short)0, (byte)16, PINKey);
		// update the key in the security class
		securite.setPINKey(bArray, apdu);
		// the purse response to the terminal
		applet.admSetPINKeyResp( apdu);        
	}

/* read the current state of the purse*/
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
	  
      //verifyAccessCondition     
      requires true;
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
	void admGetState(APDU apdu)
	    throws ISOException { 
		byte apduBuffer[] = apdu.getBuffer();
		securite.verifyState(PurseApplet.ADM_GET_STATE);
		// check if the access conditon grants the execution of this command
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION, PurseApplet.ADM_GET_STATE);
		// the purse response to the terminal includes the current state of the applet
		// with the Open Platform functionnalities
		applet.admGetStateResp(OPSystem.getCardContentState(),  apdu);
	}

/* modify the cryptographic key used to authenticate a bank certificate obtained if the
   amount of the credit is too important.
   @param bArray: a cryptogram containing the cryptographic key*/
    //code modified by Nestor CATANO 25/10/01
    //addition of throws clause
    /*@ 
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ;

      //setCreditAuthorizationKey
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
	       ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //setCreditAuthorizationKey, 
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                       && JCSystem._transactionDepth == 1 ; 
      //verifyState, setCreditAuthorizationKey
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED  ||
                                e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ; 
    */
	void admSetCreditAuthorizationKey(byte[] bArray, APDU apdu)
	    throws ISOException, TransactionException {
		byte apduBuffer[] = apdu.getBuffer();
		// check the applet's state
		securite.verifyState(PurseApplet.ADM_SET_CREDIT_AUTHORIZATION_KEY);
		// check if the current access condition grants the execution of this comand
		//securite.verifyAccessCondition(
		//	apduBuffer, securite.SYSTEM_SESSION, PurseApplet.ADM_SET_CREDIT_AUTHORIZATION_KEY);
		// decrypt the bArray. This key is encrypted by the purse provider because
		// a cryptographic key is confidential. The result is stored in creditAuthorizationKey
		//securite.decryptSignatureKey(bArray,PurseApplet.ADM_SET_CREDIT_AUTHORIZATION_KEY);
		// update the key in the security class
		securite.setCreditAuthorizationKey(bArray, apdu);
		// the purse response to the terminal
		applet.admSetCreditAuthorizationKeyResp( apdu); 
	}

/* modify the cryptographic key used to sign the credit certificate generated by 
   the purse after a CREDIT session. 
   @param bArray: a cryptogram containing the cryptographic key*/
    //code modified by Nestor CATANO 25/10/01
    //addition of throws clause
    /*@ 
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ;

      //setCreditSignatureKey
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
	       ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //setCreditSignatureKey
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                       && JCSystem._transactionDepth == 1 ; 

      //verifyState, setCreditSignatureKey
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ||
                                e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED  ;
    */
	void admSetCreditSignatureKey(byte[] bArray, APDU apdu)
	    throws ISOException, TransactionException {
		//byte apduBuffer[] = apdu.getBuffer();
		securite.verifyState(PurseApplet.ADM_SET_CREDIT_SIGNATURE_KEY);
		// check if the current access condition grants the execution of this command
		//securite.verifyAccessCondition(
		//	apduBuffer, securite.SYSTEM_SESSION, PurseApplet.ADM_SET_CREDIT_SIGNATURE_KEY);
		// decrypt the bArray. This key is encrypted by the purse provider because
		// a cryptographic key is confidential. The result is stored in creditSignatureKey	
		//decryptKey(bArray, (short)0, (byte)16, creditSignatureKey);
		// update the key in the security class
		securite.setCreditSignatureKey(bArray, apdu);
		// the purse response to the terminal
		applet.admSetCreditSignatureKeyResp( apdu);
	}

/* modify the cryptographic key used to sign the debit certificate generated by the purse
   after a DEBIT session.
   @param bArray: a cryptogram containing the cryptographic key*/
    //code modified by Nestor CATANO 25/10/01
    //addition of throws clause
    /*@ 
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ;

      //setDebitSignatureKey
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
	       ISOException.systemInstance._reason ; 
      requires true;
      ensures true;
      //setDebitSignatureKey
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                       && JCSystem._transactionDepth == 1 ; 

      //verifyState, setDebitSignatureKey
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ||
                                e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ;
    */
	void admSetDebitSignatureKey(byte[] bArray, APDU apdu) 
	throws ISOException, TransactionException {
		//byte apduBuffer[] = apdu.getBuffer();
		securite.verifyState(PurseApplet.ADM_SET_DEBIT_SIGNATURE_KEY);
		// check if the current access condition grants the execution of this command
		//securite.verifyAccessCondition(
		//	apduBuffer, securite.SYSTEM_SESSION, PurseApplet.ADM_SET_DEBIT_SIGNATURE_KEY);
		// decrypt the bArray. This key is encrypted by the purse provider because
		// a cryptographic key is confidential. The result is stored in debitSignatureKey	
		//decryptKey(bArray, (short)0, (byte)16, debitSignatureKey);
		// update the key in the security class
		securite.setDebitSignatureKey(bArray, apdu);
		// the purse response to the terminal
		applet.admSetDebitSignatureKeyResp( apdu);
	}

	
/* modify the cryptographic key use to generate the session key of an administrative
   session.
   @param bArray: the cryptogram containing the cryptographic key*/
    //code modified by Nestor CATANO 25/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 

      //setAdministrativeKey
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
	       ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //verifyState, setAdministrativeKey
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED  ||     
                                e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ;
      //setAdministrativeKey
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                       && JCSystem._transactionDepth == 1 ;   
    */
    void admSetAdministrativeKey(byte[] bArray, APDU apdu) 
	throws ISOException, TransactionException{
	//byte apduBuffer[] = apdu.getBuffer();
	securite.verifyState(PurseApplet.ADM_SET_ADMINISTRATIVE_KEY);
	// check if the current access condition grants the execution of this command
	//securite.verifyAccessCondition(
	//	apduBuffer, securite.SYSTEM_SESSION, PurseApplet.ADM_SET_ADMINISTRATIVE_KEY
	//);
	// decrypt the bArray. This key is encrypted by the purse provider because
	// a cryptographic key is confidential. The result is stored in administrativeKey
	//decryptKey(bArray, (short)0, (byte)16, administrativeKey);
	// update the key in the security class
	securite.setAdministrativeKey(bArray, apdu);
	// the purse response to the terminal
	applet.admSetAdministrativeKeyResp( apdu); 
    }

/* modify the cryptographic key used to generate the session key of a SYSTEM
   session.
   @param bArray: the cryptogram containing the cryptographic key*/
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 

      //setSystemKey
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //verifyState, setSystemKey
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ||
                                e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ;

      //setSystemKey
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                       && JCSystem._transactionDepth == 1 ; 
    */
    void admSetSystemKey(byte[] bArray, APDU apdu) 
	throws ISOException, TransactionException {
	//byte apduBuffer[] = apdu.getBuffer();
	securite.verifyState(PurseApplet.ADM_SET_SYSTEM_KEY);
	// check if the current access condition grants the execution of this command
	//securite.verifyAccessCondition(
	//	apduBuffer, securite.SYSTEM_SESSION, PurseApplet.ADM_SET_SYSTEM_KEY);
	// decrypt the bArray. This key is encrypted by the purse provider because
	// a cryptographic key is confidential. The result is stored in systemKey	
	//decryptKey(bArray, (short)0, (byte)16, systemKey);
	// update the key in the security class
	securite.setSystemKey(bArray, apdu);
	// the purse response to the terminal
	applet.admSetSystemKeyResp( apdu); 
    }

/* modify the cryptographic key used to generate the session keys of an Exchange session.
   @param bArray: the cryptogram containing the cryptographic key*/
// re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 

      //setExchangeKey
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //setExchangeKey
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                       && JCSystem._transactionDepth == 1 ; 

      //verifyState, setExchangeKey
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED || 
                                e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED;
    */
	void admSetExchangeKey(byte[] bArray, APDU apdu)
	    throws ISOException, TransactionException {
		//byte apduBuffer[] = apdu.getBuffer();
		securite.verifyState(PurseApplet.ADM_SET_EXCHANGE_KEY);
		securite.setExchangeKey(bArray,apdu);
		/*verifyAccessCondition(
			apduBuffer, SYSTEM_SESSION, PurseApplet.ADM_SET_EXCHANGE_KEY
		);      
		decryptKey(bArray, (short)0, (byte)16, exchangeKey);*/
		applet.admSetExchangeKeyResp( apdu);
	}


/* modify the cryptographic key used to check the certificate of the exchange rate.
   @param bArray: the cryptogram containing the cryptographic key*/
// re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
	  
      //setExchangeRateKey
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //verifyState, setExchangeRateKey
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ||
                                e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ; 

      //setExchangeRateKey
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL &&
                                       JCSystem._transactionDepth == 1 ; 
    */
    void admSetExchangeRateKey(byte[] bArray, APDU apdu) 
	throws ISOException, TransactionException{
	//byte apduBuffer[] = apdu.getBuffer();
	securite.verifyState(PurseApplet.ADM_SET_EXCHANGE_RATE_KEY);
	securite.setExchangeRateKey(bArray, apdu);
	/*verifyAccessCondition(
	  apduBuffer, SYSTEM_SESSION, PurseApplet.ADM_SET_EXCHANGE_RATE_KEY
	  );
	  decryptKey(bArray, (short)0, (byte)16, exchangeRateKey);*/
	applet.admSetExchangeKeyRateResp( apdu);
    }



/* modify the adminsitrative code used to authenticate an administrator during
   a ADMINISTRATIVE or a SYSTEM session.
   @param newCode: the new code of the administrator*/
   // WARNING THE ADMINISTRATIVE CODE MAY NEED TO BE IN THE SECURITY CLASS
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
	void admSetAdministrativeCode( short newCode, APDU apdu) 
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		securite.verifyState(PurseApplet.ADM_SET_ADMINISTRATIVE_CODE);
		// check if the current access condition grants the execution of this command
		securite.verifyAccessCondition(
			apduBuffer, Security.SYSTEM_SESSION, PurseApplet.ADM_SET_ADMINISTRATIVE_CODE);
		// format the new code
		//Util.setShort(t1_4,(short)0, newCode);
		// update the administrative code
		//administrativeCode.update(t1_4, (short)0, (byte)2);
		securite.updateAdministrativeCode(newCode);
		// the purse response to the terminal
		applet.admSetAdministrativeCodeResp( apdu);
	}

/*****************************************************************************************/
/*************              Methods of the loyalties inteface             ****************/
/*****************************************************************************************/	
	

/* check if the history file of the purse contains a transaction related with a given 
    loyalty.
    @param aid: the aid of the loyalty
    @return resu: a boolean indicating if there is a transaction (TRUE) or not (FALSE)*/
    /*@
      //modifies \nothing ;
      requires aid != null ;
      ensures true ;
      //exsures (Exception) false ;
     */
    boolean isThereTransaction(AID aid) {
		boolean resu = false;
		// verify if the loyalty is registered within the purse
		AllowedLoyalty al = loyaltiesTable.getAllowedLoyalty(aid);
		if(al != null) {
			// check if the loyalty has a transaction in the purse
			resu  = (0 < transactionRecord.getNbTransactions(al));
		}
		// indicates if the purse has a transaction for the loyalty aid
		return resu;
	}


/* a loyalty asks to the purse its related transactions that have not been taken
   into account.
   @param aid: the aid of the loyalty*/
    /*@
      //modifies \nothing ;
      modifies JCSystem._transactionDepth;
      requires aid != null ;
      // requires that one of the salers in the allowed loyalty is
      // actually in transaction record, so that trans != null
      requires (\exists int i; 
                 (transactionRecord.firstIndex < transactionRecord.newIndex ?
                  transactionRecord.firstIndex <= i && 
                  i < transactionRecord.newIndex :
                 (0 <= i && i < transactionRecord.newIndex) || 
                 (transactionRecord.firstIndex <= i && 
                  i < transactionRecord.MAX_ENTRIES)) &&
               transactionRecord.data[i] != null &&
               transactionRecord.data[i].isValid &&
               transactionRecord.data[i].type == Transaction.TYPE_DEBIT &&
               ! transactionRecord.data[i].given &&
               (\exists int m; 0 <= m & m < loyaltiesTable.nbLoyalties && 
                  (\forall int k; 0 <= k & 
                                  k < loyaltiesTable.data[m].aid.length ==>
                   loyaltiesTable.data[m].aid[k] == aid.theAID[k]) && 
                   (\exists int j; 0 <= j && j < AllowedLoyalty.MAX_SALERS &&
                     (\forall int l; 
                       0 <= l && l < Transaction.PARTNER_ID_LENGTH ==>
                       loyaltiesTable.data[m].data[j].data[l] == 
                         transactionRecord.data[i].id[l]))));
      ensures true ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (NullPointerException) false ;      
      exsures (ISOException) false ;      
      exsures (TransactionException e) 
                 (\old(JCSystem._transactionDepth) == 1 && 
                 e._reason == TransactionException.IN_PROGRESS) || 
		 (\old(JCSystem._transactionDepth) == 0 &&
		 e._reason == TransactionException.NOT_IN_PROGRESS) ;
    */
    // Code modified by Marieke HUISMAN 11/12/01
    // inclusion of throws clause
    TransactionInterface getTransaction(AID aid) 
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException  { 
	TransactionInterface resu = null;
	// verify that the loyalty is registered within the purse
	AllowedLoyalty al = loyaltiesTable.getAllowedLoyalty(aid);
	if(al != null) {
	    // indicates the number of transactions related to the loyalty
	    short nb = transactionRecord.getNbTransactions(al);
	    // check if there is some transactions
	    if(nb > 0) {
		// extract one transaction
		Transaction trans = transactionRecord.getTransaction(al);
		// mark the transaction as sent to the loyalty
		trans.markAsGiven();
                // precondition clone trans.terminalSN != terminalTC
                // can not be established: uniqueness annotation needed
		transactionInterface.clone(trans);
		// indicate the number of remaining transactions related to the loyalty
                //@ assert nb > 0;
		transactionInterface.setReste((byte)(nb - (byte)1));
		resu = transactionInterface;
	    }
	}
	return resu;
    }


/* indicate to a loyalty the current inverse exchange rate.
   @return invExchangeRate: the inverse exchange rate.*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == invExchangeRate ;
      //exsures (Exception) false ;
    */
    Decimal getInvExchangeRate() {
	return invExchangeRate;
    }
    
	
/********************Purse functionalities from the administrative session*****************/	
	/**
	 Read the value of the amount of credit without the bank authorization.
	 Return: this value and the current currency
	 */
// readded by LC 21/11/00

    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
	void admGetCreditAmountWOAut(APDU apdu)
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		// check the state of the purse
		securite.verifyState(PurseApplet.ADM_GET_CREDIT_AMOUNT_WO_AUT);
		// check the current access condition
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION, PurseApplet.ADM_GET_CREDIT_AMOUNT_WO_AUT
		);
		// the purse response to the terminal
		applet.admGetCreditAmountWOAutResp(
			creditAmountWOAuthorization, currency, apdu
		);
	}


	/**
	 *	Modify the value of the maximum credit without a bank authorization
	 *	@param d the new value.This value is provided in the current currency
	 */     
// readded by LC 21/11/00

    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
	void admSetCreditAmountWOAut(Decimal d, APDU apdu)
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		// check the state of the applet
		securite.verifyState(PurseApplet.ADM_SET_CREDIT_AMOUNT_WO_AUT);
		// check the current access condition
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION, PurseApplet.ADM_SET_CREDIT_AMOUNT_WO_AUT);
		// The access conditions are correct, the value can be changed
		JCSystem.beginTransaction();
		try{creditAmountWOAuthorization.setValue(d);}
		catch(DecimalException de)
		    {ISOException.throwIt(PurseApplet.DECIMAL_OVERFLOW);}
		JCSystem.commitTransaction();
		applet.admSetCreditAmountWOAutResp( apdu);
	}
	
/* Read the max number of transaction allowed */
// re-added by LC 21/11/00


    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
	void admGetMaxTransactionCounter(APDU apdu)
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		// check the state of the appelt
		securite.verifyState(PurseApplet.ADM_GET_MAX_TRANSACTION_COUNTER);
		// check the current access condition
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION,
			PurseApplet.ADM_GET_MAX_TRANSACTION_COUNTER);
		applet.admGetMaxTransactionCounterResp(maxTransactionCounter, apdu);
	}


/*Modify the maximum number of transaction allowed */
// re-added by LC 21/11/00

    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
	void admSetMaxTransactionCounter(short mt, APDU apdu)
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		// check the state of the applet
		securite.verifyState(PurseApplet.ADM_SET_MAX_TRANSACTION_COUNTER);
		// check the current access condition
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION,
			PurseApplet.ADM_SET_MAX_TRANSACTION_COUNTER);
		// check if the value is greater or equal than the previous one	
		if(mt >= maxTransactionCounter) {
			short code = ISO7816.SW_DATA_INVALID;
			ISOException.throwIt(code);
		}
		    maxTransactionCounter = mt;
		applet.admSetMaxTransactionCounterResp( apdu);
	}

/* read the current currency of the purse*/
// re-added by LC 21/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
	void admGetCurrency(APDU apdu) 
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		// check the applet of the state
		securite.verifyState(PurseApplet.ADM_GET_EXCHANGE_RATE);
		// check the current access condition
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION, PurseApplet.ADM_GET_EXCHANGE_RATE
		);
		applet.admGetCurrencyResp(currency, apdu);
	}



/*	read the exchange rate */
// re-added by LC 21/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	   
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
	void admGetExchangeRate(APDU apdu) 
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		// check the state of the applet
		securite.verifyState(PurseApplet.ADM_GET_EXCHANGE_RATE);
		// check the acces condition
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION, PurseApplet.ADM_GET_EXCHANGE_RATE
		); 
		applet.admGetExchangeRateResp(exchangeRate, apdu);
	}
	
	/**read an entry in the loyalty table*/
// re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
	void admGetLoyaltiesTable(byte index, APDU apdu) 
	throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		// check the applet's state
		securite.verifyState(PurseApplet.ADM_GET_LOYALTIES_TABLE);
		// check the current access condition
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION, 
			PurseApplet.ADM_GET_LOYALTIES_TABLE
		);
		AllowedLoyalty t = loyaltiesTable.getAllowedLoyalty(index);
		if(t == null) {
			short code = ISO7816.SW_DATA_INVALID;
			ISOException.throwIt(code);
		}
		byte reste = (byte)(loyaltiesTable.getNbLoyalties() - (byte)(index + (byte)1));
		short offset = (short)0;
		byte max = t.MAX_SALERS;
		for(byte i = (byte)0;i < max;i++) {
			offset = Util.arrayCopyNonAtomic(
				t.getSaler(i).getBytes(), (short)0, temp, offset, SalerID.ID_LENGTH
			);
		}
		applet.admGetLoyaltiesTableResp(
			t.getAID(), t.isToBeInformed(), temp, offset, reste, apdu
		);
	}


	/**suppress an entry in the loyalty table*/
// re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
    void admDelLoyaltiesTable (byte index, APDU apdu)
	throws ISOException  {
	byte apduBuffer[] = apdu.getBuffer();
	// check the applet's state
	securite.verifyState(PurseApplet.ADM_DEL_LOYALTIES_TABLE);
	// check the current access condition
        securite.verifyAccessCondition(
				       apduBuffer, Security.ADMINISTRATIVE_SESSION, PurseApplet.ADM_DEL_LOYALTIES_TABLE
				       );
	
	// Modified by L. CASSET and H. MARTIN 18/01/01 (test added)
	if((loyaltiesTable.getNbLoyalties() > index) && (index >= 0)) {
	    JCSystem.beginTransaction();
	    loyaltiesTable.delLoyalty(index);
	    JCSystem.commitTransaction();
	} else {
	    short code = ISO7816.SW_DATA_INVALID;
	    ISOException.throwIt(code);
	}                
	applet.admDelLoyaltiesTableResp(apdu);
    }


    /*add an entries in loyalty table*/
// re-added by LC 22/11/00

    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
	void admAddLoyaltiesTable(byte[] aid, boolean bool, byte[] salerId, APDU apdu) 
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		// check the applet's state
		securite.verifyState(PurseApplet.ADM_ADD_LOYALTIES_TABLE);
		// check the current access condition
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION, PurseApplet.ADM_ADD_LOYALTIES_TABLE
		);
		try {
			loyaltiesTable.addLoyalty(aid, bool, salerId);
		} catch(LoyaltiesTableException lte) {
			ISOException.throwIt(PurseApplet.LOYALTIES_TABLE_FULL);
		} catch(AllowedLoyaltyException ale) {
			ISOException.throwIt(PurseApplet.SALERS_TABLE_FULL);
		}
		applet.admAddLoyaltiesTableResp( apdu);
	}

/* read the maximum value for the balance*/
// re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
    void admGetMaxBalance(APDU apdu)
	throws ISOException {
	byte apduBuffer[] = apdu.getBuffer();
	//check the applet's state
	securite.verifyState(PurseApplet.ADM_GET_MAX_BALANCE);
		// check the current access condition
	securite.verifyAccessCondition(
				       apduBuffer, Security.ADMINISTRATIVE_SESSION, 
				       PurseApplet.ADM_GET_MAX_BALANCE
				       );
	applet.admGetMaxBalanceResp(maxBalance, apdu);
    }


/* modify the maximum value for the balance*/
// re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
	void admSetMaxBalance(Decimal d, APDU apdu) 
	    throws ISOException{
		byte apduBuffer[] = apdu.getBuffer();
		// check the applet's state
		securite.verifyState(PurseApplet.ADM_SET_MAX_BALANCE);
		// check the current access condition
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION, 
			PurseApplet.ADM_SET_MAX_BALANCE
		);

		if(d.isSmallerThan(balance)) {
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		}
		try{maxBalance.setValue(d);}
		catch(DecimalException de) 
		    {ISOException.throwIt(PurseApplet.DECIMAL_OVERFLOW);}
		applet.admSetMaxBalanceResp(apdu);           
	}


	/* read the max debit without PIN value*/
// re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
	void admGetMaxDebitWOPIN(APDU apdu)
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		// check the applet's state
		securite.verifyState(PurseApplet.ADM_GET_MAX_DEBIT_WO_PIN);
		//check the current access condition
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION, 
			PurseApplet.ADM_GET_MAX_DEBIT_WO_PIN
		);
		applet.admGetMaxDebitWOPINResp(maxDebitWOPIN, apdu);
	}


/* modify the max debit without PIN value*/
// re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
	void admSetMaxDebitWOPIN(Decimal d, APDU apdu)
	    throws ISOException {
		byte apduBuffer[] = apdu.getBuffer();
		// check the applet's state
		securite.verifyState(PurseApplet.ADM_SET_MAX_DEBIT_WO_PIN);
		// check the current access condition
		securite.verifyAccessCondition(
			apduBuffer, Security.ADMINISTRATIVE_SESSION, PurseApplet.ADM_SET_MAX_DEBIT_WO_PIN
		);
		if(d.isNegatif()) {
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		}
		try{maxDebitWOPIN.setValue(d);}
		catch(DecimalException de)
		    {ISOException.throwIt(PurseApplet.DECIMAL_OVERFLOW);}
		applet.admSetMaxDebitWOPINResp(  apdu);
	}


/* read the max number of transaction allowed without re-entering the pin code*/
// re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED 
    */
    void admGetMaxTransactionWOPIN(APDU apdu)
	throws ISOException {
	byte apduBuffer[] = apdu.getBuffer();
	// check the applet's state
	securite.verifyState(PurseApplet.ADM_GET_MAX_TRANSACTION_WO_PIN);
		// check the current access condition
	securite.verifyAccessCondition(
				       apduBuffer, Security.ADMINISTRATIVE_SESSION,
				       PurseApplet.ADM_GET_MAX_TRANSACTION_WO_PIN
				       );
	applet.admGetMaxTransactionWOPINResp(maxTransactionWOPIN, apdu);
    }
    
    
    /*modification of the max transaction without pin value*/
    // re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */
    void admSetMaxTransactionWOPIN(byte n, APDU apdu) 
	throws ISOException {
	byte apduBuffer[] = apdu.getBuffer();
	// check the applet's state
	securite.verifyState(PurseApplet.ADM_SET_MAX_TRANSACTION_WO_PIN);
		// check the current access condition
	securite.verifyAccessCondition(
				       apduBuffer, Security.ADMINISTRATIVE_SESSION,
				       PurseApplet.ADM_SET_MAX_TRANSACTION_WO_PIN);
	if(n < 0)
	    ISOException.throwIt(ISO7816.SW_DATA_INVALID);
	maxTransactionWOPIN = n;
		applet.admSetMaxTransactionWOPINResp(  apdu);
    }
    
    /**
       read the access condition for a given command
       *	@param id: the identifier of the command.
       *	@param apduBuffer : this buffer is used to verify the secure messaging
       */
    // re-added by LC 22/11/00
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      ensures true ;
      //verifyState
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */
    void admGetControlAccesTable(byte id, APDU apdu) 
	throws ISOException {
        // check the applet's state
        securite.verifyState(PurseApplet.ADM_GET_CONTROL_ACCESS_TABLE);
        // this table is managed in the security class
	applet.admGetControlAccessTableResp(securite.getControlAccessTable(id,apdu),apdu);
    }
    
    
    /*	 
	 modification of the access condition of a specific command
	 @param id : the id of the command
	 @param ac : the new access condition
	 @param apduBuffer: used to check the secure messaging if necessary
    */
    //re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 

      //setControlAccessTable 
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth,
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //verifyState, setControlAccessTable
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED || 
				e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ||
				e._reason == ISO7816.SW_WRONG_DATA ;	 

      //setControlAccessTable      
      exsures (TransactionException e) ( e._reason == TransactionException.BUFFER_FULL  &&
                                         JCSystem._transactionDepth == 1) ;
    */
    void admSetControlAccesTable(byte id, byte ac, APDU apdu)
	throws ISOException, TransactionException {
	// check the applet's state
        securite.verifyState(PurseApplet.ADM_SET_CONTROL_ACCESS_TABLE);
        // this table is managed in the security class
        securite.setControlAccessTable(id, ac, apdu);
	applet.admSetControlAccessTableResp(apdu);
    }
	
    /* read an entry of the table containing all the exchange performed by the purse*/
    // re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      //verifyState
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */
    void admGetExchangeRecord(byte index, APDU apdu)
	throws ISOException {
	byte apduBuffer[] = apdu.getBuffer();
	// check the applet's state
	securite.verifyState(PurseApplet.ADM_GET_EXCHANGE_RECORD);
	// check the current access condition
	securite.verifyAccessCondition(
				       apduBuffer, Security.ADMINISTRATIVE_SESSION, 
				       PurseApplet.ADM_GET_EXCHANGE_RECORD);
	ExchangeSession t = exchangeRecord.getExchangeSession(index); 
	if(t == null) {
	    short code  = ISO7816.SW_DATA_INVALID;
	    ISOException.throwIt(code);
	}
	// Modified by LC and HM 10/01/2001
	// byte reste = (byte)((exchangeRecord.getNbEntries() - (index + (byte)1))%10);
	byte reste = (byte)((exchangeRecord.getNbEntries() - (index + (byte)1))%10);
	    applet.admGetExchangeRecordResp(
					    t.getSessionNumber(),t.getDate(),
					    t.getHeure(),t.getId(),
					    t.getAncienneDevise(),
					    t.getNouvelleDevise(),
					    t.getStatus(),
					    t.isValid(), reste, apdu);
    }
    
    /* read an entry in the currency table*/
    // re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState 
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ||
                                (e._reason == ISO7816.SW_DATA_INVALID &&
				  currencyTable.nbData > index && index >= 0 
				  ) ;
    */
    void admGetCurrencyTable(byte index, APDU apdu)
	throws ISOException {
	byte apduBuffer[] = apdu.getBuffer();
	// check the applet's state
	securite.verifyState(PurseApplet.ADM_GET_CURRENCY_TABLE);
	// check the current access condition
	securite.verifyAccessCondition(
				       apduBuffer, Security.ADMINISTRATIVE_SESSION, 
				       PurseApplet.ADM_GET_CURRENCY_TABLE);
	byte d = (byte)0;
	byte r = (byte)0;
	if((currencyTable.getNbData() > index) && (index >= 0)) {
	    d = currencyTable.getData(index);
	    r = (byte)(currencyTable.getNbData() - index);
	    r--;
	} else {
	    short code = ISO7816.SW_DATA_INVALID;
	    ISOException.throwIt(code);            
	}
	applet.admGetCurrencyTableResp(d, r,  apdu);
    }
    
    /*modify a value in the currency table*/
    // re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState, if(...)
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ||
                                (e._reason == ISO7816.SW_DATA_INVALID &&
				  currencyTable.nbData > index && index >= 0 
				  ) ;
    */
    void admSetCurrencyTable(byte index, byte devise, APDU apdu)
	throws ISOException {
	byte apduBuffer[] = apdu.getBuffer();
	// check the applet's state
	securite.verifyState(PurseApplet.ADM_SET_CURRENCY_TABLE);
	// check the current access condition
	securite.verifyAccessCondition(
				       apduBuffer, Security.ADMINISTRATIVE_SESSION, 
				       PurseApplet.ADM_SET_CURRENCY_TABLE);
	if((currencyTable.getNbData() > index) && (index >= 0)) {
	    JCSystem.beginTransaction();
	    currencyTable.setData(index, devise);
	    JCSystem.commitTransaction(); 
	} else {
	    short code = ISO7816.SW_DATA_INVALID;
	    ISOException.throwIt(code);
	}
	applet.admSetCurrencyTableResp( apdu);
    }

    /* add a new entry in the currency table*/
    // re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState, if(...)
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED

      // beginTransaction ...
    */
    void admAddCurrencyTable(byte devise, APDU apdu) 
	throws ISOException {
	byte apduBuffer[] = apdu.getBuffer();
	// check the applet's state
	securite.verifyState(PurseApplet.ADM_ADD_CURRENCY_TABLE);
	// check the current access condition
	securite.verifyAccessCondition(
				       apduBuffer, Security.ADMINISTRATIVE_SESSION, 
				       PurseApplet.ADM_ADD_CURRENCY_TABLE);
	JCSystem.beginTransaction();
	currencyTable.addCurrency(devise);          
	JCSystem.commitTransaction(); 

	applet.admAddCurrencyTableResp( apdu);
    }


    /* suppress an entry in the table of currencies*/
    // re-added by LC 22/11/00
    //code modified by Nestor CATANO 24/10/01
    //addition of throws clause
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      //verifyState
      modifies securite.State ; 
      //verifyAccessCondition
      modifies securite.temp[*], securite.SMKey ;
      modifies TransactionException.systemInstance._reason, JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;	  
      ensures true;
      //verifyState, if(...)
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED || 
                                ( e._reason == ISO7816.SW_DATA_INVALID &&
				  currencyTable.nbData > index && index >= 0 )
    */
    void admDelCurrencyTable(byte index, APDU apdu) 
    throws ISOException{
	byte apduBuffer[] = apdu.getBuffer();
	// check the applet's state
	securite.verifyState(PurseApplet.ADM_DEL_CURRENCY_TABLE);
	// check the current access condition
	securite.verifyAccessCondition(
				       apduBuffer, Security.ADMINISTRATIVE_SESSION,
				       PurseApplet.ADM_DEL_CURRENCY_TABLE);
	if((currencyTable.getNbData() > index) && (index >= 0)) {
	    JCSystem.beginTransaction();
	    currencyTable.delCurrency(index);
	    JCSystem.commitTransaction();
	} else {
	    short code = ISO7816.SW_DATA_INVALID;
	    ISOException.throwIt(code);
	}                
	applet.admDelCurrencyTableResp( apdu);
    }
    
    
    
	
    
    
    


/*****************************************************************************************/
/************************   Private Methods of the purse *********************************/
/*****************************************************************************************/

/* Once the pin is verified, one can only perform a limited number of transactions
   before it is required to enter the pin code once more. The fololowing method check
   if the limited number of transactions is not overflown. If it is the case, an exception
   is thrown and caught by the JCRE that leaves the word status 
   <code>PurseApplet.TOO_MUCH_TRANSACTION_WO_PIN</code> in the apdu buffer.
   @param m: the method that use the pin code authentication*/

    //strange: the global variable "testCounter" is only used in the verifyDurationOfPin. 
    //Besides this fact, verifyDurationOfPin doesn't take any decision taking "testCounter"
    //into account. The variable testCounter is a protected variable but it's not used
    //for any other class belongig to current package.
    byte testCounter = 0;
    //code modifies by Nestor CATANO 24/10/01
    //inclusion of throws clause
    /*@
      modifies transactionWOPIN ;
      requires true ;
      ensures transactionWOPIN == \old(transactionWOPIN) + 1;
      exsures (ISOException e)e._reason == PurseApplet.TOO_MUCH_TRANSACTION_WO_PIN  &&
                              transactionWOPIN >= maxTransactionWOPIN ;
    */
    private void verifyDurationOfPin(byte m) 
    throws ISOException{
	// check in case of a multiple transactions with the same PIN authentication
	AccessControl ac = securite.getAccessControl(m);
	// check if the transactionWOPIN is greater than the maxTransactionWOPIN
	testCounter += (byte)2;
	if(transactionWOPIN >= maxTransactionWOPIN) {
	    // throws the exception
	    short code = PurseApplet.TOO_MUCH_TRANSACTION_WO_PIN;
	    testCounter++;
	    ISOException.throwIt(code);
	}
	// increase the counter of the number of transaction without Pin
	transactionWOPIN++;
    }

    
	/* the certificate is made of 23 bytes:
	 *	1 byte for the operation kind
	 *	2*2 bytes: the exchange rate
	 *  2*2 bytes: the inverse of the exchange rate
	 *  4 bytes for the applet serial number
	 *  2 bytes for the purse transaction counter
	 *  8 bytes for the signature of the previous data*/
	 // WARNING THIS METHOD IS NOT ACTUALLY USED
	 // IT MAY BE DEVOTED TO THE SECURITY CLASS
	/*void verifyBankCertificate(byte[] bankCert) {
		short offset = (short)0;
		offset = Util.arrayCopy(bankCert, (short)0, temp, (short)0, (short)15);
		// check the certificate with the exchangeRateKey
		if(!PacapCertificate.verify(
			temp, (short)0, offset, exchangeRateKey, bankCert, (short)15, ivNull
		)) {
		    // throws an exception if the cetificate is not correct
			ISOException.throwIt(PurseApplet.BANK_CERTIFICATE_ERR);
		}
		// check the correctness of the certificate data
		byte op = (byte)0;
		byte asn = (byte)9;
		byte atc = (byte)13;
		if(bankCert[op] != securite.EXCHANGE_SESSION)
		    // throws an exception if the session is not an EXCHANGE session
			ISOException.throwIt(PurseApplet.BANK_CERTIFICATE_ERR);
		// check the applet serial number provided by the certificate
		byte cmp = Util.arrayCompare(
			bankCert, (short)asn, appletSN, (short)0, (short)4
		);
		if(cmp != 0)
			ISOException.throwIt(PurseApplet.BANK_CERTIFICATE_ERR);
		// check the transaction counter provided by the certificate
		if(Util.getShort(bankCert,atc) != transactionCounter)
			ISOException.throwIt(PurseApplet.BANK_CERTIFICATE_ERR);
	}*/

    /* test if the history file is full. If it is the case, the concerned loyalties
       must be informed if they have subscribed to this service.*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures true ;
      //exsures (Exception) false ;
    */
    private void processLogFullIfNeeded() {
	if(transactionRecord.isFull()) {
	    
	    // Added by L. CASSET and H. MARTIN 22/01/01
            // on cherche dans la table des loyalties ceux qu'il faut
            // pr�venir          
            byte borne = loyaltiesTable.getNbLoyalties();
            for (byte index = (byte) 0; index < borne; index++) {
                AllowedLoyalty al = loyaltiesTable.getAllowedLoyalty(index);
                if ( al.isToBeInformed() ){
		    informIt(al.getAID());   
                }                
            }
            // End of addition
            
	    // look in the table the loyalties that must be informed
	    // IT IS NOT IMPLEMENTED
	}
    }

    /* inform the loyalties registred in the purse that the currency is about to be changed*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures true ;
      //exsures (Exception) false ;
      // MH: loyaltiesTable.getAllowedLoyalty(index) might return null
      // should be tested for, before calling JCSystem.lookupAID
      // ESC/Java warns that there might be a cast error
    */
    private void informLoyaltiesOfExchangingRate() {	    
	// Added by L. CASSET and H. MARTIN
        byte borne = loyaltiesTable.getNbLoyalties();
        for (byte index = (byte) 0; index < borne; index++) {
            AllowedLoyalty al = loyaltiesTable.getAllowedLoyalty(index);
            // on doit cherche si l'applet existe bien
            AID aid = JCSystem.lookupAID(al.getAID(),(short) 0, (byte) 16);
            if ( aid == null )return;
            // le JCRE va demander au loyalty l'interface purse
            // que celui-ci doit implementer
            Shareable so = JCSystem.getAppletShareableInterfaceObject(aid, (byte) 1);
            // si le serveur ne retourne rien il ne faut pas adresser un "null"
            if ( so != null) {
                try{
                    // on transtype vers l'interface qu'on attend
                    // c'est le purse qui a d�fini ette interface
                    LoyaltyPurseInterface purseInterface 
                        = (LoyaltyPurseInterface) so;
                    // on fait notre boulot d'avertissment                
                    purseInterface.exchangeRate();
                }
                catch (ClassCastException cce){
                    // on n'a pas reussi � transtyper
                    // on devrait le virer de notre liste
                    // loyaltiesTable.delLoyalty(aid);                                        
                }
            }     
        }
        // End of addition
        
	// IT IS NOT IMPLEMENTED
    }

    /* send a logfull signal to the loyalty aid.
       @param aid: the aid of the loyalty to inform of the logfull*/
    /*@
      //modifies \nothing ;
      requires bArray != null ;
      requires 16 <= bArray.length ; 
      ensures true ;
      // ESC/Java warns that there might be a cast error
      //exsures (Exception) false ;
    */
    private void informIt(byte[] bArray) {
	
	// Added by L. CASSET and H. MARTIN
	// on doit cherche si l'applet existe bien
	AID aid = JCSystem.lookupAID(bArray,(short) 0, (byte) 16);
	if ( aid == null )return;
	// le JCRE va demander au loyalty l'interface purse que celui-ci doit
	// implementer
	Shareable so = JCSystem.getAppletShareableInterfaceObject(aid,(byte) 1);
	// si le serveur ne retourne rien il ne faut pas adresser un "null"
	if ( so != null) {
	    try{
            // on transtype vers l'interface qu'on attend
		// c'est le purse qui a d�fini ette interface
            LoyaltyPurseInterface purseInterface
                = (LoyaltyPurseInterface) so;
            // on fait notre boulot d'avertissment                
            purseInterface.logFull();
        }
        catch (ClassCastException cce){
            // on n'a pas reussi � transtyper
            // on devrait le virer de notre liste
            // loyaltiesTable.delLoyalty(aid);
            
            // ou du moins lui retirer la notification
            // loyaltiesTable.removeNotification(aid);
        }
	}
	// End of addition
	
	// IT IS NOT IMPLEMENTED
    }

    
    /* end the administrative mode*/
    //code modified by Nestor CATANO
    //inclusion of throws clause
    /*@
      modifies securite.terminalSessionType ;
      modifies securite.administrativeCode.flags, 
               securite.administrativeCode.flags[OwnerPIN.VALIDATED],
               securite.administrativeCode.triesLeft[0] ;
      requires true ;
      ensures securite.terminalSessionType == Security.NOT_INITIALIZED ; 
      ensures securite.administrativeCode.flags != null ; 
      exsures (SystemException e) e._reason == SystemException.NO_TRANSIENT_SPACE;
    */
    private void endAdminMode()
	throws SystemException {
        // the session type is no longer ADMINISTRATIVE nor SYSTEM. It is NOT INITIALIZED
	securite.setTerminalSessionType(Security.NOT_INITIALIZED);
	// 
	securite.resetAdministrativeCode();
    }
    
    /* choice of the external authentication for a debit. It depends of the value of the debit
       amount.
       @param  tsn: the terminal serial number
       @return: True if a mutual authentication is mandatory, False otherwise*/
    /*@
      // modifies nothing;
      requires montant != null ;
      ensures  \result ==  
               (montant.intPart * Decimal.PRECISION + montant.decPart >=
                maxDebitWOExternalAut.intPart * Decimal.PRECISION + 
                maxDebitWOExternalAut.decPart);
      // exsures (Exception) false;
    */
    private boolean choixAuthentification(byte[] tsn, Decimal montant) {
	// we want a mutual authentication if the montant value is greater than the value
	// of maxDebitWOexternalAut
        return montant.isGreaterThan(maxDebitWOExternalAut);	
    }

}
