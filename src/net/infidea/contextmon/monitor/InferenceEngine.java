package net.infidea.contextmon.monitor;

import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public class InferenceEngine {
	public static final int ATTR_SIZE = 10;
	public static final int DEM_SIZE = 3;
	
	private J48 classifier;
	private Instances instances;
	
	public InferenceEngine(Context context) {
		// TODO Auto-generated constructor stub
		
		try {
			classifier = new J48();
			ArffLoader loader = new ArffLoader();
			loader.setSource(context.getAssets().open("dataset/activity_recognition_labeled_simple.arff"));
			Instances dataset = loader.getDataSet();
			dataset.setClassIndex(dataset.numAttributes()-1);
			Log.d("SIT", "ATTR SIZE: "+dataset.numAttributes());
			classifier.buildClassifier(dataset);
			instances = loader.getStructure();
			instances.setClassIndex(instances.numAttributes()-1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String classify(double[] values) {
		if(values == null) return "";
		instances.delete();
		instances.add(new DenseInstance(1.0, values));
		return classify(instances);
	}
	
	public String classify(Instances instances) {
		try {
			Log.d("SituationInformer", "Classifying...");
			Instance inst = instances.firstInstance();
			double pred = classifier.classifyInstance(inst);
			String label = instances.classAttribute().value((int)pred);
			String prevLabel = instances.classAttribute().value((int)inst.classValue());
			Log.d("SituationInformer", "Label: "+pred+" "+label);
			Log.d("SituationInformer", "Label: "+prevLabel);
			return label;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public void close() {
	}
	
	public static double[] toLinearArray(ContentValues[] measurements, int size) {
		double[] result = new double[size*3+2];
		if(measurements.length < size) return null;
		String arffLogX = "";
		String arffLogY = "";
		String arffLogZ = "";
		for(int i=0; i<size; i++) {
			result[i+1] = (double)Math.round(measurements[i].getAsDouble("value0") * 100) / 100;
			result[size+i+1] = (double)Math.round(measurements[i].getAsDouble("value1") * 100) / 100;
			result[size*2+i+1] = (double)Math.round(measurements[i].getAsDouble("value2") * 100) / 100;
			arffLogX+=String.format("%.2f,", result[i+1]);
			arffLogY+=String.format("%.2f,", result[size+i+1]);
			arffLogZ+=String.format("%.2f,", result[size*2+i+1]);
		}
		Log.d("BASKET", arffLogX+arffLogY+arffLogZ);
		result[0] = 1;
		result[size*3+1] = 1;
		return result;
	}
}
