package com.hujian.breastCancer;

import org.apache.commons.lang.text.StrBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by hujian on 2017/3/27.
 */
public class BreastFileHandler {

    public static void main(String[] args) throws FileNotFoundException {

        /**
         * features: 0 , 1 , 2, 5
         */
        StrBuilder[] strBuilders = new StrBuilder[10];

        for( int i = 0 ; i < 10; i ++ ){
            strBuilders[i] = new StrBuilder();
        }

        String inputFile  = "I:\\breast-cancer.txt";

         FileInputStream fileInputStream = null;
         Scanner scanner = null;

         fileInputStream = new FileInputStream(inputFile);
         scanner = new Scanner(fileInputStream,"UTF-8");

         while (scanner.hasNextLine()){
             String[] lines = scanner.nextLine().split(",");

             if( lines == null || lines.length != 11 ){
                 continue;
             }

             strBuilders[0].append( lines[1] +"\t" + lines[10] + "\n");
             strBuilders[1].append( lines[2] +"\t" + lines[10] + "\n");
             strBuilders[2].append( lines[3] +"\t" + lines[10] + "\n");
             strBuilders[3].append( lines[4] +"\t" + lines[10] + "\n");
             strBuilders[4].append( lines[5] +"\t" + lines[10] + "\n");
             strBuilders[5].append( lines[6] +"\t" + lines[10] + "\n");
             strBuilders[6].append( lines[7] +"\t" + lines[10] + "\n");
             strBuilders[7].append( lines[8] +"\t" + lines[10] + "\n");
             strBuilders[8].append( lines[9] +"\t" + lines[10] + "\n");
             strBuilders[9].append( lines[10] +"\t" + lines[10] + "\n");

         }

         for( int i = 0 ;i < strBuilders.length; i ++ ){
             PrintWriter writer = new PrintWriter(new File("I:\\breast\\"+i));
             writer.println(strBuilders[i].toString());
             writer.close();
         }

         System.out.println("end");

    }

}
