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

		stroke2.ListObjDTW = Utils.GetInstance().GetUtilsGeneral().ConvertVectorToDTWObj(stroke2.SpatialSamplingVector);
		
		ArrayList<IDTWObj> listDTWCoords1 = stroke1.ListObjDTW;
		ArrayList<IDTWObj> listDTWCoords2 = stroke2.ListObjDTW;
		
		UtilsDTW dtwCoords = new UtilsDTW(listDTWCoords1, listDTWCoords2);
		DTWDistance = dtwCoords.getDistance();
		
		double[] vectorStored = stroke1.SpatialSamplingVector;
		double[] vectorAuth = stroke2.SpatialSamplingVector;
		
		MinCosineDistance = 
			Utils.GetInstance().GetUtilsVectors().MinimumCosineDistanceScore(vectorStored, vectorAuth);
	}
}
