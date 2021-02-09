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
 *  @author Abdellah El Marouani, Ludovic Casset, Jean-Louis Lanet & Hugues Martin
 *  @date 13/11/00
 *  @version 2.0.1
 *------------------------------------------------------------------------------
 */


package test.Julia.EPurse;

//import com.gemplus.pacap.utils.*;

import javacard.framework.JCSystem;
import javacard.framework.ISOException;
import javacard.framework.ISO7816;
import javacardx.crypto.Cipher;
import javacard.security.Signature;
import javacard.framework.APDU;
import javacard.framework.AID;
import javacard.framework.OwnerPIN;
import javacard.framework.Util;
//import visa.openplatform.OPSystem;

////Code added by Nestor CATANO
import javacard.framework.PINException ;
import javacard.framework.TransactionException ;
import javacard.framework.SystemException ;
import javacard.security.CryptoException ;


/*The security of the purse*/
public class Security {
    /*@
      invariant terminalSessionType == NOT_INITIALIZED | 
                terminalSessionType == VERIFY_PIN_SESSION | 
                terminalSessionType == SYSTEM_SESSION| 
                terminalSessionType == ADMINISTRATIVE_SESSION | 
		terminalSessionType == CREDIT_SESSION | 
                terminalSessionType == DEBIT_SESSION |
		terminalSessionType == EXCHANGE_SESSION ;

      invariant State == SELECTABLE | 
                State == PERSONALIZED | 
                State == BLOCKED | 
                State == SELECTABLE_PERSONALIZED | 
                State == PERSONALIZED_BLOCKED | 
		State == SELECTABLE_PERSONALIZED_BLOCKED ;

      invariant accessControlTable != null && 
                accessControlTable.data.length == (initArray.length / 2);

      invariant	
                accessControlTable.data[0].methode == 
                                           PurseApplet.APP_INIT_DEBIT &&
		((AccessCondition)accessControlTable.data[0]).condition == 
                                           AccessCondition.FREE &&

                accessControlTable.data[1].methode == 
                                           PurseApplet.APP_INIT_CREDIT &&
		((AccessCondition)accessControlTable.data[1]).condition == 
                                           AccessCondition.FREE &&

                accessControlTable.data[2].methode == 
                                           PurseApplet.APP_INIT_EXCHANGE &&
		((AccessCondition)accessControlTable.data[2]).condition == 
                                           AccessCondition.FREE &&

                accessControlTable.data[3].methode == 
                                           PurseApplet.APP_CREDIT &&
		((AccessCondition)accessControlTable.data[3]).condition == 
                                           AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[4].methode ==  
                                           PurseApplet.APP_DEBIT &&
		((AccessCondition)accessControlTable.data[4]).condition == 
                                           AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[5].methode == 
                                           PurseApplet.APP_EXCHANGE_CURRENCY &&
		((AccessCondition)accessControlTable.data[5]).condition == 
                                           AccessCondition.SECURE_MESSAGING  &&

                accessControlTable.data[6].methode == 
                                           PurseApplet.APP_GET_BALANCE &&
		((AccessCondition)accessControlTable.data[6]).condition == 
                                           AccessCondition.FREE &&

                accessControlTable.data[7].methode == 
                                    PurseApplet.ADM_INIT_ADMINISTRATIVE_MODE &&
		((AccessCondition)accessControlTable.data[7]).condition == 
                                           AccessCondition.FREE &&

                accessControlTable.data[8].methode == 
                                           PurseApplet.ADM_VERIFY_ADM_PIN &&
		((AccessCondition)accessControlTable.data[8]).condition == 
                                           AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[9].methode == 
                                           PurseApplet.ADM_GET_ASN &&
		((AccessCondition)accessControlTable.data[9]).condition == 
                                           AccessCondition.FREE &&

                accessControlTable.data[10].methode == 
                                            PurseApplet.ADM_SET_ASN &&
		((AccessCondition)accessControlTable.data[10]).condition == 
                                            AccessCondition.SECURE_MESSAGING &&
                accessControlTable.data[11].methode == 
                                            PurseApplet.ADM_GET_BALANCE &&

		((AccessCondition)accessControlTable.data[11]).condition == 
                                            AccessCondition.FREE &&

                accessControlTable.data[12].methode ==  
                                    PurseApplet.ADM_GET_CREDIT_AMOUNT_WO_AUT &&
		((AccessCondition)accessControlTable.data[12]).condition == 
                                            AccessCondition.FREE &&

                accessControlTable.data[13].methode == 
                                    PurseApplet.ADM_SET_CREDIT_AMOUNT_WO_AUT &&
		((AccessCondition)accessControlTable.data[13]).condition ==  
                                   (byte)(AccessCondition.SECURE_MESSAGING | 
                                          AccessCondition.SECRET_CODE) &&

                accessControlTable.data[14].methode == 
                                          PurseApplet.ADM_GET_CURRENCY_TABLE &&
		((AccessCondition)accessControlTable.data[14]).condition == 
                                            AccessCondition.FREE &&

                accessControlTable.data[15].methode == 
                                          PurseApplet.ADM_SET_CURRENCY_TABLE &&
		((AccessCondition)accessControlTable.data[15]).condition == 
                                     (byte)(AccessCondition.SECURE_MESSAGING |
                                            AccessCondition.SECRET_CODE) &&

                accessControlTable.data[16].methode == 
                                          PurseApplet.ADM_DEL_CURRENCY_TABLE &&
		((AccessCondition)accessControlTable.data[16]).condition == 
                                      (byte)(AccessCondition.SECURE_MESSAGING |
                                             AccessCondition.SECRET_CODE) &&

                accessControlTable.data[17].methode == 
                                          PurseApplet.ADM_ADD_CURRENCY_TABLE &&
		((AccessCondition)accessControlTable.data[17]).condition == 
                                      (byte)(AccessCondition.SECURE_MESSAGING |
                                             AccessCondition.SECRET_CODE) &&

                accessControlTable.data[18].methode == 
                                      PurseApplet.ADM_GET_TRANSACTION_RECORD &&
		((AccessCondition)accessControlTable.data[18]).condition == 
                                            AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[19].methode == 
                                 PurseApplet.ADM_GET_MAX_TRANSACTION_COUNTER &&
		((AccessCondition)accessControlTable.data[19]).condition == 
                                            AccessCondition.FREE &&

                accessControlTable.data[20].methode == 
                                 PurseApplet.ADM_SET_MAX_TRANSACTION_COUNTER &&
		((AccessCondition)accessControlTable.data[20]).condition == 
                                 (byte)(AccessCondition.SECURE_MESSAGING | 
                                        AccessCondition.SECRET_CODE) &&

                accessControlTable.data[21].methode == 
                                          PurseApplet.ADM_GET_EXCHANGE_RECORD&&
		((AccessCondition)accessControlTable.data[21]).condition == 
                                            AccessCondition.SECURE_MESSAGING&&

                accessControlTable.data[22].methode == 
                                            PurseApplet.ADM_GET_CURRENCY &&
		((AccessCondition)accessControlTable.data[22]).condition == 
                                            AccessCondition.FREE &&

                accessControlTable.data[23].methode == 
                                          PurseApplet.ADM_GET_EXCHANGE_RATE &&
		((AccessCondition)accessControlTable.data[23]).condition == 
                                            AccessCondition.FREE &&

                accessControlTable.data[24].methode == 
                                         PurseApplet.ADM_GET_LOYALTIES_TABLE &&
		((AccessCondition)accessControlTable.data[24]).condition == 
                                            AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[25].methode == 
                                         PurseApplet.ADM_DEL_LOYALTIES_TABLE &&
		((AccessCondition)accessControlTable.data[25]).condition == 
                                      (byte)(AccessCondition.SECURE_MESSAGING |
                                             AccessCondition.SECRET_CODE) &&

                accessControlTable.data[26].methode == 
                                         PurseApplet.ADM_ADD_LOYALTIES_TABLE &&
		((AccessCondition)accessControlTable.data[26]).condition == 
                                      (byte)(AccessCondition.SECURE_MESSAGING |
                                             AccessCondition.SECRET_CODE) &&

                accessControlTable.data[27].methode == 
                                            PurseApplet.ADM_GET_MAX_BALANCE &&
		((AccessCondition)accessControlTable.data[27]).condition == 
                                            AccessCondition.FREE &&

                accessControlTable.data[28].methode == 
                                            PurseApplet.ADM_SET_MAX_BALANCE &&
		((AccessCondition)accessControlTable.data[28]).condition == 
                                      (byte)(AccessCondition.SECURE_MESSAGING |
                                             AccessCondition.SECRET_CODE) &&

                accessControlTable.data[29].methode == 
                                        PurseApplet.ADM_GET_MAX_DEBIT_WO_PIN &&
		((AccessCondition)accessControlTable.data[29]).condition == 
                                            AccessCondition.FREE &&

                accessControlTable.data[30].methode == 
                                        PurseApplet.ADM_SET_MAX_DEBIT_WO_PIN &&
		((AccessCondition)accessControlTable.data[30]).condition == 
                                      (byte)(AccessCondition.SECURE_MESSAGING |
                                             AccessCondition.SECRET_CODE) &&

                accessControlTable.data[31].methode == 
                                         PurseApplet.ADM_GET_EXPIRATION_DATE &&
		((AccessCondition)accessControlTable.data[31]).condition == 
                                            AccessCondition.FREE &&

                accessControlTable.data[32].methode == 
                                         PurseApplet.ADM_SET_EXPIRATION_DATE &&
		((AccessCondition)accessControlTable.data[32]).condition == 
                                            AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[33].methode == 
                                            PurseApplet.ADM_SET_DEBIT_KEY &&
		((AccessCondition)accessControlTable.data[33]).condition == 
                                            AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[34].methode == 
                                            PurseApplet.ADM_SET_CREDIT_KEY &&
		((AccessCondition)accessControlTable.data[34]).condition ==  
                                            AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[35].methode == 
                                            PurseApplet.ADM_SET_PIN_KEY &&
		((AccessCondition)accessControlTable.data[35]).condition == 
                                            AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[36].methode == 
                                            PurseApplet.ADM_GET_STATE &&
		((AccessCondition)accessControlTable.data[36]).condition == 
                                            AccessCondition.FREE &&

                accessControlTable.data[37].methode == 
                                            PurseApplet.ADM_SET_STATE &&
		((AccessCondition)accessControlTable.data[37]).condition == 
                                      (byte)(AccessCondition.SECURE_MESSAGING |
                                             AccessCondition.SECRET_CODE) &&

                accessControlTable.data[38].methode == 
                                PurseApplet.ADM_SET_CREDIT_AUTHORIZATION_KEY &&
		((AccessCondition)accessControlTable.data[38]).condition == 
                                            AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[39].methode == 
                                    PurseApplet.ADM_SET_CREDIT_SIGNATURE_KEY &&
		((AccessCondition)accessControlTable.data[39]).condition == 
                                            AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[40].methode == 
                                     PurseApplet.ADM_SET_DEBIT_SIGNATURE_KEY &&
		((AccessCondition)accessControlTable.data[40]).condition == 
                                            AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[41].methode == 
                                            PurseApplet.ADM_SET_EXCHANGE_KEY &&
		((AccessCondition)accessControlTable.data[41]).condition == 
                                            AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[42].methode == 
                                       PurseApplet.ADM_SET_EXCHANGE_RATE_KEY &&
		((AccessCondition)accessControlTable.data[42]).condition == 
                                            AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[43].methode == 
                                    PurseApplet.ADM_GET_CONTROL_ACCESS_TABLE &&
		((AccessCondition)accessControlTable.data[43]).condition == 
                                            AccessCondition.FREE &&

                accessControlTable.data[44].methode == 
                                    PurseApplet.ADM_SET_CONTROL_ACCESS_TABLE &&
		((AccessCondition)accessControlTable.data[44]).condition == 
                                    (byte)(AccessCondition.SECURE_MESSAGING | 
                                           AccessCondition.SECRET_CODE) &&

                accessControlTable.data[45].methode == 
                                      PurseApplet.ADM_SET_ADMINISTRATIVE_KEY &&
		((AccessCondition)accessControlTable.data[45]).condition == 
                                            AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[46].methode == 
                                            PurseApplet.ADM_SET_SYSTEM_KEY &&
		((AccessCondition)accessControlTable.data[46]).condition == 
                                            AccessCondition.SECURE_MESSAGING &&

                accessControlTable.data[47].methode == 
                                     PurseApplet.ADM_SET_ADMINISTRATIVE_CODE &&
		((AccessCondition)accessControlTable.data[47]).condition == 
                                    (byte)(AccessCondition.SECURE_MESSAGING | 
                                           AccessCondition.SECRET_CODE) &&

                accessControlTable.data[48].methode == 
                                  PurseApplet.ADM_SET_MAX_TRANSACTION_WO_PIN &&
		((AccessCondition)accessControlTable.data[48]).condition == 
                                  (byte)(AccessCondition.SECURE_MESSAGING | 
                                         AccessCondition.SECRET_CODE) &&

                accessControlTable.data[49].methode == 
                                            PurseApplet.ADM_END_SESSION &&
		((AccessCondition)accessControlTable.data[49]).condition == 
                                            AccessCondition.FREE ;

      invariant DEFAULT_KEY != null ;
      invariant DEFAULT_ADMINISTRATIVE_CODE != null ;
      invariant t1_4 != null ;
      invariant t1_4.length == 4;
      invariant t2_4 != null ;
      invariant t2_4.length == 4;
      invariant t3_8 != null ;
      invariant t3_8.length == 8;
      invariant t4_5 != null ;
      invariant t4_5.length == 5;
      invariant t5_16 != null ;
      invariant t5_16.length == 16;
      invariant temp != null ;
      invariant temp.length == 80;
      invariant initArray != null ;
      invariant initArray.length / 2 >= 0;
      invariant (\forall byte j; (j >= 0 && j < initArray.length / 2) ==>
                 initArray[(byte)(j * 2 + 1)] == AccessCondition.FREE ||
                 initArray[(byte)(j * 2 + 1)] == AccessCondition.LOCKED ||
                 initArray[(byte)(j * 2 + 1)] == AccessCondition.SECRET_CODE ||
                 initArray[(byte)(j * 2 + 1)] == 
                                            AccessCondition.SECURE_MESSAGING ||
                 initArray[(byte)(j * 2 + 1)] == (AccessCondition.SECRET_CODE |
                                            AccessCondition.SECURE_MESSAGING));
      invariant ivNull != null ;
      invariant 0 <= ivNull.length && ivNull.length < 256; // JavaCard!
      invariant aid != null ;
      invariant 5 <= aid.length && aid.length <= 16;
      invariant commandAllowed != null ;

      invariant debitKey != null;
      invariant PINKey != null;
      invariant creditKey != null;
      invariant debitSignatureKey != null;
      invariant creditSignatureKey != null;
      invariant creditAuthorizationKey != null;
      invariant exchangeKey != null;
      invariant exchangeRateKey != null;
      invariant administrativeKey != null;
      invariant systemKey != null;

      invariant decryptionKey != null;
      invariant signatureKey != null;
      invariant SMKey != null;
      invariant defaultKey != null;

      invariant accessCondition != null;
      invariant administrativeCode != null;
	
    */
    
