package com.hujian.trident.ml.regression.Perceptron;

import com.hujian.trident.ml.regression.RegressOr;
import com.hujian.trident.ml.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/3/1.
 * @ref com.hujian.trident.ml.classifier.Perceptron package
 *
 */
public class PerceptronRegression implements RegressOr {

    private static final long serialVersionUID = -103640004534121412L;

    private double[] weights;
    private double bias = 0.0;
    private double learningRate = 0.1;

    public PerceptronRegression(){}

    /**
     *
     * @param bias
     * @param learningRate
     */
    public PerceptronRegression(double bias,double learningRate){
        this.bias = bias;
        this.learningRate = learningRate;
    }


    @Override
    public Double predict(double[] features) {
        //init the weight if null
        if( this.weights == null ){
            this.weights = new double[ features.length ];
        }
        return MathUtils.dot( features, this.weights ) + this.bias;
    }

    @Override
    public List<Double> multiPredict(List<double[]> featuresList) {
        if( featuresList == null || featuresList.size() == 0 ){
            return null;
        }
        List<Double> predictionList = new ArrayList<Double>();
        for( double[] features: featuresList ){
            predictionList.add( this.predict( features ) );
        }
        return predictionList;
    }

    @Override
    public void update(Double expectValue, double[] features) {
        //get the prediction value
        Double predictionValue = this.predict( features );

        //update the regression model
        Double errorValue = expectValue - predictionValue;

        for( int i = 0 ; i < features.length ; i ++ ){
            Double updates = features[i]  * errorValue * this.learningRate;
            this.weights[ i ] += updates;
        }
        /**
         * DEBUG AREA
         */
        System.out.print("weights:\n");
        for( double d:this.weights ){
            System.out.println(d);
        }
    }

    @Override
    public void multiUpdate(List<Double> expectValueList, List<double[]> featuresList) {
        if( expectValueList == null || expectValueList.size() == 0 ){
            return ;
        }
        for( int i = 0; i < featuresList.size() ; i ++ ){
            this.update( expectValueList.get( i ), featuresList.get( i ) );
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

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
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
