package com.hujian.trident.ml.clustering.Kmeans;

import com.hujian.trident.ml.help.IHelp;

import java.util.List;

/**
 * Created by hujian on 2017/2/23.
 */
public interface Cluster extends IHelp {

    /**
     * return the cluster index for the given feature.
     * using when train the cluster model.
     * this function do not update the cluster model.
     * @param features
     * @return
     */
    Integer classify( double [] features );

    /**
     * sometimes,we need get some features's cluster index.
     * you can use this function to get multi features cluster
     * index after classify action.
     * @param features
     * @return
     */
    List<Integer> multiClassify( List< double[] > features );

    /**
     * return the cluster index for the given feature.
     * using when test/use cluster model.
     * this function will update the old cluster model.
     * @param features
     * @return
     */
    Integer update( double [] features );

    /**
     * you can use this function to get multi features cluster index.
     * and update the cluster model.
     * @param features
     * @return
     */
    List< Integer > multiUpdate( List<double [] >  features);

    /**
     * return the cluster model's centroids
     * @return
     */
    double [][] getCentroids();

    /**
     * reset the cluster model.
     */
    void reset();

}
