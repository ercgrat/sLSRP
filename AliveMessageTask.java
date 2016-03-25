
public class AliveMessageTask extends Thread {
	int interval;
	public AliveMessageTask(int interval){
		this.interval = interval;
	}
	@Override
	public void run() {
		
		while(true){
			//TODO Periodically set up socket connection to send alive messages
			try {
				Thread.sleep(interval*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
