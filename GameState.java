
public class GameState {
	
	static int state;
		static final int GAME_INIT=0;
		static final int GAME_RUN=1;
		static final int GAME_BLUE_GOAL_SCORED=2;
		static final int GAME_RED_GOAL_SCORED=3;
		static final int GAME_PERIOD_OVER =4;
		static final int GAME_OVER =5;
	static double delay;
		static final double GAME_GOAL_DELAY=3;
		static final double GAME_PERIOD_DELAY=3;
	static float time;
	static int period;
	static int periodLength;
	static int numOfPeriods;
	static int blueScore;
	static int redScore;
	static boolean randomPlayerPositions;
	
	public static boolean isGameDelayed(){
		return isGoalScored()||state==GAME_PERIOD_OVER;
	}
	public static boolean isGoalScored(){
		return state==GAME_RED_GOAL_SCORED||state==GAME_BLUE_GOAL_SCORED;
	}
}
