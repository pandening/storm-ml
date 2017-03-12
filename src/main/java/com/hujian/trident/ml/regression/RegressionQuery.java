package com.hujian.trident.ml.regression;

import backtype.storm.tuple.Values;
import com.hujian.trident.ml.core.Instance;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hujian on 2017/3/1.
 */
public class RegressionQuery extends BaseQueryFunction<MapState<RegressOr>,Double> {

    private static final long serialVersionUID = -8970858930000644113L;

    private String regressionModelName;

    /**
     *
     * @param regressionModelName
     */
    public RegressionQuery( String  regressionModelName){
        this.regressionModelName = regressionModelName;
    }

    @Override
    public List<Double> batchRetrieve(MapState<RegressOr> regressOrMapState, List<TridentTuple> list) {
        List<Double> returnLabelList = new ArrayList<Double>();

        //get the old model from map state
        List<RegressOr> regressOrList = regressOrMapState
                .multiGet(Arrays.asList( Arrays.asList( (Object)this.regressionModelName ) ));
        if( regressOrList != null && regressOrList.size() != 0 ){
            RegressOr regressOr_ = regressOrList.get( 0 );
            if( regressOr_ == null ){
                //empty result
                for( int i = 0 ;i < returnLabelList.size() ; i ++ ){
                    returnLabelList.add( null );
                }
            }else{
                //query from old classifier model
                Instance<Double> instance;
                for( TridentTuple tridentTuple:list ){
                    instance = (Instance<Double>) list.get( 0 );
                    returnLabelList.add( regressOr_.predict( instance.getFeatures() ) );

                }
            }
        }else{/*no old classifier model.return empty query set*/
            for( int i = 0 ; i < returnLabelList.size(); i ++ ){
                returnLabelList.add( null );
            }
        }
        return returnLabelList;
    }

    @Override
    public void execute(TridentTuple tridentTuple, Double aDouble, TridentCollector tridentCollector) {
        tridentCollector.emit( new Values( aDouble ));
    }

    public String getRegressionModelName() {
        return regressionModelName;
    }

    public void setRegressionModelName(String regressionModelName) {
        this.regressionModelName = regressionModelName;
    }
}
