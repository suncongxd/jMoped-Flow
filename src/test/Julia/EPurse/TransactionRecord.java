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
// import instruction is needed by ESC/Java 
import javacard.framework.ISOException;

import javacard.framework.Util;
/* in this table are stored the transaction performed by the purse. it is cyclic table*/
class TransactionRecord extends Object{
    /*@
      invariant MAX_ENTRIES == data.length ;
      invariant firstIndex >= 0 && firstIndex < data.length;
      invariant nb_entries >= 0 && nb_entries <= MAX_ENTRIES;
      invariant newIndex >= 0 && newIndex < data.length ;
      invariant \nonnullelements(data) ;
      invariant (nb_entries == 0 ?
                 (firstIndex == newIndex) :
                 (newIndex >= firstIndex ? 
                   nb_entries == newIndex - firstIndex :
                   nb_entries == (MAX_ENTRIES - firstIndex) + newIndex));
     */

	////////////////      ATTRIBUTES       ////////////////

    /* the size of the transaction table*/
    /*@ spec_public */ private static final byte MAX_ENTRIES = (byte)100;
	//private static final byte MAX_ENTRIES = (byte)10;

    /* the number of entries now stored in this file*/
    /*@ spec_public */ private byte nb_entries = (byte)0;

    /* the index of the next transaction to record*/
    /*@ spec_public */ private short newIndex = (byte)0;

    /* the index of the oldest transaction*/
    /*@ spec_public */ private short firstIndex = (byte)0;

	/* the transaction table*/
    /*@ spec_public */ private Transaction[] data = new Transaction[MAX_ENTRIES];


	///////////////     CONSTRUCTEUR     ////////////////
    /*@
      requires true ;
      ensures \fresh(this) ;
      ensures (\forall int k; (k>=0 && k<MAX_ENTRIES)==>\fresh(data[k])) ;
      ensures newIndex == 0;
      ensures firstIndex == 0;
      ensures nb_entries == 0;
      //exsures (RuntimeException) false ;
    */
	TransactionRecord() {
		//data = new Transaction[MAX_ENTRIES];
		for(short i = 0;i < MAX_ENTRIES;i++) {
			data[i] = new Transaction();
		}
	}

