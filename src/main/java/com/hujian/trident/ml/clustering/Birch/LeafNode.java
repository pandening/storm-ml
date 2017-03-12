package com.hujian.trident.ml.clustering.Birch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/2/25.
 */
public class LeafNode extends TreeNode {

    private static final long serialVersionUID = - 786545340090871076L;

    private Integer L = 10;
    private double T = 2.8;
    private List<MinCluster> children;
    private LeafNode pre;
    private LeafNode next;
    private Integer dimen;

    public LeafNode(Integer dimen) {
        super(dimen);
        this.dimen = dimen;
        children = new ArrayList<MinCluster>();
    }

    /**
     * i think you should use this construct
     * @param dimen
     * @param t
     * @param l
     */
    public LeafNode(Integer dimen,Double t,Integer l){
        super(dimen);
        this.dimen = dimen;
        this.T = t;
        this.L = l;
        children = new ArrayList<MinCluster>();
    }

    public LeafNode( double[] features){
        super( features );
        children = new ArrayList<MinCluster>();
    }
    public LeafNode( double[] features,double t,Integer l ){
        super(features);
        this.T = t;
        this.L = l;
        children = new ArrayList<MinCluster>();
    }

    /**
     * add a child node for this node
     * @param minCluster
     */
    public void addChild( MinCluster minCluster ){
        this.children.add( minCluster );
    }

    /**
     * delete a child node from this node
     * @param minCluster
     */
    public void deleteChild(MinCluster minCluster ){
        this.children.remove( this.children.indexOf( minCluster ) );
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
                double distance = this.children.get( i ).getCf().
                        getDistanceTo(this.children.get( j ).getCf());
                if( distance > maxDistance ){
                    maxDistance = distance;
                    furthest_child_a = i ;
                    furthest_child_b = j;
                }
            }
        }

        LeafNode leafNode = new LeafNode(this.dimen);
        leafNode.addChild( this.children.get( furthest_child_b ) );
        if( this.getParentTreeNode() == null ){
            NonLeafNode root = new NonLeafNode(this.dimen);
            root.setN( this.getN() );
            root.setLS( this.getLS() );
            root.setSS( this.getSS() );
            this.setParentTreeNode( root );
            root.addChild( this );
        }

        leafNode.setParentTreeNode( this.getParentTreeNode() );
        ((NonLeafNode)this.getParentTreeNode()).addChild( leafNode );
        for( int i = 0; i < length ; i ++ ){
            if( i != furthest_child_a && i != furthest_child_b ){
                if( this.children.get( i ).getCf()
                        .getDistanceTo( this.children.get( furthest_child_b ).getCf() )<
                        this.children.get( i ).getCf()
                                .getDistanceTo(this.children.get( furthest_child_a ).getCf())){
                    leafNode.addChild( this.children.get( i ) );
                }
            }
        }
        for( MinCluster minCluster: leafNode.getChildren() ){
            leafNode.addAnotherCF( minCluster.getCf(),true );
            this.deleteChild( minCluster );
            this.addAnotherCF( minCluster.getCf(),false );
        }

        if( this.next != null ){
            leafNode.setNext( this.next );
            this.next.setPre( leafNode );
        }

        this.next = leafNode;
        leafNode.setPre( this );
        NonLeafNode pn = (NonLeafNode) this.getParentTreeNode();
        if( pn.getChildren().size() > pn.getB() ){
            this.getParentTreeNode().split();
        }
    }

    @Override
    void absorbDubCluster(MinCluster minCluster) {
        CF cf = minCluster.getCf();
        int nearestChildIndex = 0 ;
        double minDistance = Double.MAX_VALUE;
        int length = this.children.size();

        if( length > 0 ){
            for( int i = 0 ; i < length ; i ++ ){
                double distance = cf.getDistanceTo( this.children.get( i ).getCf() );
                if( distance < minDistance  ){
                    nearestChildIndex = i ;
                    minDistance = distance;
                }
            }
            double mergeDiameter = MinCluster.getDiameter(minCluster,
                    this.children.get( nearestChildIndex ));
            if( mergeDiameter > T ){
                this.addChild( minCluster );
                if( this.children.size() > L ){
                    this.split();
                }
            }else{
                this.children.get( nearestChildIndex ).mergeCluster( minCluster );
            }

        }else{/*no child till now*/
            this.addChild( minCluster );
        }
        this.addCFUpToRoot( minCluster.getCf() );
    }

    public Integer getL() {
        return L;
    }

    public void setL(Integer l) {
        L = l;
    }

    public double getT() {
        return T;
    }

    public void setT(double t) {
        T = t;
    }

    public List<MinCluster> getChildren() {
        return children;
    }

    public void setChildren(List<MinCluster> children) {
        this.children = children;
    }

    public LeafNode getPre() {
        return pre;
    }

    public void setPre(LeafNode pre) {
        this.pre = pre;
    }

    public LeafNode getNext() {
        return next;
    }

    public void setNext(LeafNode next) {
        this.next = next;
    }
}
