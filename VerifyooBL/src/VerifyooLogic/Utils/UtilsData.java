package VerifyooLogic.Utils;

import VerifyooLogic.Statistics.Interfaces.IBaseParam;
import VerifyooLogic.DataObjects.ValueFreq;
import VerifyooLogic.DataObjects.Params.NumericalParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by roy on 2/17/2016.
 */
public class UtilsData {
    public static void AddNumericalParameter(HashMap<String, IBaseParam> HashParameters, String name, double value, int weight) {
        HashParameters.put(name, new NumericalParam(value, name, weight));
    }    

    public static ArrayList<VerifyooLogic.DataObjects.ValueFreq> GetListOfValueFreqs(double[] listValues) {
        HashMap<Double, ValueFreq> map = new HashMap<>();
        ValueFreq tempEntry;

        ArrayList<ValueFreq> listValueFreqs = new ArrayList<>();

        double avgPause = 0;
        for (int idxEventPause = 0; idxEventPause < listValues.length; idxEventPause++) {
            avgPause += listValues[idxEventPause];
            if (map.containsKey(listValues[idxEventPause])) {
                tempEntry = map.get(listValues[idxEventPause]);
                map.remove(tempEntry);
                tempEntry.Frequency++;
                map.put(listValues[idxEventPause], tempEntry);
            }
            else {
                tempEntry = new ValueFreq(listValues[idxEventPause]);
                map.put(listValues[idxEventPause], tempEntry);
                listValueFreqs.add(tempEntry);
            }
        }

        Collections.sort(listValueFreqs, new Comparator<ValueFreq>() {
            @Override
            public int compare(ValueFreq valueFreq1, ValueFreq valueFreq2) {
                if (Math.abs(valueFreq1.Frequency) > Math.abs(valueFreq2.Frequency)) {
                    return -1;
                }
                if (Math.abs(valueFreq1.Frequency) < Math.abs(valueFreq2.Frequency)) {
                    return 1;
                }
                return 0;
            }
        });

        return listValueFreqs;
    }
}
