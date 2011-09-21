package cheep.model;

import java.util.Arrays;

/**
 * This class models a product. A product is composed of a list of features,
 * where each one of them may be enabled or disabled.
 * @author mateus
 *
 */

public class Product {
	
	private final boolean[] features;
	
	public Product (boolean[] features) {
		this.features = features;
	}
	
	public int getTotalNumberFeatures() {
		return features.length;
	}
	
	public boolean[] getFeatures() {
		return features;
	}
	
	public int getDistinctFeatures() {
		int distinct = 0;
		
		for (boolean f : features) {
			if(f) {
				distinct++;
			}
		}
		return distinct;
	}
	
	public String toString() {
		return Arrays.toString(features);
	}
}
