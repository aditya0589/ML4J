package mlcore.dataframe.transformations;

import java.util.*;
import mlcore.dataframe.DataFrame;

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

    public DataFrame FillNullsWithMeasure(DataFrame df, String columnName, String measureInLowerCase) {
        // fill the null values of a perticular column with a specified measure of central tendency
        // takes a dataframe and returns a dataframe
    }

    public DataFrame FillNullsWithMeasure(DataFrame df, List<String> columnNames, String measureInLowercase) {
        // fill the null values of a perticular column with a specified measure of central tendency
        // takes a dataframes and returns a dataframe
    }

}
