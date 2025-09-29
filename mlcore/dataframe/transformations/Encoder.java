package mlcore.dataframe.transformations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    
}
