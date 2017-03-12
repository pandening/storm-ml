package com.hujian.trident.ml.regression;

import com.hujian.trident.ml.help.IHelp;

import java.util.List;

/**
 * Created by hujian on 2017/3/1.
 * @link https://www.zybuluo.com/pastqing/note/269974
 * @link https://wiilzhang.gitbooks.io/ml-v4-2/di_3_zhou/liu_3001_luo_ji_hui_5f5228_logistic_regression__6_.html
 * @link http://www.cnblogs.com/jianxinzhou/p/4070149.html
 * @link http://tech.meituan.com/intro_to_logistic_regression.html
 */
public interface RegressOr extends IHelp{

    /**
     * predict the feature's value
     * @param features
     * @return
     */
    Double predict(double[] features);

    /**
     * multi predict
     * @param featuresList
     * @return
     */
    List<Double> multiPredict( List<double[]> featuresList);

    /**
     * update the regression model
     * @param expectValue
     * @param features
     */
    void update( Double expectValue, double[] features );

    /**
     * multi update
     * @param expectValueList
     * @param featuresList
     */
    void multiUpdate( List< Double > expectValueList, List<double[]> featuresList );

    /**
     * reset the regression model
     */
    void reset();
}
