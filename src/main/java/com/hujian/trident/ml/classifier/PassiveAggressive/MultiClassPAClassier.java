package com.hujian.trident.ml.classifier.PassiveAggressive;

import backtype.storm.utils.Utils;
import com.hujian.trident.ml.classifier.Classifier;
import com.hujian.trident.ml.core.InputDataType;
import com.hujian.trident.ml.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/2/28.
 *
 * @paper Exact Passive-Aggressive Algorithm for Multi class Classification Using Support Class
 * @paper Online Passive-Aggressive Algorithms
 * @link http://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=3&ved=0ahUKEwiOnMb6yrHSAh
 * UBLZQKHWgHCuAQFggsMAI&url=%68%74%74%70%3a%2f%2f%77%77%77%2e%6b%65%2e%74%75%2d%64%61%72%6d%73%
 * 74%61%64%74%2e%64%65%2f%6c%65%68%72%65%2f%61%72%63%68%69%76%2f%77%73%30%37%30%38%2f%6d%6c%2d%7
 * 3%65%6d%2f%46%6f%6c%69%65%6e%2f%4a%65%61%6e%2d%42%61%70%74%69%73%74%65%5f%42%65%68%75%65%74%2
 * e%70%64%66&usg=AFQjCNHdTV5MaA9HwxExM8rW9vsxqdTMxw
 *
 * more infomation about multi-pa algorithm,you can search on google with the key words
 * "multi class passive-aggression algortihm" and check out the top 10 result.
 */
public class MultiClassPAClassier implements Classifier<Integer>{

    private static final long serialVersionUID = -9087101593640555121L;

    /**
     * the weights
     */
    private double[][] weights;
    private PATypeEnum paType = PATypeEnum.PA;
    private double constant = 0.001;
    private Integer nbClass;
    private Integer right = 0;
    private Integer error = 0;


    public MultiClassPAClassier(){}

    /**
     * you can give me the pa algorithm type
     * pa pa-i pa-ii
     * @param paType
     */
    public MultiClassPAClassier(PATypeEnum paType){
        assert paType instanceof PATypeEnum : "paType must instance of PATypeEnum";
        this.paType = paType;
    }

    /**
     * the constructor
     * @param paType
     * @param nbClass
     * @param c
     */
    public MultiClassPAClassier(PATypeEnum paType,Integer nbClass,double c){
        this.paType = paType;
        this.nbClass = nbClass;
        this.constant = c;
    }

    @Override
    public Integer getRightCount() {
        return this.right;
    }

    @Override
    public Integer getErrorCount() {
        return this.error;
    }

    @Override
    public Integer classify(double[] features) {
        //init the weight vector if it is null
        if( this.weights == null ){
            this.weights = new double[this.nbClass][features.length];
            for(int i = 0; i < this.weights.length; i ++){
                for( int j = 0 ;j < features.length; j ++ ){
                    this.weights[i][j] = 0.0;
                }
            }
        }
        Integer prediction = null;
        Double highestScore = - Double.MIN_VALUE;

        Double currentClassScore;
        double[] currentWeights;
        //find the highest score to each class.
        for( int i = 0; i < this.nbClass; i ++ ){
            currentWeights = this.weights[i];
            currentClassScore = MathUtils.dot(currentWeights,features);
            if( currentClassScore > highestScore ){
                prediction = i ;
                highestScore = currentClassScore;
            }
        }
        return prediction;
    }

    @Override
    public List<Integer> multiClassify(List<double[]> featuresList) {
        if( featuresList == null || featuresList.size() == 0 ){
            return null;
        }
        List< Integer > resultList = new ArrayList<Integer>();
        for( double[] feature: featuresList ){
            resultList.add( this.classify( feature ) );
        }
        return resultList;
    }

    @Override
    public void update(Integer label, double[] features, InputDataType dataType) {

        //get the prediction class
        Integer predictionClass = this.classify( features );

        /**
         * you do not update the model
         */
        if( dataType == InputDataType.TEST_DATA ){
            if( predictionClass.equals(label) ){
                this.right ++;
            }else{
                this.error ++;
            }
            return;
        }
        /**
         * real time to adjust the classify model
         */
        if( dataType == InputDataType.GRACEFUL_DATA ){
            if( predictionClass.equals(label) ){
                this.right ++;
            }else{
                this.error ++;
            }
        }
        /**
         * do some assert
         */
        if( this.weights == null || features == null || features.length == 0
                || label == null || predictionClass == null){
            return;
        }

        //compute the loss value by loss function
        //more info ref check out the title of this file.
        double loss = 1.0 - (MathUtils.dot(this.weights[label], features)
                - MathUtils.dot(this.weights[predictionClass], features));
        double updates = 0.0;

        //compute the update value by the type.
        switch (this.paType) {
            case PA:
                updates = loss / (1 + 2 * Math.pow(MathUtils.powerNorm(features), 2));
                break;
            case PA_I:
                updates = Math.min(this.constant / 2, loss / (2 * Math.pow(MathUtils.powerNorm(features), 2)));
                break;
            case PA_II:
                updates = 0.5 * (loss / (Math.pow(MathUtils.powerNorm(features), 2) + (1 / (2 * this.constant))));
                break;
        }

        double[] currentWeights;

        /**
         *          {  W(i) + adjust_vector     i is the true label.
         *  W(i) =  {
         *          {  W(i) - adjust_vector     i is the prediction label
         *
         *  the vector weight will update each round anyway.if the algorithm find
         *  it predict right for the input features,it will reward itself by add
         *  an addition update vector is computed by the support function just like
         *  above.or punish itself while find predict wrong for input feature by
         *  subtract an addition vector is computed by support function.
         *
         *  this is " machine learning ". and  "online learning" , the algorithm will
         *  adjust it compute method by the real time feedback by the system to keep
         *  the algorithm right enough.
         *
         */
        for (int i = 0; i < this.nbClass; i++) {
            currentWeights = this.weights[i];
            if (i != label && i != predictionClass) {
            } else if (i == label) {
                this.weights[i] = MathUtils.add(currentWeights, MathUtils.multi(features, updates));
            } else if (i == predictionClass) {
                this.weights[i] = MathUtils.subtract(currentWeights, MathUtils.multi(features, updates));
            }
        }

        /**
         * DEBUG AREA
         *//*
        System.out.print("call the update with parameter : [ ");
        for( double d: features ){
            System.out.print( d+" , " );
        }
        System.out.print(" ]\nweights:\n");
        for( int i = 0 ;i < this.nbClass; i ++ ){
            System.out.print("Class "+i+" =>\n");
            for(double d: this.weights[i] ){
                System.out.print(d+" ");
            }
            System.out.print("\n");
        }*/

    }

    @Override
    public void multiUpdate(List<Integer> labelList, List<double[]> featuresList) {
        if( featuresList == null || featuresList.size() == 0 ){
            return;
        }
        for( int i = 0 ;i < featuresList.size() ; i ++ ){
            this.update( labelList.get( i ), featuresList.get( i ),null);
        }
    }

    @Override
    public void reset() {
        this.weights = null;
    }

    public double[][] getWeights() {
        return weights;
    }

    public void setWeights(double[][] weights) {
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

    public Integer getNbClass() {
        return nbClass;
    }

    public void setNbClass(Integer nbClass) {
        this.nbClass = nbClass;
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
