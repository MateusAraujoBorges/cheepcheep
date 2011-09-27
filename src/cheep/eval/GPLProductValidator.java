package cheep.eval;

import static org.junit.Assert.*;

import java.lang.ref.Reference;
import java.util.ArrayList;

import org.junit.Test;

import cheep.model.Product;
import cheep.model.ProductSet;

public class GPLProductValidator extends ProductValidator {
/**
  	0:Benchmark

	+ (ONEORMORE):
	1: Number
	2: Connected
	3: Transpose StronglyConnected :: StrongC
	4: Cycle
	5: MSTPrim
	6: MSTKruskal
	7: Shortest
	+ (END ONEORMORE)
	
	OPT:
	EXCLUSIVE:
	8: BFS
	xxxxxxx
	9: DFS
	ENDEXCLUSIVE
	10: Search
	ENDOPT
	
	11: Weighted
	
	EXCLUSIVE:
	12: Directed
	xxxxx
	13: Undirected
	ENDEXCLUSIVE:
	14: Base
 */
	
	public final static String[] reference = new String[] { "Benchmark", "Number",
			"Connected", "Transpose StronglyConnected", "Cycle", "MSTPrim",
			"MSTKruskal", "Shortest", "BFS", "DFS", "Search", "Weighted",
			"Directed", "Undirected", "Base" };
	
	@Override
	public boolean validate(Product t) {
//		System.out.println("validating T:" + t);
		boolean[] features = t.getFeatures();
		boolean isValid = true;
		//0
		isValid &= features[0];
		
		//1-7
		int counter = 0;
		for(int i = 1; i <= 7; i++) {
			if(features[i])
				counter++;
		}
		isValid &= counter >= 1;
		
		//8-10
		boolean src = false;
		boolean bfs = features[8];
		boolean dfs = features[9];
		boolean search = features[10];
		
		if(bfs || dfs || search) {//check if src feature is active
			src = true;
			isValid &= (bfs ^ dfs) && search;
		}
		                          
		//11
		boolean weighted = features[11];
		isValid &= weighted;

		//12-13
		boolean gtp = false;
		boolean direct = features[12];
		boolean undirect = features[13];
				
		gtp = direct ^ undirect;//check if gtp feature is active
		isValid &= gtp;
		
		//14
		isValid &= features[14];
		
		
		if(features[1]) { //Number implies Gtp and Src ;
			isValid &= gtp && src;
		}
		
		if(features[2]) { //Connected implies Undirected and Src ;
			isValid &= src && features[13];
		}
		
		if(features[3] ) { //StrongC implies Directed and DFS ;
			isValid &= dfs && direct;
		}
		
		if(features[4]) { //Cycle implies Gtp and DFS ;
			isValid &= gtp && dfs; 
		}
		
		boolean mstkruskal = features[6];
		boolean mstprim = features[5];

		if(mstkruskal || mstprim) { //MSTKruskal or MSTPrim implies Undirected and Weighted ;
			isValid &= undirect && dfs; 
			isValid &= mstkruskal ^ mstprim; // MSTKruskal or MSTPrim implies not (MSTKruskal and MSTPrim) ;
		}
		

		if(features[7]) { //Shortest implies Directed and Weighted ;
			isValid &= direct && weighted; 
		}
		
		return isValid;
	}
	
	@Test
	public void testValidator() {
		//BENCHMARK,NUMBER,SHORTEST,BFS,SEARCH,WEIGHTED,DIRECTED,BASE
		boolean[] b2 = new boolean[]{true,true,false,false,false,false,false,false,true,false,true,true,true,false,true};
		boolean[] bn2 = new boolean[]{true,true,false,false,false,false,false,false,true,true,true,true,true,false,true};
		boolean[] set1= new boolean[]{true,true,true,true,true,true,true,true,true,false,true,true,true,true,true};
		Product p = new Product(b2);
		assertTrue(validate(p));
		p = new Product(bn2);
		assertFalse(validate(p));
		p = new Product(set1);
		assertFalse(validate(p));
	}
	
	@Test
	public void testValidator2() {
//		BENCHMARK,NUMBER,TRANSPOSE,STRONGLYCONNECTED,CYCLE,SHORTEST,DFS,SEARCH,WEIGHTED,DIRECTED,BASE
		boolean[] b1 = new boolean[]{true,true,false,true,true,false,false,true,false,true,true,true,true,false,true};
		Product p = new Product(b1);
		assertTrue(validate(p));
//		BENCHMARK,NUMBER,SHORTEST,BFS,SEARCH,WEIGHTED,DIRECTED,BASE
		boolean[] b2 = new boolean[]{true,true,false,false,false,false,false,true,true,false,true,true,true,false,true};
		p = new Product(b2);
		assertTrue(validate(p));
//		BENCHMARK,NUMBER,CYCLE,MSTPRIM,DFS,SEARCH,CONNECTED,WEIGHTED,UNDIRECTED,BASE
		boolean[] b3 = new boolean[]{true,true,true,false,true,true,false,false,false,true,false,true,false,true,true};
		p = new Product(b3);
		assertTrue(validate(p));
//		BENCHMARK,NUMBER,MSTPRIM,BFS,SEARCH,CONNECTED,WEIGHTED,UNDIRECTED,BASE
//		BENCHMARK,NUMBER,CYCLE,MSTKRUSKAL,DFS,SEARCH,CONNECTED,WEIGHTED,UNDIRECTED,BASE
//		BENCHMARK,NUMBER,MSTKRUSKAL,BFS,SEARCH,CONNECTED,WEIGHTED,UNDIRECTED,BASE
	}
	
