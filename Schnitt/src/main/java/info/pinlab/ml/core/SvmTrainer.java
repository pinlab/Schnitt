package info.pinlab.ml.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterPoint;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationProblem;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationProblemImpl;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationSVM;
import edu.berkeley.compbio.jlibsvm.binary.BinaryModel;
import edu.berkeley.compbio.jlibsvm.binary.C_SVC;
import edu.berkeley.compbio.jlibsvm.kernel.GaussianRBFKernel;
import edu.berkeley.compbio.jlibsvm.kernel.SigmoidKernel;
import edu.berkeley.compbio.jlibsvm.util.SparseVector;

/**
 * Adatpter to jLibSVM 
 * 
 * @author Gabor Pinter
 *
 */
public class SvmTrainer implements BinaryClassifier{

	private BinaryModel<Boolean, SparseVector> model;
	private BinaryClassificationProblem<Boolean, SparseVector> problem = null;
	private BinaryClassificationSVM<Boolean, SparseVector> svm;
	private ImmutableSvmParameterPoint<Boolean, SparseVector> params;
	private final ImmutableSvmParameterPoint.Builder<Boolean, SparseVector> paramBuilder;
	
	public SvmTrainer(){
		GaussianRBFKernel kernel = new GaussianRBFKernel(0);
		
		paramBuilder = new ImmutableSvmParameterPoint.Builder<Boolean, SparseVector>();
		paramBuilder.eps = 0.01f;
		paramBuilder.C = 5;
		paramBuilder.kernel = new SigmoidKernel(0.4f, 1.0f); 
		params = paramBuilder.build();
		
		
		svm = new C_SVC<Boolean, SparseVector>();
		svm.validateParam(params);
	}

	
	private int vecDim = -1;
	private int[] vecIxs = new int[0];
	
	
	public SparseVector featVec2SparseVec(FeatureVector vec){
		SparseVector sparse = new SparseVector(vecDim);
		sparse.indexes = vecIxs;
		sparse.values = new float[vecDim];
		for(int i = 0; i< vecDim;i++){
			sparse.values[i] = (float)vec.feats[i];
		}
		return sparse;
	}
	
	
	
	@Override
	public void setTrainData(Accumulator<Boolean, FeatureVector> acc1, Accumulator<Boolean, FeatureVector> acc2){
		if(acc1==null || acc2==null){
			throw new IllegalArgumentException("Arguments can't be null!");
		}
		Boolean label1 = acc1.getLabel();
		Boolean label2 = acc2.getLabel();
		if(label1.equals(label2)){
			throw new IllegalArgumentException("Different accumulator labels are expected!");
		}
		Map<SparseVector, Boolean> examples = new HashMap<SparseVector, Boolean>();
		Map<SparseVector, Integer> ids = new HashMap<SparseVector, Integer>();

		vecDim = acc1.getDim();
		vecIxs = new int [vecDim];
		for(int i = 0 ; i< vecDim ;i++)
			vecIxs[i] = i;
		
		for(FeatureVector vec : acc1){
			SparseVector sparse = featVec2SparseVec(vec);
			examples.put(sparse, label1);
			ids.put(sparse, vec.id);
		}
		for(FeatureVector vec : acc2){
			SparseVector sparse = featVec2SparseVec(vec);
			examples.put(sparse, label2);
			ids.put(sparse, vec.id);
		}
		
		model = null; //-- new data requires new model!
		problem = new BinaryClassificationProblemImpl<Boolean, SparseVector>(Boolean.class, examples, ids);
		System.out.println("Counts: " + problem.getExampleCounts() + " vs " + examples.size());
		
	}
	
	synchronized public void train(){
		model = svm.train(problem, params);
		System.out.println(model.getTrueLabel());
		System.out.println(model.getFalseLabel());
		
	}
	
	@Override
	synchronized public List<Boolean> predictLabel(List<FeatureVector> data) throws IllegalStateException {
		if(model==null)
			throw new IllegalStateException("First #train() before predict!");
		
		List<Boolean> labels = new ArrayList<Boolean>(data.size());
		for(FeatureVector vec : data){
			Boolean lab = model.predictLabel(featVec2SparseVec(vec));
			labels.add(lab);
//			if ("true".equals(lab.toLowerCase())){
//				labels.add(true);
//			}else{
//				labels.add(false);
//			}
		}
		return labels;
	}
	
	synchronized public Boolean predictLabel(FeatureVector vec) throws IllegalStateException {
		return model.predictLabel(featVec2SparseVec(vec));
	}

	BinaryModel<Boolean, SparseVector> getModel(){
		return model;
	}
	
	public static void main(Boolean[] args) {
		SvmTrainer trainer = new SvmTrainer();
//		trainer.setTrainData(acc1, acc2);
	}





}

