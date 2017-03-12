package com.hujian.trident.ml.regression.Ftrl;

import com.hujian.trident.ml.regression.RegressOr;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hujian on 2017/3/2.
 * @paper Ad Click Prediction: a View from the Trenches
 */
public class FtrlRegression implements RegressOr{

    private static final long serialVersionUID = 1234858132010644113L;

    private double alpha = 0.2;
    private double beta =1.1;
    private double L1 =0.1;
    private double L2 = 1.5;

    //n-z-w
    double[] n;
    double[] z;
    double[] w; /*weight vector*/

    public FtrlRegression(){}

    /**
     * constructor
     * @param alpha
     * @param beta
     * @param l1
     * @param l2
     */
    public FtrlRegression( double alpha,double beta,double l1,double l2 ){
        this.alpha = alpha;
        this.beta = beta;
        this.L1 = l1;
        this.L2 = l2;
    }

    @Override
    public Double predict(double[] features) {
        //init the n-z-w vector if null( first time )
        if( this.n == null || this.z == null || this.w == null ){
            int length = features.length;
            Random random = new Random();
            this.n = new double[length];
            this.z = new double[length];
            this.w = new double[length];
        }
        //get the prediction result
        Double fm = 0.0;
        for( double f:features ){
            fm += f;
        }
        return (1.0 / ( 1.0 + Math.exp( -1 * fm ) ));
    }

    @Override
    public List<Double> multiPredict(List<double[]> featuresList) {
        if( featuresList == null ||featuresList.size() == 0 ){
            return null;
        }
        List<Double> predictionList = new ArrayList<Double>();
        for( double[] features:featuresList ){
            predictionList.add( this.predict( features ) );
        }
        return predictionList;
    }

    @Override
    public void update(Double expectValue, double[] features) {
        //get the prediction value
        Double predictionValue = this.predict( features );
        Double g = predictionValue - expectValue;
        Double gg = Math.pow( g, 2.0 );
        //update the regression model
        for( int i = 0 ;i < features.length;i++ ){
          double sigma = ( Math.sqrt( this.n[i] + gg ) - Math.sqrt( this.n[i] )) / this.alpha;
          this.z[ i ] += (g - sigma * this.w[i]);
          this.n[i] += gg;
          Double label = this.z[ i ] < 0.0 ? -1.0 : 1.0;
          //update the weight vector according to the n vector and w vector
          if( label * this.z[i] <= this.L1){
              this.w[ i ] = 0.0;
          }else{
              this.w[ i ] = ( label * this.L1 - this.z[i] ) /
                      (( this.beta + Math.sqrt( this.n[i] ) ) / this.alpha + this.L2 );
          }
        }

        /**
         *          DEBUG AREA
         *   the n,z,w vector
         */
        /*System.out.println("n vector:");
        for( double d : this.n ){
            System.out.println(d);
        }
        System.out.println("z vector:");
        for( double d : this.z ){
            System.out.println(d);
        }*/
        System.out.println("w vector:");
        for( double d : this.w ){
            System.out.println(d);
        }
        //Utils.sleep(2000);

    }

    @Override
    public void multiUpdate(List<Double> expectValueList, List<double[]> featuresList) {
        if( featuresList != null && featuresList.size() != 0 ){
            for( int i = 0 ; i < featuresList.size() ; i ++ ){
                this.update( expectValueList.get( i ), featuresList.get( i ) );
            }
        }
    }

    @Override
    public void reset() {
        this.n = null;
        this.z = null;
        this.w = null;
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

    public double getL1() {
        return L1;
    }

    public void setL1(double l1) {
        L1 = l1;
    }

    public double getL2() {
        return L2;
    }

    public void setL2(double l2) {
        L2 = l2;
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
