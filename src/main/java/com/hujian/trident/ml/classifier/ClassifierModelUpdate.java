package com.hujian.trident.ml.classifier;

import com.hujian.trident.ml.core.InputDataType;
import com.hujian.trident.ml.core.Instance;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hujian on 2017/2/27.
 */
public class ClassifierModelUpdate<L> extends BaseStateUpdater<MapState<Classifier<L>>>{

    private static final long serialVersionUID = -2903890181994862536L;

    private String classifierModelName ;
    private Classifier<L> classifier;

    static Long trainInstanceCount = 0L;

    private Map<String,Integer> classificationStatistic;

    /**
     * just show
     */
    private void showClassificationStatistic(){
        if( this.classificationStatistic == null ){
            classificationStatistic = new HashMap<String, Integer>();
            System.out.println("null classification info.");
            return;
        }
        Integer right = this.classificationStatistic.get("right") == null ? 0 : this.classificationStatistic.get("right");
        Integer error = this.classificationStatistic.get("error") == null ? 0 : this.classificationStatistic.get("error");
        System.out.println("---------classification statistic information-------");
        System.out.println("total prediction:"+(right+error)+"  right:"+right+"  error:"+error);
        if( right != 0 ){
            System.out.println("rightRate:"+(1.0 * right / ( right +error ))*100 + "%");
            System.out.println("errorRate:"+(1.0 * error / ( right +error ))*100 + "%");
        }
        System.out.println("the model trained by "+ClassifierModelUpdate.trainInstanceCount + " instance");
    }

    /**
     * get total train till now
     * @return
     */
    public static Long getTrainInstanceCount(){
        return trainInstanceCount;
    }

    /**
     * the constructor method
     * @param classifierModelName
     * @param classifier
     */
    public ClassifierModelUpdate( String classifierModelName,Classifier<L>  classifier){
        this.classifierModelName = classifierModelName;
        this.classifier = classifier;
        this.classificationStatistic = new HashMap<String, Integer>();
    }


    @Override
    public void updateState(MapState<Classifier<L>> classifierMapState, List<TridentTuple> list, TridentCollector tridentCollector) {
        //get the old classifier model from stat map
        List<Classifier<L>> classifierList = classifierMapState.multiGet(
                Arrays.asList( Arrays.asList( (Object) this.classifierModelName )));
        Classifier<L> classifier_ = null;
        if( classifierList != null && classifierList.size() != 0 ){
            classifier_ = classifierList.get( 0 );
        }

        //the first time?
        if( classifier_ == null ){
            classifier_ = this.classifier;
        }

        //update the classifier model by the tuple
        Instance<L> instance;
        for( TridentTuple tridentTuple : list ){
            instance = (Instance<L>) tridentTuple.get( 0 );

            // 500000 for training 500000 for testing
            if( trainInstanceCount < 10000 ){
                instance.setDataType(InputDataType.TRAIN_DATA);
            }else{
                instance.setDataType(InputDataType.TEST_DATA);
            }

            //if you want to test real time model
            instance.setDataType(InputDataType.GRACEFUL_DATA);


            classifier_.update( instance.getLabel(),instance.getFeatures() ,instance.getDataType());

            ////////////////////////////////////////////////////////////////////////
            trainInstanceCount ++;
            if(trainInstanceCount % 100 == 0){
                this.classificationStatistic.put("right",classifier_.getRightCount());
                this.classificationStatistic.put("error",classifier_.getErrorCount());
                this.showClassificationStatistic();
            }
           ////////////////////////////////////////////////////////////////////////

        }

        //store the real-time model
        classifierMapState.multiPut(Arrays.asList( Arrays.asList( (Object) this.classifierModelName ) ),
                Arrays.asList( classifier_ ));

    }

    public String getClassifierModelName() {
        return classifierModelName;
    }

    public void setClassifierModelName(String classifierModelName) {
        this.classifierModelName = classifierModelName;
    }

    public Classifier<L> getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier<L> classifier) {
        this.classifier = classifier;
    }

}
