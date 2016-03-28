package sLSRP;
import java.net.*;
import java.io.*;

class NetUtils {

	static ServerSocket serverSocket() {
		try {
			return new ServerSocket(0);
		} catch (IOException e) {
			System.out.println(e);
		}
		return null;
	}
	
	static SocketBundle acceptClient(ServerSocket socket) {
		SocketBundle client = new SocketBundle();
		try {
			client.socket = socket.accept();
			client.out = new DataOutputStream(client.socket.getOutputStream());
			client.in = new DataInputStream(client.socket.getInputStream());
		} catch(IOException e) {
			System.out.println(e);
		}
		
		return client;
	}
	
	static SocketBundle clientSocket(String host, int port) {
		SocketBundle server = new SocketBundle();
		try {
			server.socket = new Socket(host, port);
			server.out = new DataOutputStream(server.socket.getOutputStream());
			server.in = new DataInputStream(server.socket.getInputStream());
		} catch(IOException e) {
			System.out.println(e);
		}
		return server;
	}

}

class SocketBundle {
	Socket socket;
	DataOutputStream out;
	DataInputStream in;
}