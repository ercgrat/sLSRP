package sLSRP;
import java.io.*;
import java.net.*;

public class NameServer {
    public static void main(String[] args) {
        
        RouterData[] entries = new RouterData[100];
        
        // Create the listening socket
        ServerSocket serverSocket = NetUtils.serverSocket();
        try {
            System.out.println("Listening on port " + serverSocket.getLocalPort() + " at IP Address " + IpChecker.getIp() + ".");
		} catch(IOException e) {
            e.printStackTrace();
        }

        // Wait for incoming connections
        while(true) {
            System.out.println("Waiting to accept incoming client...");
            SocketBundle client = NetUtils.acceptClient(serverSocket);
            String ip = client.socket.getInetAddress().toString();
            int port = client.socket.getPort();
            
            int connectionType = -1;
            int routerId = -1;
            try {
                connectionType = client.in.readInt();
                routerId = client.in.readInt();
            
                if(connectionType == 0) { // Register with the name server
                    entries[routerId] = new RouterData(routerId, ip, port);
                } else if(connectionType == 1) { // Request info about a router
                    client.out.writeInt(entries[routerId].toString().length());
                    client.out.writeChars(entries[routerId].toString());
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
            
        }
    }
}