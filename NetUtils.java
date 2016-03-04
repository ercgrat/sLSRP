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
			client.out = new PrintWriter(client.socket.getOutputStream(), true);
			client.in = new BufferedReader(new InputStreamReader(client.socket.getInputStream()));
		} catch(IOException e) {
			System.out.println(e);
		}
		
		return client;
	}
	
	static SocketBundle clientSocket(String host, int port) {
		SocketBundle server = new SocketBundle();
		try {
			server.socket = new Socket(host, port);
			server.out = new PrintWriter(server.socket.getOutputStream(), true);
			server.in = new BufferedReader(new InputStreamReader(server.socket.getInputStream()));
		} catch(IOException e) {
			System.out.println(e);
		}
		return server;
	}

}

class SocketBundle {
	Socket socket;
	PrintWriter out;
	BufferedReader in;
}