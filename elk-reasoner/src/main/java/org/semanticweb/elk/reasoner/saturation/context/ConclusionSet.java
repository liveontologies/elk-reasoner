package org.semanticweb.elk.reasoner.saturation.context;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * An object containing {@link Conclusion}s. Every {@link Conclusion} is stored
 * in this {@link ConclusionSet} at most once.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ConclusionSet {

	/**
	 * Adds the given {@link Conclusion} to this {@link Context} if it does not
	 * already contained there
	 * 
	 * @param conclusion
	 *            the {@link Conclusion} to be added
	 * @return {@code true} if this {@link Context} has changed as a result of
	 *         this operation and {@link false} otherwise
	 */
	public boolean addConclusion(Conclusion conclusion);

	/**
	 * Removes the given {@link Conclusion} from this {@link Context}
	 * 
	 * @param conclusion
	 *            the {@link Conclusion} to be removed
	 * @return {@code true} if this {@link Context} has changed as a result of
	 *         this operation and {@link false} otherwise
	 */
	public boolean removeConclusion(Conclusion conclusion);

	/**
	 * Checks if the given {@link Conclusion} is contained in this
	 * {@link Context}
	 * 
	 * @param conclusion
	 *            the {@link Conclusion} to be checked
	 * @return {@code true} if {@link Conclusion} is contained in this
	 *         {@link Context} and {@code false} otherwise
	 */
	public boolean containsConclusion(Conclusion conclusion);

}
