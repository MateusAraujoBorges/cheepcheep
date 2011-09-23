package cheep.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import operator.ProductSetMutation;
import operator.ValidProductSetMutation;

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
import org.uncommons.watchmaker.framework.selection.StochasticUniversalSampling;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.framework.termination.Stagnation;

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

	public static void main(String[] args) {
		Validator<Product> valP = new GPLProductValidator();
		Validator<ProductSet> valPset = new ProductSetValidator(valP, 0.9, 0.99);

//		CandidateFactory<Product> pfac = new ProductFactory(15);
//		CandidateFactory<ProductSet> psfac = new ProductSetFactory(pfac, 10);
		
		CandidateFactory<Product> pfac = new ValidProductFactory(15,valP);
		CandidateFactory<ProductSet> psfac = new ValidProductSetFactory(pfac, 10,valPset);
		
//		List<EvolutionaryOperator<ProductSet>> a = new ArrayList<EvolutionaryOperator<ProductSet>>(3);
//		a.add(new ProductSetMutation(new Probability(0.50), new Probability(0.25), new Probability(0.20), pfac));

		List<EvolutionaryOperator<ProductSet>> a = new ArrayList<EvolutionaryOperator<ProductSet>>(3);
		a.add(new ValidProductSetMutation(new Probability(0.50), new Probability(0.25), new Probability(0.20), pfac,valPset));

		
		EvolutionaryOperator<ProductSet> pipeline = new EvolutionPipeline<ProductSet>(a);
		Random rng = new MersenneTwisterRNG();
		EvolutionEngine<ProductSet> engine = new SteadyStateEvolutionEngine<ProductSet>(
				psfac, pipeline, valPset, new TournamentSelection(
						new Probability(0.75)), 1, true, rng);
		engine.addEvolutionObserver(new EvolutionLogger());
		List<EvaluatedCandidate<ProductSet>> finalPopulation = engine.evolvePopulation(10, // 100 individuals in the population.
				1, // 5% elitism.
//				new GenerationCount(500), new Stagnation(20, true));
				new GenerationCount(500));
	
		for (EvaluatedCandidate<ProductSet> ec : finalPopulation) {
			System.out.printf("Final Element: maxFitness:%f %s\n",ec.getFitness(),ec.getCandidate());
			
		}
	}
}

class EvolutionLogger implements EvolutionObserver<ProductSet> {

	@Override
	public void populationUpdate(PopulationData<? extends ProductSet> data) {
		System.out.printf("Generation %d: maxFitness:%f %s\n",
				data.getGenerationNumber(), data.getBestCandidateFitness(),
				data.getBestCandidate());
		System.out.println("Mean fitness: " + data.getMeanFitness()
				+ " stdDeviation: " + data.getFitnessStandardDeviation());
	}

}