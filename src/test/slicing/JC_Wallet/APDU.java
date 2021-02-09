//package javacard.framework;
package test.slicing.JC_Wallet;

//import com.sun.javacard.impl.PrivAccess;
//import com.sun.javacard.impl.PackedBoolean;
//import com.sun.javacard.impl.NativeMethods;
//import com.sun.javacard.impl.Constants;

public final class APDU{

  // This APDU class implements the T=0 transport protocol.

//  private static final short BUFFERSIZE = Constants.APDU_BUFFER_LENGTH;
  private static final byte IFSC = 1;//information field size for ICC i.e Maximum Incoming BlockSize in T=1
  private static final short IFSD = 258;//information field size for IFD i.e Maximum Outgoing BlockSize in T=1

  private static APDU theAPDU;
  /**
   * The APDU will use the buffer byte[]
   * to store data for input and output.
   */
    private /* @  spec_public @*/ byte[] buffer;

  private static byte[] ramVars;
  private static final byte LE  = (byte) 0;
  private static final byte LR  = (byte)(LE+1);
  private static final byte LC  = (byte)(LR+1);
  private static final byte PRE_READ_LENGTH = (byte)(LC+1);
  private static final byte RAM_VARS_LENGTH = (byte) (PRE_READ_LENGTH+1);

  // status code constants
  private static final short BUFFER_OVERFLOW = (short) 0xC001;
  private static final short READ_ERROR = (short) 0xC003;
  private static final short WRITE_ERROR = (short) 0xC004;
  private static final short INVALID_GET_RESPONSE = (short) 0xC006;

  // procedure byte type constants
  private static final byte ACK_NONE = (byte) 0;
  private static final byte ACK_INS = (byte)1;
  private static final byte ACK_NOT_INS = (byte)2;

  // Le = terminal expected length
  private short getLe() {
    if ( ramVars[LE]==(byte)0 ) return (short) 256;
    else return (short)(ramVars[LE] & 0xFF);
    }
  private void setLe( byte data ) { ramVars[LE] = data; }

  // Lr = our response length
  private short getLr() {
/*    if (getLrIs256Flag()) return 256;
    else return (short)(ramVars[LR] & 0xFF);*/
    return (short)(ramVars[LR] & 0xFF);
    }
  private void setLr( byte data ) { ramVars[LR] = data; }

  // Lc = terminal incoming length
/*  private byte getLc() { return ramVars[LC]; }*/
  private void setLc( byte data ) { ramVars[LC] = data; }

  // PreReadLength = length already received via setIncommingAndReceive(). Used for undo operation.
  private byte getPreReadLength() { return ramVars[PRE_READ_LENGTH]; }
/*  private void setPreReadLength( byte data ) { ramVars[PRE_READ_LENGTH] = data; }
*/
//  private PackedBoolean thePackedBoolean;
  private byte incomingFlag, outgoingFlag, outgoingLenSetFlag,
               lrIs256Flag, sendInProgressFlag, noChainingFlag, noGetResponseFlag;

  // IncomingFlag = setIncoming() has been invoked.
/*  private boolean getIncomingFlag() { return thePackedBoolean.get( incomingFlag ); }*/
//  private void setIncomingFlag() { thePackedBoolean.set( incomingFlag ); }
/*  private void resetIncomingFlag() { thePackedBoolean.reset( incomingFlag ); }
*/
  // SendInProgressFlag = No procedure byte needs to be sent.
/*  private boolean getSendInProgressFlag() { return thePackedBoolean.get( sendInProgressFlag ); }
  private void setSendInProgressFlag() { thePackedBoolean.set( sendInProgressFlag );}
  private void resetSendInProgressFlag() { thePackedBoolean.reset( sendInProgressFlag ); }
*/
  // OutgoingFlag = setOutgoing() has been invoked.
/*  private boolean getOutgoingFlag() { return thePackedBoolean.get( outgoingFlag ); }*/
//  private void setOutgoingFlag() { thePackedBoolean.set( outgoingFlag ); }
/*  private void resetOutgoingFlag() { thePackedBoolean.reset( outgoingFlag ); }

  // OutgoingLenSetFlag = setOutgoingLen() has been invoked.
  private boolean getOutgoingLenSetFlag() { return thePackedBoolean.get( outgoingLenSetFlag ); }
  private void setOutgoingLenSetFlag() { thePackedBoolean.set( outgoingLenSetFlag ); }
  private void resetOutgoingLenSetFlag() { thePackedBoolean.reset( outgoingLenSetFlag ); }

  // LrIs256Flag = Lr is not 0. It is actually 256. Saves 1 byte of RAM.
  private boolean getLrIs256Flag() { return thePackedBoolean.get( lrIs256Flag ); }*/
//  private void setLrIs256Flag() { thePackedBoolean.set( lrIs256Flag ); }
//  private void resetLrIs256Flag() { thePackedBoolean.reset( lrIs256Flag ); }

