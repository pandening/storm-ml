package com.hujian.trident.hybrid.data;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by hujian on 2017/3/12.
 */
public class AbaloneDataSpout implements IBatchSpout {
    private static final long serialVersionUID = - 201141034134211498L;

    private  String dataSourcePath = null;
    private  Integer BitchSize = 10;
    private  Long  instanceId = 0L;

    private FileInputStream fileInputStream = null;
    private Scanner scanner = null;

    /**
     * you should give me the file path,then i will read the file by liens and
     * emit the tuple to next bolt.
     *
     * @ref use the UCI data set,you can find the data source by the follow link.
     * @link http://archive.ics.uci.edu/ml/datasets/HIGGS#
     *
     * @param dataSourcePath
     * @param bitchSize
     */
    public AbaloneDataSpout( String dataSourcePath ,Integer bitchSize){
        this.dataSourcePath = dataSourcePath;
        this.BitchSize = bitchSize;
    }

    /**
     * constructor
     * @param dataSourcePath
     */
    public AbaloneDataSpout( String dataSourcePath ){
        this.dataSourcePath =dataSourcePath;
    }

    @Override
    public void open(Map map, TopologyContext topologyContext) {
        //get the input stream
        try {
            this.fileInputStream = new FileInputStream(this.dataSourcePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //get the scanner
        this.scanner = new Scanner(this.fileInputStream,"UTF-8");
    }

    @Override
    public void emitBatch(long l, TridentCollector tridentCollector) {
        //create the instance and emit the batch to next bolt.
        List<Instance<Integer>> instanceList = new ArrayList<Instance<Integer>>();
        while( scanner.hasNextLine() ){
            String[] lines = scanner.nextLine().split(",");
            Integer label = lines[0].equals("M") ? 0 : ( lines[0].equals("F") ? 1: 2 );
            double[] features = new double[lines.length - 1];
            for( int i =1 ; i < lines.length; i ++ ){
                features[i-1] = Double.parseDouble( lines[i] );
            }
            /**
             * attach instance id.
             */
            Instance<Integer> instance_ = new Instance<Integer>(instanceId ++,label,features);

            if( instance_ == null ){
                continue;
            }

            instanceList.add(instance_);
            if( instanceList.size() >= this.BitchSize ){
                for( Instance<Integer> instance:instanceList ){
                    Values values =new Values();
                    values.add( instance.getInstanceId() );
                    values.add(instance.getLabel());
                    for (int i = 0; i < instance.getFeatures().length; i++) {
                        values.add(instance.features[i]);
                    }
                    tridentCollector.emit(values);
                    return;
                }
                instanceList.clear();
            }
        }
        //if left
        if( instanceList.size() != 0 ){
            for( Instance<Integer> instance:instanceList ){
                Values values =new Values();
                values.add( instance.getInstanceId() );
                values.add(instance.getLabel());
                for (int i = 0; i < instance.getFeatures().length; i++) {
                    values.add(instance.features[i]);
                }
                tridentCollector.emit(values);
            }
        }
        System.out.println("no more data.....");
        Utils.sleep(1000000);
    }

    @Override
    public void ack(long l) {

    }

    @Override
    public void close() {
        //close
        this.scanner.close();
        try {
            this.fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map getComponentConfiguration() {
        return null;
    }

    @Override
    public Fields getOutputFields() {
        //29 dimen. 28 features dimen , 1 label ,hei hei hei !!
        String[] fieldNames = new String[10];
        fieldNames[0] = "id";
        fieldNames[1] = "label";
        /**
         * start from x1.....
         */
        for( int i = 1 ;i <= 8; i ++ ){
            fieldNames[i+1] = "x"+i;
        }

        return new Fields(fieldNames);
    }

    public String getDataSourcePath() {
        return dataSourcePath;
    }

    public void setDataSourcePath(String dataSourcePath) {
        this.dataSourcePath = dataSourcePath;
    }

    public Integer getBitchSize() {
        return BitchSize;
    }

    public void setBitchSize(Integer bitchSize) {
        BitchSize = bitchSize;
    }

    public FileInputStream getFileInputStream() {
        return fileInputStream;
    }

    public void setFileInputStream(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }
}
