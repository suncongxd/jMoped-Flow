/*
 * Rotor.java - ENIGMA Rotor implementation
 *
 * Copyright (c) 1998, Charles McManis, All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies.
 *
 * CHUCK MCMANIS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. CHUCK MCMANIS
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT
 * OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package test.slicing.Enigma;
/**
 * This class implements the basic ENIGMA rotor construct. It effectively
 * exports two interfaces, set() which sets the key, and process() which
 * processes a single character through the rotor stack.
 */
public class Rotor {

    /* 'contents' contains the rotor's scrambling code. */
    private byte[] contents;

    /* Notch indicates when the next rotor needs to be rotated */
    private short notch;

    /* Index holds this rotor's rotation. */
    private short index = 0;

    private Rotor next, prev;

    /*
     * Construct a new rotor, and use the passed in byte array
     * as its substitution function.
     */
    public Rotor(short notch, byte data[]) {
        this.contents = data;
        this.notch = notch;
        index = 0;
    }

    /*
     * Constructs a new rotor and links it "ahead" of the passed in rotor.
     * this lets the Enigma applet build up a stack of rotors. 
     */
    public Rotor(Rotor prv, short notch, byte data[]) {
        this.next = prv;
        prv.prev = this;
        this.notch = notch;
        this.contents = data;
    }

    /*
     * This string provides a character mapping from ascii to the
     * BASE64 values and vice-versa.
     */
    private static byte char_map[];

    /*
     * The reflector, this isn't a rotor per se, it serves to both
     * scramble the characters and to reflect them back through the
     * mechanism. It too is manually initialized.
     */
    private static byte reflector[];

    /* work around for a JiB bug. The static initializer for this
     * class isn't run on loading so you can use a static initalizer
     * here like you do in Enigma to initialize the character map
     * and reflector arrays.
     */	
    static void setMaps(byte chars[], byte refl[]) {
	char_map = chars;
	reflector = refl;
    }

    /*
     * Given a character, find its index in the array ...
     */
    private short indexOf(byte v[], byte b) {
	for (short i = 0; i < v.length; i++) {
	    if (b == v[i]) return i;
	}
	return 0;
    }

    /*
     * Send a character backwards through the rotor's mapping function.
     * If this is not the first rotor in the stack (prev is not null)
     * then also send it through that rotor's inverse mapping recursively.
     */
    private byte inverse(byte c) {
        byte r;
        short xi;

        xi = indexOf(contents, c);
	r = char_map[(xi - index) & 0x3f];
	return (prev == null) ? r : prev.inverse(r);
    }

    /*
     * Rotate all rotors in the stack.
     *
     * This method implements a rotor rotation. The effect of this is
     * that the substitution table for the next character will be
     * different than for the previous character. As with the encrypt
     * method, if the index is a multiple of the  notch, then the
     * next rotor in the stack is also rotated.
     */
    public void rotate() {
        if (notch == 0)
            return;

        index = (short)((index + 1) & 0x3f);

        if ((index % notch == 0) && (next != null)) {
            next.rotate();
        }
    }

    /*
     * recursive version of set(). This method pulls bytes
     * out of the key array and stores them in the index field
     * of the rotor.
     */
    private void set(byte key[], int slot) {
	index = indexOf(char_map, key[slot]); 
        if (next != null)
            next.set(key, ++slot); 
    }
	

    /**
     * Set the encryption/decryption key for the rotor stack.
     *
     * This method sets the "key" for the rotor stack by pre-setting
     * each rotor in the stack to the passed in character. Each 
     * character is used in turn until the byte array runs out or the
     * rotors run out. Note that if you don't have enough bytes for
     * the rotors used you WILL get unpredictable results.
     */
    public void set(byte key[]) {
	set(key, 0);
    }

    /**
     * Encrypt a single byte of data with the current rotor stack.
     * 
     * This method implements the ENIGMA machine encryption. Each
     * rotor is given the character to encrypt and the last rotor
     * in the stack reflects the character back through the stack.
     * If the loop were not recursive the steps for a 3 rotor system
     * would be:
     *	    - left most rotor forward
     *	    - middle rotor forward
     *      - right rotor forward  
     *	    - Reflect the character
     *	    - Right rotor inverse
     *	    - Middle rotor inverse
     *	    - Left rotor inverse
     *	Then return the final output byte.
     */
    public byte encrypt(byte c) {
        short xi;
	byte r;

        xi = indexOf(char_map, c);

	// substitute character, if more rotors recursively continue.
	r = contents[(xi + index) & 0x3f];
	if (next != null)
	    return next.encrypt(r);

	// last rotor in the stack so reflect it back
        xi = indexOf(char_map, r);
        r = reflector[xi];

	// do the inverse mappings recursively.
	return inverse(r);
    }

}
