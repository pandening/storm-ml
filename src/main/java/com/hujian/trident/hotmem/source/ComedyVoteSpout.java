package com.hujian.hotmem.source;

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
 * Created by hujian on 2017/3/23.
 */
public class ComedyVoteSpout implements IBatchSpout {

    private static final long serialVersionUID = - 201141034134211498L;

    private  String dataSourcePath = null;
    private  Integer BitchSize = 10;
    private Long id = null;

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
    public ComedyVoteSpout( String dataSourcePath ,Integer bitchSize){
        this.dataSourcePath = dataSourcePath;
        this.BitchSize = bitchSize;
        this.id = 0L;
    }

    /**
     * constructor
     * @param dataSourcePath
     */
    public ComedyVoteSpout( String dataSourcePath ){
        this.dataSourcePath = dataSourcePath;
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
        List<ComedyComparisonsInstance> instanceList = new ArrayList<ComedyComparisonsInstance>();
        while( scanner.hasNextLine() ){
            String[] lines = scanner.nextLine().split(",");
            if( lines.length != 3 ){
                continue;
            }

            ComedyComparisonsInstance comparisonsInstance =
                    new ComedyComparisonsInstance(lines[0],lines[1],lines[2],id ++);

            if( comparisonsInstance == null ){
                continue;
            }

            instanceList.add(comparisonsInstance);

            if( instanceList.size() >= this.BitchSize ){
                for( ComedyComparisonsInstance instance:instanceList ){
                    tridentCollector.emit(new Values( instance.getInstanceId(),instance.getComedyLeft(),
                            instance.getComedyRight(),instance.getComedyVote()));
                    //Utils.sleep( 1000 );
                }
                instanceList.clear();
                return;
            }
        }
        //if left
        if( instanceList.size() != 0 ){
            for( ComedyComparisonsInstance instance:instanceList ){
                tridentCollector.emit(new Values( instance.getInstanceId(),instance.getComedyLeft(),
                        instance.getComedyRight(),instance.getComedyVote()));
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
        return new Fields("id","comedyLeft","comedyRight","comedyVote");
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
}
