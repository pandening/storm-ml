# open-streamer

## What is open-streamer ?

  Open-Streamer is a library base on Storm platform,it is described by Trident api.and it focus on the real-time
  algorithm and online learnning algorithm,this library has implemented some classical algorithms type,like classifier,
  clustering,Regression,Cardinality,and Average Counting.etc,you can build some smart applications with this library
  over big data environment,it's easy to use this library on your project.I will give the start-tutorial for you to 
  help you start to use this library.This library is not so ORIGINAL,you must know the Machine Learnning algorithm
  Library Over Storm : Trident-Ml,open-streamer  extends trident-ml,Thanks Trident-ml's open source spirit.

## Open-Stream Algorithms Overviews:
* Average 
     - Moving Average [[1]] (#ref1) 
     - EWMA average[[2]] (#ref2) 
* Cardinality 
     - LogLog Cardinality[[3]] (#ref3)
     - HyperLogLog cardinality[[4]] (#ref4)
     - Adaptive Counting Cardinality[[5]] (#ref5)
     - Linear Counting 
* Classification
     - Committee Classifier[[6]] (#ref6)
     - Passive Aggressive Classifier[[7]] (#ref7)
     - Perceptron Classifier[[8]] (#ref8)
     - Winnow Classifier[[9]] (#ref9)
     - Balanced Winnow Classifier[[10]] (#ref10)
     - Modify Banalced Winnow Classifier[[11]] (#ref11)
* Clustering
     - Birch
     - Canopy
     - K-means
* Frequency Counting
     - Count Sketch[[12]] (#ref12)
     - Lossy Counting[[13]] (#ref13)
     - Stick Sampling Counting[[14]] (#ref14)
     - Space Saving[[15]] (#ref15)
     - Top-k 
* Regression
     - Ftrl regression[[16]] (#ref16)
     - Perceptron Regression[[17]] (#ref17)
     - Passive Aggression Regression
     
Tutorial

------------------------------
 You should have a spout for your Topology(DAG),you can Reference <https://github.com/pandening/open-streamer/blob/master/src/main/java/com/hujian/trident/ml/examples/data/DoubleSpout.java>
 Then,the data flow from spout will needto be transformed to an Object instance of com.hujian.trident.ml.core.Instance,there is a good and
 sample instance creator for you in the package: com.hujian.trident.ml.core.InstanceCreator , you can use this creator to create an instance
 and then emit the data flow to downstream.you should know about Trident's Api,like Function,Filter,StateUpdate,etc,for example,if you want 
 to do some filter work on the data flow,you can let the data flow into a filter of Trident,then emit the data that you want to the downstram.
 
  you can builder your topology with Trident Apis,for example,you can build an topology to run an average algorithm,like Moving Average,
  the only thing you need to do is adjust the runtime parameter , the follow java code will let you know how to use this library.
  
  average is instance of IAverage,you can let average = new MovingAverage or EWMAAverage.
  
 ```java
         TridentTopology tridentTopology = new TridentTopology();

         tridentTopology.newStream(topologyName,new DoubleSpout(10))
                 .each(new Fields("item","frequency","type"),
                         new CountEntryInstanceCreator<Double>(),new Fields("instance"))
                 .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                         new AverageModelUpdater("average-model-update",average),new Fields("average"))
                 .newValuesStream()
                 .each(new Fields("average"),new ShowAverageFunction(),new Fields("done"))
                 .each(new Fields("done"),new ShowAverageFunction(),new Fields(""));
 ```
 There is an Integrated java code <https://github.com/pandening/open-streamer/blob/master/src/main/java/com/hujian/trident/ml/GPAPPBuilder.java>
 
### A complex demo for this library
Hybrid Classifier , a complex demo for this library,you can add Arbitrary Classifiers to the factory,the factory will choose some of
    its to classify the instrance,in the actual demo,I use 4 classifiers to test the hybrid classifier,a Committee Classifier,and 3 
    Passive Aggressive Classifier(Pa,Pa-I,PA-II),the data flow will be classified by the Committee Classiffer firstly,the Classification 
    result will store at a singleton class,you can implement your storage by implement IStore,then the data flow will continue flow to
    downstream, the PA Classifier will receive the instance,the PA classifier will first do classify,get the classication result,then
    get the classification list of this instance by instance id(each instance will be signed a instanceID),then the project will judge,
    if Committee's classication result equals PA's result,then end of classifying,get the classification result,and remove the instance
    from storage,then go to a Trident Function named EndFunction,do some print work,you can do more complexer work here,and,if 
    Comittee's result != PA's result,the data will continue to next classifier PA-I,do some work like PA classifier,and if necessary,the
    PA-II classifier will do the same work like PA,PA-I,and after PA-II classifier,if there is no same classification result in the 
    result list of this instance,the program will vote an classifier's result to you according to a weight vector,this vector will maintain
    by each Classifier,if any Classifier can get the classification result,the weight vector will be updated,the rules to update is:
      -(1) scanning each classifier's classification result,if the Classifier's classification result is the final result,then the 
      classifier's weight will add 1L
      -(2) after updating the weight vector,for some reasons,we need to normalize the weight vector's sum to 100(or others small value)
    in the final classifier,the program also do some statistic work,like Right/Error count,you can print the information to watch the 
    process of algorithm running.
    
    
 Relevant Knowledge
----------------------------------
* Storm [[18]] (#ref18)
* Trident [[19]] (#ref19)
* Trident-ml [[20]] (#ref20)
* Mahout [[21]] (#ref21)

    
Authors
----------------------------
Jian Hu,NanKai Edu,Tian Jin,China,2013.9 - 2017.6 (compute science and technology)

Email:<1425124481@qq.com>     
     
Links & References
-----------------------------
`[1]` <a name = ref1> </a> Key Words: Moving Average , Goolgle 

`[2]` <a name = ref2> </a> <http://blog.csdn.net/x_i_y_u_e/article/details/44194761>

`[3]` <a name = ref3> </a> <http://blog.csdn.net/keshixi/article/details/46730231>

`[4]` <a name = ref4> </a> <http://algo.inria.fr/flajolet/Publications/FlFuGaMe07.pdf>

`[5]` <a name = ref5> </a> Fast and Accurate Traffic Matrix Measurement Using Adaptive Cardinality Counting

`[6]` <a name = ref6> </a> A Multi-class Linear Learning Algorithm Related to Winnow

`[7]` <a name = ref7> </a> Online Passive-Aggressive Algorithms

`[8]` <a name = ref8> </a> <http://www.cnblogs.com/jerrylead/archive/2011/04/18/2020173.html>

`[9]` <a name = ref9> </a> <https://en.wikipedia.org/wiki/Winnow_(algorithm)>

`[10]`<a name = ref10> </a> Single-Pass Online Learning: Performance, VotingSchemes and Online Feature Selection

`[11]`<a name = ref11> </a> Gender Identification on Twitter Using the Modified Balanced Winnow

`[12]`<a name = ref12> </a> <http://dimacs.rutgers.edu/~graham/pubs/papers/freqvldbj.pdf>

`[13]`<a name = ref13> </a> Approximate Frequency Counts over Data Streams

`[14]`<a name = ref14> </a> Approximate Frequency Counts over Data Streams

`[15]`<a name = ref15> </a> Efficient Computation of Frequent and Top-k Elements in Data Streams

`[16]`<a name = ref16> </a> Ad Click Prediction: a View from the Trenches

`[17]`<a name = ref17> </a> Online Passive-Aggressive Algorithms

`[18]`<a name = ref18> </a> <http://storm.apache.org/>

`[19]`<a name = ref19> </a> <https://github.com/apache/storm/tree/master/storm-core/src/jvm/org/apache/storm/trident>

`[20]`<a name = ref20> </a> <https://github.com/pmerienne/trident-ml>

`[21]`<a name = ref21> </a> <http://mahout.apache.org/>
     
 Copyright and license
----------------------------------------

Copyright 2013-2017 Pierre Merienne

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
