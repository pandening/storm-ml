package com.hujian.trident.experiment.data;

import java.io.Serializable;

/**
 * Created by hujian on 2017/3/17.
 */
public class IDUtils implements Serializable{

    private static final long serialVersionUID = 908042L;

    private static IDUtils ourInstance = new IDUtils();

    private Long id = 0L;

    public static IDUtils getInstance() {
        return ourInstance;
    }

    private IDUtils() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * increment the id
     * @param step
     */
    public void increaseID(Long step){
        this.id += step;
    }
}
