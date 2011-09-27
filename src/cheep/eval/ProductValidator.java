package cheep.eval;

import java.util.List;

import cheep.model.Product;

public abstract class ProductValidator implements Validator<Product>{

	
	abstract public boolean validate(Product t);

	abstract public boolean[] xorCombination(Product t);
	
	@Override
	public double getFitness(Product candidate,
			List<? extends Product> population) {
		if(validate(candidate))		
			return 1;
		else
			return 0;
	}

	@Override
	public boolean isNatural() {
		return true;
	}
	
	
}
