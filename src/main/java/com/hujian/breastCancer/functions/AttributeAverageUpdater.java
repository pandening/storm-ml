package com.hujian.breastCancer.functions;

import backtype.storm.tuple.Values;
import com.hujian.breastCancer.source.BreastInstance;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/3/27.
 */
public class AttributeAverageUpdater extends BaseFunction {

    private List<Average> benignCancerAverage = null;
    private List<Average> MalignancyCancerAverage = null;
    private BreastInstance breastInstance = null;

    private Long reach = 0L;

    /**
     * constructor
     */
    public AttributeAverageUpdater(){
        this.benignCancerAverage = new ArrayList<>();
        this.MalignancyCancerAverage = new ArrayList<>();

        for( int i =0 ;i < 9; i ++ ){
            this.benignCancerAverage.add(new Average());
            this.MalignancyCancerAverage.add(new Average());
        }
    }


    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        //get the instance
        if( tridentTuple == null ){
            return;
        }

        this.breastInstance = (BreastInstance)tridentTuple.get( 0 );

        if( this.breastInstance == null ){
            System.out.println("--get an null breast instance--");
            return;
        }

        //update the average model
        double[] features = this.breastInstance.getFeatures();

        if( features == null ){
            System.out.println("--get an empty features--");
            return;
        }

        this.reach ++;

        if( reach >= 380 ){//classification
            //emit the result to next bolt
            //classification,beginResult,Malignancy,features
            tridentCollector.emit( new Values(this.breastInstance.getClassification(),
                    this.benignCancerAverage,this.MalignancyCancerAverage,this.breastInstance.getFeatures()));
        }else{ //train the model
            //begin or Malignancy
            if( this.breastInstance.getClassification().equals( 2 ) ){
                for( int i = 0 ;i < features.length; i ++ ){
                    this.benignCancerAverage.get( i ).update( features[i] );
                }
            }else if( this.breastInstance.getClassification().equals( 4 ) ){
                for( int i = 0 ;i < features.length; i ++ ){
                    this.MalignancyCancerAverage.get( i ).update( features[i] );
                }
            }
            //emit the result to next bolt
            //classification,beginResult,Malignancy,features
            tridentCollector.emit( new Values(this.breastInstance.getClassification(),
                    this.benignCancerAverage,this.MalignancyCancerAverage,null));
        }
    }
}
