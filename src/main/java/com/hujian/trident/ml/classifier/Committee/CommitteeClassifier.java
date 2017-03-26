package com.hujian.trident.ml.classifier.Committee;

import com.hujian.trident.ml.classifier.Classifier;
import com.hujian.trident.ml.core.InputDataType;
import com.hujian.trident.ml.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/3/1.
 * @paper A Multi-class Linear Learning Algorithm Related to Winnow
 * search the key words in google for more information
 */
public class CommitteeClassifier implements Classifier<Integer> {

    private static final long serialVersionUID = 101641034534251498L;

    private double[][] weights;
    private double alpha = 1.1; /*assert alpha > 1.0 */
    private Integer nbClass = 3;

    /**
     * constructor
     * @param alpha
     * @param nbClass
     */
    public CommitteeClassifier( double alpha, Integer nbClass){
        this.alpha = alpha;
        this.nbClass = nbClass;
    }

    @Override
    public Integer getRightCount() {
        return null;
    }
    
    @Override
    public Integer getErrorCount() {
        return null;
    }

    /**
     * Assume there are n sub-experts. Each sub-expert has a positive weight that is used to vote
     * for k different classes; let Wi be the weight of sub-expert i. A sub-expert can vote for several
     * classes by spreading its weight with a prediction distribution. For example, if k = 3, a
     * sub-expert may give 3/5 of its weight to class 1, 1/5 of its weight to class 2, and 1/5 of its
     * weight to class 3. Let Xi represent this prediction distribution, where x~ is the fraction of the
     * weight sub-expert i gives to class j . The vote for class j is L~=I WiX~. Committee predicts
     * the class that has the highest vote. (On ties, the algorithm picks one of the classes involved
     * in the tie.) We call the function computed by this prediction scheme a linear-max function,
     * since it is the maximum class value taken from a linear combination of the SUb-expert predictions.
     * @param features
     * @return
     */
    @Override
    public Integer classify(double[] features) {
        //init the weight if null
        if( this.weights == null ){
            int length = features.length;
            assert length != 0 :"the features' size must > 0";
            this.weights = new double[length][this.nbClass];
            for( int i = 0 ; i < length ; i ++ ){
                for( int j = 0 ; j < this.nbClass ; j ++ ){
                    this.weights [i][j] = 1.0 / length;
                }
            }
        }

        Integer predictionClass = null;
        int length = features.length;
        assert length != 0 :"the features' size must > 0";
        //find the highest score class.
        double[] classScores = new double[this.nbClass];
        for( int i = 0; i < this.nbClass; i ++ ){
            classScores[i] = 0.0;
        }
        for( int i = 0 ; i < length ; i ++ ){
            for( int j = 0 ; j < this.nbClass ; j ++ ){
                classScores [j] += features[i] * this.weights[i][j];
            }
        }

        Double highestScore = - Double.MAX_VALUE;
        for( int i = 0 ; i <this.nbClass ; i ++ ){
            if( classScores[i] >highestScore){
                highestScore = classScores[i];
                predictionClass = i;
            }
        }

        return predictionClass;
    }

    @Override
    public List<Integer> multiClassify(List<double[]> featuresList) {
        if( featuresList == null || featuresList.size() == 0 ){
            return null;
        }
        List<Integer > predictionList = new ArrayList<Integer>();
        for( double[] features: featuresList  ){
            predictionList.add( this.classify( features ) );
        }
        return predictionList;
    }

    @Override

    public void update(Integer label, double[] features,InputDataType dataType) {
        //get the prediction class
        Integer predictionClass = this.classify( features );

        //do some assert
        if( label == null || features == null || features.length == 0 || dataType == null
                || predictionClass == null){
            return;
        }

        //update the classifier model while predicting an error class
        if( ! label.equals( predictionClass ) ){
            //update the classifier model
            for( int i = 0; i < features.length; i ++ ){
                for( int j = 0; j < this.nbClass; j ++ ){
                    /**
                     * the follow code create by myself.the weight adjust according to the prediction class result.
                     */
                    if( j == label ){
                        this.weights[i][j] *= 1.5;
                    }else if( j == predictionClass ){
                        this.weights[i][j] *= 0.5;
                    }else{
                        double scalar = features[i] *( this.weights[i][label] - this.weights[i][predictionClass]);
                        scalar = Math.pow( this.alpha , scalar );
                        this.weights[i][j] += scalar;
                    }
                }
            }

            //normalize the weight[i] sum to 1
            for( int i = 0; i < features.length; i ++){
                double vote = 0.0;
                for( double d: this.weights[i] ){
                    vote += d;
                }
                this.weights[i] = MathUtils.div( this.weights[i],vote );
            }

            /**
             * DEBUG AREA
             */
            /*System.out.print("weights:\n");
            for( int i = 0 ;i < this.weights.length; i ++ ){
                System.out.print("weight "+i+" =>\n");
                for(double d: this.weights[i] ) {
                    System.out.println(d + " ");
                }
            }*/

        }
    }
    @Override
    public void multiUpdate(List<Integer> labelList, List<double[]> featuresList) {
        if( labelList == null || labelList.size() == 0 ){
            return;
        }
        for( int i = 0 ; i < labelList.size() ; i ++ ){
            this.update( labelList.get( i ), featuresList.get( i ) ,null);
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

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
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
