package mlcore.dataframe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DataFrame {
    private Map<String, List<Object>> data;

    private Map<String, String> columnTypes;

    
    private int nRows;
    private int nCols;

    public DataFrame() {
        this.data = new HashMap<>();
        this.columnTypes = new HashMap<>();
        this.nRows = 0;
        this.nCols = 0;
    }


    public DataFrame(Map<String, List<Object>> inputData) {
        this.data = new HashMap<>(inputData);
        this.columnTypes = new HashMap<>();

        if (!inputData.isEmpty()) {
            this.nCols = inputData.size();
            this.nRows = inputData.values().iterator().next().size();
        } else {
            this.nCols = 0;
            this.nRows = 0;
        }
    }
    // gets the data of the dataframe. return it as a map object
    public Map<String, List<Object>> getData() {
        return data;
    }
    //gets the count of the rows in the data
    public int getCountRows() {
        return nRows;
    }
    // gets the count of the columns of data
    public int getCountCols() {
        return nCols;
    }

    public static DataFrame readCSV(String filePath, String delimiter) {
        Map<String, List<Object>> csvData = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            if (line == null) return new DataFrame(); // empty file

            // Step 1: Get headers
            String[] headers = line.split(delimiter);
            for (String header : headers) {
                csvData.put(header.trim(), new ArrayList<>());
            }

            // Step 2: Read rows
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                for (int i = 0; i < headers.length; i++) {
                    String val = values[i].trim();
                    Object parsedVal = parseValue(val);
                    csvData.get(headers[i]).add(parsedVal);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new DataFrame(csvData);
    }
    private static Object parseValue(String val) {
        try {
            if (val.contains(".")) return Double.valueOf(val);
            else return Integer.valueOf(val);
        } catch (NumberFormatException e) {
            return val; // keep as String if not numeric
        }
    }

    public static DataFrame head() {
        // returns the first 5 rows of all the columns in the dataframe
        // answer is of type mlcore.dataframe.DataFrame
    }

    public static DataFrame getColumn(String columnName) {
        //returns the column with the specified column name
        // returns not found error if column is not present
        // returns answer of type mlcore.dataframe.DataFrame

    }
    public static DataFrame getColumn(List<String> columnNames) {
        //returns the columns with the specified column names in the list
        // returns not found error if column is not present
        // returns answer of type mlcore.dataframe.DataFrame

    }
    public static Map<String, Map<String, Double>> getStats() {
        // returns stats like mean, median, mode, count, std, variance, 
        //Q1, Q2, Q3 etc
    }
}