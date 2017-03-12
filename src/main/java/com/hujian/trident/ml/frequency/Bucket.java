package com.hujian.trident.ml.frequency;

/**
 * Created by hujian on 2017/3/6.
 */
public class Bucket<T> extends CountEntryWithMaxError<T>{

    private static final long serialVersionUID = -287019L;

    public Bucket(T item, long frequency, long maxError) {
        super(item, frequency, maxError);
    }

    public Bucket(T item, long maxError) {
        super(item, maxError);
    }

    public void setMaxError( long maxError ){
        this.maxError = maxError;
    }

    public long getMaxError(){
        return this.maxError;
    }

}
