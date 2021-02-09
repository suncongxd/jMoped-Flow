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

// Code modified by Nestor CATANO 21/05/2001
//import instruction is needed by ESC/Java to refer TransactionException
import javacard.framework.TransactionException;
// import instruction is needed by ESC/Java to refer _transactionDepth
import javacard.framework.JCSystem;

/* history of the exchange sessions performed by the purse*/
class ExchangeRecord {
    /*@
      invariant data != null ;
      invariant MAX_ENTRIES == data.length ;
      invariant newIndex >= 0 && newIndex < data.length ;
      invariant \nonnullelements(data) ;
      invariant firstIndex >= 0 && firstIndex < data.length;
      invariant (nb_entries == 0 ?
                 (firstIndex == newIndex) :
                 (newIndex >= firstIndex ? 
                   nb_entries == newIndex - firstIndex :
                   nb_entries == (MAX_ENTRIES - firstIndex) + newIndex));
      invariant nb_entries >= 0 && nb_entries <= MAX_ENTRIES;
    */
	////////////////      ATTRIBUTES       ////////////////

    /* the size of the transaction table*/
    // temporarily modified by Rodolphe Muller on 19/06/2000
    //	private static final byte MAX_ENTRIES = (byte)100;
    /*@ spec_public */ private static final byte MAX_ENTRIES = (byte)10;
    /* number of entries actually stored*/
    /*@ spec_public */ private byte nb_entries = (byte)0;
    /* index of the next transaction to store*/
    /*@ spec_public */ private short newIndex = (byte)0;
    /* index of the older transaction*/
    /*@ spec_public */	private short firstIndex = (byte)0;
    /*  the transaction table*/
    // temporarily removed by Rodolphe Muller on 19/06/2000
    //	private ExchangeSession[] data = null;
    /*@ spec_public */ private ExchangeSession[] data = new ExchangeSession[MAX_ENTRIES];


	///////////////     CONSTRUCTOR     ////////////////
    /*@
      requires true ;
      ensures \fresh(this) ;
      ensures (\forall int k; k >= 0 && k < MAX_ENTRIES ==> \fresh(data[k]));
      ensures nb_entries == 0;
      ensures newIndex == 0;
      ensures firstIndex == 0;
      //exsures (RuntimeException) false ;
     */
	ExchangeRecord () {
//		data = new ExchangeSession[MAX_ENTRIES];*
		for(short i = 0;i < MAX_ENTRIES;i++) {
			data[i] = new ExchangeSession();
		}
	}


	////////////////       METHODS      ///////////////
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == nb_entries ;
      //exsures (RuntimeException)false ;
     */
	byte getNbEntries() {
		return nb_entries;
	}

/*	@param index transaction index in the table
 *	@return the transaction*/
    /*@
      //modifies \nothing ;
      requires index >= 0 ;
      ensures \result == data[index%10] ;
      //exsures (RuntimeException)false ;
    */
	ExchangeSession getExchangeSession(short index) {
		return data[index%10];
	}

/* store a copy of the given transacation the data table
 *	@param t the transaction to store */
    // ESC/Java cannot establish that this method preserves the invariant
    /*@
      modifies data[*], newIndex, nb_entries, firstIndex ;
      requires t != null ;
      requires data[newIndex] != null ;
      requires data[newIndex].terminalTC != t.id;
      requires data[newIndex].terminalSN != t.id;
      requires data[newIndex].terminalSN != t.terminalTC;
      ensures data[\old(newIndex)].sessionNumber == t.sessionNumber ;
      ensures data[\old(newIndex)].ancienneDevise == t.ancienneDevise ;
      ensures data[\old(newIndex)].nouvelleDevise == t.nouvelleDevise ;
      ensures data[\old(newIndex)].isValid == true ;
      ensures data[\old(newIndex)].status == t.status ;
      ensures (\forall int i; 0 <= i & i < t.ID_LENGTH ==> 
                                     t.id[i] == data[\old(newIndex)].id[i]);
      ensures (\forall int i; 0 <= i & i < t.TTC_LENGTH ==> 
                        t.terminalTC[i] == data[\old(newIndex)].terminalTC[i]);
      ensures (\forall int i; 0 <= i & i < t.TSN_LENGTH ==> 
                        t.terminalSN[i] == data[\old(newIndex)].terminalSN[i]);
      ensures nb_entries == (\old(nb_entries) == MAX_ENTRIES ?
                             \old(nb_entries) :
                            (byte)(\old(nb_entries) + 1));
      ensures firstIndex == (\old(nb_entries) < MAX_ENTRIES ?
                             \old(firstIndex) :
                             (\old(firstIndex) + 1) % MAX_ENTRIES);
      ensures newIndex == (\old(newIndex) + 1) % MAX_ENTRIES;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    void addExchangeSession(ExchangeSession t)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException {
	data[newIndex].clone(t);
	data[newIndex].valid();
	newIndex = nextIndex(newIndex);
	// Modified by LC and HM 10/01/2001
        // MH, 29/10/2001, uncommented this code again, and commented the
        // line nb_entries++, to establish invariant.
	if(nb_entries < MAX_ENTRIES){
	    nb_entries++;
	} else {
	    firstIndex = nextIndex(firstIndex);
        }
// 	nb_entries++;
    }

/*	check whether the data table is full or not
 *	@return : the result of the test*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == (nb_entries == MAX_ENTRIES) ;
      //exsures (RuntimeException)false ;
    */
	boolean isFull() {
		return nb_entries == MAX_ENTRIES;
	}

/* erase the transaction which index is given
 *	@param index the transaction index*/
    // MH, 31/10/01: notice that it is required that index is 'between'
    // firstIndex and newIndex. Deleting an element that is not in the
    // range messes up the structure, deletes the first element in the
    // structure.
    /*@
      modifies data[*], nb_entries, newIndex;
      requires data[index] != null;
      requires nb_entries > 0;
      requires firstIndex < newIndex ? 
               (firstIndex <= index & index < newIndex) :
               ((firstIndex <= index & index < MAX_ENTRIES) | 
                (0 <= index & index < newIndex));
      ensures (\forall int i; 0 <= i & i < MAX_ENTRIES ==>
                  index <= newIndex ? 
                    ((index <= i & i < newIndex) ?
                        data[i] == \old(data[i + 1]) :
                        data[i] == \old(data[i]))
                  : (((index <= i & i < MAX_ENTRIES) |
                      (0 <= i & i < newIndex)) ?
                        data[i] == \old(data[(i + 1) % MAX_ENTRIES]) :
                        data[i] == \old(data[i])));
      ensures nb_entries == \old(nb_entries) - 1;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
    */
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clauses
    void deleteExchangeSession(short index)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException  {
	if(nb_entries > 0) {
	    data[index].unvalid();
	    // used to obtain a coherent table
	    defrag(index);
	}
    }


