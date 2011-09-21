package cheep.tests;

import java.util.ArrayList;
import java.util.List;

import operator.ProductSetMutation;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.framework.termination.Stagnation;

import cheep.eval.ProductSetValidator;
import cheep.eval.ProductValidator;
import cheep.eval.Validator;
import cheep.model.Product;
import cheep.model.ProductFactory;
import cheep.model.ProductSet;
import cheep.model.ProductSetFactory;

public class TestWatchmaker {

	public static void main(String[] args) {
		CandidateFactory<Product> pfac = new ProductFactory(5);
		CandidateFactory<ProductSet> psfac = new ProductSetFactory(pfac, 10);
		
		List<EvolutionaryOperator<ProductSet>> a = new ArrayList<EvolutionaryOperator<ProductSet>>(3);
		a.add(new ProductSetMutation(new Probability(0.15), new Probability(0.30), new Probability(0.20), pfac));
		
		Validator<Product> valP = new ProductValidator();
		Validator<ProductSet> valPset = new ProductSetValidator(valP);
		
		EvolutionaryOperator<ProductSet> pipeline = new EvolutionPipeline<ProductSet>(a);
		EvolutionEngine<ProductSet> engine = new GenerationalEvolutionEngine<ProductSet>(psfac,
                 pipeline,valPset, new RouletteWheelSelection(), new MersenneTwisterRNG());
		engine.addEvolutionObserver(new EvolutionLogger());
		List<EvaluatedCandidate<ProductSet>> finalPopulation = engine.evolvePopulation(100, // 100 individuals in the population.
				5, // 5% elitism.
				new GenerationCount(500), new Stagnation(20, true));
	
		for (EvaluatedCandidate<ProductSet> ec : finalPopulation) {
			System.out.printf("Final Element: maxFitness:%f %s\n",ec.getFitness(),ec.getCandidate());
			
		}
	}
}

class EvolutionLogger implements EvolutionObserver<ProductSet> {

	@Override
	public void populationUpdate(PopulationData<? extends ProductSet> data) {
		System.out.printf("Generation %d: maxFitness:%f %s\n",data.getGenerationNumber(),data.getBestCandidateFitness(),data.getBestCandidate());
	}
	
}