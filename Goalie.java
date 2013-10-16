import java.awt.Point;
import java.util.ArrayList;


public class Goalie {
	int walkDirection = 0; //1=up,0=stop,-1=down
	int walkSpeed = 1000;
	int turnSpeed = 540;
	
	int x;
	int y;
	int vx = 0;
	int vy;
	int bearing;
	
	double size = 41;
	
	Point rayOrigin;
	
	boolean isBlue;
	boolean holdingBall;
	
	Goalie(boolean team, Point rayStart){
		isBlue = team;
		rayOrigin = rayStart;
		
		if(isBlue){
			
		}else{
			
		}
	}
	
	public void goalieAI(Entity ball, ArrayList<Entity> playerList){
		//draw a ray trace from the center of the back of the goal to the ball, find where the goalie should be
		
	}
}
