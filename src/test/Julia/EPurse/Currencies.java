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


/* the list of all currencies supported by the purse*/
 
public class Currencies extends Object{
    
    /*@
      invariant 0 <= nbData && nbData <= MAX_DATA;
      invariant MAX_DATA == data.length;
    */
    
    ////////////////      ATTRIBUTES       ////////////////
    
    private /*@ spec_public */ byte MAX_DATA = (byte)20;
    
    //------------- constant of the currencies supported by the purse -----------------//
    
    public static final byte REFERENCE				= (byte)0x00;
    public static final byte EURO				= (byte)0x01;
    public static final byte FRANC				= (byte)0x02;
    public static final byte LIVRE_STERLING			= (byte)0x03;
    public static final byte DEUTSCH_MARK			= (byte)0x04;
    public static final byte FRANC_SUISSE			= (byte)0x05;
    public static final byte LIRE_ITALIENNE			= (byte)0x06;
    public static final byte FRANC_BELGE			= (byte)0x07;
    public static final byte DOLLAR_US				= (byte)0x08;

    private /*@ spec_public */ byte nbData = (byte)0;
    //private byte[] data = null;
    private /*@ spec_public */ /*@ non_null */ byte[] data = new byte[MAX_DATA];
 

    ///////////////     CONSTRUCTOR     ////////////////
    /*@ 
      requires true ;
      ensures \fresh(this) ;
      ensures data[0]==REFERENCE ;
      ensures data[1]==EURO ;
      ensures data[2]==FRANC ;
      ensures data[3]==LIVRE_STERLING ;
      ensures data[4]==DEUTSCH_MARK ;
      ensures data[5]==FRANC_SUISSE ;
      ensures data[6]==LIRE_ITALIENNE ;
      ensures data[7]==FRANC_BELGE ;
      ensures data[8]==DOLLAR_US ;
      ensures nbData == 9 ; 
      // exsures (RuntimeException) false ;
    */
    Currencies() {
	// data = new byte[MAX_DATA];*
	addCurrency(REFERENCE);
	addCurrency(EURO);
	addCurrency(FRANC);
	addCurrency(LIVRE_STERLING);
	addCurrency(DEUTSCH_MARK);
	addCurrency(FRANC_SUISSE);
	addCurrency(LIRE_ITALIENNE);
	addCurrency(FRANC_BELGE);
	addCurrency(DOLLAR_US);      
    }
    
    ////////////////       METHODS      ///////////////
    
    /*@ 
      modifies nbData, data[nbData] ;
      ensures nbData == (\old(nbData) < MAX_DATA ? \old(nbData)+1 : \old(nbData)) ;
      ensures data[\old(nbData)] == (\old(nbData) < MAX_DATA ? cur: data[nbData]) ;
      //exsures (RuntimeException) false ;
    */
    void addCurrency(byte cur) {
	if(nbData < MAX_DATA) {
	    data[nbData] = cur;
	    nbData++;
	}                
    }
    
        
    /*@ 
      modifies data[*], nbData ;
      requires nbData < data.length ;
      ensures (index >= 0 && index  < \old(nbData)) ==> 
                (nbData == \old(nbData) - 1) &&
		(\forall int k; 
                    (k >= 0 && k < index) ==> data[k] == \old(data[k])) &&
		(\forall int k; 
                    (k >= index && k < \old(nbData) - 1) ==> 
                                             (data[k] == \old(data[k+1]))) ;
      ensures (index < 0 || index  >= \old(nbData)) ==> 
                (nbData == \old(nbData))  &&
		(\forall int i; 
                    (i >= 0 && i < MAX_DATA) ==> (data[i] == \old(data[i])));
      //exsures (RuntimeException) false ;
    */
    void delCurrency(byte index) {
	byte i = index;
	if (i >= 0 && i < nbData) {
	    byte b = (byte)(nbData - (byte) 1);
	    while(i < b) {
		data[i] = data[(byte)(i+1)];
		i++;
	    }
	    // added by LC and HM 10/01/2001
	    nbData--;
	}
    }

    /* check if a given currency is in the table
     * @param cur : the currency searched
     * @return: true if the currency is supported by the purse, otherwise false 
     */

    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == (\exists int i; 
                             (i >= 0 && i < MAX_DATA && data[i] == cur)) ;
      //exsures (RuntimeException) false ;
    */
    boolean contens(byte cur) {
	boolean resu = false;
	byte i = (byte)0;
	boolean trouve = false;
	while(i < MAX_DATA && ! resu) {
	    if(data[i] == cur) {
		resu = true;
	    } else
		i++;
	}
	return resu;
    }
    
    /*@ 
      //modifies \nothing ;
      requires true ;
      ensures \result == nbData ;
      //exsures (RuntimeException) false ;
    */
    byte getNbData() {
	return nbData;
    }
    
    /*@ 
      //modifies \nothing ;
      requires 0 <= i && i < MAX_DATA;
      ensures \result == data[i] ;
      //exsures (RuntimeException) false ;
    */
    byte getData(byte i) {
	return data[i];
    }

    /*@ 
      modifies this.data[index] ;
      requires 0 <= index && index < (this.data).length ;
      ensures this.data[index] == data ;
    */
    void setData(byte index, byte data) {
	this.data[index] = data;
    }

}
