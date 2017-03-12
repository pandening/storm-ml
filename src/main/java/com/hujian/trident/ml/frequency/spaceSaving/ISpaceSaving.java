package com.hujian.trident.ml.frequency.spaceSaving;

import com.hujian.trident.ml.help.IHelp;

import java.util.List;
import java.util.Map;

/**
 * Created by hujian on 2017/3/6.
 */
public interface ISpaceSaving< T extends Comparable > extends IHelp {
    /**
     * increase the item's frequency
     * @param item
     * @param increment
     * @return
     */
    Boolean add( T item , Long increment);

    /**
     * get the item's frequency
     * @param item
     * @return
     */
    Long frequency(T item);

    /**
     * multi increase
     * @param items
     * @param increments
     * @return
     */
    List<Boolean> multiAdd(List<T> items,List<Long> increments);

    /**
     * get the top k elements from the map
     * @param k
     * @return
     */
    List<Map.Entry<T,Long>> peek(int k);

    /**
     * get the total items count.
     * @return
     */
    Long totalItemsReach();

}