    ////////////////////   ATTRIBUTES    ///////////////////////


	//------------- Default constant values-------------------//

/* The default cryptographic key used during the personalization state*/
    private static final byte[] DEFAULT_KEY = new byte[] {
		(byte)0x11, (byte)0x11, (byte)0x11, (byte)0x11,
		(byte)0x11, (byte)0x11, (byte)0x11, (byte)0x11,
		(byte)0x11, (byte)0x11, (byte)0x11, (byte)0x11,
		(byte)0x11, (byte)0x11, (byte)0x11, (byte)0x11,
	};
	
/*the administrative code for the administrative and the system session*/
	private static final byte[] DEFAULT_ADMINISTRATIVE_CODE = new byte[] {
		(byte)0x02, (byte)0x01
	};	
    
    /* the different state of the applet*/
    public static final byte SELECTABLE = (byte)0x01;
    public static final byte PERSONALIZED = (byte)0x02;
	public static final byte BLOCKED = (byte)0x04;
	public static final byte SELECTABLE_PERSONALIZED = (byte)0x03;
	public static final byte PERSONALIZED_BLOCKED = (byte)0x06;
	public static final byte SELECTABLE_PERSONALIZED_BLOCKED = (byte)0x07;
	
	//---------- The different Terminal Session type with the terminal --------//

	public static final byte NOT_INITIALIZED		= (byte)0xA0;
	public static final byte VERIFY_PIN_SESSION		= (byte)0xA1;
	public static final byte SYSTEM_SESSION			= (byte)0xA2;
	public static final byte ADMINISTRATIVE_SESSION	= (byte)0xA3;
	public static final byte CREDIT_SESSION			= (byte)0xA4;
	public static final byte DEBIT_SESSION			= (byte)0xA5;
	public static final byte EXCHANGE_SESSION		= (byte)0xA6;
	
	/*The cryptographic mother key for all the debit session key*/
	private PacapKey debitKey = new PacapKey(/*JCSystem.NOT_A_TRANSIENT_OBJECT*/(byte)0);
	
/*The cryptographic mother key for all the verify pin session key*/	
// modify in accordance with the deletion of the applicative key 13/11/00
	private PacapKey PINKey = new PacapKey(/*JCSystem.NOT_A_TRANSIENT_OBJECT*/(byte)0);
	
/*The cryptographic mother key for all the credit session key*/
	private PacapKey creditKey = new PacapKey(/*JCSystem.NOT_A_TRANSIENT_OBJECT*/(byte)0);
	
/*The cryptographic key used to sign the debit certificate. This certificate must be sent to 
the bank (the card holder's bank) and therefore must not be changed by the terminal: the 
terminal does not know this key and is not able to modify the certificate*/
	private PacapKey debitSignatureKey = new PacapKey(/*JCSystem.NOT_A_TRANSIENT_OBJECT*/(byte)0);
	
/*The cryptographic key used to sign the credit certificate. This certificate must be sent to 
the bank (the card holder's bank) and therefore must not be changed by the terminal: the 
terminal does not know this key and is not able to modify the certificate*/
	private PacapKey creditSignatureKey = new PacapKey(/*JCSystem.NOT_A_TRANSIENT_OBJECT*/(byte)0);
	
/*When performing a credit, the card may ask an authorisation from the bank. It is the case
if the credit amount is greater than the creditamountWOauthorization value. The terminal
contacts the bank that checks the card holder's account and identity and sends back a signed 
authorisation for the credit. The following key allows the card to check this authorisation.*/
	private PacapKey creditAuthorizationKey;

/*The cryptographic mother key used during an exchange session*/
	private PacapKey exchangeKey = new PacapKey(/*JCSystem.NOT_A_TRANSIENT_OBJECT*/(byte)0);
	
/*The change of currency requires an authentication of the exchange rate sends by a bank.	
This authentication is broought by a certificate asked by the terminal to the purse applet 
provider. The following key allows the purse to check this certificate.*/
/*@ spec_public */ private PacapKey exchangeRateKey = new PacapKey(/*JCSystem.NOT_A_TRANSIENT_OBJECT*/(byte)0);
	
/*The cryptographic mother key used during an administrative session.*/
 /*@ spec_public */ private PacapKey administrativeKey = new PacapKey(/*JCSystem.NOT_A_TRANSIENT_OBJECT*/(byte)0);
    
/*The cryptographic mother key used during a system session.*/	
/*@ spec_public */ private PacapKey systemKey = new PacapKey(/*JCSystem.NOT_A_TRANSIENT_OBJECT*/(byte)0);
	
//------------------ The current session keys ---------------//
/*The current session keys are computed during the init phase of all the commands of the
purse. Their computation depends on a mother key that is chosen depending on which session
is being executed. The goal of a session key is to be used only once and its computation is 
based on different elements that make this key unique.*/

/*The key used to decypher data(ensures confidentiality)*/
/*@ spec_public */ private PacapKey decryptionKey	= new PacapKey(/*JCSystem.CLEAR_ON_DESELECT*/(byte)2);
/*The key used to sign the data (ensures integrity)*/
/*@ spec_public */ private PacapKey signatureKey	= new PacapKey(/*JCSystem.CLEAR_ON_DESELECT*/(byte)2);
/*The key used to do Secure Messaging. Note that the secure messaging is performed other
an apdu and then it is done in the purseapplet class. That is this key is sent to the 
purseapplet class.*/
/*@ spec_public */ private PacapKey SMKey			= new PacapKey(/*JCSystem.CLEAR_ON_DESELECT*/(byte)2);
	
/*The cryptographic mother key used during the non personlized phase of the applet.*/
/*@ spec_public */ private PacapKey defaultKey = new PacapKey(/*JCSystem.NOT_A_TRANSIENT_OBJECT*/(byte)0);	
	
/*The following variable indicates which session is being performed by the purse. Initialy, 
the session is NOT-INITIALIZED. The different values of this variable are: NOT_INITIALIZED,
VERIFY_PIN_SESSION, CREDIT_SESSION, DEBIT_SESSION, EXCHANGE_SESSION, ADMINISTRATIVE_SESSION,
SYSTEM_SESSION.*/
    /*@ spec_public */ private byte terminalSessionType = NOT_INITIALIZED;

/*The current security state of the purse*/
/*@ spec_public */ private AccessCondition accessCondition = new AccessCondition();
	
/*The administrator pin code: the card holder pin code is managed by the< card manager and is
the same for all applications on the card. However, each application can manage is own set of
code as the administrator code for the purse.*/
// WARNING THE NUMBER OF TRIES IS 3 AND THE SIZE OF THE PIN CODE IS 2 DIGITS
/*@spec_public*/ private OwnerPIN administrativeCode = new OwnerPIN((byte)3, (byte)2);	

/*Temporary tables*/ 
    /*@spec_public*/ private byte t1_4[] =
		JCSystem.makeTransientByteArray((short)4, /*JCSystem.CLEAR_ON_DESELECT*/(byte)2);
    /*@spec_public*/ private byte t2_4[] =
		JCSystem.makeTransientByteArray((short)4, /*JCSystem.CLEAR_ON_DESELECT*/(byte)2);
    /*@spec_public*/ private byte t3_8[] =
		JCSystem.makeTransientByteArray((short)8, /*JCSystem.CLEAR_ON_DESELECT*/(byte)2);
    /*@spec_public*/ private byte t4_5[] =
		JCSystem.makeTransientByteArray((short)5, /*JCSystem.CLEAR_ON_DESELECT*/(byte)2);
    /*@spec_public*/ private byte t5_16[] =
		JCSystem.makeTransientByteArray((short)16, /*JCSystem.CLEAR_ON_DESELECT*/(byte)2);
    /*@spec_public*/ private byte temp[] =
		JCSystem.makeTransientByteArray((short)80, /*JCSystem.CLEAR_ON_DESELECT*/(byte)2);

	/* the state of the applet*/
	public byte State;
	
	
	/* the initialisation vector used in cryptography*/
/*@ spec_public */	private byte ivNull[] = {0, 0, 0, 0, 0, 0, 0, 0};
	
	/*This variable indicates whether the card holder is authenticated with its PIN code or not*/
/*@ spec_public */	private boolean authPin = false;
	
	/*The aid of the appelt*/	
/*@ spec_public */	private byte[] aid;
	
