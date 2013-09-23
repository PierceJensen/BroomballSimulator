//This class is the standard game thing type object, known as entities. This class
//operates every function that is not affected by other entities. It is also used
//to define initial constants for each entity via each different init function
//antoerh test comment
import java.util.Random;

public class Entity extends GameMechanics{
	
	double x;//position
	double y;//--------
	double preX = 0;
	double preY = 0;
	double vx;//velocity
	double vy;//--------
	double ax;//acceleration
	double ay;//------------
	double linFriction; //linear friction
	double maxlinSpeed;//maximum linear speed
	
	double walkForce;
	double sideWalkForce;
	
	double bearing;//radial position
	double radv; //radial velocity
	double rada; //radial acceleration
	double radFriction;//radial friction
	double maxRadSpeed;//maximum radial speed
	
	double mass;
	double radInertia; //rotational inertia
	
	double scaleOffset = 1;
	
	double size;
	
	int type;//playertype
	int walkDirection;
	int sideWalkDirection;
	
	boolean walking = false;
	boolean sideWalking = false;
	
	Random generator;
	
	private final double BALL_ELASTICITY=1;
	
	Entity(){
		generator = new Random();
	}

	//ship and structure initialization
	public void playerInit(){
		this.linFriction = 500;
		this.radFriction = 100;
		this.size = 200;
		this.type = 0;
		this.maxRadSpeed = 100;
		this.maxlinSpeed = 1000;
		this.mass = 5;
		this.radInertia = 5;
		this.walkForce = 5000;
		this.sideWalkForce = 5000;
	}
	
	public void ballInit(){
		this.linFriction = 500;
		this.radFriction = 0;
		this.size = 41;
		this.type = 1;
		this.maxRadSpeed = 300;
		this.maxlinSpeed = 10000;
		this.mass = 5;
		this.radInertia = 5;
	}
	
