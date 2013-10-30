//This class manages the client-side of the game Client stateflow is managed by the while->switch loop.
//It is also capable of starting a new server instance.
//This is another test comment
import java.awt.DisplayMode;
import java.io.*;
import java.net.*;


public class Client extends Thread {
	public static void main(String[] args){
		new Client().init();
		
		System.out.println("Client exit");
	}
	ScreenManager sm;
	
	Server gameServer;
	
	GameRender gameRender;
	
	ClientStreamReader clientReader;
	ObjectOutputStream clientSender;
	
	private int errorCode;
	private final int JOIN_UNKNOWNHOST = 1; 
	
	Socket sock;
	
	private static DisplayMode modes[] = {
		new DisplayMode(1920,1080,32,0),
		new DisplayMode(1920,1080,24,0),
		new DisplayMode(1920,1080,16,0),
		new DisplayMode(1600,900,32,0),
		new DisplayMode(1600,900,24,0),
		new DisplayMode(1600,900,16,0),
		new DisplayMode(1200,675,32,0),
		new DisplayMode(1200,675,24,0),
		new DisplayMode(1200,675,16,0),
		new DisplayMode(800,450,32,0),
		new DisplayMode(800,450,24,0),
		new DisplayMode(800,450,16,0),
		/*new DisplayMode(1366,768,32,0),
		new DisplayMode(1366,768,24,0),
		new DisplayMode(1366,768,16,0),
		new DisplayMode(1280,1024,32,0),
		new DisplayMode(1280,1024,24,0),
		new DisplayMode(1280,1024,16,0),
		new DisplayMode(1024,768,32,0),
		new DisplayMode(1024,768,24,0),
		new DisplayMode(1024,768,16,0),
		new DisplayMode(800,600,32,0),
		new DisplayMode(800,600,24,0),
		new DisplayMode(800,600,16,0),
		new DisplayMode(640,480,32,0),
		new DisplayMode(640,480,24,0),
		new DisplayMode(640,480,16,0),*/
	};
	
	private void init(){
		sm = new ScreenManager();
		DisplayMode dm = sm.findFirstCompatibleMode(modes);
		sm.setFullScreen(dm);
		
		clientLoop();
	}
	
	private void clientLoop(){
		boolean breakLoop = false;
		MenuRender mainMenu = new MenuRender();
		while(true){
			//menuRender is the next state, and returns which thing is clicked on
			int nextState = mainMenu.menu(sm, errorCode);
			switch (nextState){
			case 1 ://new game
				startGame();
				break;
			case 2 ://exit
				breakLoop = true;
				break;
			case 5 ://join game
				joinGame(mainMenu);
			default :
				break;
			}
			
			if(breakLoop){
				break;
			}
		}

		sm.restoreScreen();
	}
	
	private void startGame(){
		//starts the mechanics iterator
		gameServer = new Server();
		gameServer.run();
		
		ObjectInputStream objInStream = null;
		try{
			//wait for the server to settle
			Thread.sleep(1000);
			
			//initialize tcp connection
			sock = new Socket("localhost", 13337);
			System.out.println(sock.getInetAddress());
			//set receive buffer size
			sock.setReceiveBufferSize(512);
			
			objInStream = new ObjectInputStream(sock.getInputStream());
			
			clientReader = new ClientStreamReader(objInStream);
			clientSender = new ObjectOutputStream(sock.getOutputStream());
			
			
			//renders game data
			gameRender = new GameRender();
			gameRender.gameRender(sm, clientReader, clientSender);//This is the render game loop!
			
			//END GAME CODE STARTS NOW//
			
			//end the reader
			clientReader.running = false;
			
			//game close down code
			sock.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
		//end server
		gameServer.serverStop();
		
	}
	
	private void joinGame(MenuRender menu){
		ObjectInputStream objInStream = null;
		try{
			//initialize tcp connection
			sock = new Socket(menu.IP, 13337);
			//set receive buffer size
			sock.setReceiveBufferSize(512);
			
			objInStream = new ObjectInputStream(sock.getInputStream());
			
			clientReader = new ClientStreamReader(objInStream);
			clientSender = new ObjectOutputStream(sock.getOutputStream());
			
			
			//renders game data
			gameRender = new GameRender();
			gameRender.gameRender(sm, clientReader, clientSender);//This is the render game loop!
			
			//END GAME CODE STARTS NOW//
			
			//end the reader
			clientReader.running = false;
			
			//game close down code
			sock.close();
		} catch(Exception e){
			if(e instanceof UnknownHostException){
				errorCode = JOIN_UNKNOWNHOST;
			}else{
				e.printStackTrace();
			}
		}
	}
}