	/*The table that contains the different rights required to access a command*/
/*@ spec_public */	private static byte[] initArray = new byte[] {
		PurseApplet.APP_INIT_DEBIT, AccessCondition.FREE,
		PurseApplet.APP_INIT_CREDIT, AccessCondition.FREE,
		PurseApplet.APP_INIT_EXCHANGE, AccessCondition.FREE,
		PurseApplet.APP_CREDIT, AccessCondition.SECURE_MESSAGING,
		PurseApplet.APP_DEBIT, AccessCondition.SECURE_MESSAGING,
		PurseApplet.APP_EXCHANGE_CURRENCY, AccessCondition.SECURE_MESSAGING,
		PurseApplet.APP_INIT_VERIFY_PIN, AccessCondition.FREE,
		PurseApplet.APP_VERIFY_PIN, AccessCondition.SECURE_MESSAGING,
		PurseApplet.APP_GET_BALANCE, AccessCondition.FREE,
		PurseApplet.ADM_INIT_ADMINISTRATIVE_MODE, AccessCondition.FREE,
		PurseApplet.ADM_VERIFY_ADM_PIN, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_GET_ASN, AccessCondition.FREE,
		PurseApplet.ADM_SET_ASN, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_GET_BALANCE, AccessCondition.FREE,
		PurseApplet.ADM_GET_CREDIT_AMOUNT_WO_AUT, AccessCondition.FREE,
		PurseApplet.ADM_SET_CREDIT_AMOUNT_WO_AUT, (byte)(AccessCondition.SECURE_MESSAGING | AccessCondition.SECRET_CODE),
		PurseApplet.ADM_GET_CURRENCY_TABLE, AccessCondition.FREE,
		PurseApplet.ADM_SET_CURRENCY_TABLE, (byte)(AccessCondition.SECURE_MESSAGING | AccessCondition.SECRET_CODE),
		PurseApplet.ADM_DEL_CURRENCY_TABLE, (byte)(AccessCondition.SECURE_MESSAGING | AccessCondition.SECRET_CODE),
		PurseApplet.ADM_ADD_CURRENCY_TABLE, (byte)(AccessCondition.SECURE_MESSAGING | AccessCondition.SECRET_CODE),
		PurseApplet.ADM_GET_TRANSACTION_RECORD, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_GET_MAX_TRANSACTION_COUNTER, AccessCondition.FREE,
		PurseApplet.ADM_SET_MAX_TRANSACTION_COUNTER, (byte)(AccessCondition.SECURE_MESSAGING | AccessCondition.SECRET_CODE),
		PurseApplet.ADM_GET_EXCHANGE_RECORD, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_GET_CURRENCY, AccessCondition.FREE,
		PurseApplet.ADM_GET_EXCHANGE_RATE, AccessCondition.FREE,
		PurseApplet.ADM_GET_LOYALTIES_TABLE, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_DEL_LOYALTIES_TABLE, (byte)(AccessCondition.SECURE_MESSAGING | AccessCondition.SECRET_CODE),
		PurseApplet.ADM_ADD_LOYALTIES_TABLE, (byte)(AccessCondition.SECURE_MESSAGING | AccessCondition.SECRET_CODE),
		PurseApplet.ADM_GET_MAX_BALANCE, AccessCondition.FREE,
		PurseApplet.ADM_SET_MAX_BALANCE, (byte)(AccessCondition.SECURE_MESSAGING | AccessCondition.SECRET_CODE),
		PurseApplet.ADM_GET_MAX_DEBIT_WO_PIN, AccessCondition.FREE,
		PurseApplet.ADM_SET_MAX_DEBIT_WO_PIN, (byte)(AccessCondition.SECURE_MESSAGING | AccessCondition.SECRET_CODE),
		PurseApplet.ADM_GET_MAX_TRANSACTION_WO_PIN, AccessCondition.FREE,
		PurseApplet.ADM_GET_EXPIRATION_DATE, AccessCondition.FREE,
		PurseApplet.ADM_SET_EXPIRATION_DATE, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_SET_DEBIT_KEY, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_SET_CREDIT_KEY, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_SET_PIN_KEY, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_GET_STATE, AccessCondition.FREE,
		PurseApplet.ADM_SET_STATE, (byte)(AccessCondition.SECURE_MESSAGING | AccessCondition.SECRET_CODE),
		PurseApplet.ADM_SET_CREDIT_AUTHORIZATION_KEY, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_SET_CREDIT_SIGNATURE_KEY, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_SET_DEBIT_SIGNATURE_KEY, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_SET_EXCHANGE_KEY, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_SET_EXCHANGE_RATE_KEY, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_GET_CONTROL_ACCESS_TABLE, AccessCondition.FREE,
		PurseApplet.ADM_SET_CONTROL_ACCESS_TABLE,
                (byte)(AccessCondition.SECURE_MESSAGING | AccessCondition.SECRET_CODE),
		PurseApplet.ADM_SET_ADMINISTRATIVE_KEY, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_SET_SYSTEM_KEY, AccessCondition.SECURE_MESSAGING,
		PurseApplet.ADM_SET_ADMINISTRATIVE_CODE,
                (byte)(AccessCondition.SECURE_MESSAGING | AccessCondition.SECRET_CODE),
		PurseApplet.ADM_SET_MAX_TRANSACTION_WO_PIN, (byte)(AccessCondition.SECURE_MESSAGING | AccessCondition.SECRET_CODE),
		PurseApplet.ADM_END_SESSION, AccessCondition.FREE
// command removed by Rodolphe Muller on 12/07/2000
//		, PurseApplet.ADM_REGISTER, AccessCondition.FREE
       /* Others access conditions can be added here*/
	};
    /*@ spec_public */ private AccessControlTable accessControlTable = new AccessControlTable(initArray);

	
    /* initialisation of the table containing the security
       policy. Commands can only be executed in a certain state*/
    /*@ spec_public */
  private static byte[] commandAllowed = new byte[] {
	/*	PurseApplet.APP_INIT_DEBIT,*/ PERSONALIZED,
	/*	PurseApplet.APP_INIT_CREDIT,*/ PERSONALIZED,
	/*	PurseApplet.APP_INIT_EXCHANGE,*/ PERSONALIZED,
	/*	PurseApplet.APP_INIT_VERIFY_PIN,*/ PERSONALIZED,
	/*	PurseApplet.ADM_INIT_ADMINISTRATIVE_MODE,*/ SELECTABLE_PERSONALIZED_BLOCKED,
	/*	PurseApplet.ADM_VERIFY_ADM_PIN, */SELECTABLE_PERSONALIZED_BLOCKED,
	/*	PurseApplet.ADM_GET_ASN, */SELECTABLE_PERSONALIZED_BLOCKED,
    /*	PurseApplet.ADM_GET_BALANCE,*/ PERSONALIZED,
	/*	PurseApplet.ADM_GET_CREDIT_AMOUNT_WO_AUT,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_SET_CREDIT_AMOUNT_WO_AUT,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_GET_CURRENCY_TABLE,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_SET_CURRENCY_TABLE,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_DEL_CURRENCY_TABLE,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_GET_TRANSACTION_RECORD,*/ BLOCKED , /*12/01/2001 PERSONALIZED_BLOCKED ,*/
	/*	PurseApplet.ADM_GET_MAX_TRANSACTION_COUNTER,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_SET_MAX_TRANSACTION_COUNTER,*/ SELECTABLE ,
	/*	PurseApplet.ADM_GET_EXCHANGE_RECORD, */ BLOCKED , /* 16/01/2001 PERSONALIZED_BLOCKED ,*/
	/*	PurseApplet.ADM_GET_CURRENCY,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_GET_EXCHANGE_RATE,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_GET_LOYALTIES_TABLE,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_DEL_LOYALTIES_TABLE,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_ADD_LOYALTIES_TABLE,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_GET_MAX_BALANCE,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_SET_MAX_BALANCE,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_GET_MAX_DEBIT_WO_PIN,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_SET_MAX_DEBIT_WO_PIN,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_GET_MAX_TRANSACTION_WO_PIN,*/ PERSONALIZED ,
	/*	PurseApplet.ADM_GET_EXPIRATION_DATE,*/ PERSONALIZED_BLOCKED ,
	/*	PurseApplet.ADM_SET_EXPIRATION_DATE,*/ SELECTABLE ,
	/*	PurseApplet.ADM_SET_DEBIT_KEY,*/ SELECTABLE,
	/*	PurseApplet.ADM_SET_CREDIT_KEY,*/ SELECTABLE,
	/*	PurseApplet.ADM_SET_PIN_KEY,*/ SELECTABLE,
	/*	PurseApplet.APP_CREDIT,*/ PERSONALIZED,
	/*	PurseApplet.APP_DEBIT,*/ PERSONALIZED,
	/*	PurseApplet.APP_EXCHANGE_CURRENCY,*/ PERSONALIZED,
	/*	PurseApplet.ADM_GET_STATE,*/ SELECTABLE_PERSONALIZED_BLOCKED,
	/*	PurseApplet.APP_GET_BALANCE, */PERSONALIZED,
	/*	PurseApplet.APP_VERIFY_PIN, */PERSONALIZED,
	/*	PurseApplet.ADM_SET_CREDIT_AUTHORIZATION_KEY,*/ SELECTABLE ,
	/*	PurseApplet.ADM_SET_CREDIT_SIGNATURE_KEY,*/SELECTABLE ,
	/*	PurseApplet.ADM_SET_DEBIT_SIGNATURE_KEY,*/ SELECTABLE ,
	/*	PurseApplet.ADM_SET_EXCHANGE_KEY, */SELECTABLE ,
	/*	PurseApplet.ADM_SET_EXCHANGE_RATE_KEY,*/ SELECTABLE ,
	/*	PurseApplet.ADM_GET_CONTROL_ACCESS_TABLE,*/ SELECTABLE_PERSONALIZED_BLOCKED,
	/*	PurseApplet.ADM_SET_CONTROL_ACCESS_TABLE,*/ SELECTABLE ,
	/*	PurseApplet.ADM_SET_ADMINISTRATIVE_KEY,*/ SELECTABLE ,
	/*	PurseApplet.ADM_SET_SYSTEM_KEY,*/ SELECTABLE ,
	/*	PurseApplet.ADM_SET_ADMINISTRATIVE_CODE,*/SELECTABLE ,
	/*  PurseApplet.ADM_SET_MAX_TRANSACTION_WO_PIN,*/ SELECTABLE_PERSONALIZED, 
	/*	PurseApplet.ADM_ADD_CURRENCY_TABLE,*/ PERSONALIZED,
	/*	PurseApplet.ADM_SET_ASN,*/ SELECTABLE,
	/*	PurseApplet.ADM_SET_STATE,*/ SELECTABLE_PERSONALIZED_BLOCKED,
	};
	

    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      modifies PINException.systemInstance._reason;
      modifies SystemException.systemInstance._reason;
      modifies TransactionException.systemInstance._reason;
      requires true ;
      ensures State == SELECTABLE ;
      ensures \fresh(this) ;
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      //exsures (PINException) 2 > ((PIN)administrativeCode)._maxPINSize ;
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL
                                        && JCSystem._transactionDepth == 1 ;
      exsures (SystemException e) e._reason == SystemException.NO_TRANSIENT_SPACE ;
    */
    // Code modified by Nestor CATANO 29/05/2001
    // inclusion of throws clause
    Security()
	throws PINException,
	       NullPointerException,
	       ArrayIndexOutOfBoundsException,
	       TransactionException,
	       SystemException {
	// check if this initialisation is correct!!!!
	State=SELECTABLE;
	// initiate the cryptographic key
	// added for the initialization of the PIN key 13/11/00
	PINKey.setKey(DEFAULT_KEY, (short)0);
	administrativeKey.setKey(DEFAULT_KEY, (short)0);
	systemKey.setKey(DEFAULT_KEY, (short)0);
	debitKey.setKey(DEFAULT_KEY, (short)0);
	creditKey.setKey(DEFAULT_KEY, (short)0);
	exchangeKey.setKey(DEFAULT_KEY, (short)0);  
	defaultKey.setKey(DEFAULT_KEY, (short)0);
	creditSignatureKey.setKey(DEFAULT_KEY, (short)0);
	debitSignatureKey.setKey(DEFAULT_KEY, (short)0);
	exchangeRateKey.setKey(DEFAULT_KEY, (short)0);
	/*The adminsitrative code is initialized to its default value*/
	administrativeCode.update(DEFAULT_ADMINISTRATIVE_CODE, (short)0, (byte)2);
    }
    
   
   
//METHODS COPIED FROM PRIVATE METHODS OF PURSE.JAVA   

   /* check if the access condition of the command passed in parameter is FREE.
   @param idCmd: the command
   @return: a boolean indicating whether the command is FREE or not*/
    /*@
      //modifies \nothing 
      requires true;
      ensures true;
      exsures (ISOException) 
                 (\forall int j; 
                      (j >= 0 && j < accessControlTable.data.length) ==> 
                       accessControlTable.data[j].methode != idCmd);

    */
    // Code modified by Nestor CATANO 29/05/2001
    // inclusion of throws clause
    private boolean isFree(byte idCmd) 
	throws ISOException {
	// obtain the access condition of the command from the access Control table
	AccessControl aCtrl = accessControlTable.getAccessControl(idCmd);
	if(aCtrl == null)
	    // the command is not a valid one
	    //Code modified by Nestor CATANO
	    ISOException.throwIt(/*PurseApplet.ACCESS_CONDITION_ERR*/(short)0x9F04);
	// test if the access condition is FREE (True) or not (False
	return (aCtrl.getCondition() == AccessCondition.FREE);
    }

