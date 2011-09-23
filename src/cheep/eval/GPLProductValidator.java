package cheep.eval;

import static org.junit.Assert.*;

import org.junit.Test;

import cheep.model.Product;

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
	
	@Override
	public boolean validate(Product t) {
		System.out.println("validating T:" + t);
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
			isValid &= bfs ^ dfs;
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
		}
		
		isValid &= mstkruskal ^ mstprim; // MSTKruskal or MSTPrim implies not (MSTKruskal and MSTPrim) ;

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
	
}