	//-------------------------------------------------------------------------
	//
	//                            private methods
	//
	//-------------------------------------------------------------------------


/*	compress the data table
 *	@param index :the index of a newly deleted transaction */
    // ESC/Java cannot establish that this method preserves the invariant
    /*@  
      modifies data[*], nb_entries, newIndex;
      requires nb_entries > 0;
      requires firstIndex < newIndex ? 
               (firstIndex <= index & index < newIndex) :
               ((firstIndex <= index & index < MAX_ENTRIES) | 
                (0 <= index & index < newIndex));
      ensures (\forall int i; 0 <= i & i < MAX_ENTRIES ==>
                  index <= newIndex ? 
                    ((index <= i & i < newIndex) ?
                        data[i] == \old(data[i + 1]) :
                        data[i] == \old(data[i]))
                  : (((index <= i & i < MAX_ENTRIES) |
                      (0 <= i & i < newIndex)) ?
                        data[i] == \old(data[(i + 1) % MAX_ENTRIES]) :
                        data[i] == \old(data[i])));
      ensures nb_entries == \old(nb_entries) - 1;
      ensures newIndex == (\old(newIndex) - 1) % MAX_ENTRIES;
      exsures (TransactionException e) 
                  e._reason == TransactionException.BUFFER_FULL 
                  && JCSystem._transactionDepth == 1; 
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
    */ 
    // Code modified by Nestor CATANO 21/05/2001
    // inclusion of throws clause
    	private void defrag(short index)
	    throws ArrayIndexOutOfBoundsException, 
		   NullPointerException, 
		   TransactionException  {
	    // it consists of pushing back the most recent datas in the cyclic table
	    // in order to provide place for the future transaction
	    
	    // recent transaction must be moved on the left
	    //@assume (\forall int p; (p>=0 && p<data.length)==>(data[p] != null)) ;
	    for(short i = nextIndex(index);i != newIndex; i = nextIndex(i)) {
                // ESC/Java: precondition problem
                // requires es.id != terminalTC & es.id != terminalSN & ...
		data[prevIndex(i)].clone(data[i]);
		data[i].reset();
	    }
	    nb_entries--;
	    newIndex = prevIndex(newIndex);
    	}

/*	@param : a valid index of the transaction file
 *	@return the previous index */
    /*@
      //modifies \nothing ;
      requires i >= 0 && i < MAX_ENTRIES ;
      ensures \result >= 0 && \result < MAX_ENTRIES;

      // cannot be established by ESC/Java      
      ensures \result == (i - 1) % MAX_ENTRIES;
      //exsures (RuntimeException) false ;
    */
	private short prevIndex(short i) {
		// 0 <= i < max_entries
		i--;
		return (short)(i == (short)-1 ? MAX_ENTRIES - (short)1 : i);
	}

/*	@param a valid index of the transaction file
 *	@return the next index */
    /*@
      //modifies \nothing ;
      requires i >= 0 && i < MAX_ENTRIES ;
      ensures \result >= 0 && \result < MAX_ENTRIES ;

      // cannot be established by ESC/Java      
      ensures \result == (i + 1) % MAX_ENTRIES;
      //exsures (RuntimeException) false ;
    */
	private short nextIndex(short i) {
		i++;
		return (short)(i == MAX_ENTRIES ? 0 : i);
	}

}

