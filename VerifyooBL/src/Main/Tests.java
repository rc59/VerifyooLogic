package Main;

import Data.Gestures.GestureStore;
import Data.UserProfile.Raw.Template;
import Logic.Comparison.TemplateComparer;
import flexjson.JSONDeserializer;

public class Tests {
//	private static void RunTests() 
//	{
//		Test1();
//	}
//	
//	public static void Test1()
//	{
//		String strTemplate1 = GestureStore.Test1;
//		String strTemplate2 = GestureStore.Test2;
//		
//		JSONDeserializer<Template> deserializer = new JSONDeserializer<Template>();
//		
//		String msg;
//		try
//		{
//			Template template1 = deserializer.deserialize(strTemplate1);		
//			Template template2 = deserializer.deserialize(strTemplate2);
//			
//			TemplateComparer comparer = new TemplateComparer();
//			comparer.CompareTemplates(template1, template2);
//			
//			boolean result = true;
//			double score = comparer.GetScore();
//			if(score < 0.9) {
//				result = false;
//			}
//		}
//		catch(Exception exc) {
//			msg = exc.getMessage();
//		}			
//	}
//	
//	public static void main(String[] args) {
//		RunTests();
//	}
}
