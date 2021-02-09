
package test.Julia.mp;

import java.io.PrintStream;
//import jif.runtime.Runtime;
//import jif.util.ArrayList;
import java.util.ArrayList;

class Main {
    public static final void main(String[] args) throws SecurityException, IllegalArgumentException {
        Main.m = new Main();
        if (args!= null && args.length > 0)
            try {
                new Communicator().play();//args[0]);
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
//        else
 //           test("invalid usage\n");
    }
    
//    public final principal {} p;
    public static Main m;
//    private Main (principal {} p ) {
//        this.p    = p;
//    }
    /*
    public static void test{} (String{} s) throws (SecurityException)
	where authority (Alice,Bob) {
		Main m = Main.m;
		if (m==null) return;
		final principal {} pp = m.p;

		Runtime[pp] runtime = Runtime[pp].getRuntime(pp);
		
		if (runtime == null) return;
		PrintStream [{}] output = runtime.stdout(new label{});
		if (output == null) return;
		if (s == null) return;
		output.print (s);		
	}*/
}
