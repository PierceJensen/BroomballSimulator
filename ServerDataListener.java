import java.io.ObjectInputStream;


public class ServerDataListener implements Runnable {

	ObjectInputStream streams[];
	
	public boolean running = true;
	
	ServerDataListener(){
		Thread t = new Thread(this, "data listener");
		streams = new ObjectInputStream[10];
		
		t.start();
	}
	
	public void run() {
		while(running){
			for(int i=0;i<10;i++){
				if(streams[i] == null) continue;
				try{
					if(streams[i].available() <= 0) continue;
					System.out.println("recieved data");
					
					int header = streams[i].read();
					
					System.out.println(header);
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
