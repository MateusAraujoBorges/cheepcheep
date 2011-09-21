package cheep.eval;

import java.util.List;

import cheep.model.Product;

public class ProductValidator implements Validator<Product>{

	
	@Override
	public boolean validate(Product t) {
		boolean[] f = t.getFeatures();
		return f[0] && f[1] || (f[2] && f[3] && f[4]);
	}

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
