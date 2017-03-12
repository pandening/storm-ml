package com.hujian.trident.ml.utils;

/**
 * Created by hujian on 2017/2/23.
 */
public class MathUtils {

    /**
     * compute two features euclidean distance
     * @param features_a
     * @param features_b
     * @return
     */
    public static double euclideanDistance( double[] features_a, double[] features_b ){
        if (features_a.length != features_b.length) {
            throw new IllegalArgumentException("The dimensions of two features have to be equal!");
        }

        double sum = 0.0;
        for (int i = 0; i < features_a.length; i++) {
            sum += Math.pow(features_a[i] - features_b[i], 2);
        }
        return Math.sqrt(sum);
    }

    /**
     * do the multi for the features
     * @param features
     * @param scalar
     * @return
     */
    public static double[] multi( double[] features,double scalar ){
        int length = features.length;
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = features[i] * scalar;
        }
        return result;
    }

    /**
     * div
     * @param features
     * @param scalar
     * @return
     */
    public static double[] div( double[] features, double scalar ){
        assert scalar != 0;
        int length = features.length;
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = features[i] / scalar;
        }
        return result;
    }

    /**
     * do the add.
     * @param features_a
     * @param features_b
     * @return
     */
    public static double[] add( double[] features_a, double[] features_b ){
        if (features_a.length != features_b.length) {
            throw new IllegalArgumentException("The dimensions of two features have to be equal!");
        }

        double[] result = new double[features_a.length];
        assert features_a.length == features_b.length;
        for (int i = 0; i < features_a.length; i++) {
            result[i] = features_a[i] + features_b[i];
        }
        return result;
    }

    /**
     * do the subtract
     * @param features_a
     * @param features_b
     * @return
     */
    public static double[] subtract( double[] features_a, double[] features_b ){
        if (features_a.length != features_b.length) {
            throw new IllegalArgumentException("The dimensions of two features have to be equal!");
        }

        double[] result = new double[features_a.length];
        assert features_a.length == features_b.length;
        for (int i = 0; i < features_a.length; i++) {
            result[i] = features_a[i] - features_b[i];
        }
        return result;
    }


    /**
     * compute two features' dot result.
     * @param features_a
     * @param features_b
     * @return
     */
    public static double dot(double[] features_a, double[] features_b) {
        if (features_a.length != features_b.length) {
            throw new IllegalArgumentException("The dimensions have to be equal!");
        }
        double sum = 0;
        for (int i = 0; i < features_a.length; i++) {
            sum += features_a[i] * features_b[i];
        }
        return sum;
    }

    /**
     * pow 2 and sqrt
     * @param features
     * @return
     */
    public static double powerNorm( double[] features ){
        double powSqr = 0;

        for (int i = 0; i < features.length; i++) {
            powSqr += features[i] * features[i];
        }
        return Math.sqrt(powSqr);
    }

    /**
     * let the vector's sum to toSum
     * @param vector
     * @param toSum
     * @return
     */
    public static double[] normTo( double[] vector, double toSum ){
        if( vector == null || vector.length == 0 ){
            return null;
        }
        double sum = 0.0;
        for( double d: vector ){
            sum += d;
        }
        if( Math.abs( sum ) < toSum ){
            return vector;
        }
        assert (toSum != 0 && sum != 0) :"the div 0 error!!!!!!";
        double scalar = sum / toSum;
        vector = div(vector,scalar);
        return vector;
    }

    /**
     * compute the vector's sum
     * @param vector
     * @return
     */
    public static double vectorSum(double[] vector){
        if( vector == null || vector.length == 0 ){
            return 0.0;
        }
        double sum = 0.0;
        for(double d:vector){
            sum += d;
        }
        return sum;
    }

}
