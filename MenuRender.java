//This class renders the main menu for the client. It also operates the UI for the main menu

import java.awt.*;
import java.awt.event.*;


public class MenuRender implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {
	ScreenManager sm;
	
	private final int HOVER_START = 1;
	private final int HOVER_EXIT = 2;
	private final int HOVER_JOINMENU = 3;
	
	private final int MENU_MAIN = 0;
	private final int MENU_JOIN = 1;
	
	int buttonHovered;
	
	int screenWidth;
	int screenHeight;
	
	boolean running;
	int returnValue;
	
	Point mouse;
	
	Font font1;
	Font font2;
	
	String IP;
	
	int displayedMenu;
	
	private void init(){
		mouse = new Point();
		
		Window w = sm.getFullScreenWindow();
		w.setBackground(Color.BLACK);
		w.setForeground(Color.WHITE);
		
		screenHeight = w.getHeight();
		screenWidth = w.getWidth();
		
		int fontSize = (int)Math.round(/*Font Size:*/12.0/**/*screenWidth*.001);
		font1 = new Font("Arial", Font.BOLD, fontSize);
		
		fontSize = (int)Math.round(/*Font Size:*/18.0/**/*screenWidth*.001);
		font2 = new Font("Arial", Font.BOLD, fontSize);
		
		w.addMouseMotionListener(this);
		w.addMouseListener(this);
		w.addMouseWheelListener(this);
		w.addKeyListener(this);
		
		running = true;
	}
	
	public int menu(ScreenManager screenmanager){
		sm = screenmanager;
		
		init();
		
		while(running){
			Graphics2D g = sm.getGraphics();
			
			g.clearRect(0, 0, screenWidth, screenHeight);
			g.setColor(Color.WHITE);
			
			buttonHovered = 0;
			
			if(displayedMenu == 1){
				g.setFont(font2);
				g.drawString("JOIN GAME", (int) (screenWidth - screenHeight*.5), (int) (screenHeight*.17));
				
				g.setFont(font1);
				g.drawString("IP Addresss", (int) (screenHeight*.1), (int) (screenHeight*.20));
				g.drawRect((int) (screenHeight*.1), (int) (screenHeight*.22), (int) (screenHeight*.2), (int) (screenHeight*.04));
			} else if(displayedMenu == 0){
				g.setFont(font1);
				if(mouse.x > screenHeight*.1 && mouse.x < screenHeight*.21 && mouse.y > screenHeight*.18 && mouse.y < screenHeight*.20){
					g.setColor(Color.YELLOW);
					buttonHovered = HOVER_START;
				}
				g.drawString("Start Game", (int) (screenHeight*.1), (int) (screenHeight*.20));
				
				if(mouse.x > screenHeight*.1 && mouse.x < screenHeight*.15 && mouse.y > screenHeight*.26 && mouse.y < screenHeight*.28){
					g.setColor(Color.YELLOW);
					buttonHovered = HOVER_EXIT;
				}else{
					g.setColor(Color.WHITE);
				}
				g.drawString("Exit", (int) (screenHeight*.1), (int) (screenHeight*.28));
				
				if(mouse.x > screenHeight*.1 && mouse.x < screenHeight*.21 && mouse.y > screenHeight*.22 && mouse.y < screenHeight*.24){
					g.setColor(Color.YELLOW);
					buttonHovered = HOVER_JOINMENU;
				}else{
					g.setColor(Color.WHITE);
				}
				g.drawString("Join Game", (int) (screenHeight*.1), (int)(screenHeight*.24));
				
				g.setColor(Color.WHITE);
				g.drawString("Pre-Alpha", (int) (screenWidth - screenHeight*.4), (int) (screenHeight*.20));
				
				g.setFont(font2);
				g.drawString("BROOMBALL SIMULATOR", (int) (screenWidth - screenHeight*.5), (int) (screenHeight*.17));
			}
			
			g.dispose();
			
			//update the screen
			sm.update();
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return returnValue;
	}
	
	
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public void mouseWheelMoved(MouseWheelEvent e) {}
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {
		returnValue = buttonHovered;
		if(returnValue != 0){
			if(returnValue == HOVER_JOINMENU){
				displayedMenu = 1;
			}else{
			running = false;
			}
		}
	}
	public void mouseReleased(MouseEvent arg0) {}
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}
	public void mouseMoved(MouseEvent e) {
		mouse.x = e.getX();
		mouse.y = e.getY();
	}
}
