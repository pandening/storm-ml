package com.hujian.trident.ml.frequency.CountSketch;

import com.hujian.trident.ml.help.IHelp;

/**
 * Created by hujian on 2017/3/7.
 *
 * @link http://jingpin.jikexueyuan.com/article/47011.html
 * @link https://github.com/tiepologian/CountSketch/
 *
 */
public interface ICountSketch<T extends Comparable> extends IHelp {

    /**
     * add an item into the database with the increment value
     * @param item
     * @param increment
     * @return
     */
    boolean add(T item, Long increment);

    /**
     * get the item's frequency
     * @param item
     * @return
     */
    long frequency(T item);

}
