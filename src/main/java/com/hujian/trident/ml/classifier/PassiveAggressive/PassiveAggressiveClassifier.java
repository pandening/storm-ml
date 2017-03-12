package com.hujian.trident.ml.classifier.PassiveAggressive;

import com.hujian.trident.ml.classifier.Classifier;
import com.hujian.trident.ml.core.InputDataType;
import com.hujian.trident.ml.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/2/27.
 * @paper Online Passive-Aggressive Algorithms
 *
 *           Algorithm describe
 *   input: aggressiveness parameter C > 0
 *   initialize: w1 = (0,0,...,0)
 *   For t = 1,2....
 *    * receive instance: Xt of (R^n)
 *    * predict: Yt = sign( Wt.Xt)
 *    * receive correct label: Yt of {1,-1}
 *    * suffer loss l = max{0,1- Yt(Wt.Xt)}
 *    * update:
 *          1. set :
 *                 pi = l / |Xt|^2            ......PA
 *                 pi = min{C,l/|Xt|^2}       ......PA-I
 *                 pi = l / (l/|Xt|^2 + 1/2C) ......PA-II
 *
 *         2. update: Wt+1 = Wt + pi*Yt*Xt
 *
 */
public class PassiveAggressiveClassifier implements Classifier<Boolean>{

    private static final long serialVersionUID = -8903481593120555120L;

    private double[] weights;
    //default is PA
    private PATypeEnum paType = PATypeEnum.PA;
    private double constant = 0.001;

    /**
     * empty default constructor
     */
    public PassiveAggressiveClassifier(){}

    /**
     * the construct
     * @param type
     * @param c
     */
    public PassiveAggressiveClassifier( PATypeEnum type,double c ){
        this.paType = type;
        this.constant = c;
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
        //init the weight if required
        if( this.weights == null ){
            this.weights = new double[features.length];
        }
        Double evaluation = MathUtils.dot(features,weights);
        return evaluation >= 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public List<Boolean> multiClassify(List<double[]> featuresList) {
        if( featuresList == null || featuresList.size() == 0 ){
            return null;
        }
        List<Boolean> resultList = new ArrayList<Boolean>();
        for( int i = 0; i < featuresList.size() ; i++ ){
            resultList.add( this.classify( featuresList.get( i ) ) );
        }
        return resultList;
    }

    @Override
    public void update(Boolean label, double[] features,InputDataType dataType) {
        //init the weight if required
        if( this.weights == null ){
            this.weights = new double[ features.length ];
        }
        //see the title of this file.algorithm describe
        double Yt = label ? 1.0 : - 1.0;
        double loss = Math.max( 0.0, 1 - Yt * MathUtils.dot( features,weights ) );
        double update =  0;
        switch (this.paType) {
            case PA:
                update = loss / ( 1 + Math.pow( MathUtils.powerNorm( features ) , 2) );
                break;
            case PA_I:
                update = Math.min( this.constant,loss / Math.pow(MathUtils.powerNorm(features), 2));
                break;
            case PA_II:
                update = loss / (Math.pow(MathUtils.powerNorm(features), 2) + (1.0 / (2 * this.constant)));
                break;
        }

        double[] updates = MathUtils.multi(features,update * Yt);
        //update the classifier model
        this.weights = MathUtils.add( this.weights , updates );

        /**
         * DEBUG AREA
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
        System.out.print("\n");
    }

    @Override
    public void multiUpdate(List<Boolean> labelList, List<double[]> featuresList) {
        if( featuresList == null || featuresList.size() == 0 ){
            return;
        }
        for( int i = 0 ; i < featuresList.size() ; i ++ ){
            this.update(labelList.get( i ), featuresList.get( i ),null);
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

    public PATypeEnum getPaType() {
        return paType;
    }

    public void setPaType(PATypeEnum paType) {
        this.paType = paType;
    }

    public double getConstant() {
        return constant;
    }

    public void setConstant(double constant) {
        this.constant = constant;
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
