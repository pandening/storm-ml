package com.hujian.trident.ml.frequency.TopKCounting;

import com.hujian.trident.ml.frequency.CountEntry;
import com.hujian.trident.ml.frequency.IBaseFrequency;

import java.util.List;

/**
 * Created by hujian on 2017/3/7.
 */
public interface ITopK<T> extends IBaseFrequency<T>{

    /**
     * get the top k items.
     * @param k
     * @return
     */
    List<CountEntry<T>> topK(int k);
}