  // noChainingFlag = do not use <61,xx> chaining for outbound data transfer.
  // Note that the "get" method is package visible. This ensures that it is
  // entered via an "invokevirtual" instruction and not an "invokestatic"
  // instruction thereby ensuring a context switch
/*  boolean getNoChainingFlag() { return thePackedBoolean.get( noChainingFlag ); }
  private void setNoChainingFlag() { thePackedBoolean.set( noChainingFlag ); }
  private void resetNoChainingFlag() { thePackedBoolean.reset( noChainingFlag ); }

  // noGetResponseFlag = GET RESPONSE command was not received from CAD.
  private boolean getNoGetResponseFlag() { return thePackedBoolean.get( noGetResponseFlag ); }
  private void setNoGetResponseFlag() { thePackedBoolean.set( noGetResponseFlag ); }
  private void resetNoGetResponseFlag() { thePackedBoolean.reset( noGetResponseFlag ); }
*/
  /**
   * Only JCRE should create an <code>APDU</code>.
   * <p>
   * @no params
   */
  APDU(){

    /*
    buffer = JCSystem.makeTransientByteArray(BUFFERSIZE, JCSystem.CLEAR_ON_RESET);
    */
//    buffer = NativeMethods.t0InitAPDUBuffer();
//    ramVars = JCSystem.makeTransientByteArray(RAM_VARS_LENGTH, JCSystem.CLEAR_ON_RESET);

//    thePackedBoolean = PrivAccess.getPackedBoolean();
//    incomingFlag = thePackedBoolean.allocate();
//    sendInProgressFlag = thePackedBoolean.allocate();
//    outgoingFlag = thePackedBoolean.allocate();
//    outgoingLenSetFlag = thePackedBoolean.allocate();
//    lrIs256Flag = thePackedBoolean.allocate();
//    noChainingFlag = thePackedBoolean.allocate();
//    noGetResponseFlag = thePackedBoolean.allocate();
    theAPDU = this;
  }

    public byte[] getBuffer() {
	return buffer;
    }

/*  public static short getInBlockSize() {
    return IFSC;
    }
*/
/*
  public static short getOutBlockSize() {
    return IFSD;
    }
*/
  public static final byte PROTOCOL_T0   = 0;
  public static final byte PROTOCOL_T1   = 1;
/*
  public static byte getProtocol() {
    return (byte) PROTOCOL_T0;
    }
*/
/*
 *  public byte getNAD() {
    return (byte) 0;
    }
*/

  public short setOutgoing(){// throws APDUException {
    // if we've previously called this method, then throw an exception
    //if ( getOutgoingFlag() )  APDUException.throwIt( APDUException.ILLEGAL_USE );
//    setOutgoingFlag();
    return getLe();
    }

/*  public short setOutgoingNoChaining() throws APDUException {
    // if we've previously called this method, then throw an exception
    if ( getOutgoingFlag() )  APDUException.throwIt( APDUException.ILLEGAL_USE );
    setOutgoingFlag();
    setNoChainingFlag();
    return getLe();
    }
*/

  public void setOutgoingLength(short len){// throws APDUException{
//    if ( !getOutgoingFlag() )  APDUException.throwIt(APDUException.ILLEGAL_USE);
    // if we've previously called this method, then throw an exception
//    if ( getOutgoingLenSetFlag() )  APDUException.throwIt( APDUException.ILLEGAL_USE );
//    if ( len>256 || len<0 )  APDUException.throwIt(APDUException.BAD_LENGTH);
//    setOutgoingLenSetFlag();
    setLr((byte)len);
//    if ( len==256 ) setLrIs256Flag();
    }

