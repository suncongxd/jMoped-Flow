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


//package com.gemplus.pacap.utils;
package test.Julia.EPurse;

/* contains the method access conditions of the interfaces*/
public class AccessControlTable extends Object{
    /*@
      invariant \nonnullelements(data) ;
    */

    ////////////////      ATTRIBUTES       ////////////////
    /*@spec_public */ private AccessControl data[] = null;
    
    ///////////////     CONSTRUCTOR     ////////////////

    /* the table idArray and acArray contains identifiers of the
       different methods. The associated access conditions are the
       default ones of the AccessCondition class.
     * @param initArray: a table where identifiers and access
     * conditions * are associated
     * the first element of the table is the identifier, immediately
     * followed by its access condition */

    /*@
      requires initArray != null  && (byte)(initArray.length / 2) >= 0;
      requires (\forall byte j; (j >= 0 && j < initArray.length / 2) ==>
                 initArray[(byte)(j * 2 + 1)] == AccessCondition.FREE ||
                 initArray[(byte)(j * 2 + 1)] == AccessCondition.LOCKED ||
                 initArray[(byte)(j * 2 + 1)] == AccessCondition.SECRET_CODE ||
                 initArray[(byte)(j * 2 + 1)] == 
                                            AccessCondition.SECURE_MESSAGING ||
                 initArray[(byte)(j * 2 + 1)] == (AccessCondition.SECRET_CODE |
                                            AccessCondition.SECURE_MESSAGING));
      ensures data.length == (byte)(initArray.length / 2) ;
      ensures (\forall byte j; (j >= 0 && j < data.length) ==> 
                               (\fresh(data[j]) && 
			       (data[j]).methode == initArray[(byte)(j*2)] &&
			       ((AccessCondition)data[j]).condition == 
                                                 initArray[(byte)(j*2+1)] 
			       ));
      ensures \fresh(this);
      //exsures (RuntimeException) false ;
    */
    public AccessControlTable(byte[] initArray) {
	super();
	// allocate the table
	data = new AccessControl[(byte)(initArray.length / 2)];
	// allocate the object of the table
	for(byte j = 0;j < data.length;j++) {
	    // allocate and set the access condition
	    data[j] = new AccessControl(initArray[(byte)(j*2)]);
	    data[j].setCondition(initArray[(byte)(j*2+1)]);            
	}
    }
    
    
    ////////////////       METHODS      ///////////////
    
    /* return the access control for the specified method given by 
       its identifier
     * @param id : the method identifier
     * @return : the access control or null if the id is not found.
     */    
    /*@ 

      //modifies \nothing ;
      requires true ;
      ensures (\forall int j; (j >= 0 && j < data.length) ==> 
                              data[j].methode != id) ==> \result == null;
      ensures (\exists int j; (j >= 0 && j < data.length) &&
                              data[j].methode == id) ==> 
                              (\result != null && \result.methode == id);
      //exsures (RuntimeException) false ; 
    */
    public AccessControl getAccessControl(byte id) {
	// llok for the access controler
	for(byte i = 0;i < data.length;i++) {
	    if(data[i].getMethode() == id) {
		return data[i];
	    }
	}
	return null;
    }

    /*@
      //modifies \nothing ;
      requires c != null ;
      ensures (\forall int j; (j >= 0 && j <data.length) ==> 
                                       data[j].methode != id) ==> !\result ;
//    ensures \result == 
//             (((AccessCondition)getAccessControl(id)).condition == AccessCondition.FREE) ||
//             ((((AccessCondition)getAccessControl(id)).condition == AccessCondition.SECRET_CODE) && 
//                 (byte)(c.condition & AccessCondition.SECRET_CODE) == 
//                        AccessCondition.SECRET_CODE) ||
//             ((((AccessCondition)getAccessControl(id)).condition == 
//                                AccessCondition.SECURE_MESSAGING) && 
//                 (byte)(c.condition & AccessCondition.SECURE_MESSAGING) == 
//                        AccessCondition.SECURE_MESSAGING) ||
//              (((AccessCondition)getAccessControl(id)).condition == (AccessCondition.SECRET_CODE | 
//                                AccessCondition.SECURE_MESSAGING) &&
//   	        ((byte)(c.condition & (AccessCondition.SECRET_CODE | 
//                                          AccessCondition.SECURE_MESSAGING)) ==
//                                       (AccessCondition.SECRET_CODE|
//                                          AccessCondition.SECURE_MESSAGING)));
      exsures (AccessConditionException) false;
    */ 
    public boolean verify(byte id, AccessCondition c) throws AccessConditionException {
	AccessControl ac = getAccessControl(id);
	return (ac == null ? false : ac.verify(c));
    }


    public void setAccessControl(byte id, byte ac) {
	// commented by Rodolphe Muller on 16/06/2000
	// strange, this method doestn't do anything
    }
    
}

