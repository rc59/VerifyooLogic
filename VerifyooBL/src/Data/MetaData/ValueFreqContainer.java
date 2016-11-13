package Data.MetaData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ValueFreqContainer {
	protected HashMap<Double, ValueFreq> mDictFreqs;
	
	public ValueFreqContainer() {
		mDictFreqs = new HashMap<>();
	}
	
	public void AddValue(double value) {
		if(mDictFreqs.containsKey(value)) {
			mDictFreqs.get(value).Increase();				
		}
		else {
			ValueFreq tempValue;
			tempValue = new ValueFreq(value);
			mDictFreqs.put(value, tempValue);
		}
	}
	
	protected ArrayList<ValueFreq> GetSortedArray() {
		ArrayList<ValueFreq> listFreqs = new ArrayList<>();
		for (Double key : mDictFreqs.keySet()) {
			listFreqs.add(mDictFreqs.get(key));
		}
		
		Collections.sort(listFreqs, new Comparator<ValueFreq>() {
            @Override
            public int compare(ValueFreq value1, ValueFreq value2) {
                if (Math.abs(value1.GetFreq()) > Math.abs(value2.GetFreq())) {
                    return -1;
                }
                if (Math.abs(value1.GetFreq()) < Math.abs(value2.GetFreq())) {
                    return 1;
                }
                return 0;
            }
        });
		
		return listFreqs;
	}
	
	public double GetMostFreq() {
		ArrayList<ValueFreq> listFreqs = GetSortedArray();
		return listFreqs.get(0).GetValue();
	}
	
	public double GetLeastFreq() {
		ArrayList<ValueFreq> listFreqs = GetSortedArray();
		return listFreqs.get(listFreqs.size() - 1).GetValue();
	}
}