  private void setIncoming(){// throws APDUException{
    // if JCRE has undone a previous setIncomingAndReceive ignore
    if ( getPreReadLength() != 0 ) return;
    // if we've previously called this or setOutgoing() method, then throw an exception
    //if ( getIncomingFlag() || getOutgoingFlag() ) APDUException.throwIt( APDUException.ILLEGAL_USE );
//    setIncomingFlag(); // indicate that this method has been called
	byte Lc = (byte) getLe(); // what we stored in Le was really Lc
	setLc( Lc );
	setLe((byte)0);    // in T=1, the real Le is now unknown (assume 256)
	}

/*
  public short receiveBytes(short bOff){// throws APDUException {
//    if ( !getIncomingFlag() || getOutgoingFlag() ) APDUException.throwIt( APDUException.ILLEGAL_USE );
    short Lc = (short)(getLc()&0xFF);
    // T=1 check bOff against blocksize and Lc.
//    if ( (bOff<0) || ((Lc>=IFSC) && (((short)(bOff+IFSC))>=BUFFERSIZE)) ) APDUException.throwIt ( APDUException.BUFFER_BOUNDS);

    short pre =  (short)( getPreReadLength() & 0x00FF ) ;
    if ( pre != 0 ){
        setPreReadLength( (byte) 0 );
        return pre;
        }

    if ( Lc!=0 ){
        short len = NativeMethods.t0RcvData( bOff );
        if (len<0) APDUException.throwIt( APDUException.IO_ERROR );
        setLc((byte)(Lc - len));   // update RAM copy of Lc, the count remaining
        return len;
        }
    return (short)0;
    }
*/

  public short setIncomingAndReceive(){// throws APDUException {
    setIncoming();
//    return receiveBytes( (short) 5 );
		return (short)0;
    }
/*
  private short send61xx( short len ) {

    short expLen = len;
    do {
        NativeMethods.t0SetStatus ( (short)(ISO7816.SW_BYTES_REMAINING_00+(len&0xFF)) );
        short newLen = NativeMethods.t0SndGetResponse();
        if ( newLen == INVALID_GET_RESPONSE ) { // Get Response not received
            setNoGetResponseFlag();
            APDUException.throwIt(APDUException.NO_T0_GETRESPONSE);
            }
        else if ( newLen > 0 ) {
            ramVars[LE] = (byte) newLen;
            expLen = getLe();
            }
        else APDUException.throwIt( APDUException.IO_ERROR );
        }
        while ( expLen>len );

    resetSendInProgressFlag();
    return expLen;
    }
*/
/*
  public void sendBytes(short bOff, short len){// throws APDUException {

    short result;
//    if ( (bOff<0) || (len<0) || ((short)((bOff+len))>BUFFERSIZE) ) APDUException.throwIt( APDUException.BUFFER_BOUNDS );
//    if ( !getOutgoingLenSetFlag() || getNoGetResponseFlag() )  APDUException.throwIt( APDUException.ILLEGAL_USE );
    if (len==0) return;
    short Lr = getLr();
//    if (len>Lr) APDUException.throwIt( APDUException.ILLEGAL_USE );

    short Le = getLe();

    if ( getNoChainingFlag() ) {

        // Need to force GET RESPONSE for
        // Case 4 or
        // Case 2 but sending less than CAD expects
        if ( getIncomingFlag() || (Lr<Le) ) {
            Le = send61xx( Lr ); // expect Le==Lr, resets sendInProgressFlag.
            resetIncomingFlag(); // no more incoming->outgoing switch.
            }

        while ( len > Le ) { //sending more than Le
            if ( !getSendInProgressFlag() ) {
                result = NativeMethods.t0SndData( buffer, bOff, Le, ACK_INS );
                setSendInProgressFlag();
                }
            else result = NativeMethods.t0SndData(  buffer, bOff, Le, ACK_NONE );

            if ( result != 0 ) APDUException.throwIt( APDUException.IO_ERROR );

            bOff+=Le;
            len-=Le;
            Lr-=Le;
            Le = send61xx( Lr); // resets sendInProgressFlag.
            }

        if ( !getSendInProgressFlag() ) {
            result = NativeMethods.t0SndData( buffer, bOff, len, ACK_INS );
            setSendInProgressFlag();
            }

        else result = NativeMethods.t0SndData( buffer, bOff, len, ACK_NONE );

        if ( result != 0 ) APDUException.throwIt( APDUException.IO_ERROR );
        Lr-=len;
        Le-=len;
        }
    else { // noChainingFlag = FALSE
        while (len>0) {
            short temp=len;
            // Need to force GET RESPONSE for Case 4  & for partial blocks
            if ( (len!=Lr) || getIncomingFlag() || (Lr!=Le) || getSendInProgressFlag() ){
                temp = send61xx( len ); // resets sendInProgressFlag.
                resetIncomingFlag(); // no more incoming->outgoing switch.
                }
            result = NativeMethods.t0SndData( buffer, bOff, temp, ACK_INS );
            setSendInProgressFlag();

            if ( result != 0 ) APDUException.throwIt( APDUException.IO_ERROR );
            bOff+=temp;
            len-=temp;
            Lr-=temp;
            Le=Lr;
            }
        }

    setLe((byte)Le);   // update RAM copy of Le, the expected count remaining
    setLr((byte)Lr);   // update RAM copy of Lr, the response count remaining
    }
*/

