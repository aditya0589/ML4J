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
    
    public Map<String, List<Object>> getData() {
        return data;
    }
    
    public int getCountRows() {
        return nRows;
    }
    
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

    public DataFrame head() {
        Map<String, List<Object>> headData = new LinkedHashMap<>();
        for(String col : this.data.keySet()) {
            List<Object> colVals = this.data.get(col);
            headData.put(col, new ArrayList<>(colVals.subList(0, Math.min(5, colVals.size()))));
        }
        return new DataFrame(headData);
    }

    public DataFrame tail() {
        Map<String, List<Object>> tailData = new LinkedHashMap<>();
        for (String col : this.data.keySet()) {
            List<Object> colVals = this.data.get(col);
            int start = Math.max(colVals.size() - 5, 0); // handle <5 rows
            tailData.put(col, new ArrayList<>(colVals.subList(start, colVals.size())));
        }
        return new DataFrame(tailData);
    }

    public DataFrame getColumn(String columnName) {
        if (!data.containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }
        Map<String, List<Object>> specificColumn = new LinkedHashMap<>();
        specificColumn.put(columnName, new ArrayList<>(data.get(columnName)));
        return new DataFrame(specificColumn);
    }

    public DataFrame getColumn(List<String> columnNames) {
        for(String col: columnNames) {
            if(!data.containsKey(col)) {
                throw new IllegalArgumentException("Column " + col + " not found");
            }
        }

        Map<String, List<Object>> listColumns = new LinkedHashMap<>();
        
        for(String col: columnNames) {
            listColumns.put(col, new ArrayList<>(data.get(col)));
        }
        return new DataFrame(listColumns);
    }
    
    public void display() {
        List<String> headers = new ArrayList<>(this.data.keySet());
        System.out.print("Index\t");
        for(String col: headers) {
            System.out.print(col + "\t");
        }

        System.out.println();

        for (int i = 0; i < this.nRows; i++) {
            System.out.print(i + "\t");
            for (String col : this.data.keySet()) {
                List<Object> colVals = this.data.get(col);
                if (i < colVals.size()) {
                    System.out.print(colVals.get(i) + "\t"); // print 
                } else {
                    System.out.print("\t"); // missing row
                }
            }
            System.out.println();
        }
    }
}

