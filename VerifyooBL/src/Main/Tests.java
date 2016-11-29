package Main;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import Data.Gestures.GestureStore;
import Logic.Comparison.Stats.Norms.NormContainerMgr;
import Logic.Comparison.Stats.Norms.NormMgr;
import flexjson.JSONDeserializer;

public class Tests {			
	private static void RunTests() 
	{
		String msg;
		double score = 0;
		
		int fn = 0;
		
		try
		{	
			Tester t = new Tester();		
			
			double minScore = 0.80;
			String name;
			
//			Test
//			"57d6950e2f0e497411a0222c"
//			"57d695382f0e497411a025c1"
//
//			"57d6950e2f0e497411a02367"
//			"57d695382f0e497411a0244a"
									
			
			
			score = t.CompareTemplatesById("57d691c62f0e497411a00751", "57d691d62f0e497411a00e5a");
//			score = t.CompareTemplatesById("582895119a59ecac0f36d2e4", "5828956a9a59ecac0f36ea8b");
			
			
//			t.GetNorms("5832fb309a59ecac0f3b2e5a");
			
//			score = t.CompareTemplates("Roeeyshaked@gmail.com", "Roeeyshaked@gmail.com", 35);
//			score = t.CompareTemplates("Roeeyshaked@gmail.com", "abuchnick@yahoo.com", 35);
//			score = t.CompareTemplates("Roeeyshaked@gmail.com", "abuchnick@yahoo.com", 37);
//			score = t.CompareTemplates("Roeeyshaked@gmail.com", "abuchnick@yahoo.com", 38);
//			score = t.CompareTemplates("Roeeyshaked@gmail.com", "abuchnick@yahoo.com", 39);
			
			
//			for(int idx = 1; idx <= 5; idx++) {
//				name = "Roy2508-Auth" + Integer.toString(idx);
//				score = t.CompareTemplates("Roy2408-Reg", name);
//			}
//			for(int idx = 1; idx <= 10; idx++) {
//				name = "Roy2408-Hack" + Integer.toString(idx);
//				score = t.CompareTemplates("Roy2408-Reg", name);
//			}
//			score = t.CompareTemplates("Roy2408-Reg", "Roy2508-AuthHigh");
//			score = t.CompareTemplates("Roy2408-Reg", "Roy2508-AuthLow");
			
			if(score < minScore) {
				fn++;
			}
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