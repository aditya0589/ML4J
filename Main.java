import mlcore.dataframe.DataFrame;
import mlcore.dataframe.transformations.Encoder;

public class Main {
    public static void main(String[] args) {
        // Map<String, List<Object>> data = new HashMap<>();
        // data.put("Name", Arrays.asList("Alice", "Bob", "Charlie"));
        // data.put("Age", Arrays.asList(21, 22, 23));
        // data.put("City", Arrays.asList("NY", "LA", "Chicago"));

        // DataFrame df = new DataFrame(data);
        // int rows = df.getCountRows();
        // int cols = df.getCountCols();
        // System.out.println(df);
        // System.out.println(rows);
        // System.out.println(cols);
        // System.out.println(df.getData());

        DataFrame df2 = DataFrame.readCSV("C:/Aditya/Computer Science/Machine Learning/Datasets/cars-data.csv", ",");
        System.out.println(df2.getData());

        // DataFrame df3 = df2.withColumn("values", Arrays.asList(1,2,3,4,5,6));
        // System.out.println("df3:");
        // df3.display();

        // data = new HashMap<>();
        // DataFrame example = new DataFrame(data);
        // DataFrame df4 = example.withColumn("values", Arrays.asList(1,2,3,44,55,66));
        // df4.display();

        // DataFrame df5 = df4.withColumnReplaced("values", "numbers", Arrays.asList(1,2,3,4,5,6,7));
        // df5.display();

        // DataFrame df6 = df5.head().mergeDataFrameColumns(df4.head());
        // df6.display();

        df2.head().display();
        Encoder e = new Encoder();
        DataFrame df3 = e.oneHotEncoding(df2, "Model");
        System.out.println(df3.head().getData());

    }
}
