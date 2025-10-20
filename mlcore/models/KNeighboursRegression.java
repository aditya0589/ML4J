package mlcore.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mlcore.dataframe.DataFrame;

public class KNeighboursRegression extends Model {

    private int k; 
    private double[][] trainFeatures;  
    private double[] trainTargets;     

    public KNeighboursRegression(int k) {
        this.k = k;
    }

    @Override
    public void train(DataFrame X, DataFrame y) {
        int n = X.getCountRows();
        trainFeatures = X.to2DArray();

        // Get target column name
        String targetCol = y.getData().keySet().iterator().next();
        List<Object> targetList = y.getData().get(targetCol);

        trainTargets = new double[n];
        for (int i = 0; i < n; i++) {
            trainTargets[i] = Double.parseDouble(targetList.get(i).toString());
        }
    }

    @Override
    public DataFrame predict(DataFrame X) {
        double[][] features = X.to2DArray();
        int n = X.getCountRows();

        List<Object> predictions = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            double[] distances = new double[trainFeatures.length];

            for (int j = 0; j < trainFeatures.length; j++) {
                distances[j] = euclideanDistance(features[i], trainFeatures[j]);
            }

            int[] nearestIndices = getKNearestIndices(distances, k);

            double sum = 0.0;
            for (int idx : nearestIndices) {
                sum += trainTargets[idx];
            }

            predictions.add(sum / k);  // Average of k nearest targets
        }

        Map<String, List<Object>> result = new LinkedHashMap<>();
        result.put("Predictions", predictions);
        return new DataFrame(result);
    }

    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    private int[] getKNearestIndices(double[] distances, int k) {
        Integer[] indices = new Integer[distances.length];
        for (int i = 0; i < distances.length; i++) indices[i] = i;

        Arrays.sort(indices, Comparator.comparingDouble(idx -> distances[idx]));

        int[] nearest = new int[k];
        for (int i = 0; i < k; i++) nearest[i] = indices[i];
        return nearest;
    }
}
