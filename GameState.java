
public class GameState {
	
	static int state;
		static final int GAME_INIT=0;
		static final int GAME_RUN=1;
		static final int GAME_GOAL_SCORED=2;
		static final int GAME_PERIOD_OVER =3;
		static final int GAME_OVER =4;
	static double delay;
		static final double GAME_GOAL_DELAY=3;
		static final double GAME_PERIOD_DELAY=3;
	static float time;
	static int period;
	static int periodLength;
	static int numOfPeriods;
	static int blueScore;
	static int redScore;
	
	public static boolean isGameDelayed(){
		return state==GAME_GOAL_SCORED||state==GAME_PERIOD_OVER;
	}
	
}