	////////////////       METHODS      ///////////////

/*	@param index : the index of the transaction in the table
 *	@return the transaction*/
    /*@
      //modifies \nothing ;
      requires index >= 0 && index < data.length ;
      ensures \result == ((index >= 0 && index < nb_entries) ? 
                         data[index] : 
                         null) ;
      //exsures (RuntimeException) false ;
    */
	Transaction getTransaction(short index) {
		Transaction resu = null;
//		Modified by LC and HM 11/01/2001
//		if(index >= 0 && index < MAX_ENTRIES) {
		if(index >= 0 && index < nb_entries) {
			resu = data[index];
		}
		return resu;
	}

/* @return the number of transactions stored in this file */
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == nb_entries ;
      //exsures (RuntimeException) false ;
    */
    byte getNbEntries() {
	return nb_entries;
    }

/* store a copy of the transaction passed in the parameter in the data table
   @param t the transction to store*/
    /*@
      modifies JCSystem._transactionDepth ;
      modifies data[newIndex].number, data[newIndex].type ; 
      modifies data[newIndex].devise, data[newIndex].typeProduit ;
      modifies data[newIndex].taux.intPart, data[newIndex].taux.decPart ; 
      modifies data[newIndex].montant.intPart, data[newIndex].montant.decPart ;
      modifies data[newIndex].date.jour, data[newIndex].date.mois, data[newIndex].date.annee ;
      modifies data[newIndex].heure.heure, data[newIndex].heure.minute ;
      modifies data[newIndex].id[*], data[newIndex].certificat[*];
      modifies data[newIndex].terminalSN[*], data[newIndex].terminalTC[*] ;
      modifies data[newIndex].mutualAuthentification, data[newIndex].bankAutorization ; 
      modifies data[newIndex].status, data[newIndex].given ;
      modifies data[newIndex].isValid, nb_entries ;
      modifies newIndex, firstIndex;
      requires t != null ;
      requires t.terminalSN != data[newIndex].terminalTC;
      ensures JCSystem._transactionDepth == 0 ;
      ensures data[\old(newIndex)].number == t.number ; 
      ensures data[\old(newIndex)].type == t.type ; 
      ensures data[\old(newIndex)].devise == t.devise ; 
      ensures data[\old(newIndex)].typeProduit == t.typeProduit ; 
      ensures data[\old(newIndex)].taux.intPart == t.taux.intPart && 
              data[\old(newIndex)].taux.decPart == t.taux.decPart ; 
      ensures data[\old(newIndex)].montant.intPart == t.montant.intPart && 
              data[\old(newIndex)].montant.decPart == t.montant.decPart ; 
      ensures data[\old(newIndex)].date.jour == t.date.jour && 
              data[\old(newIndex)].date.mois == t.date.mois && 
	      data[\old(newIndex)].date.annee == t.date.annee ;
      ensures data[\old(newIndex)].heure.heure == t.heure.heure && 
              data[\old(newIndex)].heure.minute == t.heure.minute ;
      ensures (\forall int i; 0 <= i & i < t.CERTIFICATE_LENGTH ==>
                  data[\old(newIndex)].certificat[i] == t.certificat[i]);
      ensures (\forall int i; 0 <= i & i < 4 ==>
                  data[\old(newIndex)].terminalSN[i] == t.terminalSN[i]);
      ensures (\forall int i; 0 <= i & i < 4 ==>
                  data[\old(newIndex)].terminalTC[i] == t.terminalTC[i]);
      ensures data[\old(newIndex)].mutualAuthentification == 
                             t.mutualAuthentification ; 
      ensures data[\old(newIndex)].bankAutorization == t.bankAutorization ; 
      ensures data[\old(newIndex)].status == t.status ; 
      ensures data[\old(newIndex)].given == t.given ; 
      ensures data[\old(newIndex)].isValid == true ; 
      ensures newIndex == (\old(newIndex) + 1) % MAX_ENTRIES;
      ensures nb_entries == (\old(nb_entries) == MAX_ENTRIES ?
                             \old(nb_entries) :
                            (byte)(\old(nb_entries) + 1));
      ensures firstIndex == (\old(nb_entries) < MAX_ENTRIES ?
                             \old(firstIndex) :
                             (\old(firstIndex) + 1) % MAX_ENTRIES);
      exsures (ArrayIndexOutOfBoundsException) false ;
      exsures (NullPointerException) false ;      
      exsures (ISOException) false ;      
      exsures (TransactionException e) 
                 (\old(JCSystem._transactionDepth) == 1 && 
                 e._reason == TransactionException.IN_PROGRESS) || 
		 (\old(JCSystem._transactionDepth) == 0 &&
		 e._reason == TransactionException.NOT_IN_PROGRESS) ;
    */
    // Code modified by Nestor CATANO 24/05/2001
    // inclusion of throws clause
    void addTransaction(Transaction t)
 	throws TransactionException,
	       ISOException,
	       ArrayIndexOutOfBoundsException, 
	       NullPointerException {
	data[newIndex].clone(t);
	data[newIndex].valid();
	newIndex = nextIndex(newIndex);
	if(nb_entries < MAX_ENTRIES) {
	    nb_entries++;           
        } else {
	    firstIndex = nextIndex(firstIndex);
	}
    }

/* check if the data table is full
   @return the result of the test: true if it is full, false otherwise*/
    /*@
      //modifies \nothing ;
      requires true ;
      ensures \result == (nb_entries == MAX_ENTRIES) ;
      //exsures (RuntimeException) false ;
    */
    boolean isFull(){
	return nb_entries == MAX_ENTRIES;
    }

/* erase the transaction which indexis provided in parameter
 * @param index the transaction index*/
    // MH, 31/10/01: notice that it is required that index is 'between'
    // firstIndex and newIndex. Deleting an element that is not in the
    // range messes up the structure, deletes the first element in the
    // structure.
    /*@
      modifies JCSystem._transactionDepth, data[*], nb_entries, newIndex ;
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
                  (\old(JCSystem._transactionDepth) == 1 && 
                  e._reason == TransactionException.IN_PROGRESS) || 
		  (\old(JCSystem._transactionDepth) == 0 &&
		  e._reason == TransactionException.NOT_IN_PROGRESS) ;
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
      exsures (ISOException) false ;
    */
    void deleteTransaction(short index) 
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException {
	if(nb_entries > 0) {
	    data[index].unvalid();            
	    // the table is then compressed to have lmore place for future transactions
	    defrag(index);
	}
    }

/* return the index of the first valid transactions concerned by the saler
		@param id the saler id
		@return the 1rst valid transaction concerned by the saler or -1 if there is transaction*/
// temporarily removed by Rodolphe Muller on 16/06/2000
/*
	short getIndexTransaction(SalerID id) {
		short resu = (short)-1;
		boolean pasTrouve = true;
		// nombre d'entr�e (valides) consult�
		short i = (short)0;
		// index de la prochaine entr�e � consulter
		short index = firstIndex;
		while(i < nb_entries && pasTrouve) {
			Transaction t = data[index];
			if(t.isValid()) {
				i++;
				if(Util.arrayCompare(t.getPartnerID(),(short) 0,
										id.getBytes(), (short) 0,
										SalerID.ID_LENGTH)
					== 0 ) {                                   
					resu = i;
					pasTrouve = false;
				}                   
			}
			index = nextIndex(index);
		}
		return resu;        
	}
*/



/* return the 1rst transaction not used but implied in the specified loyalty program */
    /*@
      //modifies \nothing ;
      requires al != null ;
      ensures (\exists int i; 
                          (firstIndex < newIndex ?
                           firstIndex <= i && i < newIndex :
                           (0 <= i && i < newIndex) || 
                           (firstIndex <= i & i < MAX_ENTRIES)) &&
               data[i] != null &&
               data[i].isValid &&
               data[i].type == Transaction.TYPE_DEBIT &&
               ! data[i].given &&
               (\exists int j; 0 <= j && j < AllowedLoyalty.MAX_SALERS &&
                   (\forall int k; 
                       0 <= k && k < Transaction.PARTNER_ID_LENGTH ==>
                       al.data[j].data[k] == data[i].id[k]))) ==>
              \result != null;
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ;
    */
    // Code modified by Nestor CATANO 24/05/2001
    // inclusion of throws clause
    Transaction getTransaction(AllowedLoyalty al)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException {
        Transaction resu = null;
        // boolean that can stop the search
        boolean b = true;
        // number of transactions already visited
        short i = (short) 0;
        // index of the new entry to visit
        short index = firstIndex;
        while  ( i < nb_entries && b){
            Transaction t = data[index];
            // check if it is a valid transaction
            if ( t.isValid() ) {
                i++;
                // check if it is a debit
                if ( t.getType() == Transaction.TYPE_DEBIT 
                     && ! t.isGiven()){                
                    // check if the debit has been performed in a correct saler
                    for ( byte j = (byte) 0; j < al.getNbSalers() && b; j++){
                        SalerID sid = al.getSaler(j);
                        short cmp = Util.arrayCompare(sid.getBytes(),(short) 0,
						      t.getPartnerID(), (short) 0,
						      Transaction.PARTNER_ID_LENGTH);
                        // we stop the loop if the saler of the transaction is in the loyalty list
                        if ( cmp == 0 ) {
                            // the loyalty is concerned by this transaction
                            resu = t;
                            b = false;
			    
                        }
                    }                        
                }                
            }
            index = nextIndex(index);
        }
        return resu;
    }

/*	return the number of transaction which concern the loyalty provide in the argument of the method*/
    /*@
      //modifies \nothing ;
      requires al != null ;
      ensures true ;
      exsures (NullPointerException) false ;
      exsures (ArrayIndexOutOfBoundsException) false ;
    */
    // Code modified by Nestor CATANO 24/05/2001
    // inclusion of throws clause
	short getNbTransactions(AllowedLoyalty al)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException {
		short resu = (short)0;
		// number of valid entries already visited
		short i = (short)0;
		// index of the next entry to visit
		short index = firstIndex;
		while(i < nb_entries){
			Transaction t = data[index];
			// check if the transaction is valid
			if(t.isValid()) {
				i++;
				// check if it is a not already used debit
				if(t.getType() == Transaction.TYPE_DEBIT && !t.isGiven()) {
					// boolean used to stop the search
					boolean b = true;
					// check if the debit has been performed by the right saler
					for(byte j = (byte)0;j < al.getNbSalers() && b;j++) {
						SalerID sid = al.getSaler(j);
						short cmp = Util.arrayCompare(
							sid.getBytes(), (short)0,
							t.getPartnerID(), (short)0,
							Transaction.PARTNER_ID_LENGTH
						);
						// stop the loop if the transaction saler is in the loyalty list
						if(cmp == 0) {
							// the loyalty is concerned by the transaction
							resu++;
							b = false;
						}
					}
				}
			}
			index = nextIndex(index);
		}
		return resu;
	}


