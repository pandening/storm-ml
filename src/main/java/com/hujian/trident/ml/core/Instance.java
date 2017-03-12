package com.hujian.trident.ml.core;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by hujian on 2017/2/23.
 */
public class Instance<L> implements Serializable{

    private static final long serialVersionUID = 564356221239875601L;

    public final double[] features;
    private L label;
    private InputDataType dataType;

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
     *
     * @param features
     */
    public Instance(double[] features){
        this.features = features;
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
}
