package com.hujian.trident.ml.clustering.Birch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/2/25.
 */
public class NonLeafNode extends  TreeNode{

    private static final long serialVersionUID = -87652453678298736L;

    /**
     * the node's child
     */
    private List<TreeNode> children;
    /**
     * B-Tree's level
     */
    private Integer B = 5;
    private Integer dimen;

    public NonLeafNode(Integer dimen){
        super(dimen);
        this.dimen = dimen;
        this.children = new ArrayList<TreeNode>();
    }

    public NonLeafNode( double[] feature){
        super(feature);
    }

    /**
     * add a child node
     * @param treeNode
     */
    public void addChild(TreeNode treeNode){
        this.children.add( treeNode );
    }

    /**
     * delete a child from this non-leaf node.
     * @param treeNode
     */
    public void deleteChild( TreeNode treeNode ){
        this.children.remove( children.indexOf( treeNode ) );
    }

    @Override
    void split() {
        //find the two furthest children nodes
        int furthest_child_a = 0 ;
        int furthest_child_b = 0 ;
        double maxDistance = Double.MIN_VALUE;

        int length = this.getChildren().size();

        for( int i = 0; i < length -1; i ++ ){
            for( int j = i + 1; j <length ; j ++ ){
                double distance = this.children.get( i )
                        .getDistanceTo(this.children.get( j ));
                if( distance > maxDistance ){
                    maxDistance = distance;
                    furthest_child_a = i ;
                    furthest_child_b = j;
                }
            }
        }

        NonLeafNode nonLeafNode = new NonLeafNode(this.dimen);
        nonLeafNode.addChild( this.children.get( furthest_child_b ) );

        /**
         * if this is the root node.then we need create an new
         * root node for this b tree
         */
        if( this.getParentTreeNode() == null ){
            NonLeafNode root = new NonLeafNode(this.dimen);
            root.setN(this.getN());
            root.setLS(this.getLS());
            root.setSS(this.getSS());
            root.addChild( this );
            this.setParentTreeNode( root );
        }
        nonLeafNode.setParentTreeNode( this.getParentTreeNode() );
        ((NonLeafNode)nonLeafNode.getParentTreeNode()).addChild(nonLeafNode);
        for( int i = 0; i < length; i ++ ){
            if( i != furthest_child_a && i != furthest_child_b ){
                if( this.children.get( i )
                        .getDistanceTo( this.children.get( furthest_child_b ) ) <
                        this.children.get( i ).getDistanceTo( this.children.get( furthest_child_a ) )){
                    nonLeafNode.addChild(this.children.get( i ));
                }
            }
        }
        for( TreeNode treeNode : nonLeafNode.getChildren() ){
            nonLeafNode.addAnotherCF(treeNode,true);
            this.deleteChild(treeNode);
            this.addAnotherCF(treeNode,false);
        }
        NonLeafNode pn =(NonLeafNode) this.getParentTreeNode();
        if( pn.getChildren().size() > B ){
            this.getParentTreeNode().split();
        }
    }

    @Override
    void absorbDubCluster(MinCluster minCluster) {
        //find the nearest child from the node's children
        CF cf = minCluster.getCf();
        int nearestChildIndex = 0;
        double minDistance = Double.MAX_VALUE;

        for( int i= 0; i <this.getChildren().size() ; i++ ){
            double distance = cf.getDistanceTo(this.getChildren().get( i ));
            if( distance < minDistance ){
                nearestChildIndex = i;
                minDistance = distance;
            }
        }
        this.getChildren().get( nearestChildIndex )
                .absorbDubCluster(minCluster);
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public Integer getB() {
        return B;
    }

    public void setB(Integer b) {
        B = b;
    }
}
