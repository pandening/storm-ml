package com.hujian.trident.ml.testing;

import backtype.storm.utils.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Created by hujian on 2017/3/4.
 */
public class ReadCsvFileHandler {

    public static void main(String[] args)throws Exception {
        FileInputStream inputStream = new FileInputStream("I:\\HIGGS.csv");
        Scanner scanner = new Scanner(inputStream,"UTF-8");
        Integer linesCount = 1;
        int fileIndex = 0;
        Long startTimes = System.currentTimeMillis();
        String fileBase = "E:\\IdeaProjects\\data\\";
        String file = fileBase+"test.csv";
        FileWriter writer = new FileWriter(file);
        while( scanner.hasNextLine() ){
            String[] lines = scanner.nextLine().split(",");
            linesCount++;
            if( linesCount < 1000010 ){
                continue;
            }
            lines[0] = lines[0].substring(0,1);
            for( String ele:lines ){
                writer.append(ele+" ");
            }
            writer.append("\n");
            if( linesCount > 2000000 ){
                break;
            }
        }

        writer.close();
        scanner.close();
        inputStream.close();
        Long endTime = System.currentTimeMillis();
        System.out.println("total lines:"+linesCount +" takes time:"+(endTime - startTimes) / 1000.0 + " s");
    }
}
