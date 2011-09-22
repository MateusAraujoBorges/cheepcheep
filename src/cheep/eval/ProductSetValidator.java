package cheep.eval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import cheep.model.Product;
import cheep.model.ProductSet;

public class ProductSetValidator implements Validator<ProductSet>{
	
	private final Validator<Product> productVal;
	private final double invalidFeatureDiscount;
	
	public ProductSetValidator(Validator<Product> val,double invalidFeatureDiscount) {
		this.productVal = val;
		this.invalidFeatureDiscount = invalidFeatureDiscount;
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
		List<boolean[]> allFeatureArrays = new ArrayList<boolean[]>(); 
		double fitness = 0;
		double totalNumberFeatures = candidate.getProductSize();

		for (Product p : products) { 
			allFeatureArrays.add(p.getFeatures());
			double nDistFeatures = p.getDistinctFeatures();
			boolean isValid = productVal.validate(p);
			double partialFitness = nDistFeatures / totalNumberFeatures;
			
			if(isValid) {
				fitness += partialFitness;
			} else {
				fitness += (partialFitness * (1-invalidFeatureDiscount));
			}
		}
		
		//less distinct features, less score.
		double distinctFeatures = nDistinctFeatures(allFeatureArrays);
		fitness = fitness * (distinctFeatures / totalNumberFeatures);		
		
		return fitness > 0 ? fitness / setSize : 0;
	}

	@Override
	public boolean isNatural() {
		return true;
	}
	
	//PRECONDITION - matrix.size > 0 
	private static int nDistinctFeatures(List<boolean[]> matrix) {
		int lineSize = matrix.get(0).length;
		boolean[] distinct = new boolean[lineSize];
		Arrays.fill(distinct, true);
		
		for(boolean[] line : matrix) {
			for(int i = 0; i < lineSize; i++) {
				distinct[i] &= line[i]; 
			}
		}
		
		int total = 0;
		for(boolean b : distinct) {
			if(b) {
				total++;
			}
		}
		
		return total;
	}
}
