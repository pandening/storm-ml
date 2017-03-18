package com.hujian.trident.experiment.cardinality.functions;

import backtype.storm.utils.Utils;
import com.hujian.trident.hybrid.store.IStore;
import com.hujian.trident.hybrid.store.SampleStored;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by hujian on 2017/3/15.
 */
public class FinalFunction<T> extends BaseFunction {

    /**
     * the store
     */
    //private IStore<T,Long> statisticStore = null;
    private double sum = 0.0;
    private Long reachCount = 0L;
    private StringBuilder sb = null;

    public FinalFunction(){
        //this.statisticStore = SampleStored.getInstance();
        this.sb = new StringBuilder();
    }

    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        if( tridentTuple == null ){
            return;
        }
        //get the id and frequency
        Long instanceID = tridentTuple.getLong( 0 );
        Long frequency = tridentTuple.getLong( 1 );

        if( instanceID == null || frequency == null ){
            return;
        }

        //get the correct result
        //Long correct = (long)this.statisticStore.getSet().size();
        Long correct = instanceID;

        if( correct == 0 ){
            return;
        }

        Long distance = Math.abs( correct - frequency );
        double x = distance / (correct * 1.0);
        if( this.reachCount == 0 ){
            this.sum = x;
            this.reachCount = 1L;
            System.out.println("["+instanceID+"] ("+frequency+"/"+correct+") @"+ distance+" ["+x * 100.0+" %]");
            //id,dis%
            this.sb.append(instanceID+","+x);
        }
        else if( x <= this.sum / (this.reachCount * 1.0 ) + 0.1 ){
            this.sum += x;
            this.reachCount ++;
            System.out.println("["+instanceID+"] ("+frequency+"/"+correct+") @"+ distance+" ["+x * 100.0+" %]");
            if( instanceID < 1000000 ){
                this.sb.append("\n"+instanceID + ","+x );
            }else{
                System.out.println("----1000000 instance----");
                try {
                    PrintWriter out = new PrintWriter("E://linearcounting.txt");
                    out.println(this.sb.toString());
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Utils.sleep(1000000);
            }
        }
    }
}
