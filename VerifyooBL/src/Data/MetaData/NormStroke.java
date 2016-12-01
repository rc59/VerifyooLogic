package Data.MetaData;

import java.awt.List;
import java.util.ArrayList;

import Data.UserProfile.Extended.StrokeExtended;
import Logic.Utils.Utils;
import Logic.Utils.DTW.DTWObjCoordinate;
import Logic.Utils.DTW.IDTWObj;

public class NormStroke {	
	public ArrayList<DTWObjCoordinate> ListObjDTW;
	public double[] SpatialSamplingVector;
	public double[] SpatialSamplingVectorX;
	public double[] SpatialSamplingVectorY;
	public int StrokeIdx;
	
	public String StrokeKeyCategory;
	
	public NormStroke() {
		
	}
	
	public NormStroke(StrokeExtended stroke, int strokeIdx) {
		ListObjDTW = Utils.GetInstance().GetUtilsGeneral().ConvertVectorToDTWCoords(stroke.SpatialSamplingVector);
		SpatialSamplingVector = stroke.SpatialSamplingVector;
		StrokeIdx = strokeIdx;
		
		SpatialSamplingVectorX = new double[SpatialSamplingVector.length / 2];
		SpatialSamplingVectorY = new double[SpatialSamplingVector.length / 2];
		
		for(int idx = 0; idx < SpatialSamplingVector.length; idx += 2) {
			SpatialSamplingVectorX[idx / 2] = SpatialSamplingVector[idx];
			SpatialSamplingVectorY[idx / 2] = SpatialSamplingVector[idx + 1];
		}
	}
	
	public String ToString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("ListObjDTW:");
		DTWObjCoordinate tempCoord;
		for(int idx = 0; idx < ListObjDTW.size(); idx++) {
			tempCoord = (DTWObjCoordinate) ListObjDTW.get(idx);
			sb.append(tempCoord.X);
			sb.append(",");
			sb.append(tempCoord.Y);
			if(idx + 1 < ListObjDTW.size()) {
				sb.append(",");
			}
		}

		sb.append("Vector:");
		for(int idx = 0; idx < SpatialSamplingVector.length; idx += 2) {		
			sb.append(SpatialSamplingVector[idx]);
			sb.append(",");
			sb.append(SpatialSamplingVector[idx + 1]);
			if(idx + 1 < SpatialSamplingVector.length) {
				sb.append(",");
			}
		}
		
		return sb.toString();
	}
	
	public void FromString(String input) {
		int idxDtwObj = input.indexOf("ListObjDTW:");
		int idxVector = input.indexOf("Vector:");
		
		String strDtwObj = input.substring((idxDtwObj + "ListObjDTW:".length()), (idxVector - 1));
		String strVector = input.substring(idxVector + "Vector:".length(), input.length() - 1);
		
		String[] listStrDtwObj = strDtwObj.split(",");
		String[] listStrVector = strVector.split(",");
		
		double x;
		double y;
		
		ListObjDTW = new ArrayList<>();
		DTWObjCoordinate tempCoord;
		
		for(int idx = 0; idx < listStrDtwObj.length; idx += 2) {
			
			x = Utils.GetInstance().GetUtilsMath().Round(Double.parseDouble(listStrDtwObj[idx]), 5);
			y = Utils.GetInstance().GetUtilsMath().Round(Double.parseDouble(listStrDtwObj[idx + 1]), 5);
			tempCoord = new DTWObjCoordinate(x, y);
			ListObjDTW.add(tempCoord);
		}
		
		int vectorSize = listStrVector.length;
		SpatialSamplingVector = new double[vectorSize];
		SpatialSamplingVectorX = new double[vectorSize / 2];
		SpatialSamplingVectorY = new double[vectorSize / 2];
		
		for(int idx = 0; idx < listStrVector.length; idx += 2) {			
			x = Utils.GetInstance().GetUtilsMath().Round(Double.parseDouble(listStrVector[idx]), 5);
			y = Utils.GetInstance().GetUtilsMath().Round(Double.parseDouble(listStrVector[idx + 1]), 5);
			
			SpatialSamplingVector[idx] = x;
			SpatialSamplingVector[idx + 1] = y;
			
			SpatialSamplingVectorX[idx / 2] = x;
			SpatialSamplingVectorY[idx / 2] = y;
		}
	}
}
