package com.hujian.trident.ml.clustering.Kmeans;

import com.hujian.trident.ml.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hujian on 2017/2/23.
 */
public class Kmeans implements Cluster  {

    private static final long serialVersionUID = 234356784354567898L;

    /**
     * how many features in each cluster.
     * index is the cluster id.the value is the counts
     */
    private List< Long > clusterFeatureCounts = null;

    /**
     * the cluster's centroids
     */
    private double [][] clusterCentroids;

    /**
     * how many cluster we need to cluster to.
     */
    private Integer nbCluster ;

    /**
     * for some reasons , we need some features as the init features.
     * we need these features to set up our cluster model.
     */
    private List<double[]> coldStartFeatures = new ArrayList<double[]>();

    /**
     * the only constructor,you should offer the cluster's count
     * @param nbCluster
     */
    public Kmeans( Integer nbCluster ){
        this.nbCluster = nbCluster;
    }

    /**
     * compute the euclidean distance from the features
     * and each centroids.find the nearest centroids's index.
     * @param features
     * @return
     */
    private Integer nearestCentroidsIndex(double[] features){
        /**
         * the nearest centroids index for this features
         */
        Integer nearestCentroidsIndex_ = 0;
        Double minDistance = Double.MAX_VALUE;
        double[] currentCentroids;
        Double currentDistance ;
        for( int i = 0;i < this.clusterCentroids.length; i ++ ){
            currentCentroids = this.clusterCentroids[i];
            if( currentCentroids != null ){
                currentDistance = MathUtils.euclideanDistance(currentCentroids, features);
                if( currentDistance < minDistance ){
                    minDistance = currentDistance;
                    nearestCentroidsIndex_ = i ;
                }
            }
        }
        return nearestCentroidsIndex_;
    }

    /**
     * call this function to test the cluster before
     * classify the features.if not.you should init the cluster
     * env.
     * @return
     */
    private boolean isReady(){

        return this.clusterCentroids != null &&
                this.clusterFeatureCounts != null;

    }

    /**
     * compute the dxs.
     * ref k-means ++ algorithm.
     * @return
     */
    private double[] computeDxs(){
        double[] dxs = new double[this.coldStartFeatures.size()];
        int sum = 0;
        double[] features;
        int nearestCentroidIndex;
        double[] nearestCentroid;
        for (int i = 0; i < this.coldStartFeatures.size(); i++) {
            features = this.coldStartFeatures.get(i);
            nearestCentroidIndex = this.nearestCentroidsIndex(features);
            nearestCentroid = this.clusterCentroids[nearestCentroidIndex];
            sum += Math.pow(MathUtils.euclideanDistance(features, nearestCentroid), 2);
            dxs[i] = sum;
        }
        return dxs;
    }

    /**
     * set up the cluster model.
     * just choose k centroids to set up by k-means ++ algorithm
     * ref:https://en.wikipedia.org/wiki/K-means%2B%2B
     * and ref in chinese:http://www.cnblogs.com/shelocks/archive/2012/12/20/2826787.html
     */
    private void setUpClusterModel(){
        //init the cluster features counts
        this.clusterFeatureCounts = new ArrayList<Long>(this.nbCluster);
        for( int i = 0 ; i < this.nbCluster ; i ++ ){
            this.clusterFeatureCounts.add( 0L );
        }
        //init the centroids
        this.clusterCentroids = new double[this.nbCluster][];
        Random random = new Random();

        /**
         * first of all,choose one centroids in an random way
         */
        double[] firstCentroids = this.coldStartFeatures
                .remove( Math.abs( random.nextInt( this.coldStartFeatures.size() ) ) );
        this.clusterCentroids[0] = firstCentroids;

        /**
         * the dxs array
         */
        double[] dxs;
        //choose other nbCluster-1 centroids from init data set
        for( int i = 0;i < this.nbCluster ; i ++ ){
            dxs = computeDxs();
            //choose one new centroids
            double[] features;
            double rdm = random.nextDouble()*dxs[ dxs.length - 1 ];
            for( int j = 0; j < dxs.length; j ++ ){
                if( dxs[ j ]  >= rdm){
                    features = this.coldStartFeatures.remove( j );
                    this.clusterCentroids[ i ] = features;
                    break;
                }
            }
        }
        //clear the cold-set-up set
        this.coldStartFeatures.clear();
    }

