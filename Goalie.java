import java.awt.Point;
import java.util.ArrayList;


public class Goalie {
	int walkDirection = 0; //1=up,0=stop,-1=down
	int walkSpeed = 500;
	int turnSpeed = 540;
	
	double x;
	double y;
	double vx = 0;
	double vy;
	double ax = 0;
	double ay;
	double bearing;
	
	double targetY;
	
	double size = 50;
	
	Point rayOrigin;
	
	boolean isBlue;
	boolean holdingBall;
	
	Goalie(boolean team, Point rayStart){
		isBlue = team;
		rayOrigin = rayStart;
		
		if(isBlue){
			this.x = -1000+this.size*2;
			this.y = 3010;
		}else{
			this.x = 7000-this.size*2;
			this.y = 3010;
			this.bearing = 180;
			
		}
	}
	
	public void goalieAI(Entity ball, ArrayList<Entity> playerList, int ballPossessor){
		//draw a ray trace from the center of the back of the goal to the ball, find where the goalie should be
		double x0 = rayOrigin.x;
		double y0 = rayOrigin.y;
		double x1 = 0;
		double y1 = 0;
		if(ballPossessor != -1){
			x1 = playerList.get(ballPossessor).x;
			y1 = playerList.get(ballPossessor).y;
		}else{
			x1 = ball.x;
			y1 = ball.y;
		}
		
		this.targetY = this.x*(y1-y0)/(x1-x0)+(x1*y0-x0*y1)/(x1-x0);
		
		if(this.targetY > this.y){
			this.vy = this.walkSpeed;
		} else if(targetY < this.y) {
			this.vy = -this.walkSpeed;
		} else {
			this.vy = 0;
		}
		
		double theta = Math.toDegrees(Math.atan2(y1-y0,x1-x0));
		
		this.bearing = theta;
		
	}
	
	public void move(double period){//runs physics
		
		this.vx += this.ax*period;
		this.vy += this.ay*period;
		
		this.x += this.vx*period;
		this.y += this.vy*period;
		
		if(Math.abs(this.y - this.targetY) < this.walkSpeed*period){
			this.y = this.targetY;
		}
		
		this.y = Math.min(this.y, 3430-this.size);
		this.y = Math.max(this.y, 2590+this.size);
	}
}
