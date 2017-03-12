package com.hujian.trident.ml.examples.data;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.hujian.trident.ml.core.Instance;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by hujian on 2017/3/1.
 */
public class RandomRegressionSpout implements IBatchSpout {
    private static final long serialVersionUID = 9087100681047850872L;

    private Integer BitchSize = 10;
    private Integer featuresSize = 3;
    private Random random = null;
    private Boolean withLabel = true;


    public RandomRegressionSpout(){}

    /**
     * the constructor
     * @param bitchSize
     * @param featuresSize
     */
    public RandomRegressionSpout( Integer bitchSize,Integer featuresSize){
        this.BitchSize = bitchSize;
        this.featuresSize = featuresSize;
    }

    /**
     * @return
     */
    List<Instance<Double>> createInstance(){
        List<Instance<Double>> instanceList = new ArrayList<Instance<Double>>();
        for(int i = 0 ; i < this.getBitchSize(); i ++) {
            double[] features = new double[this.featuresSize];
            Double label =Math.abs(random.nextDouble() * 10 );
            for (int j = 0; j < this.featuresSize; j++) {
                features[j] = random.nextDouble();
            }
            instanceList.add( new Instance<Double>(label,features) );
        }
        return instanceList;
    }

    @Override
    public void open(Map map, TopologyContext topologyContext) {
        random = new Random();
    }

    @Override
    public void emitBatch(long l, TridentCollector tridentCollector) {
        List<Instance<Double>> instances = this.createInstance();
        Values values;
        for (Instance<Double> instance : instances) {
            values = new Values();
            if (this.withLabel.equals( Boolean.TRUE )) {
                values.add(instance.getLabel());
            }
            for (int i = 0; i < instance.getFeatures().length; i++) {
                values.add(instance.features[i]);
            }
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
        String[] fieldNames;

        if (this.withLabel) {
            fieldNames = new String[this.featuresSize + 1];
            fieldNames[0] = "Label";
            for (int i = 0; i < this.featuresSize; i++) {
                fieldNames[i + 1] = "i" + i;
            }
        } else {
            fieldNames = new String[this.featuresSize];
            for (int i = 0; i < this.featuresSize; i++) {
                fieldNames[i] = "i" + i;
            }
        }
        return new Fields(fieldNames);
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

    public Integer getFeaturesSize() {
        return featuresSize;
    }

    public void setFeaturesSize(Integer featuresSize) {
        this.featuresSize = featuresSize;
    }

}