	public boolean move(double p){
		period = p;
		
		preX = this.x;
		preY = this.y;
		
		this.bearing += this.radv*period; //add radial velocity to bearing
		
		if(this.bearing >= 360){
			this.bearing -= 360;
		}else if(this.bearing < 0){
			this.bearing += 360;
		}
		
		if(sqr(this.vx) + sqr(this.vy) < sqr(this.maxlinSpeed)){
			//forward motion
			if(this.walking){
				this.ax += this.applyForceX(this.walkDirection*this.walkForce*period, this.bearing);
				this.ay += this.applyForceY(this.walkDirection*this.walkForce*period, this.bearing);
			}
			
			//strafing
			if(this.sideWalking){
				this.ax += this.applyForceX(this.sideWalkDirection*this.sideWalkForce*period, this.bearing + 90);
				this.ay += this.applyForceY(this.sideWalkDirection*this.sideWalkForce*period, this.bearing + 90);
			}
		}
		
		double theta = Math.toDegrees(Math.atan2(this.vy,this.vx));
		
		if ((this.vx != 0 || this.vy != 0) && this.linFriction != 0){ //friction
			
			//X friction
			if(sign(this.vx - this.linFriction*cos((int)theta)*period) == sign(this.vx) || this.ax != 0){
				this.ax -= this.linFriction*cos((int)theta)*period;
			}else{
				this.vx = 0;
			}
			
			//Y friction
			if(sign(this.vy - this.linFriction*sin((int)theta)*period) == sign(this.vy) || this.ay != 0){
				this.ay -= this.linFriction*sin((int)theta)*period;
			}else{
				this.vy = 0;
			}
		}
		
		this.vx += this.ax;
		this.vy += this.ay;
		
		theta = Math.toDegrees(Math.atan2(this.vy,this.vx));
		
		this.ax = 0;
		this.ay = 0;
		
		
		double finalx = this.x + this.vx*period;
		double finaly = this.y + this.vy*period;
		this.x = finalx;
		this.y = finaly;
		

		//map bound handlers
		final int leftBound = -1000;
		final int topBound = 5720;
		final int bottomBound = 280;
		final int rightBound = 7000;
		final int GOAL_TOP= 3430;
		final int GOAL_BOTTOM= 2590;
		
		if(this.type == 0){//player
			if(this.x < leftBound + this.size){ //left-right map bound stopper
				this.x = leftBound + this.size;
				this.vx = 0;
			}else if(this.x > rightBound - this.size){
				this.x = rightBound - this.size;
				this.vx = 0;
			}
			
			if(this.y > topBound - this.size){ //top-bottom map bound stopper
				this.y = topBound - this.size;
				this.vy = 0;
			}else if(this.y < bottomBound + this.size){
				this.y = bottomBound + this.size;
				this.vy = 0;
			}
			//CORNER CONDITIONS
			final double CORNER_POSITION_OFFSET_MULTIPLIER = .25*.7071;//NEEDS *.7071 FOR PROPER OPERATION
double vMag =  Math.sqrt(this.vx*this.vx+this.vy*this.vy)*.7071;
			
			if(this.y<(-this.x)+100+(.7071*this.size))//Bottom Left
			{
				if(this.vy<=-this.vx)
				{
					this.vx= -vMag*Math.cos(Math.toRadians(45-this.bearing));
					this.vy= -vMag*Math.cos(Math.toRadians(45-this.bearing));
				}else{
					this.vx= vMag*Math.cos(Math.toRadians(45-this.bearing));
					this.vy= vMag*Math.cos(Math.toRadians(45-this.bearing));
					
				}
				this.x+=CORNER_POSITION_OFFSET_MULTIPLIER*this.size;
				this.y+=CORNER_POSITION_OFFSET_MULTIPLIER*this.size;
			}
			if(this.y>-(this.x)+11900+(.7071*this.size))//Top Right
			{
				
			}
			
			if(this.y>(this.x)+5900-(.7071*this.size))//Top Left
			{
				if(this.vy>=-this.vx)
				{
					this.vx= -vMag*Math.cos(Math.toRadians(45-this.bearing));
					this.vy= -vMag*Math.cos(Math.toRadians(45-this.bearing));
				}else{
					this.vx= vMag*Math.cos(Math.toRadians(45-this.bearing));
					this.vy= vMag*Math.cos(Math.toRadians(45-this.bearing));
					
				}
				this.x+=CORNER_POSITION_OFFSET_MULTIPLIER*this.size;
				this.y-=CORNER_POSITION_OFFSET_MULTIPLIER*this.size;
			}
			if( this.y<(this.x)-5900+(.7071*this.size))//Bottom Right
			{
				if(this.vy>=-this.vx)
				{
					this.vx= -vMag*Math.cos(Math.toRadians(45-this.bearing));
					this.vy= -vMag*Math.cos(Math.toRadians(45-this.bearing));
				}else{
					this.vx= vMag*Math.cos(Math.toRadians(45-this.bearing));
					this.vy= vMag*Math.cos(Math.toRadians(45-this.bearing));
					
				}
				this.x-=CORNER_POSITION_OFFSET_MULTIPLIER*this.size;
				this.y+=CORNER_POSITION_OFFSET_MULTIPLIER*this.size;
			}
			
		}
		//3430 2590
		if(this.type == 1){//ball
			if(this.x < leftBound + this.size){ //left-right map bound stopper
				if(this.y < GOAL_TOP-this.size && this.y > GOAL_BOTTOM+this.size){//if it's in a goal
					if(goalPosition == BLUE_GOAL_RIGHT){
						redScore += 1;
					}else{
						blueScore += 1;
					}
					this.x = 3000;
					this.y = 2750;
					this.vx = 0;
					this.vy = 0;
				} else {//if it's not in the goal
					this.x = leftBound + this.size;
					this.vx *= -.75;
				}
			}else if(this.x > rightBound - this.size){
				if(this.y < GOAL_TOP-this.size && this.y > GOAL_BOTTOM+this.size){
					if(goalPosition == BLUE_GOAL_RIGHT){
						blueScore += 1;
					} else {
						redScore += 1;
					}
					this.x = 3000;
					this.y = 2750;
					this.vx = 0;
					this.vy = 0;
				} else {
					this.x = rightBound - this.size;
					this.vx *= -BALL_ELASTICITY;
				}
			}
			
			if(this.y > topBound - this.size){ //top-bottom map bound stopper
				this.y = topBound - this.size;
				this.vy *= -1;
			}else if(this.y < bottomBound + this.size){
				this.y = bottomBound + this.size;
				this.vy *= -1;
			}
			//CORNER CONDITIONS
			final double CORNER_POSITION_OFFSET_MULTIPLIER = 2*.7071;//NEEDS *.7071 FOR PROPER OPERATION
			if(this.y<(-this.x)+100+(.7071*this.size))//Bottom Left
			{
				double temp= -(this.vx*BALL_ELASTICITY);
				this.vx=-(this.vy*BALL_ELASTICITY);
				this.vy=temp;
				
				this.x+=CORNER_POSITION_OFFSET_MULTIPLIER*this.size;
				this.y+=CORNER_POSITION_OFFSET_MULTIPLIER*this.size;
			}
			if(this.y>-(this.x)+11900+(.7071*this.size))//Top Right
			{
				double temp= -(this.vx*BALL_ELASTICITY);
				this.vx=-(this.vy*BALL_ELASTICITY);
				this.vy=temp;
				this.x-=CORNER_POSITION_OFFSET_MULTIPLIER*this.size;
				this.y-=CORNER_POSITION_OFFSET_MULTIPLIER*this.size;
			}
			
			if(this.y>(this.x)+5900+(.7071*this.size))//Top Left
			{
				double temp= (this.vx*BALL_ELASTICITY);
				this.vx=(this.vy*BALL_ELASTICITY);
				this.vy=temp;
				this.x+=CORNER_POSITION_OFFSET_MULTIPLIER*this.size;
				this.y-=CORNER_POSITION_OFFSET_MULTIPLIER*this.size;
			}
			if( this.y<(this.x)-5900+(.7071*this.size))//Bottom Right
			{
				double temp= (this.vx*BALL_ELASTICITY);
				this.vx=(this.vy*BALL_ELASTICITY);
				this.vy=temp;
				this.x-=CORNER_POSITION_OFFSET_MULTIPLIER*this.size;
				this.y+=CORNER_POSITION_OFFSET_MULTIPLIER*this.size;
			}
			
		}
		
		return false;
	}//end move void
	
	public double applyForceX(double force, double angle){
		
		return (force*cos((int)angle))/this.mass;
		
	}
	
	public double applyForceY(double force, double angle){
		
		return (force*sin((int)angle))/this.mass;
		
	}
	
	//custom sign method
	public double sign(double x){
		if (x > 0){
			return 1;
		}else if (x < 0){
			return -1;
		}else{
			return 0;
		}
	}
}

