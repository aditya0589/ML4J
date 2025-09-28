package mlcore.dataframe.transformations;

public class Scaler {
    // This class provides methods for performing feature scaling of the data
    /*
     * Feature Scaling is done in two ways:
     * 1. Standardization (also called as z score normalization):
     *      This is calculated by the formula 
     *              z = x - μ / σ 
     *         where x = original value of the feature
     *               μ = Mean of the data
     *               σ = standard deviation of the data
     * 
     * 2. Normalization (also called as min-max scaling)
     *      This is calculated by the formula 
     *              Xnorm = (X - Xmin) / Xmax - Xmin
     *          where 
     *              Xnorm = normalized value
     *              X = original feature value
     *              Xmin = minimal value of the feature
     *              Xmax = maximum value of the feature   
     */

     // The input of the methods of this class are of type DataFrame (mlcore.dataframe.DataFrame)
     // The output of the methods of this class are of type DataFrame (mlcore.dataframe.DataFrame)
}
