package org.semanticweb.elk.reasoner.saturation.context;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;

public interface SubClassConclusionSet {

	/**
	 * Adds the given {@link SubClassConclusion} to this {@link SubClassConclusionSet} if
	 * it does not already contained there
	 * 
	 * @param conclusion
	 *            the {@link SubClassConclusion} to be added
	 * @return {@code true} if this {@link SubClassConclusion} has changed as a
	 *         result of this operation and {@link false} otherwise
	 */
	boolean addSubConclusion(SubClassConclusion conclusion);

	/**
	 * Removes the given {@link SubClassConclusion} from this
	 * {@link SubClassConclusionSet}
	 * 
	 * @param conclusion
	 *            the {@link SubClassConclusion} to be removed
	 * @return {@code true} if this {@link SubClassConclusion} has changed as a
	 *         result of this operation and {@link false} otherwise
	 */
	boolean removeSubConclusion(SubClassConclusion conclusion);

	/**
	 * Checks if the given {@link SubClassConclusion} is contained in this
	 * {@link SubClassConclusionSet}
	 * 
	 * @param conclusion
	 *            the {@link SubClassConclusion} to be checked
	 * @return {@code true} if {@link SubClassConclusion} is contained in this
	 *         {@link SubClassConclusionSet} and {@code false} otherwise
	 */
	boolean containsSubConclusion(SubClassConclusion conclusion);

	/**
	 * @return {@code true} if this {@link SubClassConclusionSet} does not contain
	 *         any {@link SubClassConclusion}. In this case,
	 *         {@link #containsSubConclusion(ClassConclusion)} returns {@code false}
	 *         for every input.
	 */
	boolean isEmpty();

}
