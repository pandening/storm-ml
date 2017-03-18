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
import java.util.Random;

/**
 * Created by hujian on 2017/3/14.
 */
public class IntegerWithIDSpout implements IBatchSpout {
    private static final long serialVersionUID = 90804723850872L;
    private Integer BitchSize = 10;
    private Random random = null;
    private Long id = 0L;

    /**
     * the constructor
     * @param bitchSize
     */
    public IntegerWithIDSpout( Integer bitchSize){
        this.BitchSize = bitchSize;
    }
    @Override
    public void open(Map map, TopologyContext topologyContext) {
        this.random = new Random();
    }

    List<Instance<Integer>> createInstance(){
        List<Instance<Integer>> instanceList = new ArrayList<Instance<Integer>>();
        for(int i = 0 ; i < this.getBitchSize(); i ++) {
            Integer data = random.nextInt(100);

            //also,you can change the data type soon.
            instanceList.add( new Instance<Integer>(data,id,InputDataType.FREQUENCY_QUERY) );
            id++;
        }
        return instanceList;
    }

    @Override
    public void emitBatch(long l, TridentCollector tridentCollector) {
        List< Instance<Integer>> instances = this.createInstance();
        Values values;
        for (Instance<Integer> instance : instances) {
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

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }
}
