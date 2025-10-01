package mlcore.dataframe.utils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.lang.Math;

import mlcore.dataframe.DataFrame;


public class StatsUtils {
    // gives the value counts of each value in a given column
    public Map<Object, Integer> valueCounts(DataFrame df, String columnName) {
        Map<String, List<Object>> data = new LinkedHashMap<>(df.getData());
        
        if (!data.containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }

        List<Object> columnValues = data.get(columnName);
        Map<Object, Integer> counts = new LinkedHashMap<>();

        for(Object val: columnValues) {
            counts.put(val, counts.getOrDefault(val, 0) + 1);
        }
        
        return counts;
    }
    
    //mean value of the numerical column
    public Double meanColumn(DataFrame df, String columnName) {
        Map<String, List<Object>> data = new LinkedHashMap<>(df.getData());

        if (!data.containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }

        List<Object> columnValues = data.get(columnName);
        double total = 0.0;
        int count = 0;

        for (Object val : columnValues) {
            if (val instanceof Number) {
                total += ((Number) val).doubleValue(); // convert to double
                count++;
            }
        }

        if (count == 0) {
            throw new IllegalArgumentException("No numeric values found in column: " + columnName);
        }

        return total / count;
    }

    //sorting the numerical values
    public DataFrame sortByColumn(DataFrame df, String columnName, final boolean ascending) {
        Map<String, List<Object>> data = new LinkedHashMap<>(df.getData());

        if (!data.containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }

        List<Object> columnValues = data.get(columnName);
        int n = columnValues.size();

        // Step 1: make index list
        List<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            indices.add(i);
        }

        // Step 2: sort indices by column values
        indices.sort(new java.util.Comparator<Integer>() {
            @SuppressWarnings("unchecked")
            public int compare(Integer i1, Integer i2) {
                Comparable v1 = (Comparable) columnValues.get(i1);
                Comparable v2 = (Comparable) columnValues.get(i2);

                if (ascending) {
                    return v1.compareTo(v2);
                } else {
                    return v2.compareTo(v1);
                }
            }
        });

        // Step 3: reorder all columns
        Map<String, List<Object>> sortedData = new LinkedHashMap<String, List<Object>>();
        for (String col : data.keySet()) {
            List<Object> colVals = data.get(col);
            List<Object> newColVals = new ArrayList<Object>();
            for (int idx : indices) {
                newColVals.add(colVals.get(idx));
            }
            sortedData.put(col, newColVals);
        }

