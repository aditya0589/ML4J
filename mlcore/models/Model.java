package mlcore.models;
import mlcore.dataframe.DataFrame;

public abstract class Model {
    // similar to fit in sklearn. used to train the model.
    public abstract void train(DataFrame x, DataFrame y);

    // use the trained model to predict the outputs using the test data
    public abstract DataFrame predict(DataFrame X);

    // a basic metric which will be used to evaluate the model. 
    // advanced evaluation metrics are to be implemented in mlcore.metrics.
    public double score(DataFrame x, DataFrame y) {
        return 0.0;
    }

}
