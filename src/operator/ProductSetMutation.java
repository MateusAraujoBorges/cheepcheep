package operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import cheep.model.Product;
import cheep.model.ProductSet;

public class ProductSetMutation implements EvolutionaryOperator<ProductSet>{

	private final Probability productMutationChance;
	private final Probability featureMutationChance; 
	private final Probability setMutationChance;  
	private final CandidateFactory<Product> productFactory;

	/**
	 * 
	 * @param productMutationChance chance that a ProductSet is selected for mutation
	 * @param featureMutationChance chance that a feature inside a Product will be flipped
	 * @param setMutationChance chance that a ProductSet will have an element removed/added 
	 */
	
	public ProductSetMutation(Probability productMutationChance,
			Probability featureMutationChance, Probability setMutationChance,
			CandidateFactory<Product> productFactory) {
		this.productMutationChance = productMutationChance;
		this.featureMutationChance = featureMutationChance;
		this.setMutationChance = setMutationChance;
		this.productFactory = productFactory;
	}
	
	@Override
	public List<ProductSet> apply(List<ProductSet> selectedCandidates, Random rng) {
		int nCandidates = selectedCandidates.size();
		List<ProductSet> evolved = new ArrayList<ProductSet>(nCandidates);
		
		for (ProductSet ps : selectedCandidates) {
			ProductSet evps = ps;
			//first we will mutate the products
			if(productMutationChance.nextEvent(rng)) { 
				evps = mutateProductsInsideProductSet(ps, rng); 
			} 
			//and now we will mutate the sets
			if(setMutationChance.nextEvent(rng)) { 
				evps = mutateProductSet(ps, rng); 
			} 
			
			evolved.add(evps);
		}
		
		return evolved;
	}
	
	private ProductSet mutateProductSet(ProductSet products, Random rng) {
		Set<Product> ps = products.getProducts();
		int psSize = ps.size();
		boolean addProd = rng.nextBoolean();
		Product[] evolvedProducts;
		
		if(addProd) { //add a new random product
			evolvedProducts = new Product[psSize + 1];
			int i = 0;
			for (Product p : ps) {
				evolvedProducts[i] = p;
				i++;
			}
			evolvedProducts[i] = productFactory.generateRandomCandidate(rng);
			
		} else if (psSize > 1){ //remove a random element if set size > 1 
			evolvedProducts = new Product[psSize - 1];
			int toBeRemoved = rng.nextInt(psSize);
			
			int i = 0;
			boolean removed = false;
			for (Product p : ps) {
				if(removed || i != toBeRemoved) {
					evolvedProducts[i] = p;
				} else {
					removed = true;
					i--;
				}
				i++;
			}
		} else { //size = 1
			evolvedProducts = new Product[psSize];
			int i = 0;
			for (Product p : ps) {
				evolvedProducts[i] = p;
				i++;
			}
		}
		
		return new ProductSet(evolvedProducts);
	}

	private ProductSet mutateProductsInsideProductSet(ProductSet productSet, Random rng) {
		Set<Product> ps = productSet.getProducts();
		Product[] evolvedProducts = new Product[ps.size()];
		
		int i = 0;
		for(Product p: ps) {
			evolvedProducts[i] = mutateProduct(p, rng); 
			i++;
		}
		
		return new ProductSet(evolvedProducts);
	}
	
	private Product mutateProduct(Product product, Random rng) {
		boolean[] features = product.getFeatures().clone(); //make a new copy
		int nFeatures = features.length;
		
		for(int i = 0; i < nFeatures; i++) {
			if(featureMutationChance.nextEvent(rng)) { //flip it
				features[i] = !features[i];
			}
		}
		
		return new Product(features);
	}

}