    /**
     * we need some data to set up our k-means cluster model.
     * @param features
     */
    private void readyToSetUpClusterModel(double[] features){
        this.coldStartFeatures.add( features );

        /**
         * if the data is full to our stander.just run set up program
         */
        if( this.coldStartFeatures.size() >= 10 * this.nbCluster ){
            this.setUpClusterModel();
        }
    }


    @Override
    public Integer classify(double[] features) {

        /**
         * this function very easy.just return the cluster
         * index of this given features.
         */
        if( !this.isReady() ){
            throw  new IllegalStateException("kmeans cluster model is not set-up yet");
        }

        /**
         * you should,-1 means error
         */
        if( features == null || features.length == 0){
            return -1;
        }

        /**
         * find the nearest centroids for this features and return.
         */
        return this.nearestCentroidsIndex( features );
    }

    @Override
    public List<Integer> multiClassify(List<double[]> features) {
        /**
         * this function just use a loop to run this.classify
         */
        if( features == null || features.size() == 0){
            return null;
        }
        List<Integer> multiCentroidsIndex = new ArrayList<Integer>();
        for (double[] each: features) {
            multiCentroidsIndex.add( this.classify( each ) );
        }
        return multiCentroidsIndex;
    }


    @Override
    public Integer update(double[] features) {
        /**
         * call this function when you need to train the cluster model.
         * this function not only return the cluster centroids index.but also
         * update the cluster model by the new features.after updating the
         * cluster model,the new cluster model will be stored.
         */
        if( !this.isReady() ){
            /**
             * set up the cluster model by k-means ++ algorithm
             */
            this.readyToSetUpClusterModel(features);
            return null;
        }
        if( features == null || features.length == 0){
            return -1;
        }
        Integer nearestCentroidsIndex_ = this.classify( features );

        /**
         *                     DEBUG
         * the follow codes using for test the k-means algorithm.
         * remove the codes while let it work in your project.
         * and you also can see the real-time cluster info by this printer
         */
        System.out.println("call function update->");
        for(double d:features){
            System.out.print(d+"\t");
        }
        System.out.println("classify result->"+nearestCentroidsIndex_);
        System.out.println("centroids list");
        int index = 0;
        for( double[] c:this.clusterCentroids ){
            System.out.println("cluster - " + index ++);
            for( double d : c ){
                System.out.print(d+" \t");
            }
            System.out.print("\n");
        }
        System.out.println("centroids count");
        for( int i = 0 ;i < this.nbCluster ; i ++ ){
            System.out.println("cluster - " + i + " => "+this.clusterFeatureCounts.get( i ));
        }


        /**
         * update the cluster model by using this new feature
         */
        this.clusterFeatureCounts.set(nearestCentroidsIndex_,
                this.clusterFeatureCounts.get( nearestCentroidsIndex_) +1);
        /**
         * the new update's features
         */
        double[] updateFeatures = MathUtils.multi(MathUtils.subtract(features,
                this.clusterCentroids[ nearestCentroidsIndex_ ]),
                1.0 / this.clusterFeatureCounts.get(nearestCentroidsIndex_));

        this.clusterCentroids[ nearestCentroidsIndex_ ] = MathUtils.add(
                this.clusterCentroids[nearestCentroidsIndex_],updateFeatures
        );
        return nearestCentroidsIndex_;
    }

    @Override
    public List<Integer> multiUpdate(List<double[]> features) {
        /**
         * this function just use a loop to run this.update
         */
        if( features == null || features.size() == 0 ){
            return null;
        }
        List<Integer> multiNearestCentroidsIndex = new ArrayList<Integer>();
        for (double[] each: features) {
            multiNearestCentroidsIndex.add(this.update( each ));
        }
        return multiNearestCentroidsIndex;
    }

    @Override
    public double[][] getCentroids() {
        return this.clusterCentroids;
    }

    @Override
    public void reset() {
        this.clusterCentroids = null;
        this.clusterFeatureCounts = null;
        this.coldStartFeatures = new ArrayList<double[]>();
    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public String help(String function) {
        return null;
    }

    @Override
    public String help(Object type, String var) {
        return null;
    }
}