  public void sendBytesLong(byte[] outData, short bOff, short len)// throws APDUException
  {
    short sendLength = (short)buffer.length;
    while ( len>0 ) {
        if ( len<sendLength ) sendLength = len;
//        Util.arrayCopy( outData, bOff, buffer, (short)0, sendLength );
//        sendBytes( (short)0, sendLength );
        len-=sendLength;
        bOff+=sendLength;
    }
  }
/*
   public void setOutgoingAndSend( short bOff, short len) throws APDUException {
    setOutgoing();
    setOutgoingLength(len);
    sendBytes( bOff, len );
    }
*/
/*
  void resetAPDU() {
    // as described earlier, we assume case 1 or 2, so Le=P3 and Lc=0
	setLe( buffer[ISO7816.OFFSET_LC] );
	setLc( (byte)0 );

	// save Lr=0, reset flags
	setLr((byte)0);
	resetIncomingFlag();
	resetOutgoingFlag();
	resetOutgoingLenSetFlag();
    resetSendInProgressFlag();
    resetLrIs256Flag();
    resetNoChainingFlag();
    resetNoGetResponseFlag();
    setPreReadLength( (byte)0 );
    }
*/
/*
  void complete(short status) throws APDUException{

    short result;

    // Zero out APDU buffer
    Util.arrayFillNonAtomic( buffer, (short)0, BUFFERSIZE, (byte)0 );

    if ( ( !getNoGetResponseFlag() ) && ( getSendInProgressFlag() ) ) {
        short Le = (byte)getLe();
        short sendLen = (short)32;
        while ( Le > 0 ) {
            if (Le<32) sendLen=Le;
            result = NativeMethods.t0SndData( buffer, (byte) 0, sendLen, ACK_NONE );
            if ( result != 0 ) APDUException.throwIt( APDUException.IO_ERROR );
            Le-=sendLen;
            }
        }

    buffer[0] = (byte) ( status >> 8);
    buffer[1] = (byte) status;

    if (status==0) result = NativeMethods.t0RcvCommand();
    else {
        NativeMethods.t0SetStatus ( status );
        result = NativeMethods.t0SndStatusRcvCommand();
    }

    if ( result != 0 ) APDUException.throwIt( APDUException.IO_ERROR );

    resetAPDU();
    }
*/
/*
  void undoIncomingAndReceive() {
    setPreReadLength( (byte)(buffer[ISO7816.OFFSET_LC] - getLc()) );
    }
*/
/*
  public static void waitExtension() throws APDUException{

    if ( theAPDU.getNoChainingFlag() ) APDUException.throwIt( APDUException.ILLEGAL_USE );
    //send a procedure byte of 0x60 to request additional waiting time.
    short result = NativeMethods.t0Wait();
    if ( result != 0 ) APDUException.throwIt( APDUException.IO_ERROR );
    }*/
}

