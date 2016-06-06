package Main;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import Data.UserProfile.Extended.TemplateExtended;
import Data.UserProfile.Raw.Gesture;
import Data.UserProfile.Raw.MotionEventCompact;
import Data.UserProfile.Raw.Stroke;
import Data.UserProfile.Raw.Template;
import Logic.Comparison.TemplateComparer;
import flexjson.JSONDeserializer;

public class Tester {	
	public Template GetFromDB(String userName)
	{
		Template template = new Template();
		MongoClient mongo;
		try {
			mongo = new MongoClient("localhost", 27017);
			DB db = mongo.getDB("extserver-dev");
						
			DBCollection col = db.getCollection("templates");
			DBObject query = BasicDBObjectBuilder.start().add("Name", userName).get();
	        DBCursor cursor = col.find(query);
	        
	        DBObject mongoTemplate;
	        DBObject tempGesture;
	        DBObject tempStroke;
	        DBObject tempEvent;
	        
	        
	        Gesture gesture;
	        Stroke stroke;
	        MotionEventCompact event;
	        
	        BasicDBList  listGestures;
	        BasicDBList  listStrokes;
	        BasicDBList  listEvents;
	        
	        double xdpi, ydpi;
	        
	        while(cursor.hasNext()){
	        	mongoTemplate = cursor.next();	   	    	        
	        	
	        	template.ModelName = (String) mongoTemplate.get("ModelName");
	        	template.Name = (String) mongoTemplate.get("Name");
	        	template.ListGestures = new ArrayList<>();	    
	        	
	        	xdpi = ConvertToDouble(mongoTemplate.get("Xdpi"));
	        	ydpi = ConvertToDouble(mongoTemplate.get("Ydpi"));
	        	
	        	listGestures = (BasicDBList) mongoTemplate.get("ExpShapeList");
	        	
	        	for(int idxGesture = 0; idxGesture < listGestures.size(); idxGesture++) {
	        		gesture = new Gesture();
	        		
	        		tempGesture = (DBObject) listGestures.get(idxGesture);
	        		
	        		gesture.Instruction = (String) tempGesture.get("Instruction");
	        		gesture.ListStrokes = new ArrayList<>();	        				
	        		
	        		listStrokes = (BasicDBList) tempGesture.get("Strokes"); 
	        			        		
	        		for(int idxStroke = 0; idxStroke < listStrokes.size(); idxStroke++) {
	        			stroke = new Stroke();
	        			tempStroke = (DBObject) listStrokes.get(idxStroke);
	        			
	        			stroke.Length = ConvertToDouble(tempStroke.get("Length"));
	        			stroke.ListEvents = new ArrayList<>();
	        			listEvents = (BasicDBList) tempStroke.get("ListEvents"); 	        			
	        			
	        			stroke.Xdpi = xdpi;
	        			stroke.Ydpi = ydpi;
	        			
	        			for(int idxEvent = 0; idxEvent < listEvents.size(); idxEvent++) {
	        				event = new MotionEventCompact();
	        				tempEvent = (DBObject) listEvents.get(idxEvent);
	        				
	        				event.AccelerometerX = ConvertToDouble(tempEvent.get("AngleX"));
	        				event.AccelerometerY = ConvertToDouble(tempEvent.get("AngleY"));
	        				event.AccelerometerZ = ConvertToDouble(tempEvent.get("AngleZ"));
	        				event.EventTime = ConvertToDouble(tempEvent.get("EventTime"));
	        				event.TouchSurface = ConvertToDouble(tempEvent.get("TouchSurface"));
	        				event.Pressure = ConvertToDouble(tempEvent.get("Pressure"));
	        				event.VelocityX = ConvertToDouble(tempEvent.get("VelocityX"));
	        				event.VelocityY = ConvertToDouble(tempEvent.get("VelocityY"));
	        				event.Xpixel = ConvertToDouble(tempEvent.get("X"));
	        				event.Ypixel = ConvertToDouble(tempEvent.get("Y"));
	        				
	        				stroke.ListEvents.add(event);
	        			}
	        			
	        			gesture.ListStrokes.add(stroke);
	        		}
	        		
	        		template.ListGestures.add(gesture);
	        	}
	        	
	        }	
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return template;
	}
	
	protected double ConvertToDouble(Object value) {
		return Double.valueOf(value.toString());
	}
	
	public double TestSelfSimilar(Template template1, Template template2)
	{
		TemplateComparer comparer = new TemplateComparer();
		
		TemplateExtended templateBase = new TemplateExtended(template1);
		TemplateExtended templateAuth = new TemplateExtended(template2);
		
		comparer.CompareTemplates(templateBase, templateAuth);
		
		boolean result = true;
		double score = comparer.GetScore();
		if(score < 0.9) {
			result = false;
		}

		return score;
	}
	
	public double TestSelf(String name1, String name2)
	{
		Template template1 = GetFromDB(name1);		
		Template template2 = GetFromDB(name2);
		TemplateComparer comparer = new TemplateComparer();
		
		for(int idx = 41; idx >= 21; idx--)
		{
			template1.ListGestures.remove(idx);
		}
		
		for(int idx = 20; idx >= 0; idx--)
		{
			template2.ListGestures.remove(idx);
		}
					
		TemplateExtended baseTemplate = new TemplateExtended(template1);
		TemplateExtended authTemplate = new TemplateExtended(template2);
		
		comparer.CompareTemplates(baseTemplate, authTemplate);
		
		boolean result = true;
		double score = comparer.GetScore();
		if(score < 0.9) {
			result = false;
		}

		return score;
	}
	
	public double TestByName(String name1, String name2)
	{		
		Template template1 = GetFromDB(name1);		
		Template template2 = GetFromDB(name2);
		
		TemplateComparer comparer = new TemplateComparer();			
		
		TemplateExtended templateBase = new TemplateExtended(template1);
		TemplateExtended templateAuth = new TemplateExtended(template2);
		
		comparer.CompareTemplates(templateBase, templateAuth);
		
		boolean result = true;
		double score = comparer.GetScore();
		if(score < 0.9) {
			result = false;
		}
			
		
		return score;
	}
}
