package Logic.Utils;

import java.util.ArrayList;

import Data.MetaData.NormStroke;
import Data.UserProfile.Extended.GestureExtended;
import Data.UserProfile.Extended.StrokeExtended;
import Logic.Utils.DTW.DTWObjCoordinate;
import Logic.Utils.DTW.DTWObjDouble;
import Logic.Utils.DTW.IDTWObj;
import Logic.Utils.DTW.UtilsDTW;

public class UtilsShapeComparer {
	public double MinCosineDistance;
	public double DTWDistance;
	public double ConvolutionDistance;	
	
	public void Compare(NormStroke stroke1,  NormStroke stroke2) {

		stroke2.ListObjDTW = Utils.GetInstance().GetUtilsGeneral().ConvertVectorToDTWCoords(stroke2.SpatialSamplingVector);
		
		ArrayList<DTWObjCoordinate> listDTWCoords1 = stroke1.ListObjDTW;
		ArrayList<DTWObjCoordinate> listDTWCoords2 = stroke2.ListObjDTW;
				
		ArrayList<IDTWObj> listDTW1 = new ArrayList<>();
		ArrayList<IDTWObj> listDTW2 = new ArrayList<>();

		for(int idx = 0; idx < listDTWCoords1.size(); idx++) {
			listDTW1.add(listDTWCoords1.get(idx));
		}
		
		for(int idx = 0; idx < listDTWCoords2.size(); idx++) {
			listDTW2.add(listDTWCoords2.get(idx));
		}
		
		UtilsDTW dtwCoords = new UtilsDTW(listDTW1, listDTW2);
		DTWDistance = dtwCoords.getDistance();
		
		double[] vectorStored = stroke1.SpatialSamplingVector;
		double[] vectorAuth = stroke2.SpatialSamplingVector;
		
		MinCosineDistance = 
			Utils.GetInstance().GetUtilsVectors().MinimumCosineDistanceScore(vectorStored, vectorAuth);
	}
}
