package test.slicing.mp;

import java.security.*;
//import jif.util.ArrayList;
import java.util.ArrayList;
import java.util.Random;

public class Player{ //[principal P, label L] authority (P) {
    public static final int Z_BITLENGTH = 32;
    private static final int DECKSIZE = 8;

    private boolean[] available ; 
    private int[] hand;
//	private final Seal seal; 
    private String name;
    private final DNCChain chain;
    
//    private PHCrypto ph = null;
    private PHCrypto rph = null;

    private int ncards = 0; 
    private int nlength = 3;
    private int timestamp = 0;
    private int k = 0;
    private int z = 0;// DABigInteger.ZERO();
//    private final KeyPair keyPair;
    private byte[] p;
    private byte[] rPerm;
//    private PermutationMatrix matrix;
//    private PermutationMatrix matrixCopy;
//    private PHEPermutationMatrix matrix_o =  null;
//    private DAVector w;
//    private DAVector ew;
    private int idnt = 0;
    
    public Player(DNCChain chain, String name){ // throws (IllegalArgumentException{ //, NoSuchAlgorithmException{ //, MPException) where caller (P) {
//        if (chain == null || name == null)
//            throw new IllegalArgumentException();
        boolean[] available = new boolean[DECKSIZE];
        int[] hand = new int[DECKSIZE];
        for (int i = 0; i < DECKSIZE; i++) {
//            try {
                    // this is side effect!
                    available[i] = true;
                    hand[i] = -1; // means no such card 
//            } catch (IndexOutOfBoundsException ignored) {}
        }

//        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
//        if (keyGen == null)
//             throw new MPException("Key generation failed...");
//        keyGen.initialize(1024, new SecureRandom());
//        KeyPair pair = keyGen.generateKeyPair();
//        this.keyPair = pair;
        this.chain = chain;

        this.available = available;
        this.hand = hand;
        this.name = name;

//		this.seal = new Seal();
    }
/*
    public PublicKey{this} getPublic() where authority(P) {
        KeyPair pair = this.keyPair;
        PublicKey key = pair == null ? null : pair.getPublic();
        return declassify (key, {this});
    }*/
/*
    private DNCLink computeLink(int k,DataField data,byte[] prevChainingValue){ //where caller(P){
//        if (data == null || prevChainingValue == null)
//            return null;
        byte [] dataArray = data.toByteArray();
        byte [] chainingValueP = null;*/
/*        try {
            Signature dsa = Signature.getInstance("SHA1withDSA");
//            KeyPair pair = keyPair;
//            PrivateKey key = pair == null? null : pair.getPrivate();
            if (key != null && dsa != null) { 
                dsa.initSign(key);
                dsa.update(dataArray);
                dsa.update(prevChainingValue);
                chainingValueP = dsa.sign();
            }
        } catch (NoSuchAlgorithmException e) {
        } catch (InvalidKeyException e) {
        } catch (SignatureException e) {} 
*/
/*        byte[] chainingValue = DeclassifyHelper.declassifyByteArray(chainingValueP);
        return chainingValue == null? null : new DNCLink(k, data, chainingValue); 
    }*/

