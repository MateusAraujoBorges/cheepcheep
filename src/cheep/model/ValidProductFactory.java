package cheep.model;

import java.util.Random;

import cheep.eval.Validator;

/**
 * This factory only generates valid products 
 * @author mateus
 */

public class ValidProductFactory extends ProductFactory {
	
	private final Validator<Product> productValidator;
	
	/**
	 * 
	 * @param nFeatures - number of features in a chromossome
	 * @param productValidator - validator which will be used to determine if a product is valid or not
	 */
	
	public ValidProductFactory(int nFeatures,Validator<Product> productValidator) {
		super(nFeatures);
		this.productValidator = productValidator;
	}

	@Override
	public Product generateRandomCandidate(Random rng) {
		Product p;
		
		do {
			p = super.generateRandomCandidate(rng);
		} while(!productValidator.validate(p));
		
		return p;
	}

}
