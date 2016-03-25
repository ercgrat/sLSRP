package sLSRP;


public class PacketTask extends Thread {
	//
	Packet packet;
	public PacketTask(Packet packet){
		this.packet = packet;
	}
	@Override
	public void run() {
		//TODO Process the packet and set up socket connection
	}
}
