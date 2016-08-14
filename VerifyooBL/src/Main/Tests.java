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
						
			score = t.CompareTemplates("Roy0308", "Roy0308-Auth1");
			score = t.CompareTemplates("Roy0308", "Roy0308-Auth2");	
			score = t.CompareTemplates("Roy0308", "Roy0308-Auth3");	
			score = t.CompareTemplates("Roy0308", "Roy0308-Auth4");	
			score = t.CompareTemplates("Roy0308", "Roy0308-Rafi1");			
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