import java.awt.Point;
import java.util.ArrayList;


public class Goalie {
	int walkDirection = 0; //1=up,0=stop,-1=down
	int walkSpeed = 1000;
	int turnSpeed = 540;
	
	double x;
	double y;
	double vx = 0;
	double vy;
	double ax = 0;
	double ay;
	double bearing;
	
	double size = 41;
	
	Point rayOrigin;
	
	boolean isBlue;
	boolean holdingBall;
	
	Goalie(boolean team, Point rayStart){
		isBlue = team;
		rayOrigin = rayStart;
		
		if(isBlue){
			this.x = -1000+this.size;
			this.y = 3010;
		}else{
			this.x = 7000-this.size;
			this.y = 3010;
			this.bearing = 180;
			
		}
	}
	
	public void goalieAI(Entity ball, ArrayList<Entity> playerList, int ballPossessor){
		//draw a ray trace from the center of the back of the goal to the ball, find where the goalie should be
		double x0 = rayOrigin.x;
		double y0 = rayOrigin.y;
		if(ballPossessor != -1){
			double x1 = playerList.get(ballPossessor).x;
			double y1 = playerList.get(ballPossessor).y;
		}else{
			double x1 = ball.x;
			double y1 = ball.y;
		}
		
		this.y = this.x*(ball.y-rayOrigin.y)/(ball.x-rayOrigin.x)+(ball.x*rayOrigin.y-rayOrigin.x*ball.y)/(ball.x-rayOrigin.y);
		
	}
	
	public void move(double period){//runs physics
		
		this.vx += this.ax*period;
		this.vy += this.ay*period;
		
		this.x += this.vx*period;
		this.y += this.vy*period;
	}
}
