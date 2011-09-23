package cheep.model;

import java.util.Random;

import org.uncommons.watchmaker.framework.CandidateFactory;

import cheep.eval.Validator;

public class ValidProductSetFactory extends ProductSetFactory {

	private final Validator<ProductSet> productSetValidator;
	
	public ValidProductSetFactory(CandidateFactory<Product> fac,
			int maxProducts, Validator<ProductSet> psetValidator) {
		super(fac, maxProducts);
		this.productSetValidator = psetValidator;
	}
	
	@Override
	public ProductSet generateRandomCandidate(Random rng) {

		ProductSet pset;
		
		do {
			pset = super.generateRandomCandidate(rng);
		} while (!productSetValidator.validate(pset));
		
		return pset;
	}

}
