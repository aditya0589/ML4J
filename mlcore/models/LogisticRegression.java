package mlcore.models;

import mlcore.dataframe.DataFrame;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.LinkedHashSet;

public class LogisticRegression extends Model {

    private double[] weights;  // model coefficients (slope values)
    private double bias;       // intercept term
    private double learningRate; 
    private int epochs; 

    public LogisticRegression(double learningRate, int epochs) {
        this.learningRate = learningRate;
        this.epochs = epochs;
        this.bias = 0.0;
        this.weights = null; // will initialize in train() when you know feature count
    }

    @Override
    public void train(DataFrame X, DataFrame y) {
        int n = X.getCountRows();
        int m = X.getCountCols();

        // Extract original target column values
        List<Object> originalTargets = y.getColumn(y.getData().keySet().iterator().next())
                                       .getData().values().iterator().next();

        // Check for binary target
        Set<Object> uniqueValues = new LinkedHashSet<>(originalTargets);
        if (uniqueValues.size() != 2) {
            throw new IllegalArgumentException("Target column is not binary!");
        }

        // Encode target to 0/1
        Object[] uniques = uniqueValues.toArray();
        Map<Object, Integer> encodingMap = new LinkedHashMap<>();
        encodingMap.put(uniques[0], 0);
        encodingMap.put(uniques[1], 1);

        double[] targets = new double[n];
        for (int i = 0; i < n; i++) {
            targets[i] = encodingMap.get(originalTargets.get(i));
        }

        double[][] features = X.to2DArray();

        // Initialize weights and bias
        weights = new double[m];
        bias = 0.0;

        // Training loop
        for (int epoch = 0; epoch < epochs; epoch++) {
            double[] predictions = new double[n];

            // Step 1: Compute predictions using sigmoid
            for (int i = 0; i < n; i++) {
                double linearSum = bias;
                for (int j = 0; j < m; j++) {
                    linearSum += features[i][j] * weights[j];
                }
                predictions[i] = 1.0 / (1.0 + Math.exp(-linearSum));
            }

            // Step 2: Compute gradients
            double[] dW = new double[m];
            double db = 0.0;

            for (int i = 0; i < n; i++) {
                double error = predictions[i] - targets[i];
                for (int j = 0; j < m; j++) {
                    dW[j] += features[i][j] * error;
                }
                db += error;
            }

            // Step 3: Average gradients
            for (int j = 0; j < m; j++) dW[j] /= n;
            db /= n;

            // Step 4: Update weights and bias
            for (int j = 0; j < m; j++) weights[j] -= learningRate * dW[j];
            bias -= learningRate * db;
        }
    }

    @Override
    public DataFrame predict(DataFrame X) {
        double[][] features = X.to2DArray();
        int n = X.getCountRows();
        int m = X.getCountCols();

        List<Object> predictions = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            double linearSum = bias;
            for (int j = 0; j < m; j++) {
                linearSum += features[i][j] * weights[j];
            }
            double prob = 1.0 / (1.0 + Math.exp(-linearSum));
            predictions.add(prob); 
        }

        Map<String, List<Object>> result = new LinkedHashMap<>();
        result.put("Predictions", predictions);
        return new DataFrame(result);
    }
}
