package com.hujian.trident.ml.classifier;

import com.hujian.trident.ml.core.InputDataType;
import com.hujian.trident.ml.help.IHelp;

import java.util.List;

/**
 * Created by hujian on 2017/2/26.
 */
public interface Classifier<L> extends IHelp{

    /**
     * get the right count
     * @return
     */
    Integer getRightCount();

    /**
     * get error count
     * @return
     */
    Integer getErrorCount();

    /**
     * classify the given features
     * @param features
     * @return
     */
    L classify(double[] features);

    /**
     * multi classify
     * @param featuresList
     * @return
     */
    List<L> multiClassify( List<double[]> featuresList );

    /**
     * update the classify model
     * @param label
     * @param features
     * @param dataType
     */
    void update(L label, double[] features , InputDataType dataType);

    /**
     * multi update
     * @param labelList
     * @param featuresList
     */
    void multiUpdate( List<L> labelList,List<double[]> featuresList );

    /**
     * reset the classify model
     */
    void reset();
}
