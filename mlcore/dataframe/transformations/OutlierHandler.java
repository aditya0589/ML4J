package mlcore.dataframe.transformations;

import mlcore.dataframe.DataFrame;
import mlcore.dataframe.utils.StatsUtils;

import java.util.*;

public class OutlierHandler {

    public Double IQR(DataFrame df, String columnName, StatsUtils stat) {
        Double Q1 = stat.Q1Value(df, columnName);
        Double Q3 = stat.Q3Value(df, columnName);
        return Q3 - Q1;
    }

    public DataFrame RemoveOutliers(DataFrame df, String columnName) {
        int n = df.getCountRows();
        List<Object> values = df.getData().get(columnName);

        double lowerBound;
        double upperBound;

        if (n < 10) {
            // Small dataset: use mean ± 1*std
            double sum = 0;
            for (Object val : values) {
                sum += Double.valueOf(val.toString());
            }
            double mean = sum / n;

            double variance = 0;
            for (Object val : values) {
                double v = Double.valueOf(val.toString());
                variance += Math.pow(v - mean, 2);
            }
            variance = variance / n;
            double std = Math.sqrt(variance);

            lowerBound = mean - 1 * std;
            upperBound = mean + 1 * std;
        } else {
            // Larger dataset: use standard IQR
            StatsUtils stat = new StatsUtils();
            Double iqr = IQR(df, columnName, stat);
            Double q1 = stat.Q1Value(df, columnName);
            Double q3 = stat.Q3Value(df, columnName);

            lowerBound = q1 - 1.5 * iqr;
            upperBound = q3 + 1.5 * iqr;
        }

        // Filter rows within bounds
        List<Integer> validIndices = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            Double val = Double.valueOf(values.get(i).toString());
            if (val >= lowerBound && val <= upperBound) {
                validIndices.add(i);
            }
        }

        // Build filtered DataFrame
        Map<String, List<Object>> filtered = new LinkedHashMap<>();
        for (String column : df.getData().keySet()) {
            List<Object> originalColumn = df.getData().get(column);
            List<Object> newColumn = new ArrayList<>();
            for (int index : validIndices) {
                newColumn.add(originalColumn.get(index));
            }
            filtered.put(column, newColumn);
        }

        return new DataFrame(filtered);
    }

    public DataFrame RemoveOutliers(DataFrame df, List<String> columnNames) {
        int n = df.getCountRows();
        Map<String, List<Object>> data = df.getData();
        List<Integer> validIndices = new ArrayList<>();

        // Check each row
        for (int i = 0; i < n; i++) {
            boolean isValid = true;

            for (String columnName : columnNames) {
                List<Object> values = data.get(columnName);
                if (values == null) {
                    throw new IllegalArgumentException("Column " + columnName + " not found in DataFrame");
                }

                double lowerBound, upperBound;

                // Determine bounds based on dataset size
                if (n < 10) {
                    // Small dataset: mean ± 1*std
                    double sum = 0;
                    for (Object val : values) sum += Double.valueOf(val.toString());
                    double mean = sum / n;

                    double variance = 0;
                    for (Object val : values) {
                        double v = Double.valueOf(val.toString());
                        variance += Math.pow(v - mean, 2);
                    }
                    variance = variance / n;
                    double std = Math.sqrt(variance);

                    lowerBound = mean - 1 * std;
                    upperBound = mean + 1 * std;
                } else {
                    // Large dataset: IQR method
                    StatsUtils stat = new StatsUtils();
                    Double iqr = IQR(df, columnName, stat);
                    Double q1 = stat.Q1Value(df, columnName);
                    Double q3 = stat.Q3Value(df, columnName);

                    lowerBound = q1 - 1.5 * iqr;
                    upperBound = q3 + 1.5 * iqr;
                }

                // Check if current value is within bounds
                Double val = Double.valueOf(values.get(i).toString());
                if (val < lowerBound || val > upperBound) {
                    isValid = false;
                    break;
                }
            }

            if (isValid) validIndices.add(i);
        }

        // Build filtered DataFrame
        Map<String, List<Object>> filtered = new LinkedHashMap<>();
        for (String column : data.keySet()) {
            List<Object> originalColumn = data.get(column);
            List<Object> newColumn = new ArrayList<>();
            for (int index : validIndices) {
                newColumn.add(originalColumn.get(index));
            }
            filtered.put(column, newColumn);
        }

        return new DataFrame(filtered);
    }
}
