package com.hujian.trident.ml.frequency;

/**
 * Created by hujian on 2017/3/6.
 */
public class CountEntryWithMaxError<T> extends CountEntry<T> {

    private static final long serialVersionUID = -9087625001L;

    public long maxError = 0;

    /**
     *
     * @param item
     * @param frequency
     * @param maxError
     */
    public CountEntryWithMaxError(T item, long frequency,long maxError) {
        super(item, frequency);
        this.maxError = maxError;
    }

    /**
     *
     * @param item
     * @param maxError
     */
    public CountEntryWithMaxError(T item,long maxError) {
        super(item);
        this.maxError = maxError;
    }

    @Override
    public CountEntryWithMaxError<T> clone() throws CloneNotSupportedException {
        return (CountEntryWithMaxError<T>)super.clone();
    }

    @Override
    public String toString() {
        return "CountEntryWithMaxError[item=" + item + ", freq=" + this.getFrequency() + "]";
    }
}
