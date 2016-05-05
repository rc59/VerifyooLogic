package VerifyooLogic;

import java.util.ArrayList;

import VerifyooLogic.Comparison.TemplateComparer;
import VerifyooLogic.DataObjects.ResultObj;
import VerifyooLogic.UserProfile.Template;
import VerifyooLogic.Utils.UtilsGestures;
import flexjson.JSONDeserializer;

public class LogicMain {	
	public static void CheckTemplates(String strTemplate1, String strTemplate2) {	
		JSONDeserializer<Template> deserializer = new JSONDeserializer<Template>();
		
		Template template1 = deserializer.deserialize(strTemplate1);		
		Template template2 = deserializer.deserialize(strTemplate2);
		
		TemplateComparer comparer = new TemplateComparer();
		double score = comparer.Compare(template1, template2);
		
		boolean result = true;
		if(score < 0.9) {
			result = false;
		}
	}	
	
	public static void main(String[] args) {
		CheckTemplates(UtilsGestures.Roy.Test1, UtilsGestures.Roy.Test2);		
	}
}