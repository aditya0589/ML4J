package mlcore.dataframe.transformations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.crypto.Data;

import mlcore.dataframe.DataFrame;

public class Encoder {
    public DataFrame labelEncoding(DataFrame df, String columnName) {
        Map<String, List<Object>> labelEncoded = new LinkedHashMap<>(df.getData());

        if (!labelEncoded.containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }

        Map<Object, Integer> uniqMap = new HashMap<>();
        int code = 0;

        List<Object> encodedColumn = new ArrayList<>();

        for (Object val : labelEncoded.get(columnName)) {
            uniqMap.putIfAbsent(val, code++);
            encodedColumn.add(uniqMap.get(val));
        }

        labelEncoded.remove(columnName);
        labelEncoded.put(columnName + "_Encoded", encodedColumn);

        return new DataFrame(labelEncoded);
    }

    public DataFrame labelEncoding(DataFrame df, List<String> columns) {
        Map<String, List<Object>> labelEncoded = new LinkedHashMap<>(df.getData());

        for (String col : columns) {
            if (!labelEncoded.containsKey(col)) {
                throw new IllegalArgumentException("Column " + col + " not found");
            }

            Map<Object, Integer> uniqMap = new HashMap<>();
            int code = 0;
            List<Object> encodedColumn = new ArrayList<>();

            for (Object val : labelEncoded.get(col)) {
                uniqMap.putIfAbsent(val, code++);
                encodedColumn.add(uniqMap.get(val));
            }

            labelEncoded.remove(col);
            labelEncoded.put(col + "_Encoded", encodedColumn);
        }

        return new DataFrame(labelEncoded);
    }

    public DataFrame oneHotEncoding(DataFrame df, String columnName) {
        List<Object> values = df.getColumn(columnName).getData().get(columnName);
        Set<Object> unique = new LinkedHashSet<>(values);
        List<Object> categories = new ArrayList<>(unique);

        Map<Object, Integer> categoryIndexMap = new HashMap<>();
        for(int i = 0; i < categories.size(); i++) {
            categoryIndexMap.put(categories.get(i), i);
        }

        Map<String, List<Object>> onehotColumns = new LinkedHashMap<>();
        for(Object category: categories) {
            List<Object> colList = new ArrayList<>();

            for (Object value : values) {
                colList.add(0);
            }
            onehotColumns.put(columnName + "_" +category.toString(), colList);
        }
        for (int i = 0; i < values.size(); i++) {
            Object val = values.get(i);
            String colName = columnName + "_" + val.toString();
            onehotColumns.get(colName).set(i, 1);
        }

        DataFrame newdf = new DataFrame(new LinkedHashMap<>(df.getData()));
        for (Map.Entry<String, List<Object>> entry : onehotColumns.entrySet()) {
            newdf.withColumn(entry.getKey(), entry.getValue());
        }
        newdf.dropColumn(columnName);

        return newdf;
    }
    
    public DataFrame oneHotEncoding(DataFrame df, List<String> columnNames) {
        DataFrame newdf = new DataFrame(new LinkedHashMap<>(df.getData()));

        for (String columnName : columnNames) {
            if (!newdf.getData().containsKey(columnName)) {
                throw new IllegalArgumentException("Column " + columnName + " not found in DataFrame");
            }

            List<Object> values = newdf.getData().get(columnName);

            Set<Object> unique = new LinkedHashSet<>(values);
            List<Object> categories = new ArrayList<>(unique);

            Map<String, List<Object>> onehotColumns = new LinkedHashMap<>();
            int rowCount = values.size();

            for (Object category : categories) {
                String newColName = columnName + "_" + category.toString().replaceAll("\\s+", "_");
                List<Object> colData = new ArrayList<>(Collections.nCopies(rowCount, 0));
                onehotColumns.put(newColName, colData);
            }

            for (int i = 0; i < rowCount; i++) {
                Object val = values.get(i);
                String colName = columnName + "_" + val.toString().replaceAll("\\s+", "_");

                if (onehotColumns.containsKey(colName)) {
                    onehotColumns.get(colName).set(i, 1);
                }
            }
            for (Map.Entry<String, List<Object>> entry : onehotColumns.entrySet()) {
                newdf.withColumn(entry.getKey(), entry.getValue());
            }
            newdf.dropColumn(columnName);
        }
        return newdf;
    }

