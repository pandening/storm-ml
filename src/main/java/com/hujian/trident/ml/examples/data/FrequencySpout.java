package com.hujian.trident.ml.examples.data;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.hujian.trident.ml.core.CountEntry;
import com.hujian.trident.ml.core.InputDataType;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by hujian on 2017/3/6.
 */
public class FrequencySpout implements IBatchSpout {

    private static final long serialVersionUID = 908047850872L;
    private Integer BitchSize = 10;
    private Random random = null;

    /**
     * the constructor
     * @param bitchSize
     */
    public FrequencySpout( Integer bitchSize){
        this.BitchSize = bitchSize;
    }

    @Override
    public void open(Map map, TopologyContext topologyContext) {
        this.random = new Random();
    }

    List<CountEntry<Integer>> createInstance(){
        List<CountEntry<Integer>> instanceList = new ArrayList<CountEntry<Integer>>();
        for(int i = 0 ; i < this.getBitchSize(); i ++) {
            Integer item = random.nextInt(100);

            //also,you can change the data type soon.
            InputDataType inputDataType = InputDataType.FREQUENCY_STATISTIC;
            instanceList.add( new CountEntry<Integer>(item,0L,inputDataType) );
        }
        return instanceList;
    }

    @Override
    public void emitBatch(long l, TridentCollector tridentCollector) {
        List< CountEntry<Integer>> instances = this.createInstance();
        Values values;
        for (CountEntry<Integer> instance : instances) {
            values = new Values();
            values.add( instance.getItem());
            values.add( instance.getFrequency());
            values.add( instance.getInputDataType() );
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
        return new Fields("item","frequency","type");
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
