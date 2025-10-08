package mlcore.dataframe.transformations;
import java.util.*;
import mlcore.dataframe.DataFrame;

public class Splitter {
    // Provide methods for dataframe splitting, like train test split
    public Map<String, DataFrame> trainTestSplit(DataFrame df, double trainSize, long seed) {
        // The method takes the input dataframe and devids it into two train and test data.
        // However it does not seperate the target with the rest of the features.
        int n = df.getCountRows();
        List<Integer> allIndices = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            allIndices.add(i);
        }
        int trainCount = (int)(n * trainSize);

        Random rand = new Random();
        Set<Integer> trainIndicesSet = new HashSet<>();
        while(trainIndicesSet.size() < trainCount) {
            int index = rand.nextInt(n);
            trainIndicesSet.add(index);

        }
        List<Integer> trainIndices = new ArrayList<>(trainIndicesSet);
        List<Integer> testIndices = new ArrayList<>();

        for(int i = 0; i < n; i++) {
            if(! trainIndices.contains(i)) testIndices.add(i);
        }

        DataFrame trainDF = getRowsByIndices(df, trainIndices);
        DataFrame testDF = getRowsByIndices(df, testIndices);

        Map<String, DataFrame> result = new HashMap<>();
        result.put("train", trainDF);
        result.put("test", testDF);

        return result;

    }
    // obtain the Rows based on specified indices.
    public static DataFrame getRowsByIndices(DataFrame df, List<Integer> indices) {
        Map<String, List<Object>> filtered = new LinkedHashMap<>();
        for (String col : df.getData().keySet()) {
            List<Object> colData = df.getData().get(col);
            List<Object> newCol = new ArrayList<>();
            for (int idx : indices) {
                newCol.add(colData.get(idx));
            }
            filtered.put(col, newCol);
        }
        return new DataFrame(filtered);
    }
    // returns the target column as a new dataframe
    public DataFrame getTargetColumn(DataFrame df, String target) {
    
        if (!df.getData().containsKey(target)) {
            throw new IllegalArgumentException("Column " + target + " not found in DataFrame.");
        }

        Map<String, List<Object>> result = new LinkedHashMap<>();
        result.put(target, new ArrayList<>(df.getData().get(target)));

        return new DataFrame(result);
    }
    public DataFrame getTargetColumns(DataFrame df, List<String> targets) {
        Map<String, List<Object>> result = new LinkedHashMap<>();

        for (String target : targets) {
            if (!df.getData().containsKey(target)) {
                throw new IllegalArgumentException("Column " + target + " not found in DataFrame.");
            }
            result.put(target, new ArrayList<>(df.getData().get(target)));
        }

        return new DataFrame(result);
    }


}

