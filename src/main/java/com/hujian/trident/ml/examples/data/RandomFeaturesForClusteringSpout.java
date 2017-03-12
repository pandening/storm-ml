package com.hujian.trident.ml.examples.data;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import com.hujian.trident.ml.core.Instance;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;

import java.util.List;
import java.util.Map;

/**
 * Created by hujian on 2017/2/24.
 */
public class RandomFeaturesForClusteringSpout implements IBatchSpout{
    private static final long serialVersionUID = -5293861317274377258L;

    private int maxBatchSize = 1;
    private int featureSize = 3;
    private int nbClasses = 3;


    public RandomFeaturesForClusteringSpout() {
    }

    public RandomFeaturesForClusteringSpout(int featureSize) {
        this.featureSize = featureSize;
    }

    public RandomFeaturesForClusteringSpout(int featureSize, int nbClasses) {
        this.featureSize = featureSize;
        this.nbClasses = nbClasses;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void open(Map conf, TopologyContext context) {
    }

    @Override
    public void emitBatch(long batchId, TridentCollector collector) {
        List<Instance<Integer>> instances = Datasets
                .generateDataForClassification(this.maxBatchSize, this.featureSize, this.nbClasses);

        Values values;
        for (Instance<Integer> instance : instances) {
            values = new Values();
            for (int i = 0; i < instance.features.length; i++) {
                values.add(instance.features[i]);
            }
            collector.emit(values);
            Utils.sleep(1000);
        }
    }
    @Override
    public void ack(long batchId) {
    }

    @Override
    public void close() {
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map getComponentConfiguration() {
        return null;
    }

    @Override
    public Fields getOutputFields() {
        String[] fieldNames;
        fieldNames = new String[this.featureSize];
        for (int i = 0; i < this.featureSize; i++) {
            fieldNames[i] = "x" + i;
        }
        return new Fields(fieldNames);
    }

    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    public void setMaxBatchSize(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    public int getFeatureSize() {
        return featureSize;
    }

    public void setFeatureSize(int featureSize) {
        this.featureSize = featureSize;
    }

}
