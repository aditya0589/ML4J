package mlcore.models;

import mlcore.dataframe.DataFrame;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.Comparator;

class KNeighbours extends Model {
    private int k;
    private double[][] trainFeatures;
    private int[] trainLabels;
    private Map<Integer, Object> labelMapping;

    public KNeighbours(int k) {
        this.k = k;
    }

    @Override
    public void train(DataFrame X, DataFrame y) {
        int n = X.getCountRows();
        int m = X.getCountCols();

        trainFeatures = X.to2DArray();

        // Extract original target values
        List<Object> originalTargets = y.getColumn(
                y.getData().keySet().iterator().next())
                .getData().values().iterator().next();

        // Check unique classes
        Set<Object> uniqueValues = new LinkedHashSet<>(originalTargets);
        if(uniqueValues.size() < 2) 
            throw new IllegalArgumentException("Need at least 2 classes!");

        // Map each unique label to an integer
        int idx = 0;
        labelMapping = new LinkedHashMap<>();
        for(Object val: uniqueValues) {
            labelMapping.put(idx++, val);
        }

        // Encode target values as integers
        trainLabels = new int[n];
        for(int i = 0; i < n; i++) {
            for(Map.Entry<Integer, Object> entry: labelMapping.entrySet()) {
                if(entry.getValue().equals(originalTargets.get(i))) {
                    trainLabels[i] = entry.getKey();
                    break;
                }
            }
        }
    }


    public DataFrame predict(DataFrame X) {
    double[][] features = X.to2DArray();
    int n = X.getCountRows();

    List<Object> predictions = new ArrayList<>();

    for(int i = 0; i < n; i++) {
        double[] distances = new double[trainFeatures.length];
        for(int j = 0; j < trainFeatures.length; j++) {
            distances[j] = euclideanDistance(features[i], trainFeatures[j]);
        }

        int[] nearestIndices = getKNearestIndices(distances, k);

        Map<Integer, Integer> voteCount = new HashMap<>();
        for(int idx: nearestIndices) {
            int label = trainLabels[idx];
            voteCount.put(label, voteCount.getOrDefault(label, 0) + 1);
        }

        int maxVoteLabel = -1;
        int maxVoteCount = -1;
        for(Map.Entry<Integer, Integer> entry: voteCount.entrySet()) {
            if(entry.getValue() > maxVoteCount ||
               (entry.getValue() == maxVoteCount && entry.getKey() < maxVoteLabel)) {
                maxVoteCount = entry.getValue();
                maxVoteLabel = entry.getKey();
            }
        }

        predictions.add(labelMapping.get(maxVoteLabel));
    }

    Map<String, List<Object>> result = new LinkedHashMap<>();
    result.put("Predictions", predictions);
    return new DataFrame(result);
    }


    // Euclidean distance between two points
    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for(int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    // Return indices of k smallest distances
    private int[] getKNearestIndices(double[] distances, int k) {
        Integer[] indices = new Integer[distances.length];
        for(int i = 0; i < distances.length; i++) indices[i] = i;

        Arrays.sort(indices, Comparator.comparingDouble(idx -> distances[idx]));

        int[] nearest = new int[k];
        for(int i = 0; i < k; i++) nearest[i] = indices[i];
        return nearest;
    }
}
