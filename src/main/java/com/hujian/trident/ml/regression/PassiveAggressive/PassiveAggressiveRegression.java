package com.hujian.trident.ml.regression.PassiveAggressive;

import com.hujian.trident.ml.regression.RegressOr;
import com.hujian.trident.ml.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/3/1.
 * @paper Online Passive-Aggressive Algorithms
 *  part of Regression.
 *  or search the key words by google
 */
public class PassiveAggressiveRegression implements RegressOr {

    private static final long serialVersionUID = -1829001593120555121L;

    private double[] weights;
    private double ε;

    /**
     * constructor
     * @param ε
     */
    public PassiveAggressiveRegression( double ε){
        this.ε = ε;
    }


    @Override
    public Double predict(double[] features) {
        //init the weight if null
        if( this.weights == null ){
            this.weights = new double[features.length];
        }
        //sample prediction
        return MathUtils.dot(features,this.weights);
    }

    @Override
    public List<Double> multiPredict(List<double[]> featuresList) {
        if( featuresList == null || featuresList.size() == 0 ){
            return null;
        }
        List< Double > predictResult = new ArrayList<Double>();
        for( double[] features: featuresList ){
            predictResult.add( this.predict( features ) );
        }
        return predictResult;
    }

    @Override
    public void update(Double expectValue, double[] features) {
        //get the predict value
        Double prediction = this.predict( features );
        /**
         *          the update model function.
         *                      { 0                       if |w.x - y| <= ε
         *  Loss{ W(x,y)} =     {
         *                      { |w.x - y | - ε         otherwise
         *
         *   W(t+1) =  W (t) + sign(Yi - YYi) * pi * xi  pi = loss / |x| ^2
         *
         */
        Double sign = (expectValue - prediction) > 0.0 ? 1.0 : - 1.0;
        double dis =  Math.abs(prediction - expectValue);
        double loss = 0.0;
        if( dis > this.ε ){
            loss = Math.abs( dis ) - this.ε;
        }
        double pi = loss / Math.pow( MathUtils.powerNorm(features) , 2 );
        double [] updates = MathUtils.multi( features, sign * pi );

        //update the weight
        this.weights = MathUtils.add( this.weights, updates );

        /**
         * DEBUG AREA
         */
        System.out.print("weights:\n");
        for( int i = 0 ;i < this.weights.length; i ++ ){
            System.out.print("weight "+i+" =>\n");
            for(double d: this.weights ) {
                System.out.println(d + " ");
            }
        }
    }

    @Override
    public void multiUpdate(List<Double> expectValueList, List<double[]> featuresList) {
        if( featuresList == null || featuresList.size() == 0 ){
            return;
        }
        for( int i = 0 ; i < featuresList.size(); i ++ ){
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

    public double getΕ() {
        return ε;
    }

    public void setΕ(double ε) {
        this.ε = ε;
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
