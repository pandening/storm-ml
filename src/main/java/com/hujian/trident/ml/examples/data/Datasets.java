package com.hujian.trident.ml.examples.data;

import com.hujian.trident.ml.core.Instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hujian on 2017/2/24.
 */
public class Datasets {

    /**
     * generate test data for classify algorithm
     * @param size
     * @param featureSize
     * @param nbClasses
     * @return
     */
    public static List<Instance<Integer>> generateDataForClassification(int size, int featureSize, int nbClasses) {
        Random random = new Random();
        List<Instance<Integer>> samples = new ArrayList<Instance<Integer>>();
        for (int i = 0; i < size; i++) {
            Integer label = random.nextInt(nbClasses);
            double[] features = new double[featureSize];
            for (int j = 0; j < featureSize; j++) {
                features[j] = (j % (label + 1) == 0 ? 1.0 : -1.0) + random.nextDouble() - 0.5;
            }
            samples.add(new Instance<Integer>(features));
        }
        return samples;
    }
}
