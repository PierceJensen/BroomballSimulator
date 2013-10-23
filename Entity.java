//This class is the standard game thing type object, known as entities. This class
//to define initial constants for each entity via each different init function
import java.util.Random;

public class Entity extends GameMechanics{
	static final int PLAYER_SIZE= 200;
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
	
	final int[] cornerIntercept = {11900,5920,80,-5920};
	
	boolean walking = false;
	boolean sideWalking = false;
	
	Random generator;
	
	static final double BALL_ELASTICITY=.4;
	
	Entity(){
		generator = new Random();
	}

	//ship and structure initialization
	public void playerInit(){
		this.linFriction = 250;
		this.radFriction = 100;
		this.size = PLAYER_SIZE;
		this.type = 0;
		this.maxRadSpeed = 100;
		this.maxlinSpeed = 1000;
		this.mass = 100;
		this.radInertia = 5;
		this.walkForce = 100000;
		this.sideWalkForce = 100000;
	}
	
	public void ballInit(){
		this.linFriction = 1000;
		this.radFriction = 0;
		this.size = 41;
		this.type = 1;
		this.maxRadSpeed = 300;
		this.maxlinSpeed = 10000;
		this.mass = 1;
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
			if(this.walking && !this.sideWalking){
				this.ax += this.applyForceX(this.walkDirection*this.walkForce*period, this.bearing);
				this.ay += this.applyForceY(this.walkDirection*this.walkForce*period, this.bearing);
			} else if(this.sideWalking && !this.walking){//strafing
				this.ax += this.applyForceX(this.sideWalkDirection*this.sideWalkForce*period, this.bearing + 90);
				this.ay += this.applyForceY(this.sideWalkDirection*this.sideWalkForce*period, this.bearing + 90);
			} else if(this.sideWalking && this.walking){
				this.ax += this.applyForceX((this.sideWalkForce+this.walkForce)*.5*period, 
						this.bearing*this.walkDirection + 45*this.sideWalkDirection);
				this.ay += this.applyForceY((this.sideWalkForce+this.walkForce)*.5*period, 
						this.bearing*this.walkDirection + 45*this.sideWalkDirection);
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
		final int GOAL_LEFT = -1440;
		final int GOAL_RIGHT = 7420;
		
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
			for(int i=1;i<5;i++){
				if(corner[i-1].contains(this.x+this.size*cos((i)*90 - 45), this.y+this.size*sin((i)*90 - 45))){
					
					double vuDot = cos(90*i - 45)*this.vx+sin(90*i - 45)*this.vy;
					
					this.vx -= cos(90*i - 45)*vuDot;
					this.vy -= sin(90*i - 45)*vuDot;
					
					//calculate slopes
					double entSlope = Math.pow(-1, i+1);
					double cornerSlope = -1*entSlope;
					
					//define y-intercepts
					double entB = this.y - this.x*entSlope;
					double cornerB = cornerIntercept[i-1];
					
					//find the point of interception
					double interceptX = (cornerB - entB)/(entSlope - cornerSlope);
					double interceptY = entSlope*interceptX + entB;
					
					//calculate penetration amount
					double penetration = this.size - Math.sqrt(sqr(this.x-interceptX)+sqr(this.y-interceptY));
					
					//shift the entity's position
					this.x -= cos(90*i - 45)*penetration;
					this.y -= sin(90*i - 45)*penetration;
					
					break;
				}
			}
		}
		//3430 2590
		if(this.type == 1){//ball
			if(this.y+this.size>GOAL_BOTTOM && this.y-this.size<GOAL_TOP)
			{
				//Top Goal Bound
				if(this.y+this.size>GOAL_TOP && (this.x < leftBound ||this.x > rightBound)){
					this.y = GOAL_TOP - this.size;
					this.vy *= -BALL_ELASTICITY;
					this.vx *= BALL_ELASTICITY;
				}else if(this.y-this.size<GOAL_BOTTOM && (this.x < leftBound ||this.x > rightBound)){ //Bottom Goal Bound
					this.y = GOAL_BOTTOM + this.size;
					this.vy *= -BALL_ELASTICITY;
					this.vx *= BALL_ELASTICITY;
				}else if(this.x-this.size<GOAL_LEFT){ //Left Goal Bound
					this.x = GOAL_LEFT + this.size;
					this.vx *= -BALL_ELASTICITY;
					this.vy *= BALL_ELASTICITY;
				}else if(this.x+this.size>GOAL_RIGHT){ //Right Goal Bound
					this.x = GOAL_RIGHT - this.size;
					this.vx *= -BALL_ELASTICITY;
					this.vy *= BALL_ELASTICITY;
				}
			
			}else{			
				if(this.x < leftBound + this.size){ //left-right map bound stopper
						this.x = leftBound + this.size;
						this.vx *= -BALL_ELASTICITY;
						this.vy *= BALL_ELASTICITY;
				}else if(this.x > rightBound - this.size){ //RIGHT SIDE
						this.x = rightBound - this.size;
						this.vx *= -BALL_ELASTICITY;
						this.vy *= BALL_ELASTICITY;
				}
				
				if(this.y > topBound - this.size){ //top-bottom map bound stopper
					this.y = topBound - this.size;
					this.vy *= -BALL_ELASTICITY;
					this.vx *= BALL_ELASTICITY;
				}else if(this.y < bottomBound + this.size){ // BOTTOM
					this.y = bottomBound + this.size;
					this.vy *= -BALL_ELASTICITY;
					this.vx *= BALL_ELASTICITY;
				}
			}
			
			
			//corner conditions
			for(int i=1;i<5;i++){
				if(corner[i-1].contains(this.x+this.size*cos((i)*90 - 45), this.y+this.size*sin((i)*90 - 45))){
					
					double temp = this.vx*BALL_ELASTICITY*Math.pow(-1,i);
					this.vx = this.vy*BALL_ELASTICITY*Math.pow(-1,i);
					this.vy = temp;
					
					//calculate slopes
					double entSlope = Math.pow(-1, i+1);
					double cornerSlope = -1*entSlope;
					
					//define y-intercepts
					double entB = this.y - this.x*entSlope;
					double cornerB = cornerIntercept[i-1];
					
					//find the point of interception
					double interceptX = (cornerB - entB)/(entSlope - cornerSlope);
					double interceptY = entSlope*interceptX + entB;
					
					//calculate penetration amount
					double penetration = this.size - Math.sqrt(sqr(this.x-interceptX)+sqr(this.y-interceptY));
					
					//shift the entity's position
					this.x -= cos(90*i - 45)*penetration*2+abs(this.vx)*sign(cos(90*i - 45))*period*2;
					this.y -= sin(90*i - 45)*penetration*2+abs(this.vy)*sign(sin(90*i - 45))*period*2;
					
					break;
				}
			}
			//CORNER CONDITIONS
		/*	final double CORNER_POSITION_OFFSET_MULTIPLIER = 2*.7071;//NEEDS *.7071 FOR PROPER OPERATION
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
			*/
			
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

