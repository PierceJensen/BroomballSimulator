//This class renders the game for the client using data acquired from ClientStreamRender.
//It also operates in-game UI
//Added 
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;


public class GameRender implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {
	ScreenManager sm;
	
	int screenWidth;
	int screenHeight;
	double mapheight;
	double mapwidth;
	
	double aspectRatio;
	
	int faction = 1;
	
	int playerNumber;
	
	private Font font1;
	private Font font2;
	private Font font3;
	
	boolean debugInfo;
	boolean mouseClick = false;
	
	boolean running;
	
	int[] keyArray;
	
	private static int trigScale = 1;//accuracy of trigonometry tables, in entries per degree
	public static float[] sin;
	public static float[] cos;
	public static float[] tan;
	
	double magnification = 1;
	double gameTime = 0;
	double screenXFactor = 1;
	double screenYFactor = 1;
	
	Point mouse;
	Point center;
	Point screenloc;
	Point finalPos;
	Point mouseWorld;
	
	BufferedImage fieldImage;
	BufferedImage bluePlayer;
	BufferedImage redPlayer;
	BufferedImage blueActivePlayer;
	BufferedImage redActivePlayer;
	
	Robot robot;
	
	Random generator;
	
	int[] playership;
	int[] displayedUnit;
	
	int[][] playerArray;
	
	ClientStreamReader recieveDataHandler;
	ObjectOutputStream sender;