    /* check if the acces condition are enforced
   @param apdubuffer: the apdu received by the card
   @param sesstionType: the session type in which the command has to be invoked
   @param idCmd: the command invoked*/
    /*@ 
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires apduBuffer != null && 
               ISO7816.OFFSET_CDATA <= apduBuffer.length &&
               ISO7816.OFFSET_P1 <= apduBuffer.length &&
               apduBuffer[ISO7816.OFFSET_P1] >= 8 &&
               apduBuffer[ISO7816.OFFSET_P1] - (byte)8 <= apduBuffer.length;
      requires 1 + (apduBuffer[ISO7816.OFFSET_P1] - 8) <= temp.length ;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ; 
      exsures (ISOException e ) e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ; 
      exsures (SystemException e) 
                  e._reason == SystemException.NO_TRANSIENT_SPACE; 
      exsures (CryptoException) true;
    */
    // Code modified by Nestor CATANO 29/05/2001
    // inclusion of throws clause
    public void verifyAccessCondition(byte[] apduBuffer, byte sessionType, byte idCmd)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException,
               SystemException,
               CryptoException {
	// test if the current session is of the same type of sessionType
	if(terminalSessionType != sessionType)
	    //Code modified by Nestor CATANO
	    ISOException.throwIt(/*ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED*/(short)0x6982 );
	// update the current security state
	refreshAccessCondition(apduBuffer, sessionType);
	try  {
	    // check the current security state match the access condition required
	    if(!accessControlTable.verify(idCmd, accessCondition)) {
		// the current security state does not match the access condition required
		//Code modified by Nestor CATANO
		ISOException.throwIt(/*ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED*/(short)0x6982 );
	    }
	} catch(AccessConditionException ace) {
	    // an error has occured during the verification of the access condition
	    // this error is internaly treatede
	    //Code modified by Nestor CATANO
	    ISOException.throwIt(/*PurseApplet.ACCESS_CONDITION_ERR*/(short)0x9F04);
	}
    }

/* decrypt the cryptogram containing the cryptographic key.
   @param src: the cryptogram
   @param sOff:
   @param sLen:
   @return cle: the value of the key is stored in cle*/
    /*@
      //modifies \nothing ;
      requires cle != null && cle.instance != null;
      ensures true ;
      exsures (CryptoException) true ;      
    */
    private void decryptKey(byte[] src, short sOff, byte sLen, PacapKey cle) 
	throws CryptoException {
    	// decrypt the cryptogram in temp
    	short length = PacapCipher.decrypte(
    					    src, sOff, sLen, decryptionKey, temp, (short)0, ivNull);
    	// store the key in cle	
    	cle.setKey(temp, (short)0);
    }

/* copy in a global table <code>temp</code> the data for the generation of the session keys
   @param terminalSN: the terminal serial number
   @param terminalSC: the terminal session counter
   @param randT: the terminal random
   @param cardSN: the card serial number
   @param administrativeCounter: the number of administrative session
   @param randA: the purse random*/
    /*@ 
      modifies temp[*], TransactionException.systemInstance._reason ; 
      requires cardSN != null && 4 <= cardSN.length ;
      requires terminalSN != null && 4 <= terminalSN.length ;
      requires terminalSC != null && 4 <= terminalSC.length ;
      requires randA != null && 4 <= randA.length ;
      requires randT != null && 4 <= randT.length ;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1; 
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (NullPointerException) false ;
    */
    // Code modified by Nestor CATANO 30/05/2001
    // inclusion of throws clause
    private short createSessionData(
				    byte[] terminalSN, byte[] terminalSC, byte[] randT,
				    byte[] cardSN, short administrativeCounter, byte[] randA
				    )
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException,
	       TransactionException {
	short offset = (short)0;
	// add the card serial number
        offset = Util.arrayCopyNonAtomic(cardSN, (short)0, temp, offset, (short)4);
	// add the terminal serial number

        offset = Util.arrayCopyNonAtomic(terminalSN, (short)0, temp, offset, (short)4);

	// add the number of administrative session
       offset = Util.setShort(temp, offset, administrativeCounter);

	// add the terminal session counter
	offset = Util.arrayCopyNonAtomic(terminalSC, (short)0, temp, offset, (short)4);

	// add the purse random
	offset = Util.arrayCopyNonAtomic(randA, (short)0, temp, offset, (short)4);

	// add the terminal random
	offset = Util.arrayCopyNonAtomic(randT, (short)0, temp, offset, (short)4);
	return offset;
    }


/* generate the session key used to decypher. The global table <code>temp</code> contains
   the data to take into account in order to obtain different session keys.
   @param motherKey: the cryptographic mother key 
   @param terminalSN: the terminal serial number
   @param terminalSC: the terminal session counter
   @param randT: the terminal random
   @param cardSN: the card serial number
   @param administrativeCounter: the number of administrative session
   @param randA: the purse random*/ 
    /*@
      modifies temp[*], TransactionException.systemInstance._reason ;
      requires motherKey != null;
      requires cardSN != null && 4 <= cardSN.length ;
      requires terminalSN != null && 4 <= terminalSN.length ;
      requires terminalSC != null && 4 <= terminalSC.length ;
      requires randA != null && 4 <= randA.length ;
      requires randT != null && 4 <= randT.length ;
      ensures true;
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                    && JCSystem._transactionDepth == 1; 
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (NullPointerException) false ;
      exsures (CryptoException e) true;
    */
    // Code modified by Nestor CATANO 30/05/2001
    // inclusion of throws clause
    private void generateDecryptionKey(
				       PacapKey motherKey,
				       byte[] terminalSN, byte[] terminalSC, byte[] randT,
				       byte[] cardSN, short administrativeCounter, byte[] randA
				       )
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException,
	       TransactionException,
               CryptoException {
	short len;
	len = createSessionData(
				terminalSN, terminalSC, randT, cardSN, administrativeCounter, randA);
	PacapSignature.sign(
			    temp, (short)0, len,
			    motherKey,
			    t5_16, (short)0,
			    ivNull, (short)0, (short)ivNull.length);
	// inverse the mother key
	motherKey.reverseKey();
	len = createSessionData(
				terminalSN, terminalSC, randT, cardSN, administrativeCounter, randA);
	PacapSignature.sign(
			    temp, (short)0, len,
			    motherKey,
			    t5_16, (short)8,
			    t5_16, (short)0, (short)8);
	// restore the mother key
	motherKey.reverseKey();
	// set the session decryption key
	decryptionKey.setKey(t5_16, (short)0);
    }

/* generate the session key used to sign the data of a session. The global table <code>temp
   </code> contains the data to take into account in order to obtain different session keys.
   @param motherKey: the cryptographic mother key 
   @param terminalSN: the terminal serial number
   @param terminalSC: the terminal session counter
   @param randT: the terminal random
   @param cardSN: the card serial number
   @param administrativeCounter: the number of administrative session
   @param randA: the purse random*/ 
    /*@
      modifies temp[*], TransactionException.systemInstance._reason ;
      requires motherKey != null;
      requires cardSN != null && 4 <= cardSN.length ;
      requires terminalSN != null && 4 <= terminalSN.length ;
      requires terminalSC != null && 4 <= terminalSC.length ;
      requires randA != null && 4 <= randA.length ;
      requires randT != null && 4 <= randT.length ;
      ensures true;
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                    && JCSystem._transactionDepth == 1; 
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (NullPointerException) false; 
      exsures (CryptoException e) true;
    */
    // Code modified by Nestor CATANO 30/05/2001
    // inclusion of throws clause
    private void generateSignatureKey(
				      PacapKey motherKey,
				      byte[] terminalSN, byte[] terminalSC, byte[] randT,
				      byte[] cardSN, short administrativeCounter, byte[] randA
				      )
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException,
	       TransactionException, 
               CryptoException {
	short len;
	len = createSessionData(
				terminalSN, terminalSC, randT, cardSN, administrativeCounter, randA);
	PacapSignature.sign(
			    temp, (short)0, len,
			    motherKey,
			    t5_16, (short)0,
			    ivNull, (short)0, (short)ivNull.length
			    );
	// inverse the mother key
	motherKey.reverseKey();
	len = createSessionData(
				terminalSN, terminalSC, randT, cardSN, administrativeCounter, randA);
	PacapSignature.sign(
			    temp, (short)0, len,
			    motherKey,
			    t5_16, (short)8,
			    t5_16, (short)0, (short)8);
	// restore the mother key
	motherKey.reverseKey();
	// set the session signature key
	signatureKey.setKey(t5_16, (short)0);
    }

/* generate the session key used to do Secure Messaging. The global table <code>temp</code> 
   contains the data to take into account in order to obtain different session keys.
   @param motherKey: the cryptographic mother key 
   @param terminalSN: the terminal serial number
   @param terminalSC: the terminal session counter
   @param randT: the terminal random
   @param cardSN: the card serial number
   @param administrativeCounter: the number of administrative session
   @param randA: the purse random*/  
    /*@
      modifies temp[*], TransactionException.systemInstance._reason ;
      requires motherKey != null;
      requires cardSN != null && 4 <= cardSN.length ;
      requires terminalSN != null && 4 <= terminalSN.length ;
      requires terminalSC != null && 4 <= terminalSC.length ;
      requires randA != null && 4 <= randA.length ;
      requires randT != null && 4 <= randT.length ;
      ensures true;
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                    && JCSystem._transactionDepth == 1; 
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (NullPointerException) false;
      exsures (CryptoException) true;
    */
    // Code modified by Nestor CATANO 30/05/2001
    // inclusion of throws clause
    private void generateSMKey(
			       PacapKey motherKey,
			       byte[] terminalSN, byte[] terminalSC, byte[] randT,
			       byte[] cardSN, short administrativeCounter, byte[] randA
			       )
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException,
	       TransactionException,
               CryptoException {
	short len;
	len = createSessionData(
				terminalSN, terminalSC, randT, cardSN, administrativeCounter, randA);
	PacapSignature.sign(
			    temp, (short)0, len,
			    motherKey,
			    t5_16, (short)0,
			    ivNull, (short)0, (short)ivNull.length);
	// inverse the mother key
	motherKey.reverseKey();
	len = createSessionData(
				terminalSN, terminalSC, randT, cardSN, administrativeCounter, randA);
	PacapSignature.sign(
			    temp, (short)0, len,
			    motherKey,
			    t5_16, (short)8,
			    t5_16, (short)0, (short)8);
	// restore the key
	motherKey.reverseKey();
	// set the session secure messaging key 
	SMKey.setKey(t5_16, (short)0);
    }

/* generate the different session keys that have to be generated
   @param terminalSN: the terminal serial number
   @param terminalSC: the terminal session counter
   @param randT: the terminal random
   @param cardSN: the card serial number
   @param administrativeCounter: the number of administrative session
   @param randA: the purse random
   @param motherKey: the cryptographic mother key */   
    /*@
      modifies temp[*], TransactionException.systemInstance._reason ;
      requires motherKey != null;
      requires cardSN != null && 4 <= cardSN.length ;
      requires terminalSN != null && 4 <= terminalSN.length ;
      requires terminalSC != null && 4 <= terminalSC.length ;
      requires randA != null && 4 <= randA.length ;
      requires randT != null && 4 <= randT.length ;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (NullPointerException) false; 
      exsures (CryptoException) true;
    */
    // Code modified by Nestor CATANO 30/05/2001
    // inclusion of throws clause
    private void generateSessionKey(
				    byte[] terminalSN, byte[] terminalSC, byte[] randT,
				    byte[] cardSN, short administrativeCounter, byte[] randA,
				    PacapKey motherKey
				    )
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException,
	       TransactionException,
               CryptoException {
	
	// generate the decryption key
	generateDecryptionKey(
			      motherKey,
			      terminalSN, terminalSC, randT,
			      cardSN, administrativeCounter, randA
			      );
	// generate the signature key
	generateSignatureKey(
			     motherKey,
			     terminalSN, terminalSC, randT,
			     cardSN, administrativeCounter, randA
			     );
	// generate the secure messaging key
	generateSMKey(
		      motherKey,
		      terminalSN, terminalSC, randT,
		      cardSN, administrativeCounter, randA
		      );
    }

/* generate the session key for a PIN session
   @param tsn: the terminal serial number
   @param randT: the terminal random
   @param asn: the applet serial number
   @param randA: the applet random
	IT MAY BE NOT USEFUL IF THE OPEN PLATFORM FUNCTIONALITIES ARE USED
	TO BE CHECKED*/
    /*@
      modifies temp[*] ;
      requires PINKey != null;
      requires tsn != null && 4 <= tsn.length ; 
      requires randT != null && 4 <= randT.length ; 
      requires asn != null && 4 <= asn.length  ; 
      requires randA != null && 4 <= randA.length ; 
      ensures true;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (NullPointerException) false ;
      exsures (CryptoException) true;
    */
    // Code modified by Nestor CATANO 30/05/2001
    // inclusion of throws clause
    private void generatePinSessionKey(
				       byte[] tsn,byte[] randT, byte[] asn, byte[] randA
				       )
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException,
               CryptoException  {
	
	// this key is the only one used in the verify pin 
	generateSMKey2(PINKey, tsn, randT, asn, randA);
    }
    

/* generate the session key used to do secure messaging in a VERIFY PIN session
  the global table <code>temps</code> contains the data used to obtain different session keys
  the variable len indicates the length of this data.
  @param motherKey: the cryptographic mother key 
  @param tsn: the terminal serial number
  @param randT: the terminal random
  @param asn: the applet serial number
  @param randA: the applet random*/
    /*@
      modifies temp[*] ;
      requires motherKey != null;
      requires tsn != null && 4 <= tsn.length ; 
      requires randT != null && 4 <= randT.length ; 
      requires asn != null && 4 <= asn.length  ; 
      requires randA != null && 4 <= randA.length ; 
      ensures true;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (NullPointerException) false ;
      exsures (CryptoException) true;
    */
    // Code modified by Nestor CATANO 30/05/2001
    // inclusion of throws clause
    private void generateSMKey2( 
				 PacapKey motherKey, byte[] tsn, byte[] randT, byte[] asn, byte[] randA
				 ) 
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException,
               CryptoException {
	short len;
	len = createSessionData2(tsn, randT,  asn, randA);
	PacapSignature.sign(
			    temp, (short)0, len,
			    motherKey,
			    t5_16, (short)0,
			    ivNull, (short)0, (short)ivNull.length);
	// inverse the mother key
	motherKey.reverseKey();
	len = createSessionData2(tsn, randT, asn, randA);
	PacapSignature.sign(
			    temp, (short)0, len, motherKey, t5_16, (short)8, t5_16, (short)0, (short)8);
	// restore the mother key
	motherKey.reverseKey();
	// set the session secure messaging key
	SMKey.setKey(t5_16, (short)0);
    }

/* generate the data used in a VERIFY PIN session. They are stored in the global
   table <code>temp</code>
   @param tsn: the terminal serial number
   @param randT: the terminal random
   @param asn: the applet serial number
   @param randA: the applet random*/
    /*@
      modifies temp[*] ;
      requires tsn != null && 4 <= tsn.length ; 
      requires randT != null && 4 <= randT.length ; 
      requires asn != null && 4 <= asn.length  ; 
      requires randA != null && 4 <= randA.length ; 
      ensures true;
      exsures (Exception) false ;
    */
    // Code modified by Nestor CATANO 30/05/2001
    // inclusion of throws clause
    private short createSessionData2(byte[] tsn, byte[] randT, byte[] asn, byte[] randA)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException {
	short offset = (short)0;
	// add the terminal serial number
	offset = Util.arrayCopyNonAtomic(tsn, (short)0, temp, offset, (short)4);
	// add the terminal random
	offset = Util.arrayCopyNonAtomic(randT, (short)0, temp, offset, (short)4);
	// add the applet serial number
	offset = Util.arrayCopyNonAtomic(asn, (short)0, temp, offset, (short)4);
	// add the applet random
	offset = Util.arrayCopyNonAtomic(randA, (short)0, temp, offset, (short)4);
	return offset;
    }

/* generate the session key used to cypher, decypher, sign and perform secure messaging
   during a given session. These keys must be different for each session.
   @param terminalSN: the terminal serial number
   @param terminalSC: the terminal session counter
   @param randT: the terminal random
   @param cardSN: the card serial number
   @param transactionCounter: the number of transaction
   @param randA: the purse random*/
       /*@
	 modifies temp[*], TransactionException.systemInstance._reason ;
	 requires cardSN != null && 4 <= cardSN.length ;
	 requires terminalSN != null && 4 <= terminalSN.length ;
	 requires terminalSC != null && 4 <= terminalSC.length ;
	 requires randA != null && 4 <= randA.length ;
	 requires randT != null && 4 <= randT.length ;
	 ensures true;
         exsures (TransactionException e) 
                    e._reason == TransactionException.BUFFER_FULL 
                    && JCSystem._transactionDepth == 1 ; 
         exsures (ArrayIndexOutOfBoundsException) false ;
         exsures (NullPointerException) false ; 
         exsures (ISOException) terminalSessionType == NOT_INITIALIZED;
         exsures (CryptoException) true;
       */
       // Code modified by Nestor CATANO 30/05/2001
       // inclusion of throws clause
       private void generateSessionKey(
				       byte[] terminalSN, byte[] terminalSC, byte[] randT,
				       byte[] cardSN, short transactionCounter, byte[] randA
				       )
       throws ArrayIndexOutOfBoundsException, 
	      NullPointerException,
	      TransactionException,
              ISOException,
              CryptoException  {
       // define which mother key to use. It depends of the current session
       PacapKey motherKey = null;
       // if the applet is not personalized, the default key is used
       if(OPSystem.getCardContentState() == OPSystem.APPLET_SELECTABLE) {
	   motherKey = defaultKey;
       } else {
	   switch(terminalSessionType) {
	   case ADMINISTRATIVE_SESSION:
	       motherKey = administrativeKey;
	       break;
	   case SYSTEM_SESSION:
	       motherKey = systemKey;
	       break;
	   case DEBIT_SESSION:
	       motherKey = debitKey;
	       break;
	   case CREDIT_SESSION:
	       motherKey = creditKey;
	       break;
	   case EXCHANGE_SESSION:
	       motherKey = exchangeKey;            
	       break;
	   case VERIFY_PIN_SESSION:
	       motherKey = PINKey;
	       break;
	   default:
               //@ assert terminalSessionType == NOT_INITIALIZED;
	       // if the type of the session is unknown
	       //Code modified by Nestor CATANO
	       ISOException.throwIt(/*ISO7816.SW_UNKNOWN*/(short)0x6F00);
	       break;
	   }
       }
       generateSessionKey(
			  terminalSN, terminalSC, randT, cardSN, transactionCounter, randA, motherKey
			  );
   }

   
   
