package Data.MetaData;

import java.util.ArrayList;

import Data.UserProfile.Extended.StrokeExtended;
import Logic.Utils.DTW.DTWObjCoordinate;
import Logic.Utils.DTW.IDTWObj;

public class NormalizedStroke {
	public ArrayList<IDTWObj> ListCoords;
	public double[] Vector;

	public NormalizedStroke() {
		
	}
	
	public NormalizedStroke(StrokeExtended stroke) {
		if(stroke.ListEventsSpatialByDistanceExtended != null) {
			Vector = new double[stroke.ListEventsSpatialByDistanceExtended.size() * 2];		
			
			ListCoords = new ArrayList<>();
			
			int idxVector = 0;
			for(int idx = 0; idx < stroke.ListEventsSpatialByDistanceExtended.size(); idx++) {
				ListCoords.add(new DTWObjCoordinate(stroke.ListEventsSpatialByDistanceExtended.get(idx).Xnormalized, stroke.ListEventsSpatialByDistanceExtended.get(idx).Ynormalized));			
				
				Vector[idxVector] = stroke.ListEventsSpatialByDistanceExtended.get(idx).Xnormalized;
				idxVector++;
				Vector[idxVector] = stroke.ListEventsSpatialByDistanceExtended.get(idx).Ynormalized;
				idxVector++;
			}	
		}
	}
}
