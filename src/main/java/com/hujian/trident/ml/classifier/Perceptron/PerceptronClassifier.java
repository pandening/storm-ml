package com.hujian.trident.ml.classifier.Perceptron;

import com.hujian.trident.ml.classifier.Classifier;
import com.hujian.trident.ml.core.InputDataType;
import com.hujian.trident.ml.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/2/27.
 * @link http://www.cnblogs.com/jerrylead/archive/2011/04/18/2020173.html
 */
public class PerceptronClassifier implements Classifier<Boolean> {

    private static final long serialVersionUID = -103640004534251498L;

    /**
     * the PerceptronClassifier algorithm. see website follow.
     * ref: @link {http://www.cnblogs.com/fuleying/p/4487215.html}
     * and ref trident-ml.(a copy codes just)
     */
    private double[] weights;
    private double bias;
    private double threshold;
    private double learningRate;

    /**
     * auto-constructor
     */
    public PerceptronClassifier() {
        bias = 0.0;
        threshold = 0.5;
        learningRate = 0.1;
    }

    /**
     *
     * @param bias
     * @param threshold
     * @param learningRate
     */
    public PerceptronClassifier( double bias,double threshold , double learningRate ){
        this.bias = bias;
        this.threshold = threshold;
        this.learningRate = learningRate;
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
        if( this.weights == null ){
            this.weights = new double[features.length];
        }
        double evaluation = MathUtils.dot(features,weights) + this.bias;

        return evaluation > this.threshold ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public List<Boolean> multiClassify(List<double[]> featuresList) {
        if( featuresList == null || featuresList.size() == 0 ){
            return null;
        }

        List<Boolean> result = new ArrayList<Boolean>();

        //using a loop to run this.classify
        for( double[] features: featuresList ){
            result.add( this.classify( features ) );
        }
        return result;
    }

    @Override
    public void update(Boolean label, double[] features,InputDataType dataType) {
        Boolean prediction = this.classify( features );

        if( !label.equals( prediction ) ){
            Double errorValue = Boolean.TRUE.equals( label ) ? 1.0: - 1.0;
            //correction and update
            double update;
            for( int i = 0 ;i < features.length; i ++ ){
                update = features[i] * errorValue * this.learningRate;
                this.weights[ i ] += update;
            }
        }

        /**
         *           DEBUG AREA
         *   remove these code if you want
         */
        /*System.out.println("call the function update with param =>");
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
        }*/
    }

    @Override
    public void multiUpdate(List<Boolean> labelList, List<double[]> featuresList) {
        if( labelList == null || labelList.isEmpty() ){
            return;
        }
        for( int i = 0 ; i < labelList.size() ;  i ++){
            this.update( labelList.get( i ), featuresList.get( i ) ,null);
        }
    }

    @Override
    public void reset() {
        this.weights = null;
    }


    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
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
