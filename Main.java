import java.util.ArrayList;
import java.util.List;
import mlcore.dataframe.DataFrame;
import mlcore.dataframe.transformations.OutlierHandler;
import mlcore.dataframe.transformations.Scaler;
import mlcore.dataframe.transformations.Splitter;
import mlcore.models.LinearRegression;

public class Main {
    public static void main(String[] args) {
        // Load dataset
        DataFrame df = DataFrame.readCSV("dataset.csv", ",");
        
        OutlierHandler outlierHandler = new OutlierHandler();
        List<String> numericColumns = new ArrayList<>(df.getData().keySet());
        for(String col : numericColumns) {
            try {
                df = outlierHandler.RemoveOutliers(df, col);
            } catch (Exception e) {
                System.out.println("Skipping column (likely non-numeric): " + col);
            }
        }

        String targetColumn = "quality";
        DataFrame X = df.dropColumn(targetColumn);
        DataFrame y = df.getColumn(targetColumn);

        Scaler scaler = new Scaler();
        List<String> featureColumns = new ArrayList<>(X.getData().keySet()); 
        for(String col : featureColumns) {
            try {
                X = scaler.RobustScaler(X, col);
            } catch (Exception e) {
                System.out.println("Skipping column (non-numeric): " + col);
            }
        }

        Splitter splitter = new Splitter();
        var split = splitter.trainTestSplit(X, y, 0.8, 42);
        DataFrame X_train = split.get("X_train");
        DataFrame X_test = split.get("X_test");
        DataFrame y_train = split.get("y_train");
        DataFrame y_test = split.get("y_test");

        LinearRegression model = new LinearRegression(0.001, 3000);
        model.train(X_train, y_train);

        DataFrame predictions = model.predict(X_test);
        System.out.println("Predictions:");
        predictions.display();

    }
}

