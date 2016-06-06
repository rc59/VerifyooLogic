package Main;

import Data.Gestures.GestureStore;

public class Tests {			

//	private static void RunTests() 
//	{
//		String msg;
//		double score = 0;
//		try
//		{
//			Tester t = new Tester();		
//			
////			t.TestFalseNegativeAndPositive("roy_VS_rafi1");
////			t.TestFalseNegativeAndPositive("rafi_VS_roy1");
//			t.TestByName("roy-STAM123", "nitzan@effectivy.net");
//			//score = t.TestSelf("roy-STAM123", "dalal.roy@gmail.com");			
//			//score = t.TestSelf("roy-STAM2", "roy-STAM2");
//			score = t.TestSelf("dalal.roy@gmail.com", "dalal.roy@gmail.com");
//		}
//		catch(Exception exc) {
//			msg = exc.getMessage();
//		}	
//		
//		boolean success = false;
//		if(score > 0.9) {
//			success = true;
//		}
//	}	
//	
//	public static void main(String[] args) {
//		RunTests();
//	}

	private static void RunTests() 
	{
		String msg;
		double score = 0;
		try
		{
			Tester t = new Tester();		
			
			//score = t.TestSelf("roy-STAM123", "dalal.roy@gmail.com");			
			//score = t.TestSelf("roy-STAM2", "roy-STAM2");
			score = t.TestSelf("rafich1959@gmail.com", "rafich1959@gmail.com");
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