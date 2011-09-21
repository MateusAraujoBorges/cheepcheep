package cheep.eval;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

public interface Validator<T> extends FitnessEvaluator<T> {

	/**
	 * Check if this element is valid, i.e., it can be a solution for 
	 * the problem).
	 * @param t
	 * @return
	 */
	
	public boolean validate(T t);
		
}
