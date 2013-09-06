//This class listens for incoming client connections for the server

import java.io.*;
import java.net.*;

public class TcpListener implements Runnable{
	
	ServerSocket servSock;
	
	Socket[] clients;
	ObjectOutputStream[] streams;
	
	public int newPlayer;
	
	boolean running = true;

	public TcpListener() {
		clients = new Socket[10];
		streams = new ObjectOutputStream[10];
		
		Thread t = new Thread(this, "tcp listener");
		
		try{
			servSock = new ServerSocket(1337);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		t.start();
	}

	@Override
	public void run() {
		
		try{
			Socket sock = servSock.accept();
			System.out.println("client has connected");
			
			addClientToList(sock);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		while(running){//tcp listener main loop
			for(int i=0;i<clients.length;i++){//check for disconnections
				if(clients[i] == null) continue;
				
				if(!clients[i].isConnected()){
					
					try {
						clients[i].close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					streams[i] = null;
					clients[i] = null;
				}
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
		
		for(int i=0;i<clients.length;i++){//close all connections
			try {
				clients[i].close();
			} catch(Exception e){}
		}
		
		try { //close the server
			servSock.close();
		}catch(Exception e){}
	}
	
	void addClientToList(Socket sock) {
		for(int i=0;i<clients.length;i++){
			if(clients[i] == null){
				clients[i] = sock;
				
				try {
					//set up buffer size
					sock.setSendBufferSize(512);
					
					streams[i] = new ObjectOutputStream(sock.getOutputStream());
					newPlayer = i;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return;
			}
			
			try{
				sock.close();
			} catch (Exception e){
				e.printStackTrace();
			}
			System.out.println("connection refused; max clients reached");
		}
	}
}
