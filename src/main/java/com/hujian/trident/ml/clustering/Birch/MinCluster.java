package com.hujian.trident.ml.clustering.Birch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/2/25.
 */
public class MinCluster implements Serializable{

    private static final long serialVersionUID = 786534009087289064L;

    private CF cf;
    private List< String > instMarks;

    /**
     * the constructor
     * @param dimen
     */
    public MinCluster(Integer dimen){
        this.cf = new CF(dimen);
        this.instMarks = new ArrayList<String>();
    }

    /**
     * compute the CF's diameter
     * @param cf
     * @return
     */
    public static double getDiameter(CF cf){
        double diameter = 0.0;
        int n = cf.getN();

        for( int i = 0 ; i < cf.getLS().length; i ++ ){
            double ls = cf.getLS()[i];
            double ss = cf.getSS()[i];
            diameter += (2 * n * ss - 2 * ls * ss);
        }
        diameter /= ((n -1)*n);
        return Math.sqrt( diameter );
    }

    /**
     * compute the diameter after merge two min-cluster
     * @param minCluster_a
     * @param minCluster_b
     * @return
     */
    public static double getDiameter( MinCluster minCluster_a ,MinCluster minCluster_b){
        CF cf = new CF(minCluster_a.getCf());
        cf.addAnotherCF(minCluster_b.getCf(),true);
        return getDiameter(cf);
    }

    /**
     * merge two min-cluster
     * @param minCluster
     * @return
     */
    public MinCluster mergeCluster(MinCluster minCluster){
        this.cf.addAnotherCF(minCluster.getCf(),true);
        for( int i = 0; i <minCluster.getInstMarks().size(); i ++ ){
            this.instMarks.add(minCluster.getInstMarks().get( i ));
        }
        return this;
    }

    public CF getCf() {
        return cf;
    }

    public void setCf(CF cf) {
        this.cf = cf;
    }

    public List<String> getInstMarks() {
        return instMarks;
    }

    public void setInstMarks(List<String> instMarks) {
        this.instMarks = instMarks;
    }
}
