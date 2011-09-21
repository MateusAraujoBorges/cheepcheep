package cheep.model;

import java.util.Random;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/**
 * This factory generates random products to be used in
 * the evolutionary process.
 * @author mateus
 *
 */

public class ProductFactory extends AbstractCandidateFactory<Product>{

	private final int nFeatures; 
	
	public ProductFactory(int nFeatures) {
		this.nFeatures = nFeatures;
	}
	
	@Override
	public Product generateRandomCandidate(Random rng) {
		boolean[] featureList = new boolean[nFeatures];
		for(int i = 0; i < nFeatures; i++) {
			featureList[i] = rng.nextBoolean();
		}
		return new Product(featureList);
	}
}