        return new DataFrame(sortedData);
    }

    //gives the median of the numerical column
    public Double medianColumn(DataFrame df, String columnName) {
        DataFrame data = sortByColumn(df, columnName, true);

        List<Object> columnValues = data.getData().get(columnName);

        List<Double> numericalValues = new ArrayList<Double>();
        for (Object val : columnValues) {
            if (val instanceof Number) {
                numericalValues.add(((Number) val).doubleValue());
            }
        }

        int size = numericalValues.size();
        if (size == 0) {
            return 0.0;
        }

        if (size % 2 != 0) { 
            return numericalValues.get(size / 2);
        } else {
            Double middle1 = numericalValues.get((size / 2) - 1);
            Double middle2 = numericalValues.get(size / 2);
            return (middle1 + middle2) / 2.0;
        }
    }

    //Most frequent occurrence
    public Object modeColumn(DataFrame df, String columnName) {
        Map<Object, Integer> freqMap = valueCounts(df, columnName);

        Object modeValue = null;
        int maxFreq = 0;

        for (Object val : freqMap.keySet()) {
            int freq = freqMap.get(val);
            if (freq > maxFreq) {
                maxFreq = freq;
                modeValue = val;
            }
        }

        return modeValue;
    }

    
    public Double minimumValue(DataFrame df, String columnName) {
        Map<String, List<Object>> data = new LinkedHashMap<>(df.getData());

        if (!data.containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }

        List<Object> columnValues = data.get(columnName);

        Double minimum = null;

        for (Object val : columnValues) {
            if (val instanceof Number) {
                double num = ((Number) val).doubleValue();
                if (minimum == null || num < minimum) {
                    minimum = num;
                }
            }
        }

        if (minimum == null) {
            return 0.0;
        }

        return minimum;
    }

    public Double maximumValue(DataFrame df, String columnName) {
        Map<String, List<Object>> data = new LinkedHashMap<>(df.getData());

        if (!data.containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }

        List<Object> columnValues = data.get(columnName);

        Double maximum = null;

        for (Object val : columnValues) {
            if (val instanceof Number) {
                double num = ((Number) val).doubleValue();
                if (maximum == null || num > maximum) {
                    maximum = num;
                }
            }
        }

        if (maximum == null) {
            return 0.0;
        }

        return maximum;
    }

    public Double sumValue(DataFrame df, String columnName) {
        Map<String, List<Object>> data = new LinkedHashMap<>(df.getData());

        if (!data.containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }

        List<Object> columnValues = data.get(columnName);

        Double sum = 0.0;

        for (Object val : columnValues) {
            if (val instanceof Number) {
                double num = ((Number) val).doubleValue();
                sum = sum + num;
            }
        }

        return sum;
    }

    public Double varianceValue(DataFrame df, String columnName) {
        Map<String, List<Object>> data = new LinkedHashMap<>(df.getData());

        if (!data.containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }

        List<Object> columnValues = data.get(columnName);
        List<Double> numericValues = new ArrayList<Double>();

        for (Object val : columnValues) {
            if (val instanceof Number) {
                numericValues.add(((Number) val).doubleValue());
            }
        }

        int n = numericValues.size();
        if (n <= 1) {
            return 0.0; // variance undefined or zero for 0/1 values
        }

        double sum = 0.0;
        for (Double num : numericValues) {
            sum += num;
        }
        double mean = sum / n;

        double squared_sum = 0.0;
        for(Double num : numericValues) {
            squared_sum += (num - mean) * (num - mean);
        }
        
        return squared_sum / n - 1;
    }

    //Standard Deviation
    public Double sdValue(DataFrame df, String columName) {
        double standardDev = Math.sqrt(varianceValue(df, columName));
        return standardDev;
    }

    public Double Q1Value(DataFrame df, String columnName) {
        DataFrame data = sortByColumn(df, columnName, true);

        List<Object> columnValues = data.getData().get(columnName);

        List<Double> numericalValues = new ArrayList<Double>();
        for (Object val : columnValues) {
            if (val instanceof Number) {
                numericalValues.add(((Number) val).doubleValue());
            }
        }

        int size = numericalValues.size();
        if (size == 0) {
            return 0.0;
        }

        List<Double> lowerHalf = numericalValues.subList(0, size / 2);

        int halfSize = lowerHalf.size();
        if (halfSize % 2 != 0) { 
            return lowerHalf.get(halfSize / 2);
        } else {
            Double middle1 = lowerHalf.get((halfSize / 2) - 1);
            Double middle2 = lowerHalf.get(halfSize / 2);
            return (middle1 + middle2) / 2.0;
        }
    }

    public Double Q3Value(DataFrame df, String columnName) {
        DataFrame data = sortByColumn(df, columnName, true);

        List<Object> columnValues = data.getData().get(columnName);
        List<Double> numericalValues = new ArrayList<Double>();

        for (Object val : columnValues) {
            if (val instanceof Number) {
                numericalValues.add(((Number) val).doubleValue());
            }
        }

        int size = numericalValues.size();
        if (size == 0) {
            return 0.0;
        }

        int startIndex;
        if (size % 2 == 0) {
            startIndex = size / 2;
        } else {
            startIndex = (size / 2) + 1;
        }

        List<Double> upperHalf = numericalValues.subList(startIndex, size);

        int halfSize = upperHalf.size();
        if (halfSize % 2 != 0) {
            return upperHalf.get(halfSize / 2);
        } else {
            Double middle1 = upperHalf.get((halfSize / 2) - 1);
            Double middle2 = upperHalf.get(halfSize / 2);
            return (middle1 + middle2) / 2.0;
        }
    }

}

