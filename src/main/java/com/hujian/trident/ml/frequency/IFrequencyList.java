package com.hujian.trident.ml.frequency;

import java.util.List;
import java.util.Set;

/**
 * Created by hujian on 2017/3/6.
 */
public interface IFrequencyList<T>  extends IBaseFrequency<T>{

    /**
     * get the total key-set stored in the database now.
     * @return
     */
    Set<T> keySet();

    /**
     * oh,you want to find the k most frequency items .
     * @param k
     * @return
     */
    List<CountEntry<T>> peek( int k );

    /**
     * hith-level peek
     * @param k
     * @param supportValue
     * @return
     */
    List<CountEntry<T>> peek( int k, double  supportValue);

    /**
     * get total items
     * @return
     */
    List<CountEntry<T>> getFrequencyItemsList();

    /**
     * high-level function.
     * @param supportValue
     * @return
     */
    List<CountEntry<T>> getFrequencyItemsList(double supportValue);

}
