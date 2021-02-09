
package test.Julia.mp;

import java.util.ArrayList;

public class DNCChain{
	private final ArrayList chain;
	private int k = 0;
    private SerializeWriteHelper writer;
    public DNCChain(SerializeWriteHelper writer) throws IllegalArgumentException {
        if (writer == null)             
            throw new IllegalArgumentException();
	chain = new ArrayList();
        this.writer = writer;
	}

	public void addLink(DNCLink link) {
        try {
            this.chain.add(link);
            this.writer.writeDNCLink(link);
        } catch (NullPointerException e) {
        } catch (SecurityException e) {
        }
	}

    public void appendLink(DNCLink link) {
        try {
            this.chain.add(link);
        } catch (NullPointerException e) {
        }
	}
	
	public DNCLink getLink(int i) throws IndexOutOfBoundsException{    
            try {
                return (DNCLink) chain.get(i);
            } catch (NullPointerException e) {
            } catch (ClassCastException e) {
            }
            return null;
	}

     public ArrayList getChainVector() {
		return this.chain;
	}

    public int size() {
        ArrayList chain = this.chain;
        return chain == null? 0: chain.size();
    }
}























