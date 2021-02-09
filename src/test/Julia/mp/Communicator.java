
package test.Julia.mp;

import java.io.*;
//import jif.runtime.Runtime;
import java.security.*;

public class Communicator {
/*    public PrintStream out() {
        return System.out;
    }
    public InputStream in() {
        return System.in;
    }*/

    public void play(){ //String playerName){
        String playerName="Alice";
        try {
//            Main m = Main.m;
//            if (m == null) return;
//            final principal pp = m.p;
//            Runtime[pp] runtime = Runtime[pp].getRuntime(pp);
//            if (runtime == null) return;
            PrintStream out = System.out;//runtime.out();
            InputStream in  = System.in;//runtime.in();
            out.println("Playing mental poker...");
            SerializeWriteHelper writer = new SerializeWriteHelper(out);
            InputStreamReader sr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(sr);
            SerializeReadHelper reader = new SerializeReadHelper(br);

            DNCChain chain = new DNCChain(writer);
            Player player = new Player(chain, playerName);
//            out.println("Initializing " + playerName);
            try {
                 player.initProtocol();                 
                 out.println("wrote " + chain.size() + " links");
                 out.flush();     
               for (int i = 0; i < 5; i++){
                    DNCLink link = reader.readDNCLink();
                    chain.appendLink(link);
                    out.println("read " +  Integer.toString(i+1) + " links");
                    out.flush();                    
                }            
                out.println("total " + chain.size() + " links");         
                out.flush();
                for (int  i = 1; i <= 6 ; i++) {				
                    if (i % 2 == 0) {
                        if (playerName.compareTo("Alice") == 0) { // we are Alice
                            out.println("Alice trying to obtain card # " +
                                Integer.toString(i));                             
                            player.drawCard(i);   // produces 1 link                                                     
                            out.println("drawCard OK"); // 2
                            out.flush();
                            player.processSelfCardDraw(); // produces 1 link
                            out.println("processSelfCardDraw OK"); // 3
                            out.flush();
                            DNCLink link = reader.readDNCLink();
                            chain.appendLink(link);
                            out.println("got processCardDraw0 link");
                            out.flush();
                            player.finishCardDraw();
                            out.println("finishCardDraw OK");                                                    
                            out.println("Card obtained");
                            out.flush();
                        } else {  // we are bob
                            DNCLink link = reader.readDNCLink();
                            chain.appendLink(link);
                            out.println("got drawCard link");
                            out.flush();
                            link = reader.readDNCLink();
                            chain.appendLink(link);
                            out.println("got processSelfCardDraw link");
                            out.flush();
                            player.processCardDrawO();
                            out.println("processCardDraw0 OK");
                            out.flush();
                        }
                    } else {
                        if (playerName.compareTo("Bob") == 0) {
                            out.println("Bob trying to obtain card # "
                                + Integer.toString(i));                             
                            out.flush();
                            player.drawCard(i);
                            out.println("drawCard OK");
                            out.flush();
                            DNCLink link = reader.readDNCLink();
                            chain.appendLink(link);
                            out.println("got processCardDraw link");
                            out.flush();
                            player.processSelfCardDraw();
                            out.println("processSelfCardDraw OK");
                            out.flush();
                            player.finishCardDraw();
                            out.println("Card obtained");
                            out.flush();
                        } else { // we are Alice 
                            DNCLink link = reader.readDNCLink();
                            chain.appendLink(link);
                            out.println("got drawCard link");
                            out.flush();
                            player.processCardDraw();
                            out.println("processCardDraw OK");
                            out.flush();
                            link = reader.readDNCLink();
                            chain.appendLink(link);
                            out.println("got processSelfCardDraw link");
                            out.flush();
                            
                        }                
                    }
                }
                out.println("Revealing encryption key");
                writer.writeBigIntVector(player.revealPrivateKey());
                out.println("Revealing permutation");
                writer.writeByteArray(player.revealPermutation());
                out.println("Revealing permutation matrix");
                PermutationMatrix theMatrix = player.revealMatrix();
                //out.println(theMatrix.toString());
                writer.writeMatrix(theMatrix);
                out.println("Reading other encryption key");
                BigIntVector oKey = reader.readBigIntVector();
                out.println("Reading other permutation");
                byte[] oPerm = reader.readByteArray();
                out.println("Reading other permutation matrix");
                PermutationMatrix oMatrix = reader.readMatrix();
                out.println("validation");
                for (int i = 0; i < chain.size(); i++) {
                    DNCLink link = chain.getLink(i);
                    DataField data = link.getData();
                    String str = data.getPlayerName();
                    str += " " + data.getInfo() + " ";
                    if (data.getAttrib() == null)
                       str += "null";
                    else
                       str += "value";
                    if (data.getInfo().compareTo("z_i") == 0) {
                        str += " " + data.getAttrib().toString();
                    }
                    out.println(str);
                }
                boolean vv = player.validate(oKey, oPerm, oMatrix);
                out.println("validation result");
                out.println(vv);
            } catch (Exception e) {
                out.println("Exception occured: "+e.toString());
                out.flush();
            }
            int[] h1 = player.showCards();
            out.print("  Cards obtained: [");
            int j = 0;
            while (h1[j] != -1) {
                out.print(h1[j]);
                out.print(" ");
                j ++;
            }
            out.println(" ]");
        } catch (Exception ignored) {            
        }     
    }
}

