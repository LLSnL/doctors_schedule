package org.example;

import javafx.util.Pair;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SimpleLinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import javax.management.AttributeList;
import java.util.ArrayList;
import java.util.List;

public class PredictAlgorithm {

    public static ArrayList<Classifier> linearRegression(Pair<List<String>, List<List<Integer>>> instances) throws Exception {
        ArrayList<Classifier> targetFunctions = new ArrayList<>();
        for (int k = 0; k < instances.getKey().size() - 2; k++) {
            ArrayList<Attribute> attributes = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                Attribute a = new Attribute(instances.getKey().get(i));
                attributes.add(a);
            }
            Attribute a = new Attribute(instances.getKey().get(k + 2));
            attributes.add(a);

            Instances trainingDataset = new Instances("train data", attributes, 3 );
            trainingDataset.setClassIndex(trainingDataset.numAttributes() - 1);

            for (int i = 0; i < instances.getValue().size(); i++) {
                Instance instance = new DenseInstance(3);
                for (int j = 0; j < 2; j++) {
                    instance.setValue(j, instances.getValue().get(i).get(j));
                }
                instance.setValue(2, instances.getValue().get(i).get(k + 2));
                trainingDataset.add(instance);
            }
            Classifier targetFunction = new LinearRegression();
            targetFunction.buildClassifier(trainingDataset);

            /* Предикт
            Instances unlabeledInstances = new Instances("prediction set", attributes, 3);
            unlabeledInstances.setClassIndex(trainingDataset.numAttributes() - 1);
            Instance unlabeled = new DenseInstance(3);

            unlabeled.setValue(0, year);
            unlabeled.setValue(1, week);
            unlabeledInstances.add(unlabeled);
            double predict = targetFunction.classifyInstance(unlabeledInstances.get(0));
             */

            targetFunctions.add(targetFunction);
        }
        return targetFunctions;
    }
}
