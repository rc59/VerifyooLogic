package Main;

import Data.UserProfile.Extended.TemplateExtended;
import Data.UserProfile.Raw.Template;
import Logic.Comparison.TemplateComparer;
import flexjson.JSONDeserializer;

public class Tester {
	public double Test(String strTemplate1, String strTemplate2)
	{
		JSONDeserializer<Template> deserializer = new JSONDeserializer<Template>();
		double score = 0;
		String msg;
		try
		{
			Template template1 = deserializer.deserialize(strTemplate1);		
			Template template2 = deserializer.deserialize(strTemplate2);
			
			TemplateComparer comparer = new TemplateComparer();
			
			for(int idx = 41; idx >= 21; idx--)
			{
				template1.ListGestures.remove(idx);
			}
			
			for(int idx = 20; idx >= 0; idx--)
			{
				template2.ListGestures.remove(idx);
			}
			
			TemplateExtended templateExtended1 = new TemplateExtended(template1);
			TemplateExtended templateExtended2 = new TemplateExtended(template2);
			
			comparer.CompareTemplates(templateExtended1, templateExtended2);
			
			boolean result = true;
			score = comparer.GetScore();
			if(score < 0.9) {
				result = false;
			}
		}
		catch(Exception exc) {
			msg = exc.getMessage();
		}		
		
		return score;
	}
}
