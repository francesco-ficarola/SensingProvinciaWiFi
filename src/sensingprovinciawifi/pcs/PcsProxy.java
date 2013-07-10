package sensingprovinciawifi.pcs;

public class PcsProxy implements Runnable {
	
	public PcsProxy() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		
	}

}
