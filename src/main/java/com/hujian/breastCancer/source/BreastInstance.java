package com.hujian.breastCancer.source;

import java.io.Serializable;

/**
 * Created by hujian on 2017/3/27.
 */
public class BreastInstance implements Serializable {

    private Integer cancerId = null;
    private Double clumpThickness = null;
    private Double cellSize = null;
    private Double cellShape = null;
    private Double Ma = null;
    private Double SeCz = null;
    private Double bareNuclei = null;
    private Double blandC = null;
    private Double normalNucleoli = null;
    private Double mitoses = null;
    private Integer classification = null;

    private double[] features = null;


    /**
     *
     * @param cancerId
     * @param clumpThickness
     * @param cellSize
     * @param cellShape
     * @param ma
     * @param seCz
     * @param bareNuclei
     * @param blandC
     * @param normalNucleoli
     * @param mitoses
     * @param classification
     */
    public BreastInstance(int cancerId ,double clumpThickness,double cellSize,
                          double cellShape,double ma,double seCz,double bareNuclei,double blandC,
                          double normalNucleoli,double mitoses,int classification){
        this.cancerId = cancerId;
        this.clumpThickness =clumpThickness;
        this.cellSize = cellSize;
        this.cellShape =cellShape;
        this.Ma = ma;
        this.SeCz = seCz;
        this.bareNuclei = bareNuclei;
        this.blandC = blandC;
        this.normalNucleoli = normalNucleoli;
        this.mitoses = mitoses;
        this.classification = classification;
    }

    /**
     * sample constructor
     * @param features
     * @param classification
     */
    public BreastInstance(double[] features,Integer classification){
        if( features.length != 9 ){
            return;
        }

        this.features = features;
        this.classification = classification;
    }

    public Integer getCancerId() {
        return cancerId;
    }

    public void setCancerId(Integer cancerId) {
        this.cancerId = cancerId;
    }

    public Double getClumpThickness() {
        return clumpThickness;
    }

    public void setClumpThickness(Double clumpThickness) {
        this.clumpThickness = clumpThickness;
    }

    public Double getCellSize() {
        return cellSize;
    }

    public void setCellSize(Double cellSize) {
        this.cellSize = cellSize;
    }

    public Double getCellShape() {
        return cellShape;
    }

    public void setCellShape(Double cellShape) {
        this.cellShape = cellShape;
    }

    public Double getMa() {
        return Ma;
    }

    public void setMa(Double ma) {
        Ma = ma;
    }

    public Double getSeCz() {
        return SeCz;
    }

    public void setSeCz(Double seCz) {
        SeCz = seCz;
    }

    public Double getBareNuclei() {
        return bareNuclei;
    }

    public void setBareNuclei(Double bareNuclei) {
        this.bareNuclei = bareNuclei;
    }

    public Double getNormalNucleoli() {
        return normalNucleoli;
    }

    public void setNormalNucleoli(Double normalNucleoli) {
        this.normalNucleoli = normalNucleoli;
    }

    public Double getMitoses() {
        return mitoses;
    }

    public void setMitoses(Double mitoses) {
        this.mitoses = mitoses;
    }

    public Integer getClassification() {
        return classification;
    }

    public void setClassification(Integer classification) {
        this.classification = classification;
    }

    public Double getBlandC() {
        return blandC;
    }

    public void setBlandC(Double blandC) {
        this.blandC = blandC;
    }

    public double[] getFeatures() {
        return features;
    }

    public void setFeatures(double[] features) {
        this.features = features;
    }
}
