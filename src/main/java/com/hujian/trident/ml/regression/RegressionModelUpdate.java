package com.hujian.trident.ml.regression;

import com.hujian.trident.ml.core.Instance;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hujian on 2017/3/1.
 */
public class RegressionModelUpdate extends BaseStateUpdater<MapState<RegressOr>> {

    private static final long serialVersionUID = -2160370600415723002L;

    private String regressionModelName ;
    private RegressOr regressOr;

    public RegressionModelUpdate(){}

    /**
     * the constructor
     * @param regressionModelName
     * @param regressOr
     */
    public RegressionModelUpdate( String regressionModelName, RegressOr regressOr ){
        this.regressionModelName = regressionModelName;
        this.regressOr = regressOr;
    }

    @Override
    public void updateState(MapState<RegressOr> regressOrMapState, List<TridentTuple> list, TridentCollector tridentCollector) {
        //get the old regression model
        List<RegressOr> regressOrList = regressOrMapState.multiGet(Arrays.asList(
                Arrays.asList( (Object)this.regressionModelName ) ));
        //the useful model
        RegressOr regressOr_ = null;
        if( regressOrList != null && regressOrList.size() != 0 ){
            regressOr_ = regressOrList.get( 0 );
        }

        if( regressOr_ == null ){
            //no old model
            regressOr_ = this.regressOr;
        }
        Instance<Double> instance = null;
        //update
        for( TridentTuple tridentTuple: list ){
            instance = (Instance<Double>) tridentTuple.get( 0 );
            regressOr_.update( instance.getLabel(),instance.getFeatures() );
        }

        //save the new model
        regressOrMapState.multiPut(Arrays.asList(
                Arrays.asList( (Object) this.regressionModelName ) ) ,Arrays.asList( regressOr_ ));

    }

    public String getRegressionModelName() {
        return regressionModelName;
    }

    public void setRegressionModelName(String regressionModelName) {
        this.regressionModelName = regressionModelName;
    }

    public RegressOr getRegressOr() {
        return regressOr;
    }

    public void setRegressOr(RegressOr regressOr) {
        this.regressOr = regressOr;
    }
}