   /* update the current security state @param apduBuffer: the apdu
   buffer. Is is used for the signature of the secure messaging that
   it may contain @param sessionType: depending on the session type,
   the code and the keys are not the same*/
    /*@
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth ;
      requires apduBuffer != null && 
               ISO7816.OFFSET_CDATA <= apduBuffer.length &&
               ISO7816.OFFSET_P1 <= apduBuffer.length &&
               apduBuffer[ISO7816.OFFSET_P1] >= 8 &&
               apduBuffer[ISO7816.OFFSET_P1] - (byte)8 <= apduBuffer.length;
      requires 1 + (apduBuffer[ISO7816.OFFSET_P1] - 8) <= temp.length ;
      requires sessionType == NOT_INITIALIZED | 
               sessionType == VERIFY_PIN_SESSION | 
               sessionType == SYSTEM_SESSION| 
               sessionType == ADMINISTRATIVE_SESSION | 
	       sessionType == CREDIT_SESSION | 
               sessionType == DEBIT_SESSION |
	       sessionType == EXCHANGE_SESSION ;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (SystemException e) 
                  e._reason == SystemException.NO_TRANSIENT_SPACE; 
      exsures (CryptoException) true;
    */
    // Code modified by Nestor CATANO 29/05/2001
    // inclusion of throws clause
    private void refreshAccessCondition(byte[] apduBuffer, byte sessionType) 
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
               SystemException, 
               CryptoException {
	AccessCondition ac = accessCondition;
	// the access condition is FREE
	ac.reset(); 
	// the code (pin code or administrative code) has not been entered
	boolean codeValidated = false;
	switch(sessionType) {
	case SYSTEM_SESSION:
	    // the code is the administrative code
	    codeValidated = administrativeCode.isValidated();
	    break;
	case ADMINISTRATIVE_SESSION:
	    // the code is the administrative code
	    codeValidated = administrativeCode.isValidated();
	    break;
	case VERIFY_PIN_SESSION:
	    // the code is the pin code
	    codeValidated = getAuthPin();
	    break;
	case CREDIT_SESSION:
	    // the code is the pin code
	    codeValidated = getAuthPin();
	    break;
	case DEBIT_SESSION:
	    // the code is the pin code
	    codeValidated = getAuthPin();
	    break;
	case EXCHANGE_SESSION:
	    // the code is the pin code
	    codeValidated = getAuthPin();
	    break;
	default:
				// if no session has been initialized
            //@ assert sessionType == NOT_INITIALIZED;
	    break;
	}
	if(codeValidated)
	    // if a code has been entered, then it is indicated in the access condition
	    ac.addCondition(AccessCondition.SECRET_CODE);
	
	// check if there is Secure Messaging in the apdu
	// if the apdu contains secure messaging and a session has been initialized, the 
	// session keys are set and the secure messaging can be check
	if(
	   ((apduBuffer[ISO7816.OFFSET_CLA] & PurseApplet.SM_COMMAND_APDU) == // 
	    PurseApplet.SM_COMMAND_APDU)
	   && (sessionType != NOT_INITIALIZED)
	   ) {
	    // if there is secure messaging, the last 8 bytes are the secure messaging.
	    // we generate the data on which we want to check the secure messaging
	    short offset = (short)0;
	    byte leftBytes = apduBuffer[ISO7816.OFFSET_P1];
	    temp[offset++] = apduBuffer[ISO7816.OFFSET_INS];
	    offset = Util.arrayCopyNonAtomic(
					     apduBuffer, /*ISO7816.OFFSET_CDATA*/(short)0,
					     temp, offset,
					     (short)(leftBytes - (byte)8));
	    
	    short SMOffset = (short) (/*ISO7816.OFFSET_CDATA*/(short)0 + (short)(leftBytes - (byte)8));
	    // the keys are inversed from the client application to the applet
	    // so, we inverse the secure messaging key
	    SMKey.reverseKey();
	    // check the secure messaging
	    boolean SMOk = PacapSecureMessaging.verify(
						       temp, (short)0, offset, SMKey, apduBuffer, SMOffset, ivNull);
	    // restore the secure messaging key
	    SMKey.reverseKey();
	    if(SMOk)
		// if the secure messagin is ok, we add it to the access condition
		ac.addCondition(AccessCondition.SECURE_MESSAGING);
	}
    }
	
	
/*	* ask to the cardissuer if the pin code of the card holder is valid**/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == authPin ;
      // exsures (RuntimeException) false ;
     */
    private boolean getAuthPin() {
	return authPin;
    }
	
/* the certificate is made of 23 bytes:
	 *	1 byte for the operation kind
	 *	2*2 bytes: the exchange rate
	 *  2*2 bytes: the inverse of the exchange rate
	 *  4 bytes for the applet serial number
	 *  2 bytes for the purse transaction counter
	 *  8 bytes for the signature of the previous data*/
	 // WARNING THIS METHOD IS NOT ACTUALLY USED 
    /*@
     modifies temp[*], TransactionException.systemInstance._reason;
     requires bankCert != null &&
              15 <= bankCert.length ;
     requires appletSN != null && 4 <= appletSN.length;
     ensures true;
     exsures (TransactionException e) 
                 e._reason == TransactionException.BUFFER_FULL 
                 && JCSystem._transactionDepth == 1 ; 
     exsures (NullPointerException) false ;
     exsures (ArrayIndexOutOfBoundsException) false ; 
     exsures (ISOException e) e._reason == PurseApplet.BANK_CERTIFICATE_ERR ;
     exsures (CryptoException) true;
    */
    void verifyBankCertificate(byte[] bankCert, byte[] appletSN, short transactionCounter)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException,
               CryptoException {
	short offset = (short)0;
	offset = Util.arrayCopy(bankCert, (short)0, temp, (short)0, (short)15);
	// check the certificate with the exchangeRateKey
	if(!PacapCertificate.verify(
				    temp, (short)0, offset, exchangeRateKey, bankCert, (short)15, ivNull
				    )) {
	    // throws an exception if the cetificate is not correct
	    //Code modified by Nestor CATANO
	    ISOException.throwIt(/*PurseApplet.BANK_CERTIFICATE_ERR*/(short)0x9F10);
	}
	// check the correctness of the certificate data
	byte op = (byte)0;
	byte asn = (byte)9;
	byte atc = (byte)13;
	if(bankCert[op] != EXCHANGE_SESSION)
	    // throws an exception if the session is not an EXCHANGE session
	    //Code modified by Nestor CATANO
	    ISOException.throwIt(/*PurseApplet.BANK_CERTIFICATE_ERR*/(short)0x9F10);
	// check the applet serial number provided by the certificate
	byte cmp = Util.arrayCompare(
				     bankCert, (short)asn, appletSN, (short)0, (short)4
				     );
	if(cmp != 0)
	    //Code modified by Nestor CATANO
	    ISOException.throwIt(/*PurseApplet.BANK_CERTIFICATE_ERR*/(short)0x9F10);
	// check the transaction counter provided by the certificate
	if(Util.getShort(bankCert,atc) != transactionCounter)
	    //Code modified by Nestor CATANO
	    ISOException.throwIt(/*PurseApplet.BANK_CERTIFICATE_ERR*/(short)0x9F10);
    }

	
	
	
/**********************************************************************************************/	
// NEW METHODS FOR THE SECURITY CLASS
/**********************************************************************************************/

/* verify the state of the applet. Certain commands can only be executed in a particular
   state.
   @param idCmd: the identifier of the command*/
    /*@
      modifies State ;
      requires idCmd/2 - 1 >= 0 && idCmd/2 - 1 < commandAllowed.length;
      ensures true ;
      exsures (ISOException e ) e._reason == ISO7816.SW_COMMAND_NOT_ALLOWED ;
    */
    void verifyState(byte idCmd) throws ISOException{ 
        switch(OPSystem.getCardContentState()){
	case OPSystem.APPLET_SELECTABLE : State = SELECTABLE;
            break;
	case OPSystem.APPLET_PERSONALIZED : State = PERSONALIZED;
            break;
	case OPSystem.APPLET_BLOCKED : State = BLOCKED;
            break;
            // the applet state is not defined. An exception must be thrown
	    //Code modified by Nestor CATANO
	default: {ISOException.throwIt(/*ISO7816.SW_COMMAND_NOT_ALLOWED*/(short)0x6986);} 
	}
	if (State != (commandAllowed[(byte)( (idCmd/2)-1)]& State))
	    //Code modified by Nestor CATANO
            ISOException.throwIt(/*ISO7816.SW_COMMAND_NOT_ALLOWED*/(short)0x6986);
    }
   

	
/* check the current security state with the data stored in the table accessControlTable
    @param idCmd: the command
    @param sessionType: the session type in which the command is invoked
    @param apdu: the apdu of the command
    @return cond: indicate whether the verification is a success or not*/
    /*@
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth ;
      requires true;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (ISOException e) 
                  e._reason == PurseApplet.ACCESS_CONDITION_ERR && 
                  sessionType!=terminalSessionType ;
    */ 
    // Code modified by Nestor CATANO 29/05/2001
    // inclusion of throws clause
    boolean verifyAccessControlTable(byte idCmd,byte sessionType,APDU apdu )
	throws ArrayIndexOutOfBoundsException,
	       NullPointerException, 
	       TransactionException,
	       ISOException//, AccessConditionException
    {
	byte buffer[]=apdu.getBuffer();
	boolean cond = false;
	    // added by LC 22/11/00
	if(sessionType!=terminalSessionType)
	    //Code modified by Nestor CATANO
	    {ISOException.throwIt(/*PurseApplet.ACCESS_CONDITION_ERR*/(short)0x9F04);}
	else{
	    refreshAccessCondition(buffer,sessionType);
	    try{
		cond = accessControlTable.verify(idCmd,accessCondition);}
	    catch(AccessConditionException ase) 
		//Code modified by Nestor CATANO
		{ //@ assert false;
		  ISOException.throwIt(/*PurseApplet.ACCESS_CONDITION_ERR*/(short)0x9F04);}
	}
	return cond;
    }   
	
/* modify the terminal session type*/	
    /*@
      modifies terminalSessionType ;
      requires tst == NOT_INITIALIZED | 
                tst == VERIFY_PIN_SESSION | 
                tst == SYSTEM_SESSION| 
                tst == ADMINISTRATIVE_SESSION | 
		tst == CREDIT_SESSION | 
                tst == DEBIT_SESSION |
		tst == EXCHANGE_SESSION ;
      ensures terminalSessionType == tst ;
      //exsures (RuntimeException) false ;
    */
    public void setTerminalSessionType(byte tst){
	terminalSessionType = tst;
    }

/* read the terminal session type*/	    
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == terminalSessionType ;
      //exsures (RuntimeException) false ;
     */
	public byte getTerminalSessionType(){
	    return terminalSessionType;}
	    
/* generate the session keys and the card random used to build the session keys
    @param tsn: the terminal serial number
    @param ttc: the terminal transaction counter
    @param randT: the terminal random
    @param appletSN: the applet serial number
    @param transactionCounter: the number of transaction of the card
    @return t1_4: the card random*/
    /*@
      modifies temp[*], TransactionException.systemInstance._reason ;
      requires appletSN != null && 4 <= appletSN.length ;
      requires tsn != null && 4 <= tsn.length ;
      requires ttc != null && 4 <= ttc.length ;
      requires randT != null && 4 <= randT.length ;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (NullPointerException) false ; 
      exsures (ISOException) terminalSessionType == NOT_INITIALIZED;
      exsures (CryptoException) true;
    */
    // Code modified by Nestor CATANO 30/05/2001
    // inclusion of throws clause
    byte[] generateSessionKeys(byte[] tsn, byte[] ttc,byte[] randT,byte[]
			       appletSN, short transactionCounter)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException,
	       TransactionException,
               ISOException,
               CryptoException { 
	//generate the purse random.
	PacapRandom.next(t1_4, (short)0, (short)4);
        // generate session keys
	generateSessionKey(tsn, ttc, randT, appletSN, transactionCounter, t1_4);
	return t1_4;
    }

/* generate the session keys and the card random used to build the session keys of a VERIFY PIN SESSION
    @param tsn: the terminal serial number
    @param ttc: the terminal transaction counter
    @param randT: the terminal random
    @param appletSN: the applet serial number
    @param transactionCounter: the number of transaction of the card
    @return t1_4: the card random*/ 
    /*@
      modifies temp[*] ;
      requires tsn != null && 4 <= tsn.length ; 
      requires randT != null && 4 <= randT.length ; 
      requires appletSN != null && 4 <= appletSN.length ; 
      ensures true;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (NullPointerException) false ;
      exsures (CryptoException) true;
    */
    // Code modified by Nestor CATANO 30/05/2001
    // inclusion of throws clause	    
    byte[] generateSessionPINKeys(byte[] tsn, byte[] randT,byte[]
				  appletSN)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException,
               CryptoException {
	//generate the purse random.
	PacapRandom.next(t1_4, (short)0, (short)4);
        // generate session keys
	generatePinSessionKey(tsn, randT, appletSN,  t1_4);
	return t1_4;
    }
    

/* generate the signature that are sent back to the terminal after an initDebit and an initCredit.
    @param tsn: the terminal Serial Number
    @param ttc: the terminal transaction counter
    @param randT: the terminal random
    @param appletSN: the appelt Serial Number
    @param trasanctionCounter: the current transaction counter
    @param expirationDate: the applet's expiration date
    @param authen: indicate the authentication method
    @return : the signature
*/
    /*@ 
      modifies temp[*], t4_5[*], t2_4[*], 
               TransactionException.systemInstance._reason ;
      requires randT != null && 4 <= randT.length;
      requires appletSN != null && 4 <= appletSN.length;
      requires expirationDate != null;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (CryptoException) true;
    */ 
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause
    byte[] initSignature(byte[] tsn, byte[] ttc,byte[] randT,byte[]
			 appletSN, short transactionCounter, Date expirationDate, byte authen)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException,
	       TransactionException,
               CryptoException {
	AID aid = JCSystem.getAID();
	if(aid != null)
	    aid.getBytes(temp, (short)0);
	else
            {
	    Util.arrayCopyNonAtomic(
				    this.aid, (short)0, temp, (short)0, (short)this.aid.length
				    );
            }
	//	Util.arrayCopyNonAtomic(
	//    temp, (short)0, temp, (short)0, (short)5);		
        Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);
        // will be use in the function authenticateTerminalSignature
        Util.arrayCopyNonAtomic(randT, (short)0, t2_4, (short)0, (short)4);

