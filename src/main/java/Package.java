import java.io.Serializable;

/**
 * Created by hujian on 2017/3/9.
 *
 * induction the total project here.
 */
public class Package implements Serializable {

    private static final long serialVersionUID = 14230240729000901L;

    /**
     * x.y.z tag
     *
     * x is the big version number
     * y is the small version number
     * z is the update times of version y.
     * tag is a string in ( a,b,s )
     * a means online test version
     * b means offline and inside test version
     * s means online stable version.
     */
    private static final String PROJECT_VERSION = "0.0.1b";

    /**
     * There are some data stream algorithm about average.
     *
     * 1. MovingAverage  the moving average algorithm.
     * 2. EWMAAverage    the Exponentially Weighted Moving Average algorithm
     */
    private final String PACKAGE_AVERAGE = "com.hujian.trident.ml.average";

    /**
     * cardinality algorithm
     *
     * 1. LinearCounting Cardinality algorithm
     * 2. LogLog Cardinality algorithm
     * 3. HyperLogLog Cardinality algorithm
     * 4. AdapterCounting Cardinality algorithm
     */
    private final String PACKAGE_CARDINALITY = "com.hujian.trident.ml.cardinality";

    /**
     * classifier algorithm
     *
     *  ->com.hujian.trident.ml.classifier.Committee
     *
     *     => Committee classification algorithm
     *
     *  ->com.hujian.trident.ml.classifier.PassiveAggressive
     *
     *     => Passive Aggressive classification algorithm (PA)
     *     => Multi PA algorithm
     *
     *  ->com.hujian.trident.ml.classifier.Perceptron
     *
     *     => Perceptron classification algorithm
     *
     *  ->com.hujian.trident.ml.classifier.Winnow
     *
     *     =>Winnow classifier
     *     =>Balance Winnow Classifier
     *     =>Modify Balance Winnow Classifier
     *
     */
    private final String PACKAGE_CLASSIFIER = "com.hujian.trident.ml.classifier";

    /**
     * Clustering algorithm
     *
     *  ->com.hujian.trident.ml.clustering.Birch
     *
     *    => Birch clustering algorithm (main idea is CF tree)
     *
     *  ->com.hujian.trident.ml.clustering.Canopy
     *     => the canopy cluster model.the original model,you should adapter it to
     *     your own project,it's not works now.
     *     => K-means clustering
     *
     */
    private final String PACKAGE_CLUSTERING = "com.hujian.trident.ml.clustering";

    /**
     * Core Package
     *
     *  the data flow structure,and the instance creator will be defined
     *  in this package,you can find every core source in this package.
     *  the hash function will be moved to hash package soon,now the hash
     *  function will still store here for some reasons.
     *  it's nice for you to remove the code of hash part and let the project
     *  works fine in your own project.
     */
    private final String PACKAGE_CORE = "com.hujian.trident.ml.core";

    /**
     * Examples part
     *
     * you can find many examples codes here,including the usage of each
     * algorithm,and the combine-usage with trident over storm platform.
     * you can run the examples to test the algorithm.
     *                  ......
     */
    private final String PACKAGE_EXAMPLES = "com.hujian.trident.ml.examples";


    /**
     * Frequency Package
     *
     * There are some algorithms about frequency.
     *
     *  ->com.hujian.trident.ml.frequency.CountSketch
     *      =>CountSketch algorithm
     *
     *  ->com.hujian.trident.ml.frequency.lossCounting
     *      =>LossCounting algorithm
     *
     *  ->com.hujian.trident.ml.frequency.SamplingCounting
     *      =>StickySampling Counting algorithm
     *
     *  ->com.hujian.trident.ml.frequency.spaceSaving
     *      =>Space Saving Counting algorithm
     *
     *        +++  TopK algorithm  +++
     *
     *  ->com.hujian.trident.ml.frequency.topKCounting
     *      =>Sample TopK Counting algorithm
     *      =>Frequent Top K algorithm
     */
    private final String PACKAGE_FREQUENCY = "com.hujian.trident.ml.frequency";

    /**
     *   Hash Part
     *
     *  NOTICE: THIS PART IS HASH FUNCTION,THANKS GITHUB & GOOGLE SEARCH ENGINE
     *
     *   =>Jenkins Hash algorithm
     *   =>LookUp3 Hash algorithm
     *   =>Murmur Hash algorithm
     *   =>Murmur3 hash algorithm
     *   =>Spooky-32 hash algorithm
     *   =>spooky-64 hash algorithm
     */
    private final String PACKAGE_HASH = "com.hujian.trident.ml.hash";

    /**
     * Regression package
     *
     *  ->com.hujian.trident.ml.regression.Ftrl
     *      => Ftrl regression algorithm
     *
     *  ->com.hujian.trident.ml.regression.PassiveAggression
     *      => Passive Aggression regression algorithm
     *
     *  ->com.hujian.trident.ml.regression.Perceptron
     *      => Perceptron regression ( also can classification )
     */
    private final String PACKAGE_REGRESSION = "com.hujian.trident.ml.regression";

    /**
     * utils part
     *
     * just some math distance function.and the functions are static
     *
     */
    private final String PACKAGE_UTILS = "com.hujian.trident.ml.utils";


    public String getPACKAGE_AVERAGE() {
        return PACKAGE_AVERAGE;
    }

    public String getPACKAGE_CARDINALITY() {
        return PACKAGE_CARDINALITY;
    }

    public String getPACKAGE_CLASSIFIER() {
        return PACKAGE_CLASSIFIER;
    }

    public String getPACKAGE_CLUSTERING() {
        return PACKAGE_CLUSTERING;
    }

    public String getPACKAGE_CORE() {
        return PACKAGE_CORE;
    }

    public String getPACKAGE_EXAMPLES() {
        return PACKAGE_EXAMPLES;
    }

    public String getPACKAGE_FREQUENCY() {
        return PACKAGE_FREQUENCY;
    }

    public String getPACKAGE_HASH() {
        return PACKAGE_HASH;
    }

    public String getPACKAGE_REGRESSION() {
        return PACKAGE_REGRESSION;
    }

    public String getPACKAGE_UTILS() {
        return PACKAGE_UTILS;
    }
}
