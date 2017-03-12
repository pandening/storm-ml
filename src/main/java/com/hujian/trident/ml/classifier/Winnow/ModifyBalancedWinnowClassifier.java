package com.hujian.trident.ml.classifier.Winnow;

import com.hujian.trident.ml.core.InputDataType;

/**
 * Created by hujian on 2017/2/27.
 * @paper Gender Identification on Twitter Using the Modified Balanced Winnow
 * @paper Single-Pass Online Learning: Performance, VotingSchemes and Online Feature Selection
 */
public class ModifyBalancedWinnowClassifier extends BalancedWinnowClassifier {

    private static final long serialVersionUID = -108642234535251498L;

    /**
     * the constructor
     */
    public ModifyBalancedWinnowClassifier(){
        super();
    }

    /**
     * the constructor
     * @param alpha
     * @param beta
     * @param threshold
     */
    public ModifyBalancedWinnowClassifier(double alpha,double beta, double threshold){
        super(alpha,beta,threshold);
    }

    @Override
    public void update(Boolean label, double[] features,InputDataType dataType){
        Boolean prediction = this.classify( features );

        //update the classifier model while the prediction is error
        if( ! label.equals( prediction ) ){
            for( int i = 0 ; i < features.length ; i ++ ){
                if( features[i] > 0 ){
                    //demotion update
                    this.u[ i ] *= (this.alpha * ( 1 + features[i]));
                    this.v[ i ] *= (this.beta * (1 - features[i]));
                }else{
                    //promotion update
                    this.u[ i ] *= (this.beta * ( 1 - features[i] ));
                    this.v[ i ] *= (this.alpha * ( 1 + features [i]));
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
            System.out.println("\n predict right");
        }else{
            System.out.println("\n predict error");
        }
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
