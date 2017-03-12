package com.hujian.trident.hybrid.data;

import com.hujian.trident.ml.core.InputDataType;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by hujian on 2017/3/11.
 */
public class Instance<L> implements Serializable {

    private static final long serialVersionUID = -8910212341901102L;

    public final double[] features;
    private L label;
    private InputDataType dataType;
    private Long instanceId;

    /**
     * give this data a type
     * @param label
     * @param features
     * @param dataType
     */
    public Instance(L label,double[] features,InputDataType dataType){
        this.features = features;
        this.label = label;
        this.dataType = dataType;
    }

    /**
     *
     * @param label
     * @param features
     */
    public Instance(L label,double[] features){
        this.features = features;
        this.label= label;
    }

    /**
     * with instance id
     * @param label
     * @param features
     * @param id
     */
    public Instance(Long id,L label,double[] features){
        this.features = features;
        this.label= label;
        this.instanceId = id;
    }

    /**
     *
     * @param features
     */
    public Instance(double[] features){
        this.features = features;
    }


    /**
     * @param id
     * @param features
     */
    public Instance(Long id,double[] features){
        this.features = features;
        this.instanceId = id;
    }

    public double[] getFeatures(){
        return this.features;
    }

    @Override
    public String toString(){
        return Arrays.toString( this.features );
    }

    public L getLabel() {
        return label;
    }

    public void setLabel(L label) {
        this.label = label;
    }

    public InputDataType getDataType() {
        return dataType;
    }

    public void setDataType(InputDataType dataType) {
        this.dataType = dataType;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }
}
