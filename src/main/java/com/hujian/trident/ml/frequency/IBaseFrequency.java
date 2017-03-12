package com.hujian.trident.ml.frequency;

import java.io.Serializable;

/**
 * Created by hujian on 2017/3/6.
 */
public interface IBaseFrequency<T> extends Serializable{

    /**
     * you want to add an item to the database of memory?
     * @param item
     * @return return true if success.or fail.
     */
    boolean add( T item );

    /**
     * add an element to the data base and get the count by
     * increment value. true means operator successfully
     * @param item
     * @param increment
     * @return
     */
    boolean add( T item, long increment );

    /**
     * the total items in the database
     * @return
     */
    long size();

}
