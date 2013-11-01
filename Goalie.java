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
	
	double size = 200;
	
	Point rayOrigin;
	
	boolean isBlue;
	boolean holdingBall;
	boolean isLeft;
	
	static final int blueGoaliePossession = 12;
	static final int redGoaliePossession = 11;
	
	final int PROJECTION_X_LEFT =-800;
	final int PROJECTION_X_RIGHT=6800;
	final int PROJECTION_Y_TOP = 3430;
	final int PROJECTION_Y_BOTTOM = 2590;
	Goalie(boolean team, Point rayStart){
		isBlue = team;
		isLeft = team;
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
	
	public int goalieAI(Entity ball, ArrayList<Entity> playerList, int ballPossessor){
		//draw a ray trace from the center of the back of the goal to the ball, find where the goalie should be
		double x0 = rayOrigin.x;
		double y0 = rayOrigin.y;
		double x1 = 0;
		double y1 = 0;
		double vx = 0;
		double vy = 0;
		double ballBearing = 0;
		
		if(inMyPossession(ballPossessor)){
			this.bearing= 180;
			ballPossessor = -1;
			ball.x = this.x + Math.cos(Math.toRadians(this.bearing))*500;
			ball.y = this.y + Math.sin(Math.toRadians(this.bearing))*500;
			ball.vx = Math.cos(Math.toRadians(this.bearing))*1000;
			ball.vy = Math.sin(Math.toRadians(this.bearing))*1000;
				
			return ballPossessor;
		}
		if(ballPossessor != -1 && !inGoaliePossession(ballPossessor)){
			x1 = playerList.get(ballPossessor).x;
			y1 = playerList.get(ballPossessor).y;
			
			vx = playerList.get(ballPossessor).vx;
			vy = playerList.get(ballPossessor).vy;
			
			ballBearing = Math.toDegrees(Math.atan2(vy,vx));
		}else{
			x1 = ball.x;
			y1 = ball.y;
			
			vx = ball.vx;
			vy =ball.vy;
			
			ballBearing = Math.toDegrees(Math.atan2(vy,vx));
		}
		//Velocity Ray Trace Method
		if(ballBearing<0)
			ballBearing+=180;
		
		double projectedYLeft=    Math.sin(Math.toRadians(ballBearing))*(PROJECTION_X_LEFT-x1)+y1;
		double	projectedYRight=  Math.sin(Math.toRadians(ballBearing))*(PROJECTION_X_RIGHT-x1)+y1;
		
		
	double theta = Math.toDegrees(Math.atan2(y1-y0,x1-x0));
		
		this.bearing = theta;
		
			if(this.isLeft){
				if(projectedYLeft<PROJECTION_Y_TOP&&projectedYLeft>PROJECTION_Y_BOTTOM&&vx<0){
					this.targetY = projectedYLeft;

					if(this.targetY > this.y){
						this.vy = this.walkSpeed;
					} else if(targetY < this.y) {
						this.vy = -this.walkSpeed;
					} else {
						this.vy = 0;
					}
					return ballPossessor;
				}
			}else{
				if(projectedYRight<PROJECTION_Y_TOP&&projectedYRight>PROJECTION_Y_BOTTOM&&vx>0){
					this.targetY = projectedYRight;

					if(this.targetY > this.y){
						this.vy = this.walkSpeed;
					} else if(targetY < this.y) {
						this.vy = -this.walkSpeed;
					} else {
						this.vy = 0;
					}
					return ballPossessor;
				}
			}
		
		//CENTER RAY TRACE METHOD
		this.targetY = this.x*(y1-y0)/(x1-x0)+(x1*y0-x0*y1)/(x1-x0);
		
		if(this.targetY > this.y){
			this.vy = this.walkSpeed;
		} else if(targetY < this.y) {
			this.vy = -this.walkSpeed;
		} else {
			this.vy = 0;
		}
		
		return ballPossessor;
		
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
	
	public void swapSides(){
		this.isLeft=!this.isLeft;
	}
	static public boolean inGoaliePossession(int possessor){
		return possessor==blueGoaliePossession || possessor==redGoaliePossession;
	}
	private boolean inMyPossession(int possessor){
		if(possessor == redGoaliePossession && !this.isBlue){
			return true;
		}else if(possessor == blueGoaliePossession && this.isBlue){
			return true;
		}
		return false;
		
	}
}
