package mlcore.dataframe.transformations;

import java.util.*;
import mlcore.dataframe.DataFrame;
import mlcore.dataframe.utils.*;

public class NullHandler {
    // This class contains methods to handle null values in the dataset
    /*
     * This can be handled in two ways:
     * 1. Remove all rows with NULL values
     * 2. Replace NUlls with corresponding measures of central tendency
     *      1. Mean
     *      2. Median
     *      3. Mode
     */
    // input of the methods of this class are of type DataFrame (mlcore.dataframe.DataFrame)
    // output of the methods of this class must be of type DataFrame(mlcore.dataframe.DataFrame)
    public DataFrame dropRowsWithNulls(DataFrame df, List<String> columnNames) {
        // drops all rows containing null values
        Map<String, List<Object>> data = df.getData();
        int n = df.getCountRows();
        List<Integer> validIndices = new ArrayList<>();

        for(int i = 0; i < n; i++) {
            boolean isValid = true;
            for(String col : columnNames) {
                if(data.get(col).get(i) == null) {
                    isValid = false;
                    break;
                }
            }
            if(isValid) {
                validIndices.add(i);
            }
        }
        Map<String, List<Object>> filtered = new LinkedHashMap<>();
        for(String col: data.keySet()) {
            List<Object> colData = data.get(col);
            List<Object> newCol = new ArrayList<>();
            for(int index: validIndices) {
                newCol.add(colData.get(index));
            }
            filtered.put(col, newCol);
        } 
        return new DataFrame(filtered);
    }

    public DataFrame ReplaceNullsWithValue(DataFrame df, String columnName, Object value) {
        // replaces all null fields with a value specified by the user.
        Map<String, List<Object>> data = new LinkedHashMap<>(df.getData());
        List<Object> column = data.get(columnName);
        List<Object> newColumn = new ArrayList<>();
        for (Object val : column) {
            newColumn.add(val == null ? value : val);
        }
        data.put(columnName, newColumn);

        return new DataFrame(data);

    }

    public DataFrame fillNullsWithMeasure(DataFrame df, String columnName, String measureInLowerCase) {
        List<Object> column = df.getData().get(columnName);
        StatsUtils u = new StatsUtils();

        if ("mean".equals(measureInLowerCase)) {
            double sum = 0;
            int count = 0;

            for (Object val : column) {
                if (val != null) {
                    sum += Double.valueOf(val.toString());
                    count++;
                }
            }
            double mean = sum / count;
            return ReplaceNullsWithValue(df, columnName, mean);
        }

        if ("median".equals(measureInLowerCase)) {
            double median = u.medianColumn(df, columnName);
            return ReplaceNullsWithValue(df, columnName, median);
        }

        if ("mode".equals(measureInLowerCase)) {
            Object mode = u.modeColumn(df, columnName);
            return ReplaceNullsWithValue(df, columnName, mode);
        }

    // If measure is not recognized, return original DataFrame or throw exception
        throw new IllegalArgumentException("Unsupported measure: " + measureInLowerCase);
    }


    public DataFrame fillNullsWithMeasure(DataFrame df, List<String> columnNames, String measureInLowerCase) {
        DataFrame newDf = new DataFrame(new LinkedHashMap<>(df.getData())); // make a copy
        StatsUtils u = new StatsUtils();

        for (String columnName : columnNames) {
            List<Object> column = newDf.getData().get(columnName);

            if (column == null) {
                throw new IllegalArgumentException("Column " + columnName + " not found");
            }

            if (null == measureInLowerCase) {
                throw new IllegalArgumentException("Unsupported measure: " + measureInLowerCase);
            }

            else switch (measureInLowerCase) {
                case "mean" -> {
                    double sum = 0;
                    int count = 0;
                    for (Object val : column) {
                        if (val != null) {
                            sum += Double.valueOf(val.toString());
                            count++;
                        }
                    }   double mean = sum / count;
                    newDf = ReplaceNullsWithValue(newDf, columnName, mean);
                }
                case "median" -> {
                    double median = u.medianColumn(newDf, columnName);
                    newDf = ReplaceNullsWithValue(newDf, columnName, median);
                }
                case "mode" -> {
                    Object mode = u.modeColumn(newDf, columnName);
                    newDf = ReplaceNullsWithValue(newDf, columnName, mode);
                }
                default -> throw new IllegalArgumentException("Unsupported measure: " + measureInLowerCase);
            }
        }

        return newDf;
    }

}
