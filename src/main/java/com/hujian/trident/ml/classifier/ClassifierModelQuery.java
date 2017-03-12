package com.hujian.trident.ml.classifier;

import backtype.storm.tuple.Values;
import com.hujian.trident.ml.core.Instance;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.*;

/**
 * Created by hujian on 2017/2/27.
 */
public class ClassifierModelQuery<L> extends BaseQueryFunction<MapState<Classifier<L>>,L> {

    private static final long serialVersionUID = 1234858930000644113L;

    /**
     * the classifier model name,we will use this key
     * to get the old classifier model from map state
     */
    private String classifierModelName;

    private Map<String,Integer> classificationStatistic;

    /**
     * just show
     */
    private void showClassificationStatistic(){
        if( this.classificationStatistic == null ){
            System.out.println("null classification info.");
            return;
        }
        System.out.println("---------classification statistic information-------");
        Integer right = this.classificationStatistic.get("right") == null ? 0 : this.classificationStatistic.get("right");
        Integer error = this.classificationStatistic.get("error") == null ? 0 : this.classificationStatistic.get("error");
        System.out.println("total prediction:"+(right+error)+"  right:"+right+"  error:"+error);
        if( right != 0 ){
            System.out.println("rightRate:"+(1.0 * right / ( right +error ))*100 + "%");
            System.out.println("errorRate:"+(1.0 * error / ( right +error ))*100 + "%");
        }
        System.out.println("the model trained by "+ClassifierModelUpdate.trainInstanceCount + " instance");
    }

    /**
     * the constructor
     * @param classifierModelName
     */
    public  ClassifierModelQuery( String classifierModelName ){
        this.classifierModelName = classifierModelName;
        this.classificationStatistic = new HashMap<String, Integer>();
    }

    @Override
    public List<L> batchRetrieve(MapState<Classifier<L>> classifierMapState, List<TridentTuple> list) {
       List<L> returnLabelList = new ArrayList<L>();

        //get the old model from map state
        List<Classifier<L>> classifierList = classifierMapState
                .multiGet(Arrays.asList( Arrays.asList( (Object)this.classifierModelName ) ));
        if( classifierList != null && classifierList.size() != 0 ){
            Classifier<L> classifier_ = classifierList.get( 0 );
            if( classifier_ == null ){
                //empty result
                for( int i = 0 ;i < returnLabelList.size() ; i ++ ){
                    returnLabelList.add( null );
                }
            }else{
                //query from old classifier model
                Instance<L> instance;
                for( TridentTuple tridentTuple:list ){

                    instance = (Instance<L>) tridentTuple.get(0);
                    //judge the result.
                    L result = classifier_.classify( instance.getFeatures() );
                    if( result.equals( instance.getLabel() ) ){//right
                        if( this.classificationStatistic.get( "right" ) == null){
                            this.classificationStatistic.put("right",1);
                        }else{
                            this.classificationStatistic.put("right",this.classificationStatistic.get("right")+1);
                        }
                    }else{//classifier classify error
                        if( this.classificationStatistic.get( "error" ) == null){
                            this.classificationStatistic.put("error",1);
                        }else{
                            this.classificationStatistic.put("error",this.classificationStatistic.get("error")+1);
                        }
                    }
                    returnLabelList.add( result );
                }
            }
        }else{/*no old classifier model.return empty query set*/
            for( int i = 0 ; i < returnLabelList.size(); i ++ ){
                returnLabelList.add( null );
            }
        }

        //show the statistic info

        showClassificationStatistic();
        return returnLabelList;
    }

    @Override
    public void execute(TridentTuple tridentTuple, L l, TridentCollector tridentCollector) {
        tridentCollector.emit( new Values( l ));
    }

    public String getClassifierModelName() {
        return classifierModelName;
    }

    public void setClassifierModelName(String classifierModelName) {
        this.classifierModelName = classifierModelName;
    }

    public Map<String, Integer> getClassificationStatistic() {
        return classificationStatistic;
    }

    public void setClassificationStatistic(Map<String, Integer> classificationStatistic) {
        this.classificationStatistic = classificationStatistic;
    }
}
