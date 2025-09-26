import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mlcore.dataframe.DataFrame;

public class Main {
    public static void main(String[] args) {
        Map<String, List<Object>> data = new HashMap<>();
        data.put("Name", Arrays.asList("Alice", "Bob", "Charlie"));
        data.put("Age", Arrays.asList(21, 22, 23));
        data.put("City", Arrays.asList("NY", "LA", "Chicago"));

        DataFrame df = new DataFrame(data);
        int rows = df.getCountRows();
        int cols = df.getCountCols();
        System.out.println(df);
        System.out.println(rows);
        System.out.println(cols);
        System.out.println(df.getData());

        DataFrame df2 = DataFrame.readCSV("C:/Aditya/Computer Science/Machine Learning/Datasets/cars-data.csv", ",");
        System.out.println(df2.getData());
    }
}
