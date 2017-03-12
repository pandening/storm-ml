package com.hujian.trident.ml.clustering.Birch;

import java.io.Serializable;

/**
 * Created by hujian on 2017/2/25.
 */
public class BirchClusterIml implements BirchCluster ,Serializable {

    private static final long serialVersionUID = 17652453678298716L;

    /**
     * the cf tree root node.
     */
    private TreeNode CFTreeRoot = null;
    private Integer totalFeatures = 0;

    /**
     * the dimen
     */
    private Integer dimen;
    private Integer clusterCount = 0;

    /**
     * the constructor
     */
    public BirchClusterIml(Integer dimen){
        this.dimen = dimen;
        LeafNode leaf = new LeafNode(dimen);
        LeafNode leafNodeHead = new LeafNode(dimen);
        this.CFTreeRoot = leaf;
        leafNodeHead.setNext( leaf );
        leaf.setPre( leafNodeHead );
    }

    public BirchClusterIml( Integer dimen,Integer l, double t ){
        this.dimen = dimen;
        LeafNode leaf = new LeafNode(dimen,t,l);
        LeafNode leafNodeHead = new LeafNode(dimen,t,l);
        this.CFTreeRoot = leaf;
        leafNodeHead.setNext( leaf );
        leaf.setPre( leafNodeHead );
    }

    /**
     * print the CF-Tree with root node( root )
     * @param root
     */
    public void  printCfTree(TreeNode root){
        if( root instanceof NonLeafNode ){
            NonLeafNode nonLeafNode = (NonLeafNode)root;
            for( TreeNode treeNode: nonLeafNode.getChildren() ){
                printCfTree( treeNode );
            }
        }else if( root instanceof LeafNode ){/*leaf node*/
            LeafNode leafNode = (LeafNode) root;
            for( MinCluster cluster:leafNode.getChildren() ){
                System.out.println("\nA minCluster:");
                System.out.println("N= "+cluster.getCf().getN());
                for( String mark:cluster.getInstMarks() ){
                    System.out.println(mark);
                }
                System.out.print("\n");
            }
        }
    }

    @Override
    public TreeNode update(double[] features) {
        String mark = "[ ";
        for( double d:features ){
            mark += (d+" ");
        }
        mark += " ]";
        this.totalFeatures ++;
        CF cf = new CF(features);
        MinCluster minCluster = new MinCluster(dimen);
        minCluster.setCf( cf );
        minCluster.getInstMarks().add( mark );
        this.CFTreeRoot.absorbDubCluster( minCluster );
        while( this.CFTreeRoot.getParentTreeNode() != null ){
            this.CFTreeRoot = this.CFTreeRoot.getParentTreeNode();
        }


        /**
         *            DEBUG
         *   remove these code while this programming work in your project
         */
        System.out.println("call the update function with param:");
        for (double d : features) {
            System.out.print(d + "\t");
        }

        this.printCfTree(this.CFTreeRoot);

        return this.CFTreeRoot;
    }

    public TreeNode getCFTreeRoot() {
        return CFTreeRoot;
    }

    public void setCFTreeRoot(TreeNode CFTreeRoot) {
        this.CFTreeRoot = CFTreeRoot;
    }

    public Integer getTotalFeatures() {
        return totalFeatures;
    }

    public void setTotalFeatures(Integer totalFeatures) {
        this.totalFeatures = totalFeatures;
    }

    public Integer getDimen() {
        return dimen;
    }

    public void setDimen(Integer dimen) {
        this.dimen = dimen;
    }

    public Integer getClusterCount() {
        return clusterCount;
    }

    public void setClusterCount(Integer clusterCount) {
        this.clusterCount = clusterCount;
    }
}
