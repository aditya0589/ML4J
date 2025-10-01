package mlcore.dataframe.transformations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    
}
