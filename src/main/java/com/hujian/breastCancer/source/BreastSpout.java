package com.hujian.breastCancer.source;

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
 * Created by hujian on 2017/3/27.
 */
public class BreastSpout implements IBatchSpout {
    private static final long serialVersionUID = - 201141034134211498L;
    private String filePath = null;
    private FileInputStream fileInputStream = null;
    private Scanner scanner = null;

    /**
     * constructor
     * @param filePath
     */
    public BreastSpout( String filePath){
        this.filePath = filePath;
    }

    @Override
    public void open(Map map, TopologyContext topologyContext) {
        //get the input stream
        try {
            this.fileInputStream = new FileInputStream(this.filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //get the scanner
        this.scanner = new Scanner(this.fileInputStream,"UTF-8");
    }

    @Override
    public void emitBatch(long l, TridentCollector tridentCollector) {
        List<String> instanceList = new ArrayList<>();
        //create the instance and emit the batch to next bolt.
        while( scanner.hasNextLine() ){
            String[] lines = scanner.nextLine().split(",");
            if( lines.length != 11 ){
                continue;
            }
            Values values = new Values();

            for( int i = 1 ; i < lines.length - 1; i ++ ){
                values.add( Double.parseDouble( lines[i] ) );
            }

            values.add( Integer.parseInt( lines [ lines.length  - 1  ] ) );

            tridentCollector.emit( values );
        }

        System.out.println("end of file");
        Utils.sleep(10000000);
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

        String[] fields = new String[10];

        for( int i = 0 ; i < 10; i ++ ){

            fields[i] = "x"+ i ;

        }

        return new Fields(fields);
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
