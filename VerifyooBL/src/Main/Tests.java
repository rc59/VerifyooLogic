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
			
			score = t.TestByName("dalal.roy@gmail.com", "roy-STAM123");			
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