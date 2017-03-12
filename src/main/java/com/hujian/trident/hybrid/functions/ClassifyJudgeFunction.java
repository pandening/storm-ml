package com.hujian.trident.hybrid.functions;

import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import com.hujian.trident.hybrid.classifier.IClassifierFactory;
import com.hujian.trident.hybrid.classifier.SamplesClassifierFactory;
import com.hujian.trident.hybrid.data.Instance;
import com.hujian.trident.hybrid.store.IStore;
import com.hujian.trident.hybrid.store.SampleStored;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hujian on 2017/3/11.
 * if we have the result.
 */
public class ClassifyJudgeFunction extends BaseFunction {

    private static final long serialVersionUID = -242376020000000121L;

    /**
     * the store
     */
    private IStore<String,List<Integer>> store = null;
    private IClassifierFactory<Integer> classifierFactory = null;

    /**
     * the constructor
     */
    public ClassifyJudgeFunction(){
        this.store = SampleStored.getInstance();
        this.classifierFactory = SamplesClassifierFactory.getInstance();
    }

    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {

        if( tridentTuple == null || tridentTuple.size() == 0  ){
            return;
        }

        Instance<Integer> instance = (Instance<Integer>) tridentTuple.get( 0 );
        /**
         * do some assert
         */
        if( instance == null || instance.getInstanceId() == null || instance.getLabel() == null ||
                instance.getFeatures() == null){
            return;
        }

        //get the instance id
        Long instanceId = instance.getInstanceId();

        //get the classification result
        List<Integer> classifyResult = this.store.get("classification_"+instanceId);

        //is null
        if( classifyResult == null || classifyResult.size() == 0 ){
            if( this.store.doneMap().get(instanceId.toString()) != null){
                System.out.println("<The instance ["+instanceId+"] was handled done,remove it...>");
                //remove it.
                this.store.doneMap().remove( instanceId.toString() );
                Values values = new Values();
                values.add(instance.getInstanceId());
                values.add(instance.getLabel());
                for( int i =0 ;i < instance.getFeatures().length; i ++ ){
                    values.add( instance.getFeatures()[i] );
                }
                tridentCollector.emit( values );
            }else{
                System.out.println("<The Classification Result List is Null,handling....emit to next bolt ...>");
                //emit the tuple to next bolt
                Values values = new Values();
                values.add(instance.getInstanceId());
                values.add(instance.getLabel());
                for( int i =0 ;i < instance.getFeatures().length; i ++ ){
                    values.add( instance.getFeatures()[i] );
                }
                tridentCollector.emit( values );
            }
        }else{
            //is full
            if( classifyResult.size() == this.classifierFactory.getClassifiers().size() ){

                //get the result accord to the weight vector
                int index = 0 ;
                Double max = - Double.MAX_VALUE;
                for( int i = 0 ;i < this.classifierFactory.getWeight().size() ; i ++ ){
                    if( this.classifierFactory.getWeight().get( i ) > max ){
                        max = this.classifierFactory.getWeight().get( i );
                        index = i;
                    }
                }
                //get the result.change the weights
                for( int i = 0 ;i < classifyResult.size(); i ++ ){
                    if( classifyResult.get( index ).equals( classifyResult.get( i ) ) ){
                        this.classifierFactory.getWeight().
                                set( i , 1.0 + this.classifierFactory.getWeight().get( i ) );
                    }
                }
                //norm sum to 1
                Double sum = 0.0;
                for( double d: this.classifierFactory.getWeight() ){
                    sum += d;
                }
                for( int i = 0 ;i < this.classifierFactory.getWeight().size() ; i ++ ){
                    this.classifierFactory.getWeight().set( i ,
                            (this.classifierFactory.getWeight().get( i ) / sum) * 100);
                }
                //right or not?
                Integer C = instance.getLabel();
                Integer tC = classifyResult.get( index );

                System.out.println("case 1=> predict = " + tC + "  correct = " + C );


                if( C.equals( tC ) ){
                    this.classifierFactory.setRight(1L);
                }else{
                    this.classifierFactory.setError(1L);
                }
                //remove the judge result
                this.store.remove( "classification_"+instanceId );

                //emit the tuple to next bolt
                Values values = new Values();
                values.add(instance.getInstanceId());
                values.add(instance.getLabel());
                for( int i =0 ;i < instance.getFeatures().length; i ++ ){
                    values.add( instance.getFeatures()[i] );
                }
                tridentCollector.emit( values );

                //done tag
                this.store.doneMap().put(instanceId.toString(),"done");
            }else{
                //if we can classification
                Map<Integer,Integer> hash = new HashMap<>();
                int classifierIndex = -1 ;
                Integer classification = -1;
                int count = - 1;
                for( int i = 0 ;i < classifyResult.size(); i ++ ){
                    if( hash.get( classifyResult.get( i ) ) == null ){
                        hash.put(classifyResult.get( i ), 1);
                    }else{
                        hash.put( classifyResult.get( i ), hash.get( classifyResult.get( i ) ) + 1);
                        if( count < hash.get( classifyResult.get( i ) ) ){
                            classifierIndex = i;
                            classification = classifyResult.get( i );
                            count = hash.get( classifyResult.get( i ) );
                        }
                    }
                }

                //update the model
                if( classifierIndex != -1 ){
                    //we get the result.
                    for( int i = 0; i < classifyResult.size(); i ++ ){
                        if( classifyResult.get( i ).equals( classification ) ){
                            this.classifierFactory.getWeight().set( i ,
                                    this.classifierFactory.getWeight().get( i ) + 1.0);
                        }
                    }
                    //norm sum to 1
                    Double sum = 0.0;
                    for( double d: this.classifierFactory.getWeight() ){
                        sum += d;
                    }
                    for( int i = 0 ;i < this.classifierFactory.getWeight().size() ; i ++ ){
                        this.classifierFactory.getWeight().set( i ,
                                (this.classifierFactory.getWeight().get( i ) / sum) * 100.0);
                    }
                    //right or not?
                    Integer C = instance.getLabel();
                    Integer tC = classifyResult.get( classifierIndex );

                    System.out.println("case 2=> predict = " + tC + "  correct = " + C );

                    if( C.equals( tC ) ){
                        this.classifierFactory.setRight(1L);
                    }else{
                        this.classifierFactory.setError(1L);
                    }
                    //remove the judge result
                    this.store.remove( "classification_"+instanceId );

                    //done tag
                    this.store.doneMap().put(instanceId.toString(),"done");

                }else{
                    //emit the tuple to next bolt
                    //more classifiers will classify this instance
                    Values values = new Values();
                    values.add(instance.getInstanceId());
                    values.add(instance.getLabel());
                    for( int i =0 ;i < instance.getFeatures().length; i ++ ){
                        values.add( instance.getFeatures()[i] );
                    }
                    tridentCollector.emit( values );
                }
            }
        }
    }
}
