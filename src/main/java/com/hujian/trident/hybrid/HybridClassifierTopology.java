package com.hujian.trident.hybrid;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.hujian.trident.hybrid.classifier.SamplesClassifierFactory;
import com.hujian.trident.hybrid.data.AbaloneDataSpout;
import com.hujian.trident.hybrid.data.InstanceCreator;
import com.hujian.trident.hybrid.functions.ClassifierModelUpdater;
import com.hujian.trident.hybrid.functions.ClassifyJudgeFunction;
import com.hujian.trident.hybrid.functions.EndFunction;
import com.hujian.trident.ml.classifier.Classifier;
import com.hujian.trident.ml.classifier.Committee.CommitteeClassifier;
import com.hujian.trident.ml.classifier.PassiveAggressive.MultiClassPAClassier;
import com.hujian.trident.ml.classifier.PassiveAggressive.PATypeEnum;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/12.
 */
public class HybridClassifierTopology {
    /**
     * build the topology
     * @return
     */
    public static StormTopology builder(String path){

        TridentTopology tridentTopology = new TridentTopology();

        Classifier<Integer> committeeClassifier = new CommitteeClassifier(0.1,3);
        Classifier<Integer> multiPaClassifierPA = new MultiClassPAClassier(PATypeEnum.PA,3,0.001);
        Classifier<Integer> multiPaClassifierPAI = new MultiClassPAClassier(PATypeEnum.PA_I,3,0.001);
        Classifier<Integer> multiPaClassifierPAII = new MultiClassPAClassier(PATypeEnum.PA_II,3,0.001);

        /**
         * init the factory
         */
        SamplesClassifierFactory<Integer> samplesClassifierFactory = SamplesClassifierFactory.getInstance();

        samplesClassifierFactory.addClassifier( committeeClassifier );
        samplesClassifierFactory.addClassifier(multiPaClassifierPA);
        samplesClassifierFactory.addClassifier(multiPaClassifierPAI);
        samplesClassifierFactory.addClassifier(multiPaClassifierPAII);

        tridentTopology
                //Committee classifier part
                .newStream("hybrid-classifier",new AbaloneDataSpout(path,1))
                .each(new Fields("id","label","x1","x2","x3","x4","x5","x6","x7","x8"),
                        new InstanceCreator<Integer>(true), new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new ClassifierModelUpdater<Integer>("hybridClassifier",committeeClassifier),
                        new Fields("id1","label1","x01","x11", "x21","x31","x41","x51","x61","x71")).newValuesStream()
                .each(new Fields("id1","label1","x01","x11", "x21","x31","x41","x51","x61","x71"),
                        new InstanceCreator<Integer>(true), new Fields("instance1"))
                .each(new Fields("instance1"),new ClassifyJudgeFunction(),new Fields(
                        "id11","label11","x011","x111", "x211","x311","x411","x511","x611","x711"))

                //Multi-Pa classifier part
                .each(new Fields("id11","label11","x011","x111", "x211","x311","x411","x511","x611","x711"),
                        new InstanceCreator<Integer>(true), new Fields("instance2"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance2"),
                        new ClassifierModelUpdater<Integer>("hybridClassifier1",multiPaClassifierPA),
                        new Fields("id","label","x0","x1","x2","x3","x4","x5","x6","x7")).newValuesStream()
                .each(new Fields("id","label","x0","x1", "x2","x3","x4","x5","x6","x7"),
                        new InstanceCreator<Integer>(true), new Fields("instance2"))
                .each(new Fields("instance2"),new ClassifyJudgeFunction(),new Fields(
                        "id11","label11","x011","x111", "x211","x311","x411","x511","x611","x711"))

                //Multi-Pa-I classifier part
                .each(new Fields("id11","label11","x011","x111", "x211","x311","x411","x511","x611","x711"),
                        new InstanceCreator<Integer>(true), new Fields("instance3"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance3"),
                        new ClassifierModelUpdater<Integer>("hybridClassifier2",multiPaClassifierPAI),
                        new Fields("id","label","x0","x1","x2","x3","x4","x5","x6","x7")).newValuesStream()
                .each(new Fields("id","label","x0","x1", "x2","x3","x4","x5","x6","x7"),
                        new InstanceCreator<Integer>(true), new Fields("instance4"))
                .each(new Fields("instance4"),new ClassifyJudgeFunction(),new Fields(
                        "id11","label11","x011","x111", "x211","x311","x411","x511","x611","x711"))

                //Multi-Pa-II classifier part
                .each(new Fields("id11","label11","x011","x111", "x211","x311","x411","x511","x611","x711"),
                        new InstanceCreator<Integer>(true), new Fields("instance5"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance5"),
                        new ClassifierModelUpdater<Integer>("hybridClassifier3",multiPaClassifierPAII),
                        new Fields("id","label","x0","x1","x2","x3","x4","x5","x6","x7")).newValuesStream()
                .each(new Fields("id","label","x0","x1", "x2","x3","x4","x5","x6","x7"),
                        new InstanceCreator<Integer>(true), new Fields("instance6"))
                .each(new Fields("instance6"),new ClassifyJudgeFunction(),new Fields(
                        "id11","label11","x011","x111", "x211","x311","x411","x511","x611","x711"))

                //end.
                .each(new Fields("id11","label11","x011","x111", "x211","x311","x411","x511","x611","x711"),
                        new InstanceCreator<Integer>(true), new Fields("instance7"))
                .each(new Fields("instance7"),new EndFunction(),new Fields("end"));


        /**
         * return the builder
         */
        return tridentTopology.build();
    }


    public static void main(String[] args){

        String path = "E:\\IdeaProjects\\data\\train.csv";
        path = "E:\\IdeaProjects\\data\\abalone.txt";


        try {
            StormSubmitter.submitTopologyWithProgressBar("hybrid-classifier",new Config(),
                    HybridClassifierTopology.builder(path));
        } catch (AlreadyAliveException e) {
            e.printStackTrace();
        } catch (InvalidTopologyException e) {
            e.printStackTrace();
        }


        LocalCluster localCluster = new LocalCluster();

        localCluster.submitTopology("hybrid-classifier-topology",new Config(),HybridClassifierTopology.builder(path));

    }

}
