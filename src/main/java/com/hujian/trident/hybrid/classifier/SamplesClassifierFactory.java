package com.hujian.trident.hybrid.classifier;

import com.hujian.trident.ml.classifier.Classifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/3/11.
 */
public class SamplesClassifierFactory<T> implements IClassifierFactory<T> {

    private static final long serialVersionUID = -99001213123419010L;

    private static SamplesClassifierFactory instance = null;

    /**
     * the classifiers
     */
    private List<Classifier<T>> classifierList = null;
    private List<Double> weights = null;

    public Long right;
    public Long error;

    private SamplesClassifierFactory(){
        this.classifierList = new ArrayList<>();
        this.weights = new ArrayList<>();
        this.right = 0L ;
        this.error = 0L ;
    }

    /**
     * get the instance
     * @return
     */
    public static SamplesClassifierFactory getInstance(){
        if( instance == null ){
            instance = new SamplesClassifierFactory();
        }
        return instance;
    }


    @Override
    public List<Classifier<T>> getClassifiers() {
        return this.classifierList;
    }

    @Override
    public void addClassifier(Classifier<T> classifier) {
        this.classifierList.add( classifier );
        this.weights.add( 0.0 );
    }

    @Override
    public List<Double> getWeight() {
        return this.weights;
    }

    @Override
    public Long rightCount() {
        return this.right;
    }

    @Override
    public void setRight(Long c) {
        this.right += c;
    }

    @Override
    public Long errorCount() {
        return this.error;
    }

    @Override
    public void setError(Long c) {
        this.error += c;
    }

    public List<Double> getWeights(){
        return this.weights;
    }
}
