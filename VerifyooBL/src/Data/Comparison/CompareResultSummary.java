package Data.Comparison;

import java.util.ArrayList;

import Data.Comparison.Interfaces.ICompareResult;

public class CompareResultSummary {
	public ArrayList<ICompareResult> ListCompareResults;
	public ArrayList<ICompareResult> ListCompareResultsExtra;
	public double Score;
	
	public CompareResultSummary()
	{
		Score = 0;
		ListCompareResults = new ArrayList<>();
		ListCompareResultsExtra = new ArrayList<>();
	}
}
