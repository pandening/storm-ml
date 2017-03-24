package com.hujian.hotmem.redis;

import com.hujian.hotmem.mysql.Mysql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by hujian on 2017/3/23.
 */
public class TestRedis implements Serializable{

    private static final long serialVersionUID = - 12101034211498L;

    public static final String url = "jdbc:mysql://localhost/hujian";
    public static final String name = "com.mysql.jdbc.Driver";
    public static final String user = "hujian";
    public static final String password = "hujian";

    public  static void main(String[] args) throws SQLException {

        Mysql mysql = Mysql.getInstance(url,user,password,name);
        //String sql = "update comedyVote set vote = " + 10 +" where comedy_name = hujian";
        String sql = "update comedyVote set vote = 100 where comedy_name = 'hujian'";
        sql = "select * from info";

        //mysql.exec(sql);


        ResultSet resultSet = mysql.execSql(sql);
        while( resultSet.next() ){
            String id = resultSet.getString(1);
            String name = resultSet.getString(2);
            System.out.println(id+"\t"+name);
        }
        resultSet.close();



        System.exit(0);



        RedisCache redisCache = RedisCache.getInstance("10.134.72.137","6379",60000);


        System.out.println("use_cache:"+redisCache.getJedis().get("use_cache"));
        System.exit(0);


        System.out.println(redisCache.getJedis().info());

      List<String> result = redisCache.getJedis().lrange("hot_key",0,-1);


      for( String i : result ){
          System.out.println(i);
      }

        FileInputStream fileInputStream = null;
        Scanner scanner = null;

        //get the input stream
        try {
            fileInputStream = new FileInputStream("I:\\comedy_comparisons\\comedy_comparisons_votes.train");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //get the scanner
        scanner = new Scanner(fileInputStream,"UTF-8");

        Set<String> set = new HashSet<>();

        while (scanner.hasNextLine()){
            String line = scanner.nextLine();

            if( line.split(",").length != 3){
                System.out.println(line);
                continue;
            }

           // System.out.println(line.split(",")[0]+" - "+ line.split(",")[1]);

            set.add( line.split(",")[0] );
            set.add( line.split(",")[1] );
        }

        System.out.println("total comedy:"+set.size());


    }
}
