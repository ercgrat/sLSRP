
public class AliveMessageTask extends Thread {
	int interval;
	boolean isMessageDeamon = false;
	public AliveMessageTask(int interval, boolean isMessageDeamon){
		this.interval = interval;
		this.isMessageDeamon = isMessageDeamon;
	}
	@Override
	public void run() {
		if(this.isMessageDeamon){
			while(true){
				//TODO Periodically set up socket connection to send alive messages
				try {
					Thread.sleep(interval*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}else{
			//TODO Send an ACK back to the original sender.
		}
		
	}
}
