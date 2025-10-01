package mlcore.dataframe.transformations;

import mlcore.dataframe.DataFrame;
import mlcore.dataframe.utils.StatsUtils;

public class OutlierHandler {

    public Double IQR(DataFrame df, String columnName, StatsUtils stat) {
        Double Q1 = stat.Q1Value(df, columnName);
        Double Q3 = stat.Q3Value(df, columnName);
        Double iqr = Q3 - Q1;
        return iqr;
    }
    public DataFrame RemoveOutliers(DataFrame df) {
        // takes a dataframe as input, returns a new dataframe with all outliers removed
    }
}
