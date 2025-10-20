package mlcore.dataframe.transformations;
import java.util.*;
import mlcore.dataframe.DataFrame;

public class Splitter {
    // Provide methods for dataframe splitting, like train test split
    public Map<String, DataFrame> trainTestSplit(DataFrame X, DataFrame y, double trainSize, int randomState) {
        Map<String, DataFrame> split = new HashMap<>();

        int totalRows = X.getCountRows();
        int trainCount = (int) Math.round(totalRows * trainSize);

        // Generate a list of indices
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < totalRows; i++) {
            indices.add(i);
        }

        // Shuffle with a fixed seed for reproducibility
        Random random = new Random(randomState);
        Collections.shuffle(indices, random);

        // Split indices into train/test
        List<Integer> trainIndices = indices.subList(0, trainCount);
        List<Integer> testIndices = indices.subList(trainCount, totalRows);

        // Create train/test DataFrames
        DataFrame X_train = X.selectRows(trainIndices);
        DataFrame X_test = X.selectRows(testIndices);
        DataFrame y_train = y.selectRows(trainIndices);
        DataFrame y_test = y.selectRows(testIndices);

        split.put("X_train", X_train);
        split.put("X_test", X_test);
        split.put("y_train", y_train);
        split.put("y_test", y_test);

        return split;
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

