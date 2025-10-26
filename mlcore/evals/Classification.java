package mlcore.evals;
import java.util.*;
import mlcore.dataframe.DataFrame;

public class Classification {
    /*
     * The following must be implemented here:
     * 1. Confusion Matrix
     * 2. True positives
     * 3. true negatives
     * 4. false positives
     * 5. false negatives
     * 6. Accuracy
     * 7. Precision
     * 8. Recall
     * 9. Specificity
     * 10. f1 score
     * 
     */
    private static List<String> extractColumn(DataFrame df) {
        Map<String, List<Object>> data = df.getData();
        String firstKey = data.keySet().iterator().next();
        List<Object> col = data.get(firstKey);

        List<String> labels = new ArrayList<>();
        for (Object val : col) {
            if (val == null) continue;
            labels.add(val.toString());
        }
        return labels;
    }

    /**
     * Confusion matrix: Map of Maps, class -> class -> count
     */
    public static Map<String, Map<String, Integer>> confusionMatrix(DataFrame actualDF, DataFrame predictedDF) {
        List<String> actual = extractColumn(actualDF);
        List<String> predicted = extractColumn(predictedDF);

        if (actual.size() != predicted.size()) {
            throw new IllegalArgumentException("Actual and predicted must have same number of rows");
        }

        Set<String> classes = new LinkedHashSet<>(actual);
        classes.addAll(predicted);

        Map<String, Map<String, Integer>> matrix = new LinkedHashMap<>();
        for (String actualClass : classes) {
            Map<String, Integer> row = new LinkedHashMap<>();
            for (String predictedClass : classes) {
                row.put(predictedClass, 0);
            }
            matrix.put(actualClass, row);
        }

        for (int i = 0; i < actual.size(); i++) {
            String a = actual.get(i);
            String p = predicted.get(i);
            matrix.get(a).put(p, matrix.get(a).get(p) + 1);
        }

        return matrix;
    }

    /**
     * Accuracy
     */
    public static double accuracy(DataFrame actualDF, DataFrame predictedDF) {
        List<String> actual = extractColumn(actualDF);
        List<String> predicted = extractColumn(predictedDF);
        int correct = 0;

        for (int i = 0; i < actual.size(); i++) {
            if (actual.get(i).equals(predicted.get(i))) correct++;
        }
        return (double) correct / actual.size();
    }

    /**
     * Precision per class
     */
    public static Map<String, Double> precision(DataFrame actualDF, DataFrame predictedDF) {
        Map<String, Map<String, Integer>> matrix = confusionMatrix(actualDF, predictedDF);
        Map<String, Double> precisionMap = new LinkedHashMap<>();

        for (String cls : matrix.keySet()) {
            int tp = matrix.get(cls).get(cls);
            int fp = 0;
            for (String otherCls : matrix.keySet()) {
                if (!otherCls.equals(cls)) fp += matrix.get(otherCls).get(cls);
            }
            precisionMap.put(cls, tp + fp == 0 ? 0.0 : (double) tp / (tp + fp));
        }
        return precisionMap;
    }

    /**
     * Recall per class
     */
    public static Map<String, Double> recall(DataFrame actualDF, DataFrame predictedDF) {
        Map<String, Map<String, Integer>> matrix = confusionMatrix(actualDF, predictedDF);
        Map<String, Double> recallMap = new LinkedHashMap<>();

        for (String cls : matrix.keySet()) {
            int tp = matrix.get(cls).get(cls);
            int fn = 0;
            for (String otherCls : matrix.keySet()) {
                if (!otherCls.equals(cls)) fn += matrix.get(cls).get(otherCls);
            }
            recallMap.put(cls, tp + fn == 0 ? 0.0 : (double) tp / (tp + fn));
        }
        return recallMap;
    }

    /**
     * F1 score per class
     */
    public static Map<String, Double> f1Score(DataFrame actualDF, DataFrame predictedDF) {
        Map<String, Double> precision = precision(actualDF, predictedDF);
        Map<String, Double> recall = recall(actualDF, predictedDF);
        Map<String, Double> f1Map = new LinkedHashMap<>();

        for (String cls : precision.keySet()) {
            double p = precision.get(cls);
            double r = recall.get(cls);
            f1Map.put(cls, (p + r == 0 ? 0.0 : 2 * p * r / (p + r)));
        }
        return f1Map;
    }

    /**
     * Macro average
     */
    public static double macroAverage(Map<String, Double> values) {
        double sum = 0.0;
        for (double v : values.values()) sum += v;
        return sum / values.size();
    }

    /**
     * Print a detailed classification report
     */
    public static void printReport(DataFrame actualDF, DataFrame predictedDF) {
        System.out.println("----- Classification Report -----");
        System.out.printf("Accuracy: %.4f%n", accuracy(actualDF, predictedDF));

        Map<String, Double> precision = precision(actualDF, predictedDF);
        Map<String, Double> recall = recall(actualDF, predictedDF);
        Map<String, Double> f1 = f1Score(actualDF, predictedDF);

        System.out.println("\nClass-wise Metrics:");
        for (String cls : precision.keySet()) {
            System.out.printf("Class: %s | Precision: %.4f | Recall: %.4f | F1: %.4f%n",
                    cls, precision.get(cls), recall.get(cls), f1.get(cls));
        }

        System.out.printf("\nMacro Precision: %.4f%n", macroAverage(precision));
        System.out.printf("Macro Recall   : %.4f%n", macroAverage(recall));
        System.out.printf("Macro F1       : %.4f%n", macroAverage(f1));

        System.out.println("\nConfusion Matrix:");
        Map<String, Map<String, Integer>> matrix = confusionMatrix(actualDF, predictedDF);
        for (String actualCls : matrix.keySet()) {
            System.out.print(actualCls + " : ");
            for (int val : matrix.get(actualCls).values()) System.out.print(val + " ");
            System.out.println();
        }
        System.out.println("---------------------------------");
    }
}
