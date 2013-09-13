//This thread handles incoming packets as they arrive, and stores the data for later 
//reference by GameRender

import java.io.IOException;
import java.io.ObjectInputStream;

public class ClientStreamReader implements Runnable {

	public boolean running = true;
	
	private ObjectInputStream inStream;
	
	public double gameTime;
	public int playerNumber;
	
	public int[][] playerArray;
	
	public ClientStreamReader(ObjectInputStream in) {
		inStream = in;
		
		//recieve player number
		try {
			playerNumber = inStream.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//start the thread
		Thread t = new Thread(this, "client stream reader");
		t.start();
	}

	@Override
	public void run() {
		while(running){
			try{
				if(inStream.available() > 0){
					
					gameTime = inStream.readDouble();
					//playerArray = (int[][]) inStream.readObject();
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
