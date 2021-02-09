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

// import com.gemplus.pacap.utils.*;
 
/* model the time for the transactions*/
 public class Heure extends Object{

     /*@ invariant heure >= 0 && heure < 24; 
         invariant minute >= 0 && minute < 60;
     */
    
    private /*@ spec_public */ byte heure;
    private /*@ spec_public */ byte minute;
    
     /*@ 
       requires h >= 0 && h < 24 && m >= 0 && m < 60;
       modifies heure, minute ;
       ensures heure == h && minute == m ;
       exsures (HeureException) false;
     */
    public void setHeure(byte h, byte m) throws HeureException{
        if ( 0 <= h && h < 24 ){
            if ( 0 <= m && m < 60 ){
                heure = h;
                minute = m;
            }
            else HeureException.throwIt(HeureException.ERREUR_MINUTE);
        }
        else HeureException.throwIt(HeureException.ERREUR_HEURE);
    } 

     /*@ 
       modifies heure, minute ;
       requires h != null ;     
       ensures heure == h.heure && minute == h.minute ;
       exsures (HeureException)false ;
     */
    public void setHeure(Heure h) throws HeureException{
        setHeure(h.getHeure(), h.getMinute());
    } 

     /*@
       //modifies \nothing;
       requires true ;
       ensures \result == heure ;
       //exsures (RuntimeException) false ;
     */
    public byte getHeure(){
        return heure;
    }

     /*@
       //modifies \nothing;
       requires true ;
       ensures \result == minute ;
       //exsures (RuntimeException) false ;
     */
    public byte getMinute(){
        return minute;
    }
    
/* put the value of this time in the given table
 * @param bArray : the destination table
 * @param offset: the position in the table
 * @return offset + 2 (bytes)*/
     /*@
       modifies bArray[*];
       requires bArray != null && 0 <= offset && offset <= bArray.length - 2 ;
       ensures \result == (short)(offset + 2) ;
       //exsures (RuntimeException) false ;
     */
    public short  getHeure(byte [] bArray, short offset){
        short aux = offset;
        bArray[aux++] = heure;
	//@ assume 0 <= aux;
	bArray[aux++] = minute;
        return (short) (offset + (short) 2);
    }
    
 }
