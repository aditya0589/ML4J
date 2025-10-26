package mlcore.evals;
import java.util.*;
import mlcore.dataframe.DataFrame;

public class Regression {
    /*
     * The following must be implemented here:
     * 1. R squared 
     * 2. Adjusted R squared
     * 3. Mean absolute error
     * 4. Mean squared error
     * 5. Root mean squared error
     * 6. mean absolute Percentage error
     */
    private static List<Double> extractColumn(DataFrame df) {
        List<Double> values = new ArrayList<>();

        // Extract first column data
        Map<String, List<Object>> data = df.getData();
        String firstKey = data.keySet().iterator().next();
        List<Object> col = data.get(firstKey);

        for (Object val : col) {
            if (val == null) continue;
            try {
                values.add(Double.parseDouble(val.toString()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Non-numeric value found in DataFrame column: " + val);
            }
        }
        return values;
    }

    public static double meanSquaredError(DataFrame actualDF, DataFrame predictedDF) {
        List<Double> actual = extractColumn(actualDF);
        List<Double> predicted = extractColumn(predictedDF);
        checkSizes(actual, predicted);

        double sum = 0.0;
        for (int i = 0; i < actual.size(); i++) {
            double diff = actual.get(i) - predicted.get(i);
            sum += diff * diff;
        }
        return sum / actual.size();
    }

    public static double rootMeanSquaredError(DataFrame actualDF, DataFrame predictedDF) {
        return Math.sqrt(meanSquaredError(actualDF, predictedDF));
    }

    public static double meanAbsoluteError(DataFrame actualDF, DataFrame predictedDF) {
        List<Double> actual = extractColumn(actualDF);
        List<Double> predicted = extractColumn(predictedDF);
        checkSizes(actual, predicted);

        double sum = 0.0;
        for (int i = 0; i < actual.size(); i++) {
            sum += Math.abs(actual.get(i) - predicted.get(i));
        }
        return sum / actual.size();
    }

    public static double meanAbsolutePercentageError(DataFrame actualDF, DataFrame predictedDF) {
        List<Double> actual = extractColumn(actualDF);
        List<Double> predicted = extractColumn(predictedDF);
        checkSizes(actual, predicted);

        double sum = 0.0;
        for (int i = 0; i < actual.size(); i++) {
            if (actual.get(i) != 0)
                sum += Math.abs((actual.get(i) - predicted.get(i)) / actual.get(i));
        }
        return (sum / actual.size()) * 100.0;
    }

    public static double r2Score(DataFrame actualDF, DataFrame predictedDF) {
        List<Double> actual = extractColumn(actualDF);
        List<Double> predicted = extractColumn(predictedDF);
        checkSizes(actual, predicted);

        double meanActual = mean(actual);
        double ssTot = 0.0;
        double ssRes = 0.0;

        for (int i = 0; i < actual.size(); i++) {
            double y = actual.get(i);
            double yPred = predicted.get(i);
            ssTot += Math.pow(y - meanActual, 2);
            ssRes += Math.pow(y - yPred, 2);
        }
        return 1 - (ssRes / ssTot);
    }

    public static double adjustedR2(DataFrame actualDF, DataFrame predictedDF, int numFeatures) {
        List<Double> actual = extractColumn(actualDF);
        double r2 = r2Score(actualDF, predictedDF);
        int n = actual.size();
        return 1 - ((1 - r2) * (n - 1)) / (n - numFeatures - 1);
    }

    private static double mean(List<Double> values) {
        double sum = 0.0;
        for (double v : values) sum += v;
        return sum / values.size();
    }

    private static void checkSizes(List<Double> actual, List<Double> predicted) {
        if (actual.size() != predicted.size()) {
            throw new IllegalArgumentException("Actual and Predicted DataFrames must have same number of rows.");
        }
    }

    /**
     * Pretty print regression report
     */
    public static void printReport(DataFrame actualDF, DataFrame predictedDF, int numFeatures) {
        System.out.println("----- Regression Evaluation Report -----");
        System.out.printf("MSE   : %.4f%n", meanSquaredError(actualDF, predictedDF));
        System.out.printf("RMSE  : %.4f%n", rootMeanSquaredError(actualDF, predictedDF));
        System.out.printf("MAE   : %.4f%n", meanAbsoluteError(actualDF, predictedDF));
        System.out.printf("MAPE  : %.2f%%%n", meanAbsolutePercentageError(actualDF, predictedDF));
        System.out.printf("R²    : %.4f%n", r2Score(actualDF, predictedDF));
        System.out.printf("Adj R²: %.4f%n", adjustedR2(actualDF, predictedDF, numFeatures));
        System.out.println("----------------------------------------");
    }
}
