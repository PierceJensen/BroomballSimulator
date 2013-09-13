import java.io.ObjectInputStream;


public class ServerDataListener implements Runnable {

	ObjectInputStream streams[];
	
	public boolean running = true;
	
	int mouseX[];
	int mouseY[];
	
	boolean keyArray[][];
	
	ServerDataListener(){
		Thread t = new Thread(this, "data listener");
		streams = new ObjectInputStream[10];
		
		mouseX = new int[10];
		mouseY = new int[10];
		keyArray = new boolean[10][10];
		
		t.start();
	}
	
	public void run() {
		while(running){
			for(int i=0;i<10;i++){
				if(streams[i] == null) continue;
				try{
					if(streams[i].available() <= 0) continue;
					
					 mouseX[i] = streams[i].readInt();
					 mouseY[i] = streams[i].readInt();
					 keyArray[i] = (boolean[]) streams[i].readObject();
					 
					 for(int j=0;j<10;j++){
						if(keyArray[i][j])System.out.println(j);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
			}
		}
		
		for(int i=0;i<10;i++){
			try{
				if(streams[i] == null) continue;
				
				streams[i].close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
