package operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;

import cheep.eval.Validator;
import cheep.model.Product;
import cheep.model.ProductSet;

public class ValidProductSetMutation extends ProductSetMutation {

	private final Validator<ProductSet> productSetValidator;
	private final boolean lazyMode; //try mutating once; if the result is invalid return the original candidate 
	
	public ValidProductSetMutation(Probability pmc, Probability fmc, Probability smc,
			CandidateFactory<Product> pfac, Validator<ProductSet> psetValidator, boolean lazyMode) {
		
		super(pmc, fmc, smc, pfac);
		
		this.productSetValidator = psetValidator;
		this.lazyMode = lazyMode;
	}
	
	/**
	 * slow if lazyMode = true - best used with steady-state GA!
	 */
	
	@Override
	public List<ProductSet> apply(List<ProductSet> selectedCandidates, Random rng) {
		
		List<ProductSet> evolved = new ArrayList<ProductSet>(selectedCandidates.size());
		List<ProductSet> tmpList = new ArrayList<ProductSet>(1);
		tmpList.add(null);
		
		for(ProductSet p : selectedCandidates) {
			if(lazyMode) {
				tmpList.set(0, p);
				List<ProductSet> mutated = super.apply(tmpList, rng);
				ProductSet mutatedPset = mutated.get(0);
				if(productSetValidator.validate(mutatedPset)) {
					evolved.add(mutatedPset);
				} else {
					evolved.add(p); 
				}
			} else {
				ProductSet mutatedPset = p;
				
				do {
					tmpList.set(0, p);
					List<ProductSet> mutated = super.apply(tmpList, rng);
					mutatedPset = mutated.get(0);
				} while (!productSetValidator.validate(mutatedPset));
				
				evolved.add(mutatedPset);
			}
		}
		return evolved;
	}
}
