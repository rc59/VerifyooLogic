package Data.MetaData;

public class ParameterAvgPoint {
	public int IndexStart;
	public int IndexEnd;
	public IndexValue MaxValueInSection;
	
	public ParameterAvgPoint(int indexStart) {
		IndexStart = indexStart;
		MaxValueInSection = new IndexValue();
		MaxValueInSection.Index = -1;		
		IndexEnd = -1;
	}
}
