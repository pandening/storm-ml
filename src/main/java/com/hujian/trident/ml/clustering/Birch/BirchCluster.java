package com.hujian.trident.ml.clustering.Birch;

import java.io.Serializable;

/**
 * Created by hujian on 2017/2/25.
 */
public interface BirchCluster {

    /**
     * using the new features to update the birch cluster model.
     * actually,the new feature will build a b tree in memory.
     * i don't know whether right to implement the birch cluster
     * algorithm in storm platform.but i think it will work.
     * @param features
     * @return
     */
    TreeNode update(double[] features);

}