        //preparing the data to sign in order to respond.
        //WARNING: the order is very important. It must be the same than the one
        //used to format the outgoing data in the method appInitDebitResp of the
        //PurseApplet class.
        short offset = (short)0;
        // temp contains the data to be signed
        // normaly not necessary because temp[0~5] = t4_5 
        offset = Util.arrayCopyNonAtomic(
					 t4_5, (short)0, temp, offset, (short)5);
        //add the applet serial number	
        offset = Util.arrayCopyNonAtomic(
					 appletSN, (short)0, temp, offset, (short)4);
        //add the expiration date of the purse	
        temp[offset++] = expirationDate.getJour();
        temp[offset++] = expirationDate.getMois();
        temp[offset++] = expirationDate.getAnnee();
        //add the transaction counter

        offset = Util.setShort(temp, offset, transactionCounter);
        offset = Util.arrayCopyNonAtomic(
					 t1_4, (short)0, temp, offset, (short)4);
        //add the encryption algorithm and the signature algorithm	
        temp[offset++] = Cipher.ALG_DES_CBC_NOPAD;
        temp[offset++] = Signature.ALG_DES_MAC8_NOPAD;
        //add the authentication method (authentication of the terminal or not)
        temp[offset++] = authen;
        // signature generation with the signatureKey, the cryptographic session key
        // related to a debit session and generated wirh generatesessionkey
        PacapSignature.sign(
	        temp, (short)0, offset,
	        signatureKey,
	        t3_8, (short)0,
	        ivNull, (short)0, (short)ivNull.length);
	// return the signature of the data    
	return t3_8;
    }
	 
/* generate the signature that are sent back to the terminal after an initDebit and an initCredit.
    @param terminalSN: the terminal Serial Number
    @param terminalSC: the terminal transaction counter
    @param randT: the terminal random
    @param type: the type of adminsitrative session
    @param appletSN: the appelt Serial Number
    @param administrativeCounter: the current transaction counter
    @return : the signature*/
    /*@ 
      modifies temp[*], TransactionException.systemInstance._reason ;
      requires randT != null && randT.length < 256;
      requires terminalSN != null && terminalSN.length < 256;
      requires terminalSC != null && terminalSC.length < 256;
      requires appletSN != null && appletSN.length < 256;
      requires terminalSN.length + terminalSC.length + randT.length + 1 +
               appletSN.length + 2 + 4 + 2 < temp.length ;
      ensures true;
      exsures (TransactionException e) e._reason == TransactionException.BUFFER_FULL 
                                       && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (CryptoException) true;
    */ 
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause
    byte[] initAdministrativeSignature(byte[] terminalSN,
				       byte[] terminalSC,byte[] randT,byte type,byte[] appletSN,short administrativeCounter)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
               CryptoException
    {
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
			    ivNull, (short)0, (short)ivNull.length);
	return t3_8;
    }
	 
/* authenticate the data that come from the terminal in a debit 
    @param transaction: the current transaction
    @param expirationDate: the applet expiration date
    @param date: the current date
    @param heure: the current time
    @param externalAuth: indicate if a mutual authentication is necessary
    @param appletSN: the applet's serial number
    @param transactionCounter: the current transaction counter
    @return: true if the terminal is authenticated false otherwise*/
    /*@
      modifies temp[*], TransactionException.systemInstance._reason ; 
      requires transaction != null;
      requires expirationDate != null;
      requires date != null;
      requires heure != null;
      requires appletSN != null && 4 <= appletSN.length;
      requires externalAuth != null;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (CryptoException) true;
    */ 
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause
    boolean authenticateTerminalDebitSignature(Transaction transaction, Date expirationDate, 
					       Date date, Heure heure, byte[] externalAuth, byte[] appletSN, short transactionCounter)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
               CryptoException
    {
	
	AID aid = JCSystem.getAID();
	if(aid != null)
	    aid.getBytes(temp, (short)0);
	else
	    Util.arrayCopyNonAtomic(
				    this.aid, (short)0, temp, (short)0, (short)this.aid.length
				    );
			
	Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);
	        
	short offset = (short)0;
	offset = transaction.getMontant(temp, offset);
	temp[offset++] = transaction.getDevise();
	offset = transaction.getPartnerID(temp, offset);
	// computed from randT in the previous method
	offset = Util.arrayCopyNonAtomic(t2_4, (short)0, temp, offset, (short)4);
	offset = transaction.getTerminalTC(temp, offset);
	offset = transaction.getTerminalSN(temp, offset);
	offset = Util.arrayCopyNonAtomic(t4_5, (short)0, temp, offset, (short)5);
	offset = Util.arrayCopyNonAtomic(
					 appletSN, (short)0, temp, offset, (short)4);
	temp[offset++] = expirationDate.getJour();
	temp[offset++] = expirationDate.getMois();
	temp[offset++] = expirationDate.getAnnee();
	offset = Util.setShort(temp, offset, transactionCounter);
	offset = Util.arrayCopyNonAtomic(t1_4, (short)0, temp, offset, (short)4);
	temp[offset++] = Cipher.ALG_DES_CBC_NOPAD;
	temp[offset++] = Signature.ALG_DES_MAC8_NOPAD;
	temp[offset++] = (byte)1;
	offset = date.getDate(temp, offset);
	offset = heure.getHeure(temp, offset);
	//the data are constructed in the same order than in the terminal
	//then a signature over these data is computed and compared to the one sent by 
	//the terminal. If it is the same, then the authentication is done, otherwise
	//there is an error
	return PacapSignature.verify(
				     temp, (short)0, offset,
				     signatureKey,
				     externalAuth, (short)0,
				     ivNull, (short)0, (short)ivNull.length);
    }