    public DataFrame targetEncoding(DataFrame df, String categoricalColumn, String targetColumn) {
        Map<String, List<Object>> data = new LinkedHashMap<>(df.getData());

        if (!data.containsKey(categoricalColumn) || !data.containsKey(targetColumn)) {
            throw new IllegalArgumentException("Column not found in DataFrame");
        }

        List<Object> categories = data.get(categoricalColumn);
        List<Object> targets = data.get(targetColumn);

        Map<Object, Double> sumMap = new HashMap<>();
        Map<Object, Integer> countMap = new HashMap<>();

        for (int i = 0; i < categories.size(); i++) {
            Object category = categories.get(i);
            double targetValue = Double.valueOf(targets.get(i).toString());

            sumMap.put(category, sumMap.getOrDefault(category, 0.0) + targetValue);
            countMap.put(category, countMap.getOrDefault(category, 0) + 1);
        }

        Map<Object, Double> meanMap = new HashMap<>();
        for (Object category : sumMap.keySet()) {
            double mean = sumMap.get(category) / countMap.get(category);
            meanMap.put(category, mean);
        }

        List<Object> encodedColumn = new ArrayList<>();
        for (Object category : categories) {
            encodedColumn.add(meanMap.get(category));
        }

        Map<String, List<Object>> encodedData = new LinkedHashMap<>(data);
        encodedData.remove(categoricalColumn);
        encodedData.put(categoricalColumn + "_TargetEncoded", encodedColumn);

        return new DataFrame(encodedData);
    }

    public DataFrame targetEncoding(DataFrame df, List<String> categoricalColumns, String targetColumn) {
        // Step 1: Copy the data from DataFrame
        Map<String, List<Object>> data = new LinkedHashMap<>(df.getData());

        // Step 2: Check if target column exists
        if (!data.containsKey(targetColumn)) {
            throw new IllegalArgumentException("Target column not found in DataFrame");
        }

        List<Object> targets = data.get(targetColumn);

        // Step 3: Iterate through each categorical column
        for (String categoricalColumn : categoricalColumns) {

            if (!data.containsKey(categoricalColumn)) {
                throw new IllegalArgumentException("Column " + categoricalColumn + " not found in DataFrame");
            }

            List<Object> categories = data.get(categoricalColumn);

            // Maps for sum and count per category
            Map<Object, Double> sumMap = new HashMap<>();
            Map<Object, Integer> countMap = new HashMap<>();

            // Step 4: Compute sum and count for each category
            for (int i = 0; i < categories.size(); i++) {
                Object category = categories.get(i);
                double targetValue = Double.valueOf(targets.get(i).toString());

                sumMap.put(category, sumMap.getOrDefault(category, 0.0) + targetValue);
                countMap.put(category, countMap.getOrDefault(category, 0) + 1);
            }

            // Step 5: Compute mean for each category
            Map<Object, Double> meanMap = new HashMap<>();
            for (Object category : sumMap.keySet()) {
                double mean = sumMap.get(category) / countMap.get(category);
                meanMap.put(category, mean);
            }

            // Step 6: Replace category values with encoded means
            List<Object> encodedColumn = new ArrayList<>();
            for (Object category : categories) {
                encodedColumn.add(meanMap.get(category));
            }

            // Step 7: Add encoded column to dataset
            data.put(categoricalColumn + "_TargetEncoded", encodedColumn);
        }

        // Step 8: Return new DataFrame with all encoded columns
        return new DataFrame(data);
    }

    
}


