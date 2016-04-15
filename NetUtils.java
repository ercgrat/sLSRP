package sLSRP;
import java.net.*;
import java.io.*;

class NetUtils {
    
    static final int CRC32_GEN = 0x04c11db7;
    static final int CRC32_MSB = 0x80000000;
    static final int EOF = -1;

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
    
    static long getChecksum(BufferedReader br) throws IOException {
        long checksum = 0;
        int c, i, overflow;
        
        while(br.ready()) {
            c = br.read();
            if(c == EOF) {
                break;
            }
            for(i = 0x80; i != 0; i >>= 1) {
                overflow = (int)(checksum & CRC32_MSB);
                checksum = (checksum << 1) | ((i & c) != 0 ? 1 : 0);
                if(overflow != 0) {
                    checksum ^= CRC32_GEN;
                }
            }
        }
        
        return checksum;
    }

}

class SocketBundle {
	Socket socket;
	DataOutputStream out;
	DataInputStream in;
}