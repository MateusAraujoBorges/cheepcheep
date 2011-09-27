package cheep.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.management.RuntimeErrorException;

import operator.ProductSetMutation;
import operator.ValidProductSetMutation;

import org.junit.Test;
import org.uncommons.maths.number.AdjustableNumberGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.ContinuousUniformGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SteadyStateEvolutionEngine;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.selection.SigmaScaling;
import org.uncommons.watchmaker.framework.selection.StochasticUniversalSampling;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.framework.termination.Stagnation;

import cheep.eval.BerkleyProductValidator;
import cheep.eval.GPLProductValidator;
import cheep.eval.ProductSetValidator;
import cheep.eval.ProductValidator;
import cheep.eval.Validator;
import cheep.model.Product;
import cheep.model.ProductFactory;
import cheep.model.ProductSet;
import cheep.model.ProductSetFactory;
import cheep.model.ValidProductFactory;
import cheep.model.ValidProductSetFactory;

public class TestWatchmaker {
	
	public static void runGPLWithSteadyEngine() {
		ProductValidator valP = new GPLProductValidator();
		Validator<ProductSet> valPset = new ProductSetValidator(valP, 0.9, 0.99);

		
		//used only for seeding the population 
		CandidateFactory<Product> validProdFac = new ValidProductFactory(15,valP);
		CandidateFactory<ProductSet> validProdSetFac = new ValidProductSetFactory(validProdFac, 10,valPset);
		
		List<EvolutionaryOperator<ProductSet>> a = new ArrayList<EvolutionaryOperator<ProductSet>>(3);

		a.add(new ValidProductSetMutation(new Probability(1), new Probability(0.20), new Probability(0.15), validProdFac,valPset,true));
		
		EvolutionaryOperator<ProductSet> pipeline = new EvolutionPipeline<ProductSet>(a);
		Random rng = new MersenneTwisterRNG();
		EvolutionEngine<ProductSet> engine = new SteadyStateEvolutionEngine<ProductSet>(
				validProdSetFac, pipeline, valPset, new TournamentSelection(
						new Probability(0.75)), 1, true, rng);
		
		engine.addEvolutionObserver(new EvolutionLogger());
		List<EvaluatedCandidate<ProductSet>> finalPopulation = engine.evolvePopulation(50, // 100 individuals in the population.
				5, // 5% elitism.
//				new GenerationCount(500), new Stagnation(20, true));
				new GenerationCount(500));
	
		
		EvaluatedCandidate<ProductSet> best = finalPopulation.get(0);
		System.out.printf("Final Element: maxFitness:%f size:%d \n",best.getFitness(),best.getCandidate().getProducts().size());		
		for(Product p : best.getCandidate().getProducts()) {
			System.out.println(GPLProductValidator.printProduct(p.getFeatures()));
		}
		for(Product p : best.getCandidate().getProducts()) {
			System.out.println(GPLProductValidator.print01(p.getFeatures()));
		}
		
		ProductSet maxSet = maximizeFeatures(best.getCandidate(), valP, valPset);
		
		System.out.println();
		
		System.out.printf("Maximized Final Element: maxFitness:%f size:%d \n",valPset.getFitness(maxSet, null),maxSet.getProducts().size());		
		for(Product p : maxSet.getProducts()) {
			System.out.println(GPLProductValidator.printProduct(p.getFeatures()));
		}
		for(Product p : maxSet.getProducts()) {
			System.out.println(GPLProductValidator.print01(p.getFeatures()));
		}
		
//		for (EvaluatedCandidate<ProductSet> ec : finalPopulation) {
//			System.out.printf("Final Element: maxFitness:%f size:%d \n",ec.getFitness(),ec.getCandidate().getProducts().size());
//			for(Product p : ec.getCandidate().getProducts()) {
//				System.out.println(GPLProductValidator.printProduct(p.getFeatures()));
//			}
//			for(Product p : ec.getCandidate().getProducts()) {
//				System.out.println(GPLProductValidator.print01(p.getFeatures()));
//			}
//		}
	}

