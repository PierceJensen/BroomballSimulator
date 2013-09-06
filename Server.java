//This class manages all server-side tasks

import java.util.*;

class Server extends TimerTask {

	protected static boolean running = true;
	
	Timer timer;
	
	GameMechanics game;
	
	public double gameTime;
	
	TcpListener clientListen;
	
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
		
		//start client connection listener
		clientListen = new TcpListener();
		
		startLoops();
	}
	
	//start the gameloop
	public void startLoops(){
		timer = new Timer();	
		new Timer().schedule(new TimerTask() {	
			public void run() {
				gameLoop();
				
				if(!running){cancel();}
			}
		}, 0, (long) (1000*period));
	}
	
	public void stop(){
		running = false;
	}
	
	//main gameLoop
	void gameLoop(){
		if(!running){
			clientListen.running = false;
			return;
		}
		
		game.operateEntities(period);
		
		game.sendData(clientListen.streams, gameTime, clientListen.newPlayer);
		clientListen.newPlayer = -1;
		
		gameTime += period;
		
	}

}