	@Test 
	public void testValidator3() {
		boolean[] b1 = new boolean[]{true, true, false, false, false, false, true, false, false, true, true, true, false, true, true};;
		Product p1 = new Product(b1);
		assertTrue(validate(p1));

		boolean[] b2 = new boolean[]{true, true, false, true, false, false, false, true, false, true, true, true, true, false, true};;
		Product p2 = new Product(b2);
		assertTrue(validate(p2));

		boolean[] b3 = new boolean[]{true, true, true, false, false, false, false, false, true, false, true, true, false, true, true};;
		Product p3 = new Product(b3);
		assertTrue(validate(p3));

		boolean[] b4 = new boolean[]{true, true, true, false, true, true, false, false, false, true, true, true, false, true, true};;
		Product p4 = new Product(b4);
		assertTrue(validate(p4));

		boolean[] b5 = new boolean[]{true, true, false, true, true, false, false, true, false, true, true, true, true, false, true};;
		Product p5 = new Product(b5);
		assertTrue(validate(p5));

		boolean[] b6 = new boolean[]{true, false, true, false, true, true, false, false, false, true, true, true, false, true, true};;
		Product p6 = new Product(b6);
		assertTrue(validate(p6));
		
		ProductSet pset = new ProductSet(new Product[]{p1,p2,p3,p4,p5,p6});
		ProductValidator valP = new GPLProductValidator();
		Validator<ProductSet> valPset = new ProductSetValidator(valP, 0.9, 0.99);
		valPset.getFitness(pset, new ArrayList<ProductSet>());
	}
	
	@Test 
	public void testValidator4() {
		boolean[] b1 = new boolean[]{true, false, true, false, false, false, true, false, false, true, true, true, false, true, true};
		Product p1 = new Product(b1);
		assertTrue(validate(p1));

		System.out.println(print01(b1));
		System.out.println(printProduct(b1));
		
		boolean[] b2 = new boolean[]{true, true, false, true, false, false, false, true, false, true, true, true, true, false, true};
		Product p2 = new Product(b2);
		assertTrue(validate(p2));
		
		System.out.println(print01(b2));
		System.out.println(printProduct(b2));

		boolean[] b3 = new boolean[]{true, true, false, false, true, true, false, false, false, true, false, true, false, true, true};;
		Product p3 = new Product(b3);
		assertTrue(validate(p3));
		
		System.out.println(print01(b3));
		System.out.println(printProduct(b3));

		boolean[] b4 = new boolean[]{true, false, false, false, false, false, false, true, true, false, true, true, true, false, true};
		Product p4 = new Product(b4);
		assertTrue(validate(p4));
		
		System.out.println(print01(b4));
		System.out.println(printProduct(b4));

		
		ProductSet pset = new ProductSet(new Product[]{p1,p2,p3,p4});
		ProductValidator valP = new GPLProductValidator();
		Validator<ProductSet> valPset = new ProductSetValidator(valP, 0.9, 0.99);
		assertTrue(valPset.validate(pset));
		valPset.getFitness(pset, new ArrayList<ProductSet>());
	}
	
	public static String print01(boolean[] array) {
		StringBuffer sb = new StringBuffer();
		for (boolean b : array) {
			if(b) {
				sb.append(1);				
			} else {
				sb.append(0);
			}
		}
		return sb.toString();
	}

	
	public static String printProduct(boolean[] array) {
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < array.length; i++) {
			if(array[i]) {
				sb.append(reference[i]);
				sb.append(" ");
			} 
		}
		return sb.toString();
		
	}
	
	@Override
	public boolean[] xorCombination(Product t) {
		boolean[] features = t.getFeatures();
		
//		boolean PRIM = features[5];
//		boolean KRUSKAL = features[6];
		boolean BFS = features[8];
		boolean DFS = features[9];
		boolean SEARCH = features[10];
		boolean DIRECTED = features[12];
		boolean UNDIRECTED = features[13];
		
//		return new boolean[]{PRIM,KRUSKAL,BFS,DFS,SEARCH,DIRECTED,UNDIRECTED};
		return new boolean[]{BFS,DFS,SEARCH,DIRECTED,UNDIRECTED};
	}
}
