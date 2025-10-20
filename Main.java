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

        // 1️⃣ Remove outliers for numeric columns (e.g., "alcohol", "pH", "residual sugar")
        OutlierHandler outlierHandler = new OutlierHandler();
        List<String> numericColumns = new ArrayList<>(df.getData().keySet()); // adjust if needed
        for(String col : numericColumns) {
            try {
                df = outlierHandler.RemoveOutliers(df, col);
            } catch (Exception e) {
                System.out.println("Skipping column (likely non-numeric): " + col);
            }
        }

        // 2️⃣ Separate features and target
        String targetColumn = "quality";
        DataFrame X = df.dropColumn(targetColumn);
        DataFrame y = df.getColumn(targetColumn);  // returns a DataFrame

        // 3️⃣ Scale numeric features using RobustScaler safely
        Scaler scaler = new Scaler();
        List<String> featureColumns = new ArrayList<>(X.getData().keySet()); // snapshot
        for(String col : featureColumns) {
            try {
                X = scaler.RobustScaler(X, col);
            } catch (Exception e) {
                System.out.println("Skipping column (non-numeric): " + col);
            }
        }

        // 4️⃣ Train-test split
        Splitter splitter = new Splitter();
        var split = splitter.trainTestSplit(X, y, 0.8, 42);
        DataFrame X_train = split.get("X_train");
        DataFrame X_test = split.get("X_test");
        DataFrame y_train = split.get("y_train");
        DataFrame y_test = split.get("y_test");

        // 5️⃣ Train Linear Regression model
        LinearRegression model = new LinearRegression(0.001, 3000);
        model.train(X_train, y_train);

        // 6️⃣ Predict on test set
        DataFrame predictions = model.predict(X_test);
        System.out.println("Predictions:");
        predictions.display();

        // 7️⃣ Optional: Evaluate model (e.g., RMSE)
        // StatsUtils utils = new StatsUtils();
        // double rmse = utils.rmse(y_test, predictions);
        // System.out.println("RMSE: " + rmse);
    }
}
