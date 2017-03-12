package com.hujian.trident.ml.core;

import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Created by hujian on 2017/2/23.
 */
public class InstanceCreator<L> extends BaseFunction{

    private static final long serialVersionUID = 2242376024410720639L;

    protected Boolean withLabel = false;

    public InstanceCreator(){}
    public InstanceCreator( boolean withLabel ){
        this.withLabel = withLabel;
    }

    /**
     * create an instance from trident tuple.
     * @param tridentTuple
     * @return
     */
    protected Instance<L> createInstance(TridentTuple tridentTuple){
        Instance<L> instance = null;
        if( this.withLabel == true){
            L label = (L)tridentTuple.get( 0 );

            double[] features = new double[tridentTuple.size() -1];
            for( int i = 1; i < tridentTuple.size(); i ++ ){
                features[ i -1 ] = tridentTuple.getDouble( i );
            }

            instance = new Instance<L>(label,features);
        }else{
            double[] features = new double[tridentTuple.size()];
            for( int i = 0; i < tridentTuple.size(); i ++ ){
                features[ i ] = tridentTuple.getDouble( i );
            }
            instance = new Instance<L>(features);
        }
        return instance;
    }

    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        if( tridentTuple == null ){
            return;
        }
        Instance<L> instance = this.createInstance( tridentTuple );

        Values values = new Values( instance );

        tridentCollector.emit( values);
    }

    public Boolean getWithLabel() {
        return withLabel;
    }

    public void setWithLabel(Boolean withLabel) {
        this.withLabel = withLabel;
    }

}