/* generate the debit certificate that will be sent to the bank.
    @param transaction: the current transaction
    @param appletSN: the applet's Serial Number
    @param transactionCounter: the current transaction counter
    @return: the signature of the data*/
    /*@
      modifies temp[*], TransactionException.systemInstance._reason ; 
      requires appletSN != null && appletSN.length == 4;
      requires transaction != null;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (CryptoException) true;
    */ 
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause
    public byte[] generateDebitCertificate( Transaction transaction, byte[] appletSN, short transactionCounter)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
               CryptoException {
	/* Util.arrayCopyNonAtomic(aid, (short)0, temp, (short)0, (short)5);	
	   Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);*/
	
	
	AID aid = JCSystem.getAID();
	if(aid != null)
	    aid.getBytes(temp, (short)0);
	else
	    Util.arrayCopyNonAtomic(
				    this.aid, (short)0, temp, (short)0, (short)this.aid.length
				    );
	
	Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);
	
	// preparation of the data to be signed
	short offset = (short)0;
	offset = Util.arrayCopyNonAtomic(t4_5, (short)0, temp, offset, (short)5);
        //@ assert offset == 5;
	offset = transaction.getMontant(temp, offset);
        //@ assert offset == 9;
	temp[offset++] = transaction.getDevise();
        //@ assert offset == 10;
	offset = transaction.getTerminalSN(temp, offset);
        //@ assert offset == 14;
	offset = Util.arrayCopyNonAtomic(
					 appletSN, (short)0, temp, offset, (short)appletSN.length);
        //@ assert offset == 18;
	offset = transaction.getTerminalTC(temp, offset);
        //@ assert offset == 22;
	offset = Util.setShort(temp, offset, transactionCounter);            
	
	// generation of the signature of the data using the debitSignatureKey. This key
	// is only known by the purse and the card holder bank (the purse provider)
	PacapCertificate.sign(
			      temp, (short)0, offset,
			      debitSignatureKey,
			      t3_8, (short)0,
			      ivNull);
	return t3_8;
    }
	
/* authenticate the data that come from the terminal in a credit 
    @param transaction: the current transaction
    @param expirationDate: the applet expiration date
    @param date: the current date
    @param heure: the current time
    @param bankCert: the bank certificate if mandatory
    @param sign: the signature
    @param appletSN: the applet's serial number
    @param transactionCounter: the current transaction counter
    @return: true if the terminal is authenticated false otherwise*/
    /*@
      modifies temp[*], TransactionException.systemInstance._reason ; 
      requires transaction != null && expirationDate != null;
      requires date != null && heure != null;
      requires bankCert != null && 8 <= bankCert.length;
      requires appletSN != null && 4 <= appletSN.length;
      requires sign != null;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (CryptoException) true;
    */ 
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause	
    boolean authenticateTerminalCreditSignature(Transaction transaction, Date expirationDate,
						Date date, Heure heure, byte[] bankCert,byte[] sign, byte[] appletSN, short transactionCounter)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
               CryptoException
    { 
        AID aid = JCSystem.getAID();
	if(aid != null)
	    aid.getBytes(temp, (short)0);
	else
	    Util.arrayCopyNonAtomic(
				    this.aid, (short)0, temp, (short)0, (short)this.aid.length);
	// Only the first 5 bytes are used
	Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);
	
	short offset = (short)0;
        offset = transaction.getMontant(temp, offset);
        temp[offset++] = transaction.getDevise();
        offset = transaction.getPartnerID(temp, offset);
        offset = Util.arrayCopyNonAtomic(t2_4, (short)0, temp, offset, (short)4);
	offset = transaction.getTerminalTC(temp, offset);
	offset = transaction.getTerminalSN(temp, offset);
	offset = Util.arrayCopyNonAtomic(t4_5, (short)0, temp, offset, (short)5);
	offset = Util.arrayCopyNonAtomic(appletSN, (short)0, temp, offset, (short)4);
	temp[offset++] = expirationDate.getJour();
	temp[offset++] = expirationDate.getMois();
	temp[offset++] = expirationDate.getAnnee();
	offset = Util.setShort(temp, offset, transactionCounter);
	offset = Util.arrayCopyNonAtomic(t1_4, (short)0, temp, offset, (short)4);
	temp[offset++] = Cipher.ALG_DES_CBC_NOPAD;
	temp[offset++] = Signature.ALG_DES_MAC8_NOPAD;
	temp[offset++] = transaction.getBankAuthorization()?(byte)1:(byte)0; 
	offset = date.getDate(temp, offset);
	offset = heure.getHeure(temp, offset);
	offset = Util.arrayCopyNonAtomic(bankCert, (short)0, temp, offset, (short)8);
	// check the signature over the data previously generated
        return(PacapSignature.verify(
				     temp, (short)0, offset,
				     signatureKey,
				     sign, (short)0,
				     ivNull, (short)0, (short)ivNull.length)); 
    }			
    
/*authenticate the terminal signature for the exchange session
    @param transaction: the current transaction
    @param expirationDate: the applet expiration date
    @param date: the current date
    @param heure: the current time
    @param bankCert: the bank certificate if mandatory
    @param sign: the signature
    @param appletSN: the applet's serial number
    @param transactionCounter: the current transaction counter
    @return: true if the terminal is authenticated false otherwise*/	
    /*@
      modifies temp[*], TransactionException.systemInstance._reason ; 
      requires exchangeSession != null && expirationDate != null;
      requires date != null && heure != null;
      requires bankCert != null && 23 <= bankCert.length;
      requires appletSN != null && 4 <= appletSN.length;
      requires termSign != null;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (CryptoException) true;
    */ 
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause	
    boolean authenticateTerminalExchangeSignature(ExchangeSession exchangeSession, Date expirationDate,
						  Date date, Heure heure, byte[] bankCert,byte[] termSign, byte[] appletSN, short transactionCounter)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
               CryptoException
    {    
        AID aid = JCSystem.getAID();
	if(aid != null)
	    aid.getBytes(temp, (short)0);
	else
	    Util.arrayCopyNonAtomic(
				    this.aid, (short)0, temp, (short)0, (short)this.aid.length
				    );
		// only the 1rst 5 bytes are used
	Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);
	
	short offset = (short)0;
	temp[offset++] = exchangeSession.getAncienneDevise();
	temp[offset++] = exchangeSession.getNouvelleDevise();
	offset = exchangeSession.getId(temp, offset);
	offset = Util.arrayCopyNonAtomic(t2_4, (short)0, temp, offset, (short)4);
	offset = exchangeSession.getTerminalTC(temp, offset);
	offset = exchangeSession.getTerminalSN(temp, offset);
	offset = Util.arrayCopyNonAtomic(t4_5, (short)0, temp, offset, (short)5);
	offset = Util.arrayCopyNonAtomic(appletSN, (short)0, temp, offset, (short)4);
	temp[offset++] = expirationDate.getJour();
	temp[offset++] = expirationDate.getMois();
	temp[offset++] = expirationDate.getAnnee();
	offset = Util.setShort(temp, offset, transactionCounter);
	offset = Util.arrayCopyNonAtomic(t1_4, (short)0, temp, offset, (short)4);
	temp[offset++] = Cipher.ALG_DES_CBC_NOPAD;
	temp[offset++] = Signature.ALG_DES_MAC8_NOPAD;
	offset = date.getDate(temp, offset);
	offset = heure.getHeure(temp, offset);
	offset = Util.arrayCopyNonAtomic(bankCert, (short)0, temp, offset, (short)23);
	return(PacapSignature.verify(
				     temp, (short)0, offset,
				     signatureKey,
				     termSign, (short)0,
				     ivNull, (short)0, (short)ivNull.length)) ;
    }
    
/* generate the credit certificate that will be sent to the bank.
    @param transaction: the current transaction
    @param appletSN: the applet's serial number
    @param transactionCounter: the current transaction counter
    @param balance: the current value of the balance
    @return the credit certificate*/	
    /*@
      modifies temp[*], TransactionException.systemInstance._reason ; 
      requires appletSN != null && appletSN.length == 4;
      requires transaction != null;
      requires balance != null;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (CryptoException) true;
    */ 
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause	
    byte[] generateCreditCertificate(Transaction transaction, byte[] appletSN, short transactionCounter,
				     Decimal balance)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
               CryptoException
    {
        AID aid = JCSystem.getAID();
	if(aid != null)
	    aid.getBytes(temp, (short)0);
	else
	    Util.arrayCopyNonAtomic(
				    this.aid, (short)0, temp, (short)0, (short)this.aid.length);
	// Only the first 5 bytes are used
	Util.arrayCopyNonAtomic(temp, (short)0, t4_5, (short)0, (short)5);
	
	// Construction of the data of the certificate to be signed
	short offset = (short)0;
	offset = Util.arrayCopyNonAtomic(t4_5, (short)0, temp, offset, (short)5);
	
	// Decimal exception added 14/11/00
	try{
	    Decimal decimal1 = new Decimal();
	    decimal1.setValue(balance);
	    decimal1.add(transaction.getMontant());
	    offset = decimal1.getValue(temp, offset);
	}
	catch(DecimalException e){
            //@ assert false;
	    //Code modified by Nestor CATANO
	    ISOException.throwIt(/*PurseApplet.DECIMAL_OVERFLOW*/(short)0x9F15);			
	}
	temp[offset++] = transaction.getDevise();
	offset = transaction.getTerminalSN(temp, offset);
	offset = Util.arrayCopyNonAtomic(
					 appletSN, (short)0, temp, offset, (short)appletSN.length);
	offset = transaction.getTerminalTC(temp, offset);
	offset = Util.setShort(temp, offset, transactionCounter);
	
	// signature of the certificate
	PacapCertificate.sign(
			      temp, (short)0, offset,
			      creditSignatureKey,
			      t3_8, (short)0,
			      ivNull);
	return t3_8;
    }
	
    /*@
      modifies temp[0] ;
      requires decimal2 != null;
      requires exchangeSession != null;
      ensures true;
      ensures \result == t3_8 ;
      exsures (CryptoException) true;
      exsures (TransactionException e) 
                      e._reason == TransactionException.BUFFER_FULL && 
                      JCSystem._transactionDepth == 1; 
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause	
    */	
    byte[] generateExchangeSignature(ExchangeSession exchangeSession, Decimal	decimal2) 
	throws CryptoException,
               TransactionException
    {   
	short offset = (short)0;
	offset = decimal2.getValue(temp, offset);
	temp[offset++] = exchangeSession.getNouvelleDevise();
	// generate the signature
	PacapSignature.sign(
			    temp, (short)0, offset,
			    signatureKey,
			    t3_8, (short)0,
			    ivNull, (short)0, (short)ivNull.length);
	return t3_8;
    }

// update the credit authorization key
// TO BE MODIFIED for the final version
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ; 
      exsures (ISOException e ) 
                  e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ; 
    */
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause
    void setCreditAuthorizationKey(byte[] bArray, APDU apdu)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException  { 
	byte apduBuffer[] = apdu.getBuffer();
	// check if the current access condition grants the execution of this comand
	verifyAccessCondition(
			      apduBuffer, SYSTEM_SESSION, PurseApplet.ADM_SET_CREDIT_AUTHORIZATION_KEY);
	// decrypt the bArray. This key is encrypted by the purse provider because
	// a cryptographic key is confidential. The result is stored in creditAuthorizationKey
	decryptKey(bArray, (short)0, (byte)16, creditAuthorizationKey);
    }

// update the credit signature key
// TO BE MODIFIED for the final version
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ; 
      exsures (ISOException e ) 
                  e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ; 
    */
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause
    void setCreditSignatureKey(byte[] bArray, APDU apdu)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException {
	byte apduBuffer[] = apdu.getBuffer();
	// check if the current access condition grants the execution of this command
	verifyAccessCondition(
			      apduBuffer, SYSTEM_SESSION, PurseApplet.ADM_SET_CREDIT_SIGNATURE_KEY);
	// decrypt the bArray. This key is encrypted by the purse provider because
	// a cryptographic key is confidential. The result is stored in creditSignatureKey	
	decryptKey(bArray, (short)0, (byte)16, creditSignatureKey);
    }

// update the debit signature key
// TO BE MODIFIED for the final version 
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ; 
      exsures (ISOException e ) 
                  e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ; 
    */
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause   
    void setDebitSignatureKey(byte[] bArray, APDU apdu)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException  {
	byte apduBuffer[] = apdu.getBuffer();
	// check if the current access condition grants the execution of this command
	verifyAccessCondition(
			      apduBuffer, SYSTEM_SESSION, PurseApplet.ADM_SET_DEBIT_SIGNATURE_KEY);
	// decrypt the bArray. This key is encrypted by the purse provider because
	// a cryptographic key is confidential. The result is stored in debitSignatureKey	
	decryptKey(bArray, (short)0, (byte)16, debitSignatureKey);
	
    }

