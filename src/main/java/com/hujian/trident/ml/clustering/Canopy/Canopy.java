package com.hujian.trident.ml.clustering.Canopy;

import java.util.List;

/**
 * Created by hujian on 2017/2/24.
 */
public interface Canopy {

    /**
     * using canopy algorithm to determine the k-means' k value.
     * @return
     */
    Integer clusterCount();

    /**
     * add the features to a canopy.and return the canopy id.
     * @param features
     * @return
     */
    Integer addToCanopies(double[] features);

    /**
     * just run a loop to run this.addToCanopies
     * @param featuresList
     * @return
     */
    List<Integer> multiAddToCanopies( List<double[]> featuresList );

}
