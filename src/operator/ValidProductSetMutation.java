package operator;

import java.util.List;
import java.util.Random;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;

import cheep.eval.Validator;
import cheep.model.Product;
import cheep.model.ProductSet;

public class ValidProductSetMutation extends ProductSetMutation {

	private final Validator<ProductSet> productSetValidator;
	
	public ValidProductSetMutation(Probability pmc, Probability fmc, Probability smc,
			CandidateFactory<Product> pfac, Validator<ProductSet> psetValidator) {
		
		super(pmc, fmc, smc, pfac);
		
		this.productSetValidator = psetValidator;
	}
	
	/**
	 * slow - best used with steady-state GA!
	 */
	
	@Override
	public List<ProductSet> apply(List<ProductSet> selectedCandidates, Random rng) {
		
		List<ProductSet> evolved;
		
		do {
			evolved = super.apply(selectedCandidates, rng);
		} while (!checkSetList(evolved));
		
		return evolved;
	}
	
	private boolean checkSetList(List<ProductSet> selected) {
		boolean isValid = true;
		
		for(ProductSet pset : selected) {
			isValid &= productSetValidator.validate(pset);
		}
		
		return isValid;
	}

}
