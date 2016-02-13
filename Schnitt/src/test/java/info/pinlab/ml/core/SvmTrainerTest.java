package info.pinlab.ml.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class SvmTrainerTest {


	@Test
	public void testSvmTrainer() throws Exception {
		SvmTrainer trainer = new SvmTrainer();
		
		Accumulator<Boolean, FeatureVector> acc1 = 
				new Accumulator<Boolean, FeatureVector>(Boolean.FALSE, 2);
		Accumulator<Boolean, FeatureVector> acc2 = 
				new Accumulator<Boolean, FeatureVector>(Boolean.TRUE, 2);
		
		int n = 100;
		Random rand = new Random(System.currentTimeMillis());
		
		for(int i = 0;i < n ; i++){
			double x1 = 1+rand.nextDouble();
			double x2 = 1+rand.nextDouble();
			acc1.add(new FeatureVector(i, new double[]{x1, x2}));
			
			double y1 = rand.nextDouble();
			double y2 = rand.nextDouble();
			acc2.add(new FeatureVector(n+i, new double[]{y1, y2}));
		}
		
		trainer.setTrainData(acc1, acc2);
		trainer.train();
		
		
		FeatureVector testTrue = new FeatureVector(2*n+1, new double[]{0.8f, 1.2f});
		FeatureVector testFalse = new FeatureVector(2*n+1, new double[]{0.1f, -0.1f});
		
		System.out.println(
				trainer.getModel().predictValue( trainer.featVec2SparseVec(testTrue))
				);
		System.out.println(trainer.predictLabel(testTrue));
		
		System.out.println(
				trainer.getModel().predictLabel(trainer.featVec2SparseVec(testTrue))
		);
		assertTrue(trainer.predictLabel(testTrue));
		assertFalse(trainer.predictLabel(testFalse));
	}
}
