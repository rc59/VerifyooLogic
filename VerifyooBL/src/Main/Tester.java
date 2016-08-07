package Main;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import Data.Comparison.Interfaces.ICompareResult;
import Data.UserProfile.Extended.GestureExtended;
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
						
			DBCollection col = db.getCollection("templatedemos");
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
	
	public double CompareTemplatesOld(String name1, String name2)
	{		
		Template template1 = GetFromDB(name1);		
		Template template2 = GetFromDB(name2);
		
		TemplateComparer comparer = new TemplateComparer();			
		
		TemplateExtended templateBase = new TemplateExtended(template1);
		TemplateExtended templateAuth = new TemplateExtended(template2);
		
		HashMap<String, Boolean> hashInstructions = new HashMap<>();
		for(int idxGestureAuth = 0; idxGestureAuth < templateAuth.ListGestureExtended.size(); idxGestureAuth++) {
			if(!hashInstructions.containsKey(templateAuth.ListGestureExtended.get(idxGestureAuth).Instruction)) {
				hashInstructions.put(templateAuth.ListGestureExtended.get(idxGestureAuth).Instruction, true);
			}
		}
				
		ArrayList<GestureExtended> tempListGestures = new ArrayList<>();
		for(int idxGestureAuth = 0; idxGestureAuth < templateBase.ListGestureExtended.size(); idxGestureAuth++) {
			if(hashInstructions.containsKey(templateBase.ListGestureExtended.get(idxGestureAuth).Instruction)) {
				tempListGestures.add(templateBase.ListGestureExtended.get(idxGestureAuth));
				hashInstructions.remove(templateBase.ListGestureExtended.get(idxGestureAuth).Instruction);
			}			
		}
		templateBase.ListGestureExtended = tempListGestures;
		
		Collections.sort(templateBase.ListGestureExtended, new Comparator<GestureExtended>() {
            @Override
            public int compare(GestureExtended gesture1, GestureExtended gesture2) {
                if (gesture1.Instruction.compareTo(gesture2.Instruction) > 0) {
                    return 1;
                }
                if (gesture1.Instruction.compareTo(gesture2.Instruction) < 0) {
                    return -1;
                }
                return 0;
            }
        });
		
		Collections.sort(templateAuth.ListGestureExtended, new Comparator<GestureExtended>() {
            @Override
            public int compare(GestureExtended gesture1, GestureExtended gesture2) {
                if (gesture1.Instruction.compareTo(gesture2.Instruction) > 0) {
                    return 1;
                }
                if (gesture1.Instruction.compareTo(gesture2.Instruction) < 0) {
                    return -1;
                }
                return 0;
            }
        });
		
		comparer.CompareTemplates(templateBase, templateAuth);
		
		ArrayList<Double> listScores = new ArrayList<>();
		for(int idxGestureScore = 0; idxGestureScore < comparer.GetGestureComparers().size(); idxGestureScore++) {
			listScores.add(comparer.GetGestureComparers().get(idxGestureScore).GetResultsSummary().Score);
		}
		
		boolean result = true;
		double score = GetFinalScore(listScores);
		if(score < 0.9) {
			result = false;
		}		
		
		return 0;
	}
	
	public double CompareTemplates(String name1, String name2)
	{		
		Template template1 = GetFromDB(name1);		
		Template template2 = GetFromDB(name2);
		
		TemplateComparer comparer = new TemplateComparer();			
		
		TemplateExtended templateBase = new TemplateExtended(template1);
		TemplateExtended templateAuth = new TemplateExtended(template2);
		
		comparer.CompareTemplates(templateBase, templateAuth);
		double score = comparer.GetScore();
		boolean result = true;
		//double score = GetFinalScore(listScores);
		if(score < 0.9) {
			result = false;
		}		
		
		return score;
	}
	
	private double GetFinalScore(ArrayList<Double> mListScores) {
        Collections.sort(mListScores);

        double scores = 0;
        double weights = 0;
        double tempWeight;
        double finalScore = 0;
        if (mListScores.get(0) > 0) {           
        	mListScores.remove(0);
            scores = mListScores.get(0) * 1 + mListScores.get(1) * 1 + mListScores.get(2) * 1;
            finalScore = scores / 3;
        }

        return finalScore;
    }
}
