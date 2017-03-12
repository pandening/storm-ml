package com.hujian.trident.ml.cardinality;

import com.hujian.trident.ml.help.IHelp;

/**
 * Created by hujian on 2017/3/8.
 */
public interface ICardinality<T> extends IHelp {

    /**
     * return the unique item's count.
     * @return
     */
    Long cardinality();

    /**
     * update the model by the new item.
     * @param item
     */
    void update(T item);

    /**
     * merge the different model,return the new model.
     * @param cardinalityList
     * @return
     */
    ICardinality<T> merge(ICardinality<T> ... cardinalityList);

    /**
     * get the bucket's size
     * @return
     */
    int bucketSize();
}
