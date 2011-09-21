package cheep.model;

import java.util.Random;

import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/**
 * This class produces random ProductSets for use in 
 * the evolutionary process
 * @author mateus
 *
 */

public class ProductSetFactory extends AbstractCandidateFactory<ProductSet> {

	private final CandidateFactory<Product> productFactory;
	private final int maxProducts;
	
	/**
	 * Creates a ProductSetFactory.
	 * @param fac Factory which will be used to generate the products
	 * @param maxProducts Max number of products inside a ProductSet
	 */
	
	public ProductSetFactory(CandidateFactory<Product> fac, int maxProducts) {
		this.productFactory = fac;
		this.maxProducts = maxProducts;
	}
	
	@Override
	public ProductSet generateRandomCandidate(Random rng) {
		int nProducts = rng.nextInt(maxProducts) + 1;
		Product[] products = new Product[nProducts];
		
		for (int i = 0; i < nProducts; i++) {
			products[i] = productFactory.generateRandomCandidate(rng);
		}
		
		return new ProductSet(products);
	}
}
