package com.hujian.trident.ml.frequency;

import java.io.Serializable;

/**
 * Created by hujian on 2017/3/6.
 */
public class CountEntry<T> implements Serializable ,Comparable<CountEntry> ,Cloneable {

    private static final long serialVersionUID = 90872656L;

    public T item;
    private long frequency;

    /**
     * the constructor
     * @param item
     * @param frequency
     */
    public CountEntry( T item, long frequency ){
        this.item = item;
        this.frequency = frequency;
    }

    /**
     * the simple constructor, the new instance with no frequency
     * @param item
     */
    public CountEntry( T item ){
        this(item,0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof CountEntry)) {
            return false;
        }

        CountEntry other = (CountEntry) obj;
        return this.item.equals(other.item);
    }

    @Override
    public int compareTo(CountEntry o) {
        long x = o.getFrequency();
        long y = frequency;
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    @Override
    public CountEntry<T> clone() throws CloneNotSupportedException {
        try {
            CountEntry<T> clone = (CountEntry<T>)super.clone();
            return clone;
        } catch(CloneNotSupportedException e) {
            return null;
        }
    }
    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }
    @Override
    public String toString() {
        return "CountEntry[item=" + item + ", freq=" + frequency + "]";
    }

}
