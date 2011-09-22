package cheep.model;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Set of Products. This class contains methods to check if the products 
 * contained here cover all possible features. 
 * @author mateus
 *
 */

public class ProductSet {
	
	private final Set<Product> products;
	private final int productSize;
	
	
	//PRECONDITION productList.length > 0, all products have same size
	public ProductSet(Product... productList) {
		Set<Product> ps = new HashSet<Product>();
		for (Product p : productList) {
			ps.add(p);
		}
		this.products = ps;
		this.productSize = productList[0].getTotalNumberFeatures();
	}

	public Set<Product> getProducts() {
		return products;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Product p : products) {
			sb.append(p);
			sb.append(" ");
		}
		return sb.toString();
	}

	public int getProductSize() {
		return productSize;
	}
}
