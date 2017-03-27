package com.hujian.hotmem.source;

import java.io.Serializable;

/**
 * Created by hujian on 2017/3/23.
 */
public class ComedyComparisonsInstance implements Serializable{

    private static final long serialVersionUID = 56435620011L;

    /**
     * comedyLeft,comedyRight,vote
     */
    private String comedyLeft = null;
    private String comedyRight = null;
    private String comedyVote = null;
    private Long instanceId = null;

    /**
     * the constructor
     * @param comedyLeft
     * @param comedyRight
     * @param comedyVote
     */
    public ComedyComparisonsInstance(String comedyLeft,String comedyRight,String comedyVote){
        this.comedyLeft = comedyLeft;
        this.comedyRight = comedyRight;
        this.comedyVote = comedyVote;
    }

    /**
     *
     * @param comedyLeft
     * @param comedyRight
     * @param comedyVote
     * @param id
     */
    public ComedyComparisonsInstance(String comedyLeft,String comedyRight,String comedyVote,Long id){
        this.comedyLeft = comedyLeft;
        this.comedyRight = comedyRight;
        this.comedyVote = comedyVote;
        this.instanceId = id;
    }

    public String getComedyLeft() {
        return comedyLeft;
    }

    public void setComedyLeft(String comedyLeft) {
        this.comedyLeft = comedyLeft;
    }

    public String getComedyRight() {
        return comedyRight;
    }

    public void setComedyRight(String comedyRight) {
        this.comedyRight = comedyRight;
    }

    public String getComedyVote() {
        return comedyVote;
    }

    public void setComedyVote(String comedyVote) {
        this.comedyVote = comedyVote;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public String toString(){
        return "";
    }
}
