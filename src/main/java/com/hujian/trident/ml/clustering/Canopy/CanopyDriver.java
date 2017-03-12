package com.hujian.trident.ml.clustering.Canopy;

import com.hujian.trident.ml.utils.MathUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/2/24.
 */
public class CanopyDriver implements Canopy,Serializable {

    private static final long serialVersionUID = -213645214534251498L;

    /**
     * the cluster's features count.
     */
    private List<Long> clusterFeatureCounts = new ArrayList<Long>();

    /**
     * the cluster's centroids now
     */
    private List<double[]> clusterCentroids = new ArrayList<double[]>();

    /**
     * the t's value.
     */
    private double magicKValue = 0.0;

    /**
     * the distance threshold
     */
    private double T;
    /**
     * true means you offer a certain t value.
     */
    private boolean customTValue = false;

    public CanopyDriver(){}

    /**
     * what?you given me a t value?
     * @param t
     */
    public CanopyDriver( double t ){
        this.T = t;
        this.customTValue = true;
    }

    /**
     * compute the dis to each centroids,get the min cluster's index( from 0 ..)
     * @param features
     * @return
     */
    private Integer nearestClusterCentroidsIndex(double[] features){
        /**
         * the nearest centroids index for this features
         */
        Integer nearestCentroidsIndex_ = 0;
        if( this.clusterCentroids.size() == 0 ){
            return nearestCentroidsIndex_;
        }
        Double minDistance = Double.MAX_VALUE;
        double[] currentCentroids;
        Double currentDistance ;
        for( int i = 0;i < this.clusterCentroids.size(); i ++ ){
            currentCentroids = this.clusterCentroids.get(i);
            if( currentCentroids != null ){
                currentDistance = MathUtils.euclideanDistance(currentCentroids, features);
                if( currentDistance < minDistance ){
                    minDistance = currentDistance;
                    nearestCentroidsIndex_ = i ;
                }
            }
        }
        /**
         * judge if this features is covered by any canopy.
         */
        if( minDistance <= this.getMagicKValue() ){
            return nearestCentroidsIndex_;
        }else{
            /**
             * near cluster
             */
            return this.clusterFeatureCounts.size();
        }
    }

    @Override
    public Integer clusterCount() {
        return clusterCentroids.size();
    }

    @Override
    public Integer addToCanopies(double[] features) {
        if( clusterCentroids.size() == 0 ){
            /**
             * this is the first features,set the features as the 0-index's centroids
             */
            clusterCentroids.add(0,features);
            clusterFeatureCounts.add(0,1L);
            return 0;
        }
        Integer nearestCentroidsIndex_ = this.nearestClusterCentroidsIndex(features);

        /**
         *                    DEBUG
         * the follow code just for testing the canopy model.
         * remove these codes while the library work in your project
         */
        System.out.println("call the function addToCanopies=>");
        for( double d:features ){
            System.out.print(d+"\t");
        }
        System.out.println("\n canopy result -> of cluster " + nearestCentroidsIndex_);
        System.out.println("the magic t value:"+this.getMagicKValue() +
                "\t the canopies count:" +this.clusterFeatureCounts.size());
        System.out.println("canopies info");
        for( int i = 0; i < this.clusterCentroids.size();i ++ ){
            System.out.print("[centroids] canopy "+ i +":");
            for( double d:this.clusterCentroids.get(i) ){
                System.out.print(d+"\t");
            }
            System.out.print("\n [ features count] canopy "+i+" :" +this.clusterFeatureCounts.get( i ) + "\n");
        }

        /**
         * this is a new canopy.
         */
        if( nearestCentroidsIndex_  == this.clusterFeatureCounts.size() ){
            this.clusterFeatureCounts.add(nearestCentroidsIndex_,1L);
            this.clusterCentroids.add(nearestCentroidsIndex_,features);
            return nearestCentroidsIndex_;
        }
        //update the cluster centroids' features count
        this.clusterFeatureCounts.set(nearestCentroidsIndex_,
                this.clusterFeatureCounts.get(nearestCentroidsIndex_)+1);
        /**
         * update the cluster model
         */
        double[] updateFeatures = MathUtils.multi(MathUtils.subtract(features,
                this.clusterCentroids.get( nearestCentroidsIndex_ )),
                1.0 / this.clusterFeatureCounts.get(nearestCentroidsIndex_));

        this.clusterCentroids.set(nearestCentroidsIndex_ ,MathUtils.add(
                this.clusterCentroids.get(nearestCentroidsIndex_),updateFeatures
        ));

        return nearestCentroidsIndex_;
    }

    @Override
    public List<Integer> multiAddToCanopies(List<double[]> featuresList) {
        //this function just using a loop to run this.addToCanopies
        if( featuresList == null || featuresList.size() == 0 ){
            return null;
        }

        List<Integer> nearestCentroidsIndexList = new ArrayList<Integer>();

        for( double[] features: featuresList ){
            nearestCentroidsIndexList.add(this.addToCanopies(features));
        }

        return nearestCentroidsIndexList;
    }

    public double getT() {
        return T;
    }

    public void setT(double t) {
        T = t;
    }

    public List<Long> getClusterFeatureCounts() {
        return clusterFeatureCounts;
    }

    public void setClusterFeatureCounts(List<Long> clusterFeatureCounts) {
        this.clusterFeatureCounts = clusterFeatureCounts;
    }

    public List<double[]> getClusterCentroids() {
        return clusterCentroids;
    }

    public void setClusterCentroids(List<double[]> clusterCentroids) {
        this.clusterCentroids = clusterCentroids;
    }

    public double getMagicKValue() {
        if( this.customTValue == true ){
            return this.T;
        }
        if( this.clusterCentroids.size() == 0 ){
            return Double.MAX_VALUE;
        }
        double aveDistance = 0;
        double[] distance = new double[this.clusterCentroids.size() * this.clusterCentroids.size()];
        for( int i = 0; i < this.clusterCentroids.size() ; i ++ ){
            distance[i]  = 0;
        }
        int index = 0;
        for( int i = 0;i < this.clusterCentroids.size(); i ++ ){
            for( int j = 0; j < this.clusterCentroids.size(); j ++ ){
                if( i != j ){
                    distance[index++] = MathUtils.euclideanDistance(this.clusterCentroids.get(i),
                            this.clusterCentroids.get(j)) / 2;
                }
            }
        }
        for( int i = 0;i < this.clusterCentroids.size(); i ++ ){
            aveDistance += distance [i];
        }
        return aveDistance / this.clusterCentroids.size();
    }

    public void setMagicKValue(double magicKValue) {
        this.magicKValue = magicKValue;
    }

    public boolean isCustomTValue() {
        return customTValue;
    }

    public void setCustomTValue(boolean customTValue) {
        this.customTValue = customTValue;
    }
}