	public static void runBerkleyWithSteadyEngine() {
		ProductValidator valP = new BerkleyProductValidator();
		Validator<ProductSet> valPset = new ProductSetValidator(valP, 0.9, 0.99);
		
		//used only for seeding the population 
		CandidateFactory<Product> validProdFac = new ValidProductFactory(42,valP);
		CandidateFactory<ProductSet> validProdSetFac = new ValidProductSetFactory(validProdFac, 10,valPset);
		
		List<EvolutionaryOperator<ProductSet>> a = new ArrayList<EvolutionaryOperator<ProductSet>>(3);

		a.add(new ValidProductSetMutation(new Probability(1), new Probability(0.20), new Probability(0.15), validProdFac,valPset,true));
		
		EvolutionaryOperator<ProductSet> pipeline = new EvolutionPipeline<ProductSet>(a);
		Random rng = new MersenneTwisterRNG();
		EvolutionEngine<ProductSet> engine = new SteadyStateEvolutionEngine<ProductSet>(
				validProdSetFac, pipeline, valPset, new TournamentSelection(
						new Probability(0.75)), 1, true, rng);
		
		engine.addEvolutionObserver(new EvolutionLogger());
		List<EvaluatedCandidate<ProductSet>> finalPopulation = engine.evolvePopulation(50, // 50 individuals in the population.
				5, // 10% elitism.
//				new GenerationCount(500), new Stagnation(20, true));
				new GenerationCount(500));
	
		EvaluatedCandidate<ProductSet> best = finalPopulation.get(0);
		System.out.printf("Final Element: maxFitness:%f size:%d \n",best.getFitness(),best.getCandidate().getProducts().size());		
		for(Product p : best.getCandidate().getProducts()) {
			System.out.println(BerkleyProductValidator.printProduct(p.getFeatures()));
		}
		for(Product p : best.getCandidate().getProducts()) {
			System.out.println(BerkleyProductValidator.print01(p.getFeatures()));
		}
		
		ProductSet maxSet = maximizeFeatures(best.getCandidate(), valP, valPset);
		
		System.out.println();
		
		System.out.printf("Maximized Final Element: maxFitness:%f size:%d \n",valPset.getFitness(maxSet, null),maxSet.getProducts().size());		
		for(Product p : maxSet.getProducts()) {
			System.out.println(BerkleyProductValidator.printProduct(p.getFeatures()));
		}
		for(Product p : maxSet.getProducts()) {
			System.out.println(BerkleyProductValidator.print01(p.getFeatures()));
		}
		
//		for (EvaluatedCandidate<ProductSet> ec : finalPopulation) {
//			System.out.printf("Final Element: maxFitness:%f size:%d \n",ec.getFitness(),ec.getCandidate().getProducts().size());
//			for(Product p : ec.getCandidate().getProducts()) {
//				System.out.println(BerkleyProductValidator.printProduct(p.getFeatures()));
//			}
//			for(Product p : ec.getCandidate().getProducts()) {
//				System.out.println(BerkleyProductValidator.print01(p.getFeatures()));
//			}
//		}
	}

	//flip all the features turned off that don't invalidate the product
	public static ProductSet maximizeFeatures(ProductSet toMaximize, Validator<Product> valp, Validator<ProductSet> valpset) {
		Set<Product> pset = toMaximize.getProducts();
		Product[] products = new Product[pset.size()];
		
		int i = 0;
		for(Product p: pset) {
			boolean[] features = p.getFeatures();
			boolean[] fclone = features.clone();
			
			for(int j = 0; j < features.length; j++) {
				if(fclone[j] == false) {
					fclone[j] = true;
					Product tmpProd = new Product(fclone);
					
					if(valp.validate(tmpProd)) { //found an optional feature turned off
						continue;
					} else {
						fclone[j] = false;
					}
				}
			}
			
			Product maximizedProd = new Product(fclone);
			products[i] = maximizedProd;
			i++;
		}
		
		ProductSet maxPset = new ProductSet(products);
		if (valpset.validate(maxPset)) {
			return maxPset;
		} else { //should not happen - all the products are valid!
			throw new RuntimeException("What happened here?");
		}
	}
	
	public static void main(String[] args) {
//		runGPLWithSteadyEngine();
		runBerkleyWithSteadyEngine();
	}
}

class EvolutionLogger implements EvolutionObserver<ProductSet> {

	@Override
	public void populationUpdate(PopulationData<? extends ProductSet> data) {
		System.out.printf("Generation %d: maxFitness:%f size:%d  %s\n",
				data.getGenerationNumber(), data.getBestCandidateFitness(),
				data.getBestCandidate().getProducts().size(),data.getBestCandidate());
		System.out.println("Mean fitness: " + data.getMeanFitness()
				+ " stdDeviation: " + data.getFitnessStandardDeviation());
	}

}