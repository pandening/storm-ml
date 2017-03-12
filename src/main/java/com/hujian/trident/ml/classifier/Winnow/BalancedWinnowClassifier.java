package com.hujian.trident.ml.classifier.Winnow;

import com.hujian.trident.ml.classifier.Classifier;
import com.hujian.trident.ml.core.InputDataType;
import com.hujian.trident.ml.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/2/27.
 * ref:
 * @paper Single-Pass Online Learning: Performance, VotingSchemes and Online Feature Selection
 *
 * this paper offers an implement of balanced winnow classify algorithm.check out this paper
 * by search on google search with key word the paper title
 */
public class BalancedWinnowClassifier implements Classifier<Boolean> {

    private static final long serialVersionUID = -908642234534251498L;

    /**
     * the positive model weight
     */
    protected double[] u;

    /**
     * the negative model weight
     */
    protected double[] v;

    /**
     * the e.  0 < e < 1
     */
    protected double alpha = 0.5;
    protected double beta = 1.5;

    /**
     * the threshold.
     */
    protected double threshold = 1.0;

    /**
     * you just want to use the default model-params
     */
    public BalancedWinnowClassifier(){}

    /**
     *
     * @param alpha
     * @param beta
     * @param threshold
     */
    public BalancedWinnowClassifier( double alpha,double beta, double threshold ){
        this.threshold = threshold;
        this.alpha = alpha;
        this.beta = beta;
    }

    @Override
    public Integer getRightCount() {
        return null;
    }

    @Override
    public Integer getErrorCount() {
        return null;
    }

    @Override
    public Boolean classify(double[] features) {
        //init the u and v if null
        if( this.v == null || this.u == null ){
            int length = features.length;
            this.u = new double[length];
            this.v = new double[length];
            for( int i = 0 ; i < length; i ++ ){
                /**
                 * set u as θ +
                 * set v as θ -
                 */
                this.u[ i ] = 2 * this.threshold / length;
                this.v[ i ] = this.threshold / length;
            }
        }
        /**
         * f = sign( <xt,ui> - <xt,vi> - θth )
         */
        Double evaluation = MathUtils.dot(features,this.u)
                - MathUtils.dot(features,this.v) - this.threshold;
        return evaluation >= 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public List<Boolean> multiClassify(List<double[]> featuresList) {
        if( featuresList == null || featuresList.size() == 0 ){
            return null;
        }
        List<Boolean>  resultList = new ArrayList<Boolean>();
        for( double[] features : featuresList ){
            resultList.add( this.classify( features ) );
        }
        return resultList;
    }

    @Override
    public void update(Boolean label, double[] features,InputDataType dataType) {
        Boolean prediction = this.classify( features );

        //update the classifier model while the prediction is error
        if( ! label.equals( prediction ) ){
            for( int i = 0 ; i < features.length ; i ++ ){
                if( features[i] > 0 ){
                    //demotion update
                    this.u[ i ] *= this.alpha;
                    this.v[ i ] *= this.beta;
                }else{
                    //promotion update
                    this.u[ i ] *= this.beta;
                    this.v[ i ] *= this.alpha;
                }
            }
        }
        /**
         * DEBUG AREA
         */
        System.out.print("call update with param:[");
        for( double d: features ){
            System.out.print(d+" ");
        }
        System.out.print(" ]\nu list\n");
        for( double d: this.u ){
            System.out.print(d+" ");
        }
        System.out.print("\nv list\n");
        for( double d: this.v ){
            System.out.print(d+" ");
        }
        if( prediction.equals( label ) ){
            System.out.print("\n predict right");
        }else{
            System.out.print("\n predict error");
        }
    }

    @Override
    public void multiUpdate(List<Boolean> labelList, List<double[]> featuresList) {
        if( labelList == null || labelList.size() == 0 ){
            return;
        }
        for( int i = 0 ; i < featuresList.size() ; i ++ ){
            this.update( labelList.get( i ) , featuresList.get( i ) ,null);
        }
    }

    @Override
    public void reset() {
        this.u = null;
        this.v = null;
    }

    public double[] getU() {
        return u;
    }

    public void setU(double[] u) {
        this.u = u;
    }

    public double[] getV() {
        return v;
    }

    public void setV(double[] v) {
        this.v = v;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public String help(String function) {
        return null;
    }

    @Override
    public String help(Object type, String var) {
        return null;
    }

}