    //-------------------------------------------------------------------------
    //
    //                            private methods
    //
    //-------------------------------------------------------------------------
    
    
    /*compress the data table
      @param index : the index of the newly suppressed transaction    */
    // ESC/Java cannot establish that this method preserves the invariant
    /*@  
      modifies JCSystem._transactionDepth, data[*], nb_entries, newIndex ;
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
                  (\old(JCSystem._transactionDepth) == 1 && 
                  e._reason == TransactionException.IN_PROGRESS) || 
		  (\old(JCSystem._transactionDepth) == 0 &&
		  e._reason == TransactionException.NOT_IN_PROGRESS) ;
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
      exsures (NullPointerException) false;
      exsures (ArrayIndexOutOfBoundsException) false;
      exsures (ISOException) false ;
    */ 
    // Code modified by Nestor CATANO 24/05/2001
    // inclusion of throws clause
    private void defrag(short index)
	throws ArrayIndexOutOfBoundsException, 
	       NullPointerException, 
	       TransactionException,
	       ISOException  {
        // it consists of moving the recent data in the cyclic table in order to have more place for 
        // the future data
        
        // trasnactions are moved on the left
        for ( short i = nextIndex(index); i != newIndex; i = nextIndex(i)){
	    /*@ assume data[i].taux.intPart >= 0 &&  
	               data[i].montant.intPart >= 0 ;
	    */
            // ESC/Java: precondition problem
            // requires es.id != terminalTC & es.id != terminalSN & ...
            data[prevIndex(i)].clone(data[i]);
            data[i].reset();;
        }
        nb_entries--;
        newIndex = prevIndex(newIndex);        
    }
    
/* @param a valid index of the transaction file
   @return the previous index */
    /*@
      //modifies \nothing ;
      requires i >= 0 && i < MAX_ENTRIES ;
      ensures \result >= 0 && \result < MAX_ENTRIES;

      // cannot be established by ESC/Java      
      ensures \result == (i - 1) % MAX_ENTRIES;
      //exsures (RuntimeException) false ;
    */
    private short prevIndex(short i){
        // 0 <= i < max_entries
        i--;
        return (short)( i == (short) -1 ? MAX_ENTRIES - (short) 1 : i);
    }
    
    /*
      @param a valid index of the transaction file
      @return the next index */ 
    /*@
      //modifies \nothing ;
      requires i >= 0 && i < MAX_ENTRIES ;
      ensures \result >= 0 && \result < MAX_ENTRIES ;

      // cannot be established by ESC/Java      
      ensures \result == (i + 1) % MAX_ENTRIES;
      //exsures (RuntimeException) false ;
    */
    private short nextIndex(short i){
        i++;
        return (short) ( i == MAX_ENTRIES ? 0 : i);
    }
	//{{DECLARE_CONTROLS
	//}}
}

