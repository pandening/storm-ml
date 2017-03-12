package com.hujian.trident.hybrid.data;

import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Created by hujian on 2017/3/11.
 */
public class InstanceCreator<L>  extends BaseFunction{

    private static final long serialVersionUID = 2242376024410720639L;

    protected Boolean withLabel = true;

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

        Long instanceId = tridentTuple.getLong( 0 );

        if( this.withLabel == true){
            L label = (L)tridentTuple.get( 1 );

            double[] features = new double[tridentTuple.size() -1];
            for( int i = 2; i < tridentTuple.size(); i ++ ){
                features[ i -2 ] = tridentTuple.getDouble( i );
            }

            instance = new Instance<L>(instanceId,label,features);
        }else{
            double[] features = new double[tridentTuple.size()];
            for( int i = 1; i < tridentTuple.size(); i ++ ){
                features[ i - 1 ] = tridentTuple.getDouble( i );
            }
            instance = new Instance<L>(instanceId,features);
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