	//CLASS OUTLINE//
	public void gameRender(ScreenManager screenmanager, ClientStreamReader reciever, ObjectOutputStream sendStream) {
		
		sm = screenmanager;
		recieveDataHandler = reciever;
		sender = sendStream;
		
		init();
		
		while(running){
			Graphics2D g = sm.getGraphics();
			
			try{
				networkTransmit();
			}catch(Exception e){
				System.err.println("network transmission");
				e.printStackTrace();
			}
			
			try{
				renderThings(g);
			}catch(Exception e){
				System.err.println("render things");
				e.printStackTrace();
			}
			
			try{
				renderUI(g);
			}catch(Exception e){
				System.err.println("render ui");
				e.printStackTrace();
			}
			
			g.dispose();
			
			try{
				sendData();
			}catch(Exception e){
				System.err.println("send commands");
				e.printStackTrace();
			}
			
			//update the screen
			sm.update();

			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void init(){
		running = true;
		
		mapheight= 6000;
		mapwidth = 6000;
		debugInfo = false;
		
		double toRadian = Math.PI/(180*trigScale);
		
		sin = new float[(90*trigScale) + 1];
		for(int i=0;i<sin.length;i++){
			sin[i] = ((float)Math.sin(((double)i) * toRadian));
		}
		
		//cosine
		cos = new float[(90*trigScale) + 1];
		for(int i=0;i<cos.length;i++){
			cos[i] = ((float)Math.cos(((double)i) * toRadian));
		}
		
		//tangent
		tan = new float[(90*trigScale) + 1];
		for(int i=0;i<tan.length;i++){
			tan[i] = sin[i]/cos[i];
		}
		
		playerNumber = 1;
		
		try{
			robot = new Robot();
		}catch(Exception e){
			System.err.println("robot init");
			System.err.println(e);
		}
		
		finalPos = new Point();
		mouse = new Point();
		mouseWorld = new Point();
		screenloc = new Point();
		center = new Point();
		
		Window w = sm.getFullScreenWindow();
		w.setBackground(Color.BLACK);
		w.setForeground(Color.WHITE);
		
		screenHeight = w.getHeight();
		screenWidth = w.getWidth();
		aspectRatio = screenWidth/(double) (screenHeight);
		
		screenXFactor = screenWidth/(double) (mapwidth);
		screenYFactor = screenHeight/(double) (mapheight);
		
		/* initializing font styles */
		int fontSize = (int)Math.round(/*Font Size:*/12.0/**/*screenWidth*.001);
		font1 = new Font("Arial", Font.BOLD, fontSize);
		
		fontSize = (int)Math.round(/*Font Size:*/6.0/**/*screenWidth*.001);
		font2 = new Font("Arial", Font.BOLD, fontSize);
		
		fontSize = (int)Math.round(/*Font Size:*/8.0/**/*screenWidth*.001);
		font3 = new Font("Arial", Font.BOLD, fontSize);

		center.x = sm.getWidth() / 2;
		center.y = sm.getHeight() / 2;
		
		mouse.x = center.x;
		mouse.y = center.y;
		
		w.addMouseMotionListener(this);
		w.addMouseListener(this);
		w.addMouseWheelListener(this);
		w.addKeyListener(this);
		
		generator = new Random();
		
		generateField();
		
		keyArray = new int[10];
		
		playerNumber = recieveDataHandler.playerNumber;
		
	}
	
	private void networkTransmit() throws IOException{
		//retrieve arrays
		playerArray = recieveDataHandler.playerArray;
	}
	
	private void renderThings(Graphics2D g){
		g.clearRect(0,0,screenWidth,screenHeight);
		
		g.drawImage(fieldImage, 0, 0, null);
		
		//entity draw loop
		for(int i=0; i<playerArray.length; i++){
			
			int[] entity = playerArray[i];
			
			AffineTransform xform = new AffineTransform();
			Image image = imageIdToImage((int) (entity[3] + Math.floor(i/5)));
			
			xform.translate(worldXToScreen(entity[0]), worldYToScreen(entity[1]));
			xform.scale(400/(255*magnification), 400/(255*magnification));
			xform.rotate(Math.toRadians(360 - entity[2]));
			xform.translate(-(image.getWidth(null))/2, -(image.getHeight(null))/2);
			
			//draws the entity
			g.drawImage(image, xform, null);
			g.setColor(Color.RED);
			g.drawLine(worldXToScreen(entity[0]), worldYToScreen(entity[1]), (int)worldXToScreen(entity[0] + cos(entity[2])*300), (int)worldYToScreen(entity[1] + sin(entity[2])*300));
		}
		
		g.setColor(Color.RED);
		g.drawString(mouseWorld.x + ", " + mouseWorld.y,  worldXToScreen(mouseWorld.x), worldYToScreen(mouseWorld.y));
	}//end operate entities
	
	private void renderUI(Graphics2D g){
		g.setFont(font1);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
	}
	
	private void sendData(){
		try {
			sender.writeInt(mouseWorld.x);
			sender.writeInt(mouseWorld.y);
			sender.writeObject(keyArray);
			
			sender.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//reposition the mouse using robot, scroll the screen if possible
	private synchronized void containMouse(int x, int y){
		Window w = sm.getFullScreenWindow();
		if(robot != null && w.isShowing()){
			finalPos.x = x;
			finalPos.y = y;
			
			SwingUtilities.convertPointToScreen(finalPos, w);

			robot.mouseMove(x, y);
		}
	}
	
	private void generateField(){
		BufferedImage protoImage = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = protoImage.createGraphics();
		
		g.setColor(Color.WHITE);
		
		//The base measurement
		double fieldWidth = 8000;
		double fieldHeight = fieldWidth*0.68;
		
		
		double leftEdgeX = (mapwidth - fieldWidth)/2;
		double topEdgeY = (mapheight + fieldHeight)/2;
		
		//MAIN RECTS
		//base field rect
		g.fillRect(worldXToScreen(leftEdgeX), worldYToScreen(topEdgeY), (int)(fieldWidth*screenYFactor), (int)(fieldHeight*screenYFactor));
		
		//corners
		//top right
		g.setColor(Color.BLACK);
		Polygon poly = new Polygon();
		
		poly.xpoints = new int[]{worldXToScreen(leftEdgeX), worldXToScreen(fieldWidth*.1 + leftEdgeX), worldXToScreen(leftEdgeX)};
		poly.ypoints = new int[]{worldYToScreen(topEdgeY), worldYToScreen(topEdgeY), worldYToScreen(topEdgeY - fieldHeight*.15)};
		poly.npoints = 3;
		
		g.fillPolygon(poly);
		
		//bottom left
		poly.xpoints = new int[]{worldXToScreen(leftEdgeX), worldXToScreen(leftEdgeX), worldXToScreen(leftEdgeX + fieldWidth*.1)};
		poly.ypoints = new int[]{worldYToScreen(topEdgeY - fieldHeight*.85), worldYToScreen(topEdgeY - fieldHeight), worldYToScreen(topEdgeY - fieldHeight)};
		
		g.fillPolygon(poly);
		
		//bottom right
		poly.xpoints = new int[]{worldXToScreen(leftEdgeX + fieldWidth), worldXToScreen(leftEdgeX + fieldWidth), worldXToScreen(leftEdgeX + fieldWidth*.9)};
		poly.ypoints = new int[]{worldYToScreen(topEdgeY - fieldHeight*.85), worldYToScreen(topEdgeY - fieldHeight), worldYToScreen(topEdgeY - fieldHeight)};
		
		g.fillPolygon(poly);
		
		//top right
		poly.xpoints = new int[]{worldXToScreen(leftEdgeX + fieldWidth), worldXToScreen(leftEdgeX + fieldWidth), worldXToScreen(leftEdgeX + fieldWidth*.9)};
		poly.ypoints = new int[]{worldYToScreen(topEdgeY - fieldHeight*.15), worldYToScreen(topEdgeY), worldYToScreen(topEdgeY)};
		
		g.fillPolygon(poly);
		
		//goals
		//left goal
		g.setColor(Color.WHITE);
		
		double goalDepth = fieldWidth*.053;
		double goalHeight = goalDepth*2;
		
		g.fillRect(worldXToScreen(leftEdgeX - goalDepth), worldYToScreen(topEdgeY-fieldHeight/2+goalHeight/2), (int)(goalDepth*screenYFactor), (int)(goalHeight*screenYFactor));
		
		//right goal
		g.fillRect(worldXToScreen(leftEdgeX+fieldWidth), worldYToScreen(topEdgeY-fieldHeight/2+goalHeight/2), (int)(goalDepth*screenYFactor), (int)(goalHeight*screenYFactor));
		
		//LINES
		//centerline
		double lineThickness = screenHeight*.04;
		g.setColor(Color.RED);
		
		g.fillRect(worldXToScreen(leftEdgeX + fieldWidth/2 - lineThickness/2), worldYToScreen(topEdgeY), (int)(lineThickness*screenYFactor), (int)(fieldHeight*screenYFactor));
		
		//left goal arc
		double circleRadius = fieldWidth*.25;
		
		g.fillArc(worldXToScreen(leftEdgeX - circleRadius/2), 
				worldYToScreen(topEdgeY - fieldHeight/2 + circleRadius/2), 
				(int)(circleRadius*screenYFactor), 
				(int)(circleRadius*screenYFactor), 270, 180);
		g.setColor(Color.WHITE);
		g.fillArc(worldXToScreen(leftEdgeX - circleRadius/2 + lineThickness/2), 
				worldYToScreen(topEdgeY - fieldHeight/2 - lineThickness/2 + circleRadius/2), 
				(int)((circleRadius - lineThickness*.75)*screenYFactor), 
				(int)((circleRadius - lineThickness*.75)*screenYFactor), 270, 180);
		
		//right goal arc
		g.setColor(Color.RED);
		g.fillArc(worldXToScreen(leftEdgeX + fieldWidth - circleRadius/2), 
				worldYToScreen(topEdgeY - fieldHeight/2 + circleRadius/2), 
				(int)(circleRadius*screenYFactor), 
				(int)(circleRadius*screenYFactor), 90, 180);
		g.setColor(Color.WHITE);
		g.fillArc(worldXToScreen(leftEdgeX + fieldWidth - circleRadius/2 + lineThickness/2), 
				worldYToScreen(topEdgeY - fieldHeight/2 - lineThickness/2 + circleRadius/2), 
				(int)((circleRadius - lineThickness*.75)*screenYFactor), 
				(int)((circleRadius - lineThickness*.75)*screenYFactor), 90, 180);
		
		//left goal box
		double goalBoxHeight = fieldWidth*.1328;
		double goalBoxWidth = fieldWidth*.066;
		g.setColor(Color.BLUE);
		g.fillRect(worldXToScreen(leftEdgeX), worldYToScreen(topEdgeY - fieldHeight/2 + goalBoxHeight/2), 
				(int)(goalBoxWidth*screenYFactor), (int)(goalBoxHeight*screenYFactor));
		g.setColor(Color.WHITE);
		g.fillRect(worldXToScreen(leftEdgeX), worldYToScreen(topEdgeY - fieldHeight/2 + goalBoxHeight/2 - lineThickness*1.25), 
				(int)(goalBoxWidth*screenYFactor - lineThickness/4), (int)(goalBoxHeight*screenYFactor - lineThickness/2));
		
		//right goal box
		g.setColor(Color.BLUE);
		g.fillRect(worldXToScreen(leftEdgeX + fieldWidth - goalBoxWidth), worldYToScreen(topEdgeY - fieldHeight/2 + goalBoxHeight/2), 
				(int)(goalBoxWidth*screenYFactor), (int)(goalBoxHeight*screenYFactor));
		g.setColor(Color.WHITE);
		g.fillRect(worldXToScreen(leftEdgeX + fieldWidth + lineThickness*1.5 - goalBoxWidth), worldYToScreen(topEdgeY - fieldHeight/2 + goalBoxHeight/2 - lineThickness*1.25), 
				(int)(goalBoxWidth*screenYFactor - lineThickness/4), (int)(goalBoxHeight*screenYFactor - lineThickness/2));
		
		//left goal line
		g.setColor(Color.BLUE);
		g.fillRect(worldXToScreen(leftEdgeX - lineThickness), worldYToScreen(topEdgeY - fieldHeight/2 + goalHeight/2), (int)(lineThickness*screenYFactor), (int)(goalHeight*screenYFactor));
		
		g.fillRect(worldXToScreen(leftEdgeX + fieldWidth), worldYToScreen(topEdgeY - fieldHeight/2 + goalHeight/2), (int)(lineThickness*screenYFactor), (int)(goalHeight*screenYFactor));
		
		fieldImage = protoImage;
		
		final int PlayerSizeX=50;
		final int PlayerSizeY=25;
		final int ActivePlayerBorder=5;
		final int BallDiameter=5;
		
		BufferedImage BluePlayerTemp = new BufferedImage(PlayerSizeX, PlayerSizeY, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D BPT = BluePlayerTemp.createGraphics();
	
		BPT.setColor(Color.BLUE);
		
		BPT.fillRect(0, 0, PlayerSizeX, PlayerSizeY);
		bluePlayer = BluePlayerTemp;
			
		BufferedImage BluePlayerActiveTemp = new BufferedImage(PlayerSizeX, PlayerSizeY, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D BPAT = BluePlayerActiveTemp.createGraphics();
	
		BPAT.setColor(Color.WHITE);
		
		BPAT.fillRect(0, 0, PlayerSizeX, PlayerSizeY);
		
		BPAT.setColor(Color.BLUE);
		
		BPAT.fillRect(ActivePlayerBorder, ActivePlayerBorder, PlayerSizeX-2*ActivePlayerBorder, PlayerSizeY-2*ActivePlayerBorder);
		
		blueActivePlayer = BluePlayerActiveTemp;
		
		BufferedImage RluePlayerTemp = new BufferedImage(PlayerSizeX, PlayerSizeY, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D RPT = RluePlayerTemp.createGraphics();
	
		RPT.setColor(Color.RED);
		
		RPT.fillRect(0, 0, PlayerSizeX, PlayerSizeY);
		redPlayer = RluePlayerTemp;
			
		BufferedImage RedPlayerActiveTemp = new BufferedImage(PlayerSizeX, PlayerSizeY, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D RPAT = RedPlayerActiveTemp.createGraphics();
	
		RPAT.setColor(Color.WHITE);
		
		RPAT.fillRect(0, 0, PlayerSizeX, PlayerSizeY);
		
		RPAT.setColor(Color.RED);
		
		RPAT.fillRect(ActivePlayerBorder, ActivePlayerBorder, PlayerSizeX-2*ActivePlayerBorder, PlayerSizeY-2*ActivePlayerBorder);
		
		redActivePlayer = RedPlayerActiveTemp;
	}
	
	//translates a world coordinate into a screen coordinate with magnification factored in
	int worldXToScreen(double worldX){
		return (int) ((screenWidth - screenHeight)*.5 + (worldX/mapwidth)*screenHeight);
	}
	int worldYToScreen(double worldY){
		return (int) ((1 - worldY/mapheight)*screenHeight);
	}
	
	double screenXToWorld(double screenX){
		return ((screenX - (screenWidth - screenHeight)/2)*(mapwidth/screenHeight));
	}
	double screenYToWorld(double screenY){
		return (1 - screenY/screenHeight)*mapheight;
	}
	
	/*custom trigonometry functions*/
	//cosine
	public double cos(int a){
		//normalizing
		if(a>360){
			a %= 360;
		}else if(a<0){
			a %= 360;
			a += 360;
		}
		
		//return
		if(a <= 90){
			return cos[a];
		}else if(a <= 180){
			return -cos[180 - a];
		}else if(a <= 270){
			return -cos[a - 180];
		}else{
			return cos[360 - a];
		}
	}
	
	//sine
	public double sin(int a){
		//normalizing
		if(a>360){
			a %= 360;
		}else if(a<0){
			a %= 360;
			a += 360;
		}
		
		//return
		if(a <= 90){
			return sin[a];
		}else if(a <= 180){
			return sin[180 - a];
		}else if(a <= 270){
			return -sin[a - 180];
		}else{
			return -sin[360 - a];
		}
	}
	
	//tan
	public double tan(int a){
		//normalizing
		if(a>360){
			a %= 360;
		}else if(a<0){
			a += 360;
		}
		
		//return
		if(a <= 90){
			return tan[a];
		}else if(a <= 180){
			return -tan[a - 90];
		}else if(a <= 270){
			return tan[a - 180];
		}else{
			return -sin[a - 270];
		}
	}
	
	//custom mathematical square function
	double sqr(double i){
		return i*i;
	}
	
	//mouse listener methods
	public void mouseDragged(MouseEvent e){
	}

	public synchronized void mouseMoved(MouseEvent e){
		mouse.x = e.getX();
		mouse.y = e.getY();
		
		mouseWorld.x = (int) screenXToWorld(mouse.x);
		mouseWorld.y = (int) screenYToWorld(mouse.y);
	}
	
public void mousePressed(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)){
			keyArray[0] = 1;
		} else if (SwingUtilities.isRightMouseButton(e)){
			keyArray[1] = 1;
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)){
			keyArray[0] = 0;
		} else if (SwingUtilities.isRightMouseButton(e)){
			keyArray[1] = 0;
		}
	}
	
	public void mouseEntered(MouseEvent e) {}
	
	public void mouseClicked(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseWheelMoved(MouseWheelEvent e) {
	
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_ESCAPE ://escape key ends the script
			running = false;
			break;
		case KeyEvent.VK_W :
			keyArray[2] = 1;
			break;
		case KeyEvent.VK_A :
			keyArray[3] = 1;
			break;
		case KeyEvent.VK_S :
			keyArray[4] = 1;
			break;
		case KeyEvent.VK_D :
			keyArray[5] = 1;
			break;
		case KeyEvent.VK_SHIFT :
			keyArray[6] = 1;
			break;
		case KeyEvent.VK_CONTROL :
			keyArray[7] = 1;
			break;
		case KeyEvent.VK_ALT :
			keyArray[8] = 1;
			break;
		case KeyEvent.VK_SPACE :
			keyArray[9] = 1;
			break;
		default :
			break;
		}
	}
	
	public void keyReleased(KeyEvent e){
		
		switch(e.getKeyCode()){
		case KeyEvent.VK_W :
			keyArray[2] = 0;
			break;
		case KeyEvent.VK_A :
			keyArray[3] = 0;
			break;
		case KeyEvent.VK_S :
			keyArray[4] = 0;
			break;
		case KeyEvent.VK_D :
			keyArray[5] = 0;
			break;
		case KeyEvent.VK_SHIFT :
			keyArray[6] = 0;
			break;
		case KeyEvent.VK_CONTROL :
			keyArray[7] = 0;
			break;
		case KeyEvent.VK_ALT :
			keyArray[8] = 0;
			break;
		case KeyEvent.VK_SPACE :
			keyArray[9] = 0;
			break;
		default :
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	Image imageIdToImage(int id){
		switch(id + faction){
		case 1:
			return bluePlayer;
		case 2:
			return redPlayer;
		default:
			System.out.println("returned null from imageIdToImage!");
			return null;
		}
	}
}