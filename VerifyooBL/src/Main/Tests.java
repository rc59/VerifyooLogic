package Main;

import Data.Gestures.GestureStore;

public class Tests {			
	private static void RunTests() 
	{
		String msg;
		double score = 0;
		try
		{
			Tester t = new Tester();		
			
			//score = t.TestSelf("roy-STAM123", "dalal.roy@gmail.com");			
			//score = t.TestSelf("roy-STAM2", "roy-STAM2");
			score = t.TestSelf("stamStartPoint", "stamStartPoint");
		}
		catch(Exception exc) {
			msg = exc.getMessage();
		}	
		
		boolean success = false;
		if(score > 0.9) {
			success = true;
		}
	}	
	
	public static void main(String[] args) {
		RunTests();
	}
}