package com.hujian.trident.ml.average;

import com.hujian.trident.ml.help.IHelp;

/**
 * Created by hujian on 2017/3/9.
 * just for double.
 */
public interface IAverage extends  IHelp{

    /**
     * update the model
     * @param value
     */
    void update( Double value );

    /**
     * get the average of now.
     * @return
     */
    Double average();

    /**
     * reset the average model
     */
    void reset();
}
