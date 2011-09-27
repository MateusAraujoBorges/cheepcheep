package cheep.eval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import cheep.model.Product;
import cheep.model.ProductSet;

public class ProductSetValidator implements Validator<ProductSet>{
	
	private final ProductValidator productVal;
	private final double invalidFeatureDiscount;
	private final double incompleteFeatureSetDiscount;
	
	public ProductSetValidator(ProductValidator val,double invalidFeatureDiscount, double incompleteFeatureSetDiscount) {
		this.productVal = val;
		this.invalidFeatureDiscount = invalidFeatureDiscount;
		this.incompleteFeatureSetDiscount = incompleteFeatureSetDiscount;
	}
	
	@Override
	public boolean validate(ProductSet ps) { 
		boolean isValid = true;
		
		for(Product p : ps.getProducts()) {
			isValid = productVal.validate(p);
			
			if(!isValid) {
				break;
			}
		}
		
		if(isValid) {
			isValid = checkAllFeatures(ps);
		}
		
		return isValid;
	}
	
	@Override
	public double getFitness(ProductSet candidate,
			List<? extends ProductSet> population) {
		Set<Product> products = candidate.getProducts();
		int setSize = products.size();
		List<boolean[]> allFeatureArrays = new ArrayList<boolean[]>(); 
		List<boolean[]> allXorCombinations = new ArrayList<boolean[]>();
		
		double fitness = 0;
		double totalNumberFeatures = candidate.getProductSize();

		for (Product p : products) { 
			allFeatureArrays.add(p.getFeatures());
			double nDistFeatures = p.getDistinctFeatures();
			boolean isValid = productVal.validate(p);
			double partialFitness = nDistFeatures / totalNumberFeatures;
			
			if(isValid) {
				fitness += partialFitness;
				addIfNotPresent(allXorCombinations,productVal.xorCombination(p));
//				fitness += 1;
			} else {
//				fitness += 0;
				fitness += (partialFitness * (1-invalidFeatureDiscount));
			}
		}

		//less distinct features -> smaller score.
		double distinctFeatures = nDistinctFeatures(allFeatureArrays);
		if(distinctFeatures < totalNumberFeatures) {
			fitness = fitness * (1- incompleteFeatureSetDiscount);		
		}
		
		if(fitness > 0) {
			fitness = fitness * (allXorCombinations.size() / setSize);
			fitness = fitness / setSize;
//			fitness += (1.0 / setSize);
		} else {
			fitness = 0;
		}
		
		return fitness;
	}

	private void addIfNotPresent(List<boolean[]> allXorCombinations,
			boolean[] xorCombination) {

		boolean eq = false;
		for(boolean[] brr: allXorCombinations) {
			eq = Arrays.equals(xorCombination, brr);
			
			if(eq) {
				break;
			}
		}
		
		if(!eq) {
			allXorCombinations.add(xorCombination);
		}
	}

	@Override
	public boolean isNatural() {
		return true;
	}
	
	private static boolean checkAllFeatures(ProductSet pset) {
		List<boolean[]> allFeatureArrays = new ArrayList<boolean[]>(); 
		int nFeatures = pset.getProductSize();
		
		for (Product p : pset.getProducts()) { 
			allFeatureArrays.add(p.getFeatures());
		}
		
		int distinctFeatures = nDistinctFeatures(allFeatureArrays);
		
		return nFeatures == distinctFeatures;
	}
	
	//PRECONDITION - matrix.size > 0 
	private static int nDistinctFeatures(List<boolean[]> matrix) {
		int lineSize = matrix.get(0).length;
		boolean[] distinct = new boolean[lineSize];
		Arrays.fill(distinct, false);
		
		for(boolean[] line : matrix) {
			for(int i = 0; i < lineSize; i++) {
				distinct[i] |= line[i]; 
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
