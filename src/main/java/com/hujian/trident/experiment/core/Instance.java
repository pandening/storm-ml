package com.hujian.trident.experiment.core;

import com.hujian.trident.ml.core.InputDataType;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by hujian on 2017/3/14.
 */
public class Instance<L> implements Serializable{

    private static final long serialVersionUID = 564356221239875601L;

    private     L data;
    private     Long instanceId;
    private     InputDataType dataType;

    /**
     * give this data a type
     * @param dataType
     * @param id
     * @param data
     */
    public Instance(L data,Long id,InputDataType dataType){
        this.data = data;
        this.instanceId = id;
        this.dataType = dataType;
    }


    @Override
    public String toString(){
        return "["+instanceId+"]"+data;
    }

    public InputDataType getDataType() {
        return dataType;
    }

    public void setDataType(InputDataType dataType) {
        this.dataType = dataType;
    }

    public L getData() {
        return data;
    }

    public void setData(L data) {
        this.data = data;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }
}
