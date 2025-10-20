package mlcore.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mlcore.dataframe.DataFrame;


public class LinearRegression extends Model{
    
    private double[] weights;  // model coefficients (slope values)
    private double bias;       // intercept term
    final private double learningRate; 
    final private int epochs; 

    public LinearRegression(double learningRate, int epochs) {
        this.learningRate = learningRate;
        this.epochs = epochs;
        this.bias = 0.0;
        this.weights = null; // will initialize in train() when you know feature count
    }


    public void train(DataFrame X, DataFrame y) {
    int n = X.getCountRows();
    int m = X.getCountCols();  // ✅ fixed
    double[][] features = X.to2DArray();
    double[] targets = y.to1DArray();

    weights = new double[m];
    bias = 0.0;

    for (int epoch = 0; epoch < epochs; epoch++) {
        double[] predictions = new double[n];

        for (int i = 0; i < n; i++) {
            double pred = bias;
            for (int j = 0; j < m; j++) {
                pred += features[i][j] * weights[j];
            }
            predictions[i] = pred;
        }

        double[] dW = new double[m];
        double db = 0.0;

        for (int i = 0; i < n; i++) {
            double error = predictions[i] - targets[i];
            for (int j = 0; j < m; j++) {
                dW[j] += features[i][j] * error;
            }
            db += error;
        }

        for (int j = 0; j < m; j++) {
            dW[j] /= n;
        }
        db /= n;

        // Update weights
        for (int j = 0; j < m; j++) {
            weights[j] -= learningRate * dW[j];
        }
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
            double pred = bias;
            for (int j = 0; j < m; j++) {
                pred += features[i][j] * weights[j];
            }
            predictions.add(pred);
        }

        Map<String, List<Object>> result = new LinkedHashMap<>();
        result.put("Predictions", predictions);
        return new DataFrame(result);
    }
    
}
