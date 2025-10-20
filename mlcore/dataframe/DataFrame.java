package mlcore.dataframe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class DataFrame {
    protected Map<String, List<Object>> data;
    //protected Map<String, String> columnTypes;
    
    protected int nRows;
    protected int nCols;

    public DataFrame() {
        this.data = new HashMap<>();
        //this.columnTypes = new HashMap<>();
        this.nRows = 0;
        this.nCols = 0;
    }

    public DataFrame(Map<String, List<Object>> inputData) {
        this.data = new HashMap<>(inputData);
        //this.columnTypes = new HashMap<>();

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

    public DataFrame withColumn(String name, List<Object> values) {
        data.put(name, values);
        DataFrame df = new DataFrame(data);
        return df;
    }

    //updates the existing dataframe
    public void InPlaceRemoveColumn(String columnName) {
        if(!data.containsKey(columnName)) {
            throw new IllegalArgumentException("Column is not found in the dataframe");
        } else {
            data.remove(columnName);
        }
    }

    // makes a new dataframe 
    public DataFrame dropColumn(String columnName) {
        if(!data.containsKey(columnName)) {
            throw new IllegalArgumentException("Column name not found in dataframe");
        } else {
            Map<String, List<Object>> newData = new LinkedHashMap<>(data);
            newData.remove(columnName);
            return new DataFrame(newData);
        }
    }
    public DataFrame withColumnReplaced(String oldColumn, String newColumn, List<Object> values) {
        if (!data.containsKey(oldColumn)) {
            throw new IllegalArgumentException("Column name not found in dataframe: " + oldColumn);
        }
        DataFrame df = new DataFrame(new HashMap<>(this.data));

        df.data.remove(oldColumn);

        df.data.put(newColumn, values);

        return df;
    }

    public DataFrame mergeDataFrameColumns(DataFrame other) {
        int rowCount = this.data.values().iterator().next().size();
        int otherRowCount = other.data.values().iterator().next().size();
        if (rowCount != otherRowCount) {
            throw new IllegalArgumentException("Row counts must match for column-wise merge.");
        }

        Map<String, List<Object>> merged = new LinkedHashMap<>(this.data);

        for (Map.Entry<String, List<Object>> entry : other.data.entrySet()) {
            if (merged.containsKey(entry.getKey())) {
                throw new IllegalArgumentException("Duplicate column: " + entry.getKey());
            }
            merged.put(entry.getKey(), entry.getValue());
        }

        return new DataFrame(merged);
    }

    public DataFrame mergeDataFrameRows(DataFrame other) {
        if (!this.data.keySet().equals(other.data.keySet())) {
            throw new IllegalArgumentException("Columns must match for row-wise merge.");
        }

        Map<String, List<Object>> merged = new LinkedHashMap<>();
            for (String col : this.data.keySet()) {
            List<Object> combined = new ArrayList<>(this.data.get(col));
            combined.addAll(other.data.get(col));
            merged.put(col, combined);
        }

        return new DataFrame(merged);
    }

// Converts the entire DataFrame to a 2D double array
    public double[][] to2DArray() {
        int rows = this.getCountRows();
        int cols = this.getCountCols();
        double[][] array = new double[rows][cols];

        List<String> headers = new ArrayList<>(this.data.keySet());

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Object val = this.data.get(headers.get(j)).get(i);
                if (val == null) {
                    array[i][j] = 0.0; // or handle null differently
                } else {
                    array[i][j] = Double.valueOf(val.toString());
                }
            }
        }
        return array;
    }

    // Converts a single-column DataFrame to a 1D double array
    public double[] to1DArray() {
        if (this.getCountCols() != 1) {
            throw new IllegalArgumentException("DataFrame must have exactly one column to convert to 1D array");
        }
        int rows = this.getCountRows();
        double[] array = new double[rows];
        String colName = this.data.keySet().iterator().next();
        List<Object> values = this.data.get(colName);

        for (int i = 0; i < rows; i++) {
            Object val = values.get(i);
            if (val == null) {
                array[i] = 0.0; // or handle null differently
            } else {
                array[i] = Double.valueOf(val.toString());
            }
        }
        return array;
    }
    public DataFrame selectRows(List<Integer> indices) {
        Map<String, List<Object>> selected = new LinkedHashMap<>();

        for (String col : this.data.keySet()) {
            List<Object> column = this.data.get(col);
            List<Object> newCol = new ArrayList<>();

            for (int idx : indices) {
                newCol.add(column.get(idx));
            }

            selected.put(col, newCol);
        }

        return new DataFrame(selected);
    }


}

