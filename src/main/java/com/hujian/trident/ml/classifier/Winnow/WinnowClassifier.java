package com.hujian.trident.ml.classifier.Winnow;

import com.hujian.trident.ml.classifier.Classifier;
import com.hujian.trident.ml.core.InputDataType;
import com.hujian.trident.ml.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/2/27.
 * winnow algorithm reference
 *
 * @link https://my.oschina.net/sulliy/blog/807705
 * @link http://lbxc.iteye.com/blog/1515018
 * @link http://pages.cs.wisc.edu/~shuchi/courses/787-F07/scribe-notes/lecture24.pdf
 * @link https://en.wikipedia.org/wiki/Winnow_(algorithm)
 */
public class WinnowClassifier implements Classifier<Boolean> {

    private static final long serialVersionUID = -908641234534251498L;

    private double[] weights;
    private double threshold = 1.0;
    private double e = 0.5;

    public WinnowClassifier(){

    }

    /**
     * the constructor
     * @param threshold
     * @param e
     */
    public WinnowClassifier( double threshold, double e ){
        this.threshold = threshold;
        this.e = e;
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
        //init the weight
        if( this.weights == null ){
            this.weights = new double[features.length];
            for( int i = 0 ; i < features.length ;  i ++ ){
                this.weights[ i] = this.threshold / features.length;
            }
        }
        Double evaluation = MathUtils.dot(features,this.weights);
        return evaluation >= this.threshold ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public List<Boolean> multiClassify(List<double[]> featuresList) {
        if( featuresList == null || featuresList.size() == 0 ){
            return null;
        }
        List<Boolean> resultList = new ArrayList<Boolean>();
        for( double[] d: featuresList ){
            resultList.add( this.classify( d ) );
        }

        return resultList;
    }

    @Override
    public void update(Boolean label, double[] features,InputDataType dataType) {
        //predict the features' class
        Boolean prediction = this.classify( features );

        //the winnow classifier model just update while a mistake is made
        if( !label.equals( prediction ) ){
            double updateE = 1.0 + (prediction ? - this.e :this.e);
            for( int i = 0 ; i < features.length;i ++ ){
                this.weights[i] *= updateE;
            }
        }
        /**
         *           DEBUG AREA
         *   remove these code if you want
         */
        System.out.println("call the function update with param =>");
        System.out.print(" [ "+label+"  ");
        for( double d: features ){
            System.out.print(d+" ");
        }
        System.out.print(" ]\n weights=>");
        for( double d: this.weights ){
            System.out.print(d+"  ");
        }
        System.out.print("\npredict result=>");
        if( prediction.equals( label ) ){
            System.out.print(" right\n");
        }else{
            System.out.print("error\n");
        }
    }

    @Override
    public void multiUpdate(List<Boolean> labelList, List<double[]> featuresList) {
        if( featuresList == null || featuresList.size() == 0 ){
            return;
        }
        for( int i = 0 ;i < featuresList.size(); i ++ ){
            this.update( labelList.get( i ), featuresList.get( i ),null );
        }
    }

    @Override
    public void reset() {
        this.weights = null;
    }

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getE() {
        return e;
    }

    public void setE(double e) {
        this.e = e;
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
