package cheep.eval;

import java.util.List;
import java.util.Set;

import cheep.model.Product;
import cheep.model.ProductSet;

public class ProductSetValidator implements Validator<ProductSet>{
	
	private final Validator<Product> productVal;

	public ProductSetValidator(Validator<Product> val) {
		this.productVal = val;
	}
	
	@Override
	public boolean validate(ProductSet ps) { //TODO check if product cover all features
		boolean isValid = true;
		
		for(Product p : ps.getProducts()) {
			isValid = productVal.validate(p);
			
			if(!isValid) {
				break;
			}
		}
		
		return isValid;
	}
	
	@Override
	public double getFitness(ProductSet candidate,
			List<? extends ProductSet> population) {
		Set<Product> products = candidate.getProducts();
		int setSize = products.size();
		double fitness = 0;

		for (Product p : products) {
			double nFeatures = p.getTotalNumberFeatures(); //TODO extract this
			double nDistFeatures = p.getDistinctFeatures();
			boolean isValid = productVal.validate(p);
			double partialFitness = nFeatures / nDistFeatures;
			
			if(isValid) {
				fitness += partialFitness;
			} else {
				fitness -= partialFitness;
			}
		}
		
		return fitness > 0 ? fitness / setSize : 0;
	}

	@Override
	public boolean isNatural() {
		return true;
	}
}
