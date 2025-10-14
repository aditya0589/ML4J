package mlcore.dataframe.transformations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mlcore.dataframe.utils.*;
import mlcore.dataframe.DataFrame;

public class Scaler {

    public DataFrame MinMaxScaler(DataFrame df, String columnName) {
        if (!df.getData().containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }

        StatsUtils st = new StatsUtils();
        List<Object> columnValues = df.getData().get(columnName);
        List<Object> newCol = new ArrayList<>();

        double minValue = st.minimumValue(df, columnName);
        double maxValue = st.maximumValue(df, columnName);

        for (Object val : columnValues) {
            if (val == null) {
                newCol.add(null); // keep structure consistent
                continue;
            }

            double x = Double.valueOf(val.toString());
            newCol.add((x - minValue) / (maxValue - minValue));
        }

        DataFrame newDF = df.withColumn(columnName + "_scaled", newCol);
        newDF.InPlaceRemoveColumn(columnName);
        
        return newDF;
    }

    public DataFrame inverseMinMaxScaler(DataFrame df, String columnName, double originalMin, double originalMax) {
        if (!df.getData().containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }

        List<Object> columnValues = df.getData().get(columnName);
        List<Object> newCol = new ArrayList<>();

        for (Object val : columnValues) {
            if (val == null) {
                newCol.add(null); // maintain alignment
                continue;
            }

            double xScaled = Double.valueOf(val.toString());
            double xOriginal = (xScaled * (originalMax - originalMin)) + originalMin;
            newCol.add(xOriginal);
        }

        // Create new column instead of replacing/removing
        String inverseColumnName = columnName.replace("_scaled", "");

        DataFrame newDF = df.withColumn(inverseColumnName, newCol);
        newDF.InPlaceRemoveColumn(columnName);
        return newDF;
    }


    public DataFrame StandardScaler(DataFrame df, String columnName) {
        if (!df.getData().containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }

        StatsUtils st = new StatsUtils();
        List<Object> columnValues = df.getData().get(columnName);
        List<Object> newCol = new ArrayList<>();

        double mean = st.meanColumn(df, columnName);
        double sd = st.sdValue(df, columnName);

        for (Object val : columnValues) {
            if (val == null) {
                newCol.add(null); // keep structure consistent
                continue;
            }

            double x = Double.valueOf(val.toString());
            newCol.add((x - mean) / sd);
        }

        DataFrame newDF = df.withColumn(columnName + "_scaled", newCol);
        newDF.InPlaceRemoveColumn(columnName);
        
        return newDF;
    }

    public DataFrame inverseStandardScaler(DataFrame df, String columnName, double originalMean, double originalSD) {
        if (!df.getData().containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }

        List<Object> columnValues = df.getData().get(columnName);
        List<Object> newCol = new ArrayList<>();

        for (Object val : columnValues) {
            if (val == null) {
                newCol.add(null); // maintain alignment
                continue;
            }

            double xScaled = Double.valueOf(val.toString());
            double xOriginal = (xScaled * originalSD) + originalMean; // revert scaling
            newCol.add(xOriginal);
        }

        // Create new column instead of replacing/removing
        String inverseColumnName = columnName.replace("_scaled", "");

        DataFrame newDF = df.withColumn(inverseColumnName, newCol);
        newDF.InPlaceRemoveColumn(columnName);
        return newDF;
    }

    public DataFrame RobustScaler(DataFrame df, String columnName) {
        if (!df.getData().containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }

        StatsUtils st = new StatsUtils();
        List<Object> columnValues = df.getData().get(columnName);
        List<Object> newCol = new ArrayList<>();

        double median = st.medianColumn(df, columnName);
        double q1 = st.Q1Value(df, columnName);
        double q3 = st.Q3Value(df, columnName);
        double iqr = q3 - q1;

        // Handle edge case where IQR is zero
        if (iqr == 0) {
            iqr = 1; // avoids division by zero, effectively returns centered values
        }

        for (Object val : columnValues) {
            if (val == null) {
                newCol.add(null);
                continue;
            }

            double x = Double.valueOf(val.toString());
            newCol.add((x - median) / iqr);
        }

        DataFrame newDF = df.withColumn(columnName + "_scaled", newCol);
        newDF.InPlaceRemoveColumn(columnName);

        return newDF;
    }

    public DataFrame inverseRobustScaler(DataFrame df, String columnName, double originalMedian, double originalIQR) {
        if (!df.getData().containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " not found");
        }

        List<Object> columnValues = df.getData().get(columnName);
        List<Object> newCol = new ArrayList<>();

        // Handle edge case where IQR is zero
        if (originalIQR == 0) {
            originalIQR = 1;
        }

        for (Object val : columnValues) {
            if (val == null) {
                newCol.add(null); // maintain alignment
                continue;
            }

            double xScaled = Double.valueOf(val.toString());
            double xOriginal = (xScaled * originalIQR) + originalMedian;
            newCol.add(xOriginal);
        }

        // Revert column name
        String inverseColumnName = columnName.replace("_scaled", "");

        DataFrame newDF = df.withColumn(inverseColumnName, newCol);
        newDF.InPlaceRemoveColumn(columnName);

        return newDF;
    }

}
