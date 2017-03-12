package com.hujian.trident.hybrid.classifier;

import com.hujian.trident.ml.classifier.Classifier;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hujian on 2017/3/11.
 */
public interface IClassifierFactory<T> extends Serializable {

    /**
     * get the classifiers we have
     * @return
     */
    List<Classifier<T>> getClassifiers();

    /**
     * add a classifier to the list
     * @param classifier
     */
    void addClassifier(Classifier<T> classifier);

    /**
     * get the weight vector
     * @return
     */
    List<Double> getWeight();

    /**
     * get the right count
     * @return
     */
    Long rightCount();

    /**
     * set
     * @param c
     */
    void setRight(Long c);

    /**
     * get the error right
     * @return
     */
    Long errorCount();

    /**
     * set error count
     * @param c
     */
    void setError(Long c);
}
