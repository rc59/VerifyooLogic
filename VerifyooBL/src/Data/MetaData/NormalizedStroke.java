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
		if(stroke.ListEventsSpatialExtended != null) {
			Vector = new double[stroke.ListEventsSpatialExtended.size() * 2];		
			
			ListCoords = new ArrayList<>();
			
			int idxVector = 0;
			for(int idx = 0; idx < stroke.ListEventsSpatialExtended.size(); idx++) {
				ListCoords.add(new DTWObjCoordinate(stroke.ListEventsSpatialExtended.get(idx).Xnormalized, stroke.ListEventsSpatialExtended.get(idx).Ynormalized));			
				
				Vector[idxVector] = stroke.ListEventsSpatialExtended.get(idx).Xnormalized;
				idxVector++;
				Vector[idxVector] = stroke.ListEventsSpatialExtended.get(idx).Ynormalized;
				idxVector++;
			}	
		}
	}
}