// update the administrative key
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ; 
      exsures (ISOException e ) 
                  e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ; 
    */
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause   
    void setAdministrativeKey(byte[] bArray, APDU apdu)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException {
	byte apduBuffer[] = apdu.getBuffer();
	// check if the current access condition grants the execution of this command
	verifyAccessCondition(
			      apduBuffer, SYSTEM_SESSION, PurseApplet.ADM_SET_ADMINISTRATIVE_KEY
			      );
	// decrypt the bArray. This key is encrypted by the purse provider because
	// a cryptographic key is confidential. The result is stored in administrativeKey
	decryptKey(bArray, (short)0, (byte)16, administrativeKey);
	
    }
        
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ; 
      exsures (ISOException e ) 
                  e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ; 
    */
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause   
    void setSystemKey(byte[] bArray, APDU apdu)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException {
	byte apduBuffer[] = apdu.getBuffer();
	// check if the current access condition grants the execution of this command
	
	verifyAccessCondition(
			      apduBuffer, SYSTEM_SESSION, PurseApplet.ADM_SET_SYSTEM_KEY);
	// decrypt the bArray. This key is encrypted by the purse provider because
	// a cryptographic key is confidential. The result is stored in systemKey	
	decryptKey(bArray, (short)0, (byte)16, systemKey);

    }
 

    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ; 
      exsures (ISOException e ) 
                  e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ; 
    */ 
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause   
    void setPINKey(byte[] bArray, APDU apdu) 
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException {
	byte apduBuffer[] = apdu.getBuffer();
    	// check if the current access condition grants the execution of this command
	verifyAccessCondition(apduBuffer, SYSTEM_SESSION, PurseApplet.ADM_SET_PIN_KEY);
	// decrypt the bArray. This key is encrypted by the purse provider because
	// a cryptographic key is confidential. The result is stored in PINKey
	decryptKey(bArray, (short)0, (byte)16, PINKey);
    }
 
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ; 
      exsures (ISOException e ) 
                  e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ; 
    */ 
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause  
    void setCreditKey(byte[] bArray, APDU apdu)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException  {
	byte apduBuffer[] = apdu.getBuffer();
	// check if the current access condition grants the execution of this command
	verifyAccessCondition(
			      apduBuffer, SYSTEM_SESSION, PurseApplet.ADM_SET_CREDIT_KEY);
	// decrypt the bArray. This key is encrypted by the purse provider because
	// a cryptographic key is confidential. The result is stored in CreditKey
	decryptKey(bArray, (short)0, (byte)16, creditKey);
	
    }
 
 
/* set the new cryptographic key.
    @param bArray: the array containing the cyphered cyptograzphic key
    @param adpu: the apdu sent by the terminal.*/
    
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ; 
      exsures (ISOException e ) 
                  e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ; 
    */ 
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause 
    void setDebitKey(byte[] bArray, APDU apdu)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException {
	byte apduBuffer[] = apdu.getBuffer();
	// check if the current access condition grants the execution of this command
	verifyAccessCondition(
			      apduBuffer, SYSTEM_SESSION, PurseApplet.ADM_SET_DEBIT_KEY);
	// decrypt the bArray. This key is encrypted by the purse provider because
	// a cryptographic key is confidential. The result is stored in DebitKey
	decryptKey(bArray, (short)0, (byte)16, debitKey);
	
    }
 
 /* set the new cryptographic key.
    @param bArray: the array containing the cyphered cyptographic key
    @param adpu: the apdu sent by the terminal.*/
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ; 
      exsures (ISOException e ) 
                  e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ; 
    */ 
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause
    void setExchangeKey(byte[] bArray, APDU apdu)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException {
	byte apduBuffer[] = apdu.getBuffer();
	// check if the current access condition grants the execution of this command
	verifyAccessCondition(
			      apduBuffer, SYSTEM_SESSION, PurseApplet.ADM_SET_EXCHANGE_KEY);
	// decrypt the bArray. This key is encrypted by the purse provider because
	// a cryptographic key is confidential. The result is stored in exchangeKey
	decryptKey(bArray, (short)0, (byte)16, exchangeKey);
    }
    
 /* set the new cryptographic key.
    @param bArray: the array containing the cyphered cyptograzphic key
    @param adpu: the apdu sent by the terminal.*/    
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, ISOException.systemInstance._reason;
      requires true;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ; 
      exsures (ISOException e ) 
                  e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED ; 
    */ 
    // Code modified by Nestor CATANO 31/05/2001
    // inclusion of throws clause
    void setExchangeRateKey(byte[] bArray, APDU apdu)
			    throws ArrayIndexOutOfBoundsException, 
			    NullPointerException, 
			    TransactionException,
			    ISOException {
	byte apduBuffer[] = apdu.getBuffer();
	// check if the current access condition grants the execution of this command
	verifyAccessCondition(
			      apduBuffer, SYSTEM_SESSION, PurseApplet.ADM_SET_EXCHANGE_RATE_KEY);
	// decrypt the bArray. This key is encrypted by the purse provider because
	// a cryptographic key is confidential. The result is stored in DebitKey
	decryptKey(bArray, (short)0, (byte)16, exchangeRateKey);
	
    }
 
/* this method return the session key SMKey that is used to generate
    secure messaging later in PurseApplet*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == SMKey ;
      //exsures (Runtimeexception) false ;
    */
    PacapKey getSMKey()
    {return(SMKey);
    }

    
 /* reset the curretn acces condition*/  
    /*@
      modifies accessCondition.condition ;
      requires true ;
      ensures accessCondition.condition == accessCondition.FREE ;
      //exsures (Runtimeexception) false ;
    */ 
    void resetAccessCondition()
    {
	accessCondition.reset();
    }
   
 /* initialise the variable that indicates whether the pin code has
    benn entered or not*/
    /*@
      modifies authPin ;
      requires true ;
      ensures authPin == false ;
      //exsures (Runtimeexception) false ;
    */ 
    void initAuthPin()
    {    authPin = false;}
    
 /* set the variable that indicates if the pin code has been entered
    or not*/
    /*@
      modifies authPin ;
      requires true ;
      ensures authPin == value ;
      //exsures (RuntimeException) false ;
    */   
    void setAuthPin( boolean value)
    { authPin = value;}
    
 /* return the access control property of a given command*/  
    /*@ 
      //modifies \nothing ;
      requires true ;
      ensures (\forall int j; 
               j >= 0 && j < accessControlTable.data.length ==> 
               accessControlTable.data[j].methode != m) ==> (\result == null) ;
      ensures (\exists int j; 
               j >= 0 && j < accessControlTable.data.length && 
               accessControlTable.data[j].methode == m) ==> 
               \result != null && \result.methode == m;
      //exsures (RuntimeException) false ; 
    */ 
    AccessControl getAccessControl(byte m)
    {
	return accessControlTable.getAccessControl(m);
    }
    
 /* reset the use of the administrative code. This code is only used for administrative session*/   
     /*@ 
       modifies administrativeCode.flags, 
                administrativeCode.flags[OwnerPIN.VALIDATED], 
                administrativeCode.triesLeft[0];
       modifies SystemException.systemInstance._reason; 
       ensures administrativeCode.flags!=null;
       exsures (SystemException e) 
                  e._reason == SystemException.NO_TRANSIENT_SPACE;
     */
    void resetAdministrativeCode()
     throws SystemException{
     administrativeCode.reset();
   }

 /* indicate whether the code is the administrative code or not*/
    /*@
      modifies SystemException.systemInstance._reason ; 
      modifies administrativeCode.triesLeft[0], 
               administrativeCode.temps[OwnerPIN.TRIES],
               administrativeCode.temps, 
               administrativeCode.flags[OwnerPIN.VALIDATED],
	       administrativeCode.flags ;
      ensures true ;
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) 
                  2 > administrativeCode._maxPINSize ;
      exsures (SystemException e) 
                  e._reason == SystemException.NO_TRANSIENT_SPACE; 
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1; 
    */
    // Code modified by Nestor CATANO 30/05/2001
    // inclusion of throws clause
    boolean checkAdministrativeCode(short pin)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException,
	       SystemException,
               TransactionException
    {
        Util.setShort(temp, (short)0, pin);
        return administrativeCode.check(temp, (short)0, (byte)2);}
    
 /* allow to change the value of the administrative code*/   
    /*@
       modifies PINException.systemInstance._reason ;
       modifies SystemException.systemInstance._reason ;
       modifies TransactionException.systemInstance._reason ;
       ensures true ;
       exsures (NullPointerException) false ;
       exsures (ArrayIndexOutOfBoundsException) false ;
       exsures (PINException) 2 > administrativeCode._maxPINSize ;
       exsures (TransactionException e) 
                   e._reason == TransactionException.BUFFER_FULL
                   && JCSystem._transactionDepth == 1 ;
       exsures (SystemException e) 
                   e._reason == SystemException.NO_TRANSIENT_SPACE ;
    */
    // Code modified by Nestor CATANO 30/05/2001
    // inclusion of throws clause
    void updateAdministrativeCode(short newCode)
	throws PINException,
	       NullPointerException,
	       ArrayIndexOutOfBoundsException,
	       TransactionException,
	       SystemException	       
    {
        Util.setShort(t1_4,(short)0, newCode);
	// update the administrative code
	administrativeCode.update(t1_4, (short)0, (byte)2);}

/* indicate the number of remaining tries for the administrative code*/	
    /*@
      // modifies \nothing ;
      requires true ;
      ensures \result >= 0 && \result == administrativeCode.triesLeft[0] ;
      //exsures (RuntimeException) false ;
    */
    byte triesAdministrativeCode()
    {return administrativeCode.getTriesRemaining();}
    
/* generate the secure messaging over the apdu. It uses the current session 
key SMkey to sign the data. this method is called by the purseapplet class*/
    /*@
      //modifies \nothing ;
      requires src != null && dest != null;
      requires dOff >= 0;
      ensures true ;
      exsures (CryptoException e) true;
    // Code modified by Nestor CATANO 30/05/2001
    // inclusion of throws clause
     */
    short generateSecureMessaging( 
				  byte[] src, short off, short len, byte[] dest, short dOff) 
				  throws CryptoException
    {
	short size = PacapSecureMessaging.generate(
						   src, off, len, SMKey, dest, dOff, ivNull
						   );
	return size;
    }
		
/*	 modification of the access condition of a specific command
	 @param id : the id of the command
	 @param ac : the new access condition
	 @param apduBuffer: used to check the secure messaging if necessary*/
//added by LC 22/11/00
	
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth,
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ; 
      exsures (ISOException e ) 
                  e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED || 
                  //verifyAccessCondition
                  e._reason == ISO7816.SW_WRONG_DATA ; // getAccessControl
    */
    // Code modified by Nestor CATANO 29/05/2001
    // inclusion of throws clause
    void setControlAccessTable(byte id, byte ac, APDU apdu)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException { 
	byte apduBuffer[] = apdu.getBuffer();
	verifyAccessCondition(
			      apduBuffer, ADMINISTRATIVE_SESSION, PurseApplet.ADM_SET_CONTROL_ACCESS_TABLE);
	AccessControl aCtl = accessControlTable.getAccessControl(id);
	if(aCtl == null)
	    //Code modified by Nestor CATANO
	    ISOException.throwIt(/*ISO7816.SW_WRONG_DATA*/(short)0x6A80);
	aCtl.setCondition(ac);
    }
	
/* read the access condition for a given command
	@param id: the identifier of the command.
	@param apduBuffer : this buffer is used to verify the secure messaging */
// added by LC 22/11/00
    /*@
      // not checked completely, because ESC/Java times out (> 300 s)
      modifies temp[*], SMKey ;
      modifies TransactionException.systemInstance._reason, 
               JCSystem._transactionDepth, 
               ISOException.systemInstance._reason ;
      requires true;
      ensures true;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1 ; 
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ; 
      exsures (ISOException e ) 
                  e._reason == ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED || 
                 //verifyAccessCondition
                 e._reason == ISO7816.SW_WRONG_DATA ; 
     */
    // Code modified by Nestor CATANO 29/05/2001
    // inclusion of throws clause 
    AccessControl getControlAccessTable(byte id, APDU apdu)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException	 {
	byte apduBuffer[] = apdu.getBuffer();
        
        // check the current access condition
	verifyAccessCondition(
			      apduBuffer, ADMINISTRATIVE_SESSION, PurseApplet.ADM_GET_CONTROL_ACCESS_TABLE
			      );
	AccessControl aCtl = accessControlTable.getAccessControl(id);
	if(aCtl == null)
	    //Code modified by Nestor CATANO
	    ISOException.throwIt(/*ISO7816.SW_WRONG_DATA*/(short)0x6A80);
	return aCtl;	
	
    }
	
}

