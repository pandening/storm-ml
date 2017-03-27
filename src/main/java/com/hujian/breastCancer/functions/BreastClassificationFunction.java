package com.hujian.breastCancer.functions;

import com.hujian.breastCancer.source.BreastInstance;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/3/27.
 */
public class BreastClassificationFunction extends BaseFunction {

    private List<Average> benignCancerAverage = null;
    private List<Average> MalignancyCancerAverage = null;
    private double[] features = null;
    private Integer classification = null;
    private double alpha = 0.5;
    private int[] indexArray = null;

    private Long reach = 0L;
    private Long right = 0L;
    private Long error = 0L;

    /**
     * the constructor
     */
    public BreastClassificationFunction(){
        this.benignCancerAverage = new ArrayList<>();
        this.MalignancyCancerAverage = new ArrayList<>();

        for( int i =0 ;i < 9; i ++ ){
            this.benignCancerAverage.add(new Average());
            this.MalignancyCancerAverage.add(new Average());
        }
    }

    /**
     * which index att will join into compute
     * @param index
     */
    public BreastClassificationFunction(int[] index){
        this.benignCancerAverage = new ArrayList<>();
        this.MalignancyCancerAverage = new ArrayList<>();

        for( int i =0 ;i < 9; i ++ ){
            this.benignCancerAverage.add(new Average());
            this.MalignancyCancerAverage.add(new Average());
        }

        this.indexArray = index;
    }


    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        //get the instance and model
        if (tridentTuple == null) {
            return;
        }

        //get the class,average-model,features
        this.classification = tridentTuple.getInteger(0);
        this.benignCancerAverage = (List<Average>) tridentTuple.get(1);
        this.MalignancyCancerAverage = (List<Average>) tridentTuple.get(2);
        this.features = (double[]) tridentTuple.get(3);

        this.reach ++;

        if (this.features == null) {
            //train data
        } else {

            if( this.indexArray != null ){
                double benignCount  = 0;
                for( int i = 0 ; i < this.indexArray.length; i ++ ){
                    int index = this.indexArray[i];

                    if( this.features[index] < ( ( ( 1.0 - this.alpha ) * this.benignCancerAverage.get( index ).getAverage() +
                            this.alpha * this.MalignancyCancerAverage.get( index ).getAverage())) ){
                        benignCount += 1.0;
                    }
                }
                if( this.classification.equals( 2 ) ){
                    if( benignCount >= this.indexArray.length / 1.5 ){
                        right ++;
                    }else{
                        error ++;
                    }
                }else if( this.classification.equals( 4 ) ){
                    if( benignCount < this.indexArray.length / 1.5 ){
                        right ++;
                    }else{
                        error ++;
                    }
                }
            }else{

                //test data
                int benignCount  = 0 ;
                for( int i = 0 ;i < this.benignCancerAverage.size(); i ++ ){
                    if( this.features[i] < ( ( ( 1.0 - this.alpha ) * this.benignCancerAverage.get( i ).getAverage() +
                            this.alpha * this.MalignancyCancerAverage.get( i ).getAverage()))){
                        benignCount ++;
                    }
                }

                if( benignCount >= this.features.length / 1.5 ){
                    if( this.classification.equals( 2 ) ){
                        right ++;
                    }else{
                        System.out.println("["+this.reach+"][2]");
                        for( int i =0 ;i <this.features.length; i ++ ){
                            System.out.print( this.features[ i ] + " ");
                        }
                        System.out.print("\n");
                        error ++;
                    }
                }else{
                    if( this.classification.equals( 4 ) ){
                        right ++;
                    }else{
                        System.out.println("["+this.reach+"][4]");
                        for( int i =0 ;i <this.features.length; i ++ ){
                            System.out.print( this.features[ i ] + " ");
                        }
                        System.out.print("\n");
                        error ++;
                    }
                }
            }
            System.out.println("["+this.reach+"]:"+"[right/error]:"+right+"/"+error);
        }
    }
}
