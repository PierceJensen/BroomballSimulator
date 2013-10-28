//This class manages all server-side tasks

import java.util.*;

class Server extends TimerTask {

	protected static boolean running = true;
	
	Timer timer;
	
	GameMechanics game;
	
	TcpListener clientListen;
	ServerDataListener dataListen;
	
	////////////////////////////////////
	//     OPERATE PERIOD VARIABLE    //
	//	This variable sets the        //
	// period of *mechanical          //
	// operations* in seconds         //
	////////////////////////////////////
	public double period = .02;
	////////////////////////////////////
	
	public void run() {
		running = true;
		game = new GameMechanics();
		game.init();
		
		//start client data listener
		dataListen = new ServerDataListener();
		
		//start client connection listener
		clientListen = new TcpListener(dataListen);
		
		startLoops();
	}
	
	//start the gameloop
	public void startLoops(){
		timer = new Timer();	
		new Timer().schedule(new TimerTask() {	
			public void run() {
				
				gameLoop();
				
				if(!running){
					clientListen.running = false;
					clientListen.t.interrupt();
					dataListen.running = false;
					
					System.out.println("Server Exit");
					
					timer.cancel();
					timer.purge();
				}
			}
		}, 0, (long) (1000*period));
	}
	
	public void stop(){
		running = false;
	}
	
	//main gameLoop
	void gameLoop(){
		if(!running){
			return;
		}
		
		game.mouseX = dataListen.mouseX;
		game.mouseY = dataListen.mouseY;
		game.keyArray = dataListen.keyArray;
		
		game.operateEntities(period);
		
		game.sendData(clientListen.streams, GameState.time);
		
	}

}