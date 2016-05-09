package Data.UserProfile.Extended;

import java.util.ArrayList;

import Consts.ConstsParamNames;
import Data.UserProfile.Raw.Gesture;
import Data.UserProfile.Raw.Stroke;
import Logic.Comparison.Stats.StatEngine;
import Logic.Comparison.Stats.Interfaces.IStatEngine;

public class GestureExtended extends Gesture {
	public double GestureLength;
	public double GestureTotalTimeWithPauses;
	public double GestureTotalTimeWithoutPauses;
	public double GestureAverageVelocity;
	
	public ArrayList<StrokeExtended> ListStrokesExtended;
	
	protected IStatEngine mStatEngine;
	
	public GestureExtended(Gesture gesture) {		
		Instruction = gesture.Instruction;		
		ListStrokes = gesture.ListStrokes;
		
		InitParams();
		PreCalculations();	
		InitFeatures();
	}

	private void InitParams() {
		ListStrokesExtended = new ArrayList<>();
		
		mStatEngine = StatEngine.GetInstance();
		
		GestureLength = 0;
		GestureTotalTimeWithPauses = 0;
		GestureTotalTimeWithoutPauses = 0;
	}	

	private void PreCalculations() {
		Stroke tempStroke;		
		StrokeExtended tempStrokeExtended;
		
		for(int idxStroke = 0; idxStroke < ListStrokes.size(); idxStroke++) {
			tempStroke = ListStrokes.get(idxStroke);				
			tempStrokeExtended = new StrokeExtended(tempStroke, Instruction, idxStroke);
			
			ListStrokesExtended.add(tempStrokeExtended);
			GestureLength += tempStrokeExtended.Length;
			GestureTotalTimeWithoutPauses += tempStrokeExtended.StrokeTimeInterval;			
		}		
	}
	
	private void InitFeatures() {
		
		mStatEngine.AddGestureValue(Instruction, ConstsParamNames.Gesture.LENGTH, GestureLength);		
		mStatEngine.AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITHOUT_PAUSES, GestureTotalTimeWithoutPauses);
		
		CalculateGestureTotalTimeWithPauses();
		CalculateGestureAvgVelocity();
	}

	private void CalculateGestureTotalTimeWithPauses() {
		StrokeExtended strokeFirst = ListStrokesExtended.get(0);
		StrokeExtended strokeLast = ListStrokesExtended.get(ListStrokesExtended.size() - 1);
		
		double gestureStartTime = strokeFirst.ListEventsExtended.get(0).EventTime;
		double gestureEndTime = strokeLast.ListEventsExtended.get(strokeLast.ListEventsExtended.size() - 1).EventTime;
		
		GestureTotalTimeWithoutPauses = gestureEndTime - gestureStartTime;
		mStatEngine.AddGestureValue(Instruction, ConstsParamNames.Gesture.GESTURE_TOTAL_TIME_WITH_PAUSES, GestureTotalTimeWithPauses);
	}
	
	private void CalculateGestureAvgVelocity()
	{
		GestureAverageVelocity = GestureLength / GestureTotalTimeWithoutPauses;
		mStatEngine.AddGestureValue(Instruction, ConstsParamNames.Gesture.AVERAGE_VELOCITY, GestureAverageVelocity);
	}
}
