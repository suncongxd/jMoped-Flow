package test.Julia.mp;

public class Seal {
	private boolean open;

    public Seal() {
	this.open = false;
    }
    public void unseal() {
	this.open = true;
    }
    public boolean isOpen() {
	return open;// declassify (open, {this;L});
    }

	public void assertIntegrity() throws SecurityException {
            if (this.isOpen())
		throw new SecurityException();
	}

}
