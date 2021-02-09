
//package wallet;
package test.slicing.JC_Wallet;

//import javacard.framework.*;
//import javacardx.framework.*;
class ISO7816{
	final static int OFFSET_CLA=1;
	final static int OFFSET_INS=2;
	final static int SW_CLA_NOT_SUPPORTED=3;
	final static int OFFSET_LC=4;
	final static int OFFSET_CDATA=5;
}

public class Wallet{ // extends Applet {

  final static byte Wallet_CLA =(byte)0xB0;
  final static byte VERIFY = (byte) 0x20;
  final static byte CREDIT = (byte) 0x30;
  final static byte DEBIT = (byte) 0x40;
  final static byte GET_BALANCE = (byte) 0x50;
  final static int MAX_BALANCE = 0x7FFF;
  final static byte MAX_TRANSACTION_AMOUNT = 127;
  final static byte PIN_TRY_LIMIT =(byte)0x03;
  final static byte MAX_PIN_SIZE =(byte)0x08;
  final static int SW_VERIFICATION_FAILED =0x6300;
  final static int SW_PIN_VERIFICATION_REQUIRED = 0x6301;
  final static int SW_INVALID_TRANSACTION_AMOUNT = 0x6A83;
  final static int SW_EXCEED_MAXIMUM_BALANCE =0x6A84;
  final static int SW_NEGATIVE_BALANCE = 0x6A85;

//  OwnerPIN pin;
  int balance;
/*
  private Wallet (byte[] bArray,int bOffset,byte bLength){
//    pin = new OwnerPIN(PIN_TRY_LIMIT,   MAX_PIN_SIZE);
//    pin.update(bArray, bOffset, bLength);
//    register();
  } // end of the constructor

  public static void install(byte[] bArray, int bOffset, byte bLength){
    new Wallet(bArray, bOffset, bLength);
  } // end of install method
  */
  public boolean select() {
//    if ( pin.getTriesRemaining() == 0 )
//       return false;
    return true;
  }// end of select method
/*
  public void deselect() {
    // reset the pin value
    pin.reset();
  }
  */
  public void process(APDU apdu) {
    byte[] buffer = apdu.getBuffer();
    if ((buffer[ISO7816.OFFSET_CLA] == 0) &&
       (buffer[ISO7816.OFFSET_INS] == (byte)(0xA4)) )
      return;
//    if (buffer[ISO7816.OFFSET_CLA] != Wallet_CLA) ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
    switch (buffer[ISO7816.OFFSET_INS]) {
      case GET_BALANCE:   getBalance(apdu);
                          return;
      case DEBIT:         debit(apdu);
                          return;
      case CREDIT:        credit(apdu);
                          return;
      case VERIFY:        verify(apdu);
                          return;
//      default:       ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
    }
 }   // end of process method

 private void credit(APDU apdu) {
   // access authentication
//   if ( ! pin.isValidated() ) ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
    byte[] buffer = apdu.getBuffer();
    byte numBytes = buffer[ISO7816.OFFSET_LC];
    byte byteRead = (byte)(apdu.setIncomingAndReceive());
//    if ( ( numBytes != 1 ) || (byteRead != 1) )	ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
    byte creditAmount = buffer[ISO7816.OFFSET_CDATA];
/*
    if ( ( creditAmount > MAX_TRANSACTION_AMOUNT)
         || ( creditAmount < 0 ) )
        ISOException.throwIt(SW_INVALID_TRANSACTION_AMOUNT);

    if ( (short)( balance + creditAmount)  > MAX_BALANCE )
       ISOException.throwIt(SW_EXCEED_MAXIMUM_BALANCE);
*/
    balance = (int)(balance + creditAmount);
  } // end of deposit method

  private void debit(APDU apdu) {
//    if ( ! pin.isValidated() ) ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
    byte[] buffer = apdu.getBuffer();
    byte numBytes =(byte)(buffer[ISO7816.OFFSET_LC]);
    byte byteRead =(byte)(apdu.setIncomingAndReceive());

/*    if ( ( numBytes != 1 ) || (byteRead != 1) )
       ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
*/
    // get debit amount
    byte debitAmount = buffer[ISO7816.OFFSET_CDATA];

    // check debit amount
/*    if ( ( debitAmount > MAX_TRANSACTION_AMOUNT)
         ||  ( debitAmount < 0 ) )
       ISOException.throwIt(SW_INVALID_TRANSACTION_AMOUNT);
*/
    // check the new balance
/*    if ( (short)( balance - debitAmount ) < (short)0 )
         ISOException.throwIt(SW_NEGATIVE_BALANCE);
*/
    balance = (int) (balance - debitAmount);

  } // end of debit method

  private void getBalance(APDU apdu) {
    byte[] buffer = apdu.getBuffer();
    int le = apdu.setOutgoing();

//    if ( le < 2 ) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

    apdu.setOutgoingLength((byte)2);

    buffer[0] = (byte)(balance >> 8);
    buffer[1] = (byte)(balance & 0xFF);

//    apdu.sendBytes((short)0, (short)2);
  } // end of getBalance method

  private void verify(APDU apdu) {
    byte[] buffer = apdu.getBuffer();
    // retrieve the PIN data for validation.
	byte byteRead = (byte)(apdu.setIncomingAndReceive());

/*	  if ( pin.check(buffer, ISO7816.OFFSET_CDATA,
	       byteRead) == false ) ISOException.throwIt(SW_VERIFICATION_FAILED);*/
  } // end of validate method
} // end of class Wallet

