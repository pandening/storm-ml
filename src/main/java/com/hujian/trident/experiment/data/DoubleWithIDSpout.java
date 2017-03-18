package com.hujian.trident.experiment.data;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.hujian.trident.experiment.core.Instance;
import com.hujian.trident.ml.core.InputDataType;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hujian on 2017/3/15.
 */
public class DoubleWithIDSpout implements IBatchSpout {
    private static final long serialVersionUID = 90804723850872L;
    private Integer BitchSize = 10;
    private Long id = 0L;
    private IDUtils idUtils = null;

    /**
     * the constructor
     * @param bitchSize
     */
    public DoubleWithIDSpout( Integer bitchSize){
        this.BitchSize = bitchSize;
        this.idUtils = IDUtils.getInstance();
    }

    @Override
    public void open(Map map, TopologyContext topologyContext) {
    }

    List<Instance<Double>> createInstance(){
        List<Instance<Double>> instanceList = new ArrayList<Instance<Double>>();
        for(int i = 0 ; i < this.getBitchSize(); i ++) {
            Double item = Math.random() * 100.0;

            //also,you can change the data type soon.
            instanceList.add( new Instance<Double>(item,id,InputDataType.FREQUENCY_QUERY) );
            //this.idUtils.increaseID(1L);
            id++;
        }
        return instanceList;
    }

    @Override
    public void emitBatch(long l, TridentCollector tridentCollector) {
        List< Instance<Double>> instances = this.createInstance();
        Values values;
        for (Instance<Double> instance : instances) {
            values = new Values();
            values.add( instance.getInstanceId());
            values.add( instance.getData());
            values.add(instance.getDataType());
            tridentCollector.emit(values);
        }
    }

    @Override
    public void ack(long l) {
    }
    @Override
    public void close() {
    }
    @Override
    public Map getComponentConfiguration() {
        return null;
    }
    @Override
    public Fields getOutputFields() {
        return new Fields("id","data","type");
    }

    public Integer getBitchSize() {
        return BitchSize;
    }

    public void setBitchSize(Integer bitchSize) {
        BitchSize = bitchSize;
    }

}
