package com.hujian.trident.ml.clustering.Birch;

import java.io.Serializable;

/**
 * Created by hujian on 2017/2/25.
 */
public abstract class TreeNode extends CF implements Serializable{

    private static final long serialVersionUID = -218976009802320810L;

    /**
     * the tree-node's parent node
     */
    private TreeNode parentTreeNode;

    public TreeNode(Integer dimen){
        super(dimen);
    }

    /**
     * add cf up to root
     * @param cf
     */
    public void addCFUpToRoot( CF cf ){
        TreeNode treeNode = this;
        while( treeNode != null ){
            treeNode.addAnotherCF(cf,true);
            treeNode = treeNode.getParentTreeNode();
        }
    }

    /**
     * tree node split-ing..
     */
    abstract void split();

    /**
     * absorb operator
     * @param minCluster
     */
    abstract  void absorbDubCluster( MinCluster minCluster );


    public TreeNode(double[] features) {
        super(features);
    }

    public TreeNode getParentTreeNode() {
        return parentTreeNode;
    }

    public void setParentTreeNode(TreeNode parentTreeNode) {
        this.parentTreeNode = parentTreeNode;
    }
}
