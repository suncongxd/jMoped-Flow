
package test.Julia.mp;

public class MPException extends Exception{
	public MPException() {
		super();
		this.detailMessage = ""; // just empty string, no detailed message
        this.cause = null;
	}
    private final String detailMessage;
    
    private final Throwable cause;
	 
	 public MPException (String s) {
		 super();		 
		 this.detailMessage = s;
         this.cause = null;
	 }

     public MPException (String s, Throwable cause) {
        super();
        this.detailMessage = s;
        this.cause = cause;
     }
     
     public MPException (Throwable cause) {
         super();
         this.detailMessage = "";
         this.cause = cause;
     }
     
	 public String getMessage() {
		 return this.detailMessage;
	 }
	 
	 public String toString() {
		return this.getMessage(); 
	 }
     
     public Throwable getCause() {
         return this.cause;
     }
}



