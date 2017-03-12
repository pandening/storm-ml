package com.hujian.trident.ml.frequency;

/**
 * Created by hujian on 2017/3/6.
 */
public interface ISimpleFrequency<T> extends IBaseFrequency<T> {

    /**
     * Estimate the frequency for the item
     * @param item
     * @return
     */
    long estimate(T item);

    /**
     * if the database contains the item.
     * @param item
     * @return  true means contains,or no.
     */
    boolean contains( T item);

}