    // provides actual link building
/*    private void buildLink(String info, DataFieldAttribute attr){// throws (IllegalArgumentException, MPException) where caller(P) {
//        if (info == null || attr == null)
//            throw new IllegalArgumentException();
        DNCChain chain = this.chain;
//        if (chain == null)
//            throw new MPException();
        // next timestamp
        int ts = this.timestamp + 1;
        // need to declassify attr here
        DataField data = new DataField(name, ts, info, attr);
        byte[] prev;
        if (chain.size() > 0) {
//            try {
                DNCLink prevLink = chain.getLink(chain.size() - 1);
//                if (prevLink == null) 
//                    throw new MPException("prev link is null");
                prev = prevLink.getChainingValue();
//            } catch (IndexOutOfBoundsException ignored) {
//                throw new MPException("IOB");
//            }
        } else {
            prev = new byte[0]; // if chain.size == 0
        }        

        DNCLink link = computeLink(k, data, prev);
//        if (link == null)
//            throw new MPException("Computed link is null");

        chain.addLink(link);
        this.timestamp++;
        k++;    
    }
*/
/*
    private void assertSeal(){ // throws SecurityException{
//		try {
			this.seal.assertIntegrity();
//		} catch (NullPointerException ex) {throw new SecurityException();}
	}
	*/
    /*
    public void initProtocol(){// throws MPException, SecurityException where caller(P){
        assertSeal();
//        Digest C_p = null;
        PHIntVector d = new PHIntVector();
        PHIntVector e = new PHIntVector();

        DABigInteger z_u = null;
//        try {
            p = this.generatePermutation();
            ph = new PHCrypto(2);
            DABigInteger m = ph.getPublicParam();
            do {
                z_u = new DABigInteger(Z_BITLENGTH, 80,new Random());// SecureRandom.getInstance("SHA1PRNG"));
            } while ((z_u.compareTo(m) >= 0) || (z_u.compareTo(DABigInteger.ZERO()) <= 0));
            matrix = new PermutationMatrix(p, z_u);
            matrixCopy = matrix.cloneMatrix();

//            C_p = matrix.getDigest();
            int s = DECKSIZE + 2;
            BigIntVector delta = new BigIntVector(s);
            BigIntVector eps = new BigIntVector(s);
            for (int i = 0; i < s; i++) {
                DABigInteger k;
                do {
                    k = new DABigInteger(4,new Random());// SecureRandom.getInstance("SHA1PRNG"));
                } while ((k.compareTo(DABigInteger.ZERO()) == 0) ||
                         (k.multiply(z_u).compareTo(ph.getPublicParam()) >=0 ) );
                DABigInteger delta_i = k.multiply(z_u);
                delta.add(i, delta_i);

                DABigInteger eps_i;
                do {
                    eps_i = new DABigInteger(z_u.bitLength(),new Random());// SecureRandom.getInstance("SHA1PRNG"));
                } while ((eps_i.mod(z_u).compareTo(DABigInteger.ZERO()) == 0) 
                        || (eps_i.compareTo(ph.getPublicParam()) >= 0));
                eps.add(i, eps_i);

                PHInteger d_i = ph.encrypt(delta_i);
                d.add(d_i);

                PHInteger e_i = ph.encrypt(eps_i);
                e.add(e_i);
            }
            DABigInteger deltasum = delta.getBI(0);
            DABigInteger maxEps = eps.getBI(0);
            
            for (int i = 1; i < s; i++) {
                deltasum = deltasum.add(delta.getBI(i));
                if (eps.getBI(i).compareTo(maxEps) >=0 ){
                    maxEps = eps.getBI(i);
                }
            }
            
//            if (deltasum.add(maxEps).compareTo(ph.getPublicParam()) >= 0 ) {
//                throw new IllegalArgumentException("wrong security parameters, checksum fails");
//            }

            PHInteger dsum = d.getPHI(0);
            for (int i = 1 ; i < s; i++) {
                dsum  = dsum.add(d.getPHI(i));              
            }
            DABigInteger dSumDecrypted = ph.decrypt(dsum);

            w = new DAVector();
            ew = new DAVector();

            for (int i = 0; i < DECKSIZE; i++) {
                CardVector wi = new CardVector(1 + i, DECKSIZE, z_u, this.ph.getPublicParam());
                EncryptedCardVector ewi = ph.encryptCardVector(wi);
                w.add(wi);
                ew.add(ewi);
            }
            ew.permute();
//        } catch (Exception ignored) { }
            
        this.z = DeclassifyHelper.declassifyBigInt(z_u);

//        try {
            buildLink("z_i", z);
//            buildLink("C_p_i", DeclassifyHelper.declassifyDigest(C_p));
            buildLink("D", DeclassifyHelper.declassifyPHIntVector(d));
            buildLink("E", DeclassifyHelper.declassifyPHIntVector(e));
            buildLink("ew", DeclassifyHelper.declassifyDAVector(ew));
//        } catch (IllegalArgumentException ex) {
//            throw new MPException("initialization failed");}
    }
*/
/*
    private byte[] generatePermutation() {
        byte[] p = null;
//        try {
            p = new byte[DECKSIZE];
            for (int i = 0; i < DECKSIZE; i++)
                p[i] = (byte) (i + 1);
            for (int i = 0; i < 100; i++) {
                int u = (int) java.lang.Math.round((DECKSIZE - 1) * java.lang.Math.random());
                int v = (int) java.lang.Math.round((DECKSIZE - 1) * java.lang.Math.random());
                byte t = p[u];
                p[u] = p[v];
                p[v] = t;
            }
//        } catch (IndexOutOfBoundsException ignored) { }
        return p; 
    }*/
/*
    public void drawCard(int card){// throws IllegalArgumentException, MPException , SecurityException   where caller(P){
        assertSeal();*/
/*        try {
            if (!available[card - 1]) {
                throw new IllegalArgumentException("drawCard: wrong card");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("drawCard: IOB for " + 
                Integer.toString(card));
        } catch (NullPointerException e) {
            throw new MPException("drawCard: available is null");
        }*/
/*
//        try {
            DABigInteger pkey = DeclassifyHelper.declassifyBigInt(ph.getPublicParam());
            CardVector w_0 = new CardVector(card, DECKSIZE, z, pkey);
            buildLink("w_i", w_0);
//        } catch (NoSuchAlgorithmException e) { throw new MPException("drawCard: NSA"); } 
//        catch (NullPointerException e) { throw new MPException("drawCard: NPE"); }
    }
*/

/*
    public void processCardDraw(){// throws IllegalArgumentException, MPException , SecurityException    where caller(P){
        assertSeal(); 
        DABigInteger z_i = null;
        CardVector w_0 = null;
//        try {
            for (int i = 0; i < chain.size(); i++) {
                DNCLink link = chain.getLink(i);
                DataField data = link.getData();
                if (data.getPlayerName().compareTo(this.name) !=0 ) {
                    if (data.getInfo().compareTo("z_i")  == 0)
                        z_i = (DABigInteger) data.getAttrib();
                    else if (data.getInfo().compareTo("w_i") == 0)
                        w_0 = (CardVector) data.getAttrib();
                }
            }
//        } catch (IndexOutOfBoundsException ex ) { throw new MPException("processCardDraw: IOB{}");
//        } catch (NullPointerException ex) { throw new MPException("processCardDraw: NPE{}");
//        } catch (ClassCastException ex ) { throw new MPException("processCardDraw: CCE{}"); }
            
//        if (z_i == null) throw new MPException("processCardDraw: z_i == null");
//        if (w_0 == null) throw new MPException("processCardDraw: w_0 == null");
        
        CardVector w_1  = null;
//        try {
            CardVector w_0_0 = null;
            PermutationMatrix pi_ = null;
            w_0_0 = DeclassifyHelper.upgradeCardVector(w_0);
            pi_ = new PermutationMatrix(this.p, DeclassifyHelper.upgradeBigInt(z_i));
            w_1 = w_0_0.multMatrix(pi_);
//		} catch (Exception ignored) {}
//        try {
                this.buildLink("w_i", DeclassifyHelper.declassifyCardVector(w_1));
//        } catch (IllegalArgumentException ex){throw new MPException("processCardDraw failed: IAE");}
    }
*/
/*
    public void processSelfCardDraw(){ //throws MPException, SecurityException  where caller(P){assertSeal();
        EncryptedCardVector ew_i = null;
//        try {
            CardVector w_prev = null;
            for (int i = 0; i < chain.size(); i++) {
                DNCLink link = chain.getLink(i);
                DataField data = link.getData();
                if (data.getInfo().compareTo( "w_i") == 0){
                    w_prev = (CardVector) data.getAttrib();
                }
            }
            int m =  w_prev.getCardValue(z);
            CardVector w_prev_p = DeclassifyHelper.upgradeCardVector(w_prev); 
            DABigInteger z_upgr = DeclassifyHelper.upgradeBigInt(z);
            CardVector w_i = w_prev_p.multMatrix(this.matrix);

            this.matrix.setNonZero(m - 1, z_upgr);
            int mi = w_i.getCardValue(z_upgr);
            CardVector ww_i = (CardVector) this.w.get(mi-1);
            ew_i = ph.encryptCardVector(ww_i);
//        } catch (Exception ignored) { }
//        try {
            this.buildLink("en_wi", DeclassifyHelper.declassifyEncCardVector(ew_i));
//        } catch (IllegalArgumentException ex) {throw new MPException("processSelfCardDraw: IAE");}
	}
*/
/*
    public void processCardDrawO(){// throws MPException, SecurityException where caller(P){
        assertSeal();
        DABigInteger z_i = null;
        EncryptedCardVector ew_i = null;
        PHIntVector d = null;
        PHIntVector e = null;
//        try {
            for (int i = 0; i < chain.size(); i++) {
                DNCLink link = chain.getLink(i);
                DataField data = link.getData();
                if (data.getPlayerName() != this.name) {
                    if (data.getInfo().compareTo( "z_i") == 0)
                        z_i = (DABigInteger) data.getAttrib();
                    else if (data.getInfo().compareTo( "en_wi") == 0)
                        ew_i = (EncryptedCardVector) data.getAttrib();
                    else if (data.getInfo().compareTo("D") == 0)
                        d = (PHIntVector) data.getAttrib();
                    else if (data.getInfo().compareTo ("E") == 0)
                        e = (PHIntVector) data.getAttrib();
                }
            }
//        } catch (IndexOutOfBoundsException ex) { throw new MPException("processCardDrawo: IOB{}");} 
//        catch (NullPointerException ex) { throw new MPException("processCardDrawo: NPE{}"); } 
//        catch (ClassCastException ex) { throw new MPException("processCardDrawo: CCE{}");}
        
//        if (z_i == null) throw new MPException("processCardDrawO: z_i == null");
//        if (ew_i == null) throw new MPException("processCardDrawO: ew_i == null");
//        if (d == null) throw new MPException("processCardDrawO: d == null");
//        if (e == null) throw new MPException("processCardDrawO: e == null");
        
        EncryptedCardVector ew = null;
//        try {
            DABigInteger z_i_p = DeclassifyHelper.upgradeBigInt (z_i);
            DABigInteger thisz = DeclassifyHelper.upgradeBigInt (this.z);                
            EncryptedCardVector ew_i_p = DeclassifyHelper.upgradeEncCardVector(ew_i);
            PHIntVector d_p = DeclassifyHelper.upgradePHIntVector(d);
            PHIntVector e_p = DeclassifyHelper.upgradePHIntVector(e);

            if (this.matrix_o == null) {
                matrix_o = new PHEPermutationMatrix( this.matrix, d_p, e_p, z_i_p, thisz);
            }
            PHEPermutationMatrix pi_c = matrix_o;
            ew = ew_i_p.multMatrix(pi_c);
//        } catch (Exception ignored) { }

//        try {
            buildLink("en_wi", DeclassifyHelper.declassifyEncCardVector(ew));
//        } catch (IllegalArgumentException ex) { throw new MPException("processCardDrawO failed"); }
    }
*/
/*
    public void finishCardDraw(){// throws MPException, SecurityException where caller(P){
        assertSeal();
        boolean ok  = false;
//        try {
            EncryptedCardVector en_wi = null;
            for (int i = 0; i < chain.size(); i++) {
                DNCLink link = chain.getLink(i);
                DataField data = link.getData();
                if (data.getInfo().compareTo( "en_wi") == 0){
                    en_wi = (EncryptedCardVector) data.getAttrib();
                }
            }
            EncryptedCardVector en_wi_p = DeclassifyHelper.upgradeEncCardVector(en_wi);
            CardVector w = ph.decryptCardVector(en_wi_p);
            DABigInteger thisz = DeclassifyHelper.upgradeBigInt (this.z);  
            int m = w.getCardValue(thisz);
            this.hand[ncards] = m; 
            this.ncards ++;
            ok = true; 
//        } catch (Exception ex) { }
//        if (declassify (!ok, {L})) throw new MPException();
    }
*/
/*
    public void unseal(){ // where caller(P) {
//		try {
			this.seal.unseal();
//		} catch (NullPointerException ignored) {}
	}*/
	/*
    public BigIntVector revealPrivateKey(){ // where caller (P) {
        unseal();        
        BigIntVector key = null;
//        try {
            PHCrypto ph = this.ph;
            BigIntVector key1 = ph.getSecretKey();
//            declassify ({L})
                key = DeclassifyHelper.declassifyBigIntVector(key1);
//        } catch (NullPointerException ex){ }
//        try {
            this.rph = new PHCrypto(this.nlength, key);
//        } catch (IllegalArgumentException ignored) {}
        return key;
    }
    */
/*
    public byte[] revealPermutation(){ // where caller (P){
        unseal();
        this.rPerm = DeclassifyHelper.declassifyByteArray(p);
        return rPerm;
    }
    */
/*
    public PermutationMatrix revealMatrix(){ // where caller(P){
        unseal();
        return DeclassifyHelper.declassifyMatrix(matrixCopy);
    }
    */
/*
    public int[] showCards(){ // where caller(P){
        unseal();
        int[] hand = this.hand;
//        if (declassify (hand == null, {L})) return null;
//        declassify ({L})
            return  DeclassifyHelper.declassifyIntArray(hand);
    }
*/
    public boolean validate(BigIntVector oKey, byte[] oPerm, PermutationMatrix oMatrix){ // throws IllegalArgumentException, MPException where caller(P){
//        if (oKey == null) throw new IllegalArgumentException("validate: oKey null");
//        if (oPerm == null) throw new IllegalArgumentException("validate: oPerm null");
//        if (oMatrix == null) throw new IllegalArgumentException("validate: oMatrix null");

        int z_i =0;// null;
//        Digest C_p_i = null;
        DAVector ew = null;
//        try {
            for (int i = 0; i < chain.size(); i++) {
                DNCLink link = chain.getLink(i);
                DataField data = link.getData();
                if (data.getPlayerName().compareTo(this.name) !=0 ) {
//                    if (data.getInfo().compareTo("z_i")  == 0)
//                        z_i = (DABigInteger) data.getAttrib();
//                    if (data.getInfo().compareTo("C_p_i")  == 0)
//                        C_p_i = (Digest) data.getAttrib();
//                    if (data.getInfo().compareTo("ew") == 0)
//                        ew = (DAVector) data.getAttrib();
                }
            } 
//        } catch (NullPointerException ex){
//            throw new MPException("validate: NPE");
//        } catch (IndexOutOfBoundsException ex) {
//            throw new MPException("validate: IOB");
//        } catch (ClassCastException ex) {
//            throw new MPException("validate: CCE");
//        }
        if (!oMatrix.validate(oPerm, z_i))
            return false;
//        Digest oDig = oMatrix.getDigest();
//        if (oDig == null) return false;
//        if (!oDig.equals(C_p_i)) return false;
        
        PHCrypto oph = null;
//        try {
            oph = new PHCrypto(this.nlength,(Integer)oKey.get(0),
                (Integer)oKey.get(1),(Integer)oKey.get(2),(Integer)oKey.get(3));
//        } catch (IndexOutOfBoundsException ignored) {  return false;
//        } catch (ClassCastException ignored){ return false;
//        } catch (IllegalArgumentException ignored) { return false; }
//        try {
            if (ew.size() != DECKSIZE)
                return false;
            boolean[] verifiedCards = new boolean[DECKSIZE];
            for (int i = 0; i< DECKSIZE; i++)
                verifiedCards[i] = false;
            for (int i = 0; i<DECKSIZE; i++) {
                EncryptedCardVector ewi = (EncryptedCardVector) ew.get(i);
                CardVector wi  = oph.decryptCardVector(ewi);
                int value = wi.getCardValue(z_i);
                verifiedCards[value-1] = true;                
            }
            for (int i = 0; i < DECKSIZE ;i++)
                if (!verifiedCards[i]) 
                    return false;
//        } catch (IndexOutOfBoundsException ignored){ return false;
//        } catch (ClassCastException ignored){ return false;
//        } catch (NullPointerException ex ) { return false; }

		int state = 0; 
//		try {
			for (int i = 0; i < chain.size(); i++) {
				DNCLink link = chain.getLink(i);
				DataField data = link.getData();

				if ( data.getInfo().compareTo("w_i") == 0) {
					if (data.getPlayerName().compareTo(this.name) == 0) {
						CardVector wi = (CardVector) data.getAttrib();
						int value = 0;
//						try {
							value = wi.getCardValue(this.z);
//						} catch (IllegalArgumentException ex) {throw new MPException("IAE: " + ex.toString());}
						int pvalue = rPerm[value-1]; 
						i++;
                        link = chain.getLink(i);
						data = link.getData();
                        if (data.getPlayerName().compareTo(this.name) == 0) {
//                            if (data.getInfo().compareTo("en_wi") != 0) {
//                                throw new MPException("validate:unexpected chain link en_wi"); }
                            i++;
                            link = chain.getLink(i);
                            data = link.getData();
 //                           if (data.getPlayerName().compareTo(this.name) == 0)
 //                               throw new MPException("validate: unexpected chain link name"); 
                            
                            EncryptedCardVector ewi = (EncryptedCardVector) data.getAttrib();
                            CardVector wi_ = rph.decryptCardVector(ewi);
                            int ppvalue = 0;
                            String ewstr = ewi.toString();
                            String wistr = wi_.toString();
//                            try {
                                ppvalue = wi_.getCardValue(this.z);
//                            } catch (IllegalArgumentException ex) {
//                                throw new MPException("IAE: "  + ex.toString () + ", " + z.toString() + wistr);
//                            }
//                            if (oPerm[pvalue-1] != ppvalue )
//                                throw new MPException(Integer.toString(value) + " " + Integer.toString(pvalue) + " " + Integer.toString(ppvalue)  );
                        }
					} else {
						i+=2;
					}
				}
			}
//		} catch (IndexOutOfBoundsException ex ) {return false;
//		} catch (NullPointerException ex ){	return false;
//		} catch (ClassCastException ex) { return false; } 
        return true;
    }

}



