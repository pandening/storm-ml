package com.hujian.trident.hybrid.functions;

import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import com.hujian.trident.hybrid.data.Instance;
import com.hujian.trident.hybrid.store.IStore;
import com.hujian.trident.hybrid.store.SampleStored;
import com.hujian.trident.ml.classifier.Classifier;
import com.hujian.trident.ml.core.InputDataType;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.*;

/**
 * Created by hujian on 2017/3/11.
 */
public class ClassifierModelUpdater<T> extends BaseStateUpdater<MapState<Classifier<T>>> {

    private static final long serialVersionUID = -89102123419010L;

    /**
     * the model name
     */
    private String classifierModelName = null;

    /**
     * the initial classifier model
     */
    private Classifier<T> classifier = null;

    /**
     * the store
     */
    private IStore<String,List<T>> store = null;

    private Integer right = 0;
    private Integer error = 0;

    /**
     * you should offer the model name and the initial classifier model
     * @param classifierModelName
     * @param classifier
     */
    public ClassifierModelUpdater( String classifierModelName, Classifier<T> classifier ){
        this.classifierModelName = classifierModelName;
        this.classifier = classifier;
        this.store = SampleStored.getInstance();

    }

    /**
     * update the classifier model here,and save the classifier result in the stored
     * @param classifierMapState
     * @param list
     * @param tridentCollector
     */
    @Override
    public void updateState(MapState<Classifier<T>> classifierMapState, List<TridentTuple> list,
                            TridentCollector tridentCollector) {
        //if handling done
        if( list == null || list.size() == 0 ||(Instance<T>)list.get(0).get( 0 ) == null){
            System.out.println("<find useless classifier model updater>");
            return;
        }

        // get the old model.
        List<Classifier<T>> classifierList = classifierMapState.multiGet(
                Arrays.asList(Arrays.asList( (Object)this.classifierModelName )));

        Classifier<T> classifier_ = null;

        //init our classifier model
        if( classifierList != null && classifierList.size() != 0 ){
            classifier_ = classifierList.get( 0 );
        }

        //if this is the first time to train the classifier model
        if( classifier_ == null ){
            classifier_ = this.classifier;
        }

        //get the instance and train the model
        //train is train,test is classify
        Instance<T> instance = null;
        for( TridentTuple tridentTuple : list ){
            instance = (Instance<T>)tridentTuple.get( 0 );

            //if done
            if( this.store.doneMap().get( instance.getInstanceId().toString() ) != null ){
                System.out.println("->instance "+instance.getInstanceId() +" was handled done..remove it");
                this.store.doneMap().remove( instance.getInstanceId().toString() );
                continue;
            }

            Random random = new Random();
            instance.setDataType( new InputDataType[]{InputDataType.TEST_DATA,
                    InputDataType.TRAIN_DATA}[random.nextInt(2)] );


            //get the instance type
            if( instance.getDataType() == InputDataType.TRAIN_DATA ){
                //train the model
                classifier_.update(instance.getLabel(),instance.getFeatures(),instance.getDataType());
            }else if( instance.getDataType() == InputDataType.TEST_DATA ){
                //query the classification result
                T result = classifier_.classify( instance.getFeatures() );

                if( result == instance.getLabel() ){
                    right ++;
                }else{
                    error ++;
                }

                if( ( right + error ) % 100 == 0 ){
                    System.out.println("right:"+right + "  error:"+ error);
                }

                //save
                List<T> old = this.store.get("classification_"+instance.getInstanceId());
                if( old == null){
                    old = new ArrayList<>();
                    old.add( result );
                }else{
                    old.add( result );
                }
                this.store.put("classification_"+instance.getInstanceId(),old);

                //System.out.println("<test the classifier for instance ["+instance.getInstanceId()+"]>:"+result);

            }
            //emit the tuple to next bolt
            Values values = new Values();
            values.add(instance.getInstanceId());
            values.add(instance.getLabel());
            for( int i =0 ;i < instance.getFeatures().length; i ++ ){
                values.add( instance.getFeatures()[i] );
            }
            tridentCollector.emit( values );
        }
        //save the new classifier model
        classifierMapState.multiPut(Arrays.asList(Arrays.asList( (Object)this.classifierModelName )),
                Arrays.asList( classifier_ ));
    }

}
