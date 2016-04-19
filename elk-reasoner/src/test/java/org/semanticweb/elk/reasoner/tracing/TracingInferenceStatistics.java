package org.semanticweb.elk.reasoner.tracing;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Basic statistical information about the proofs for {@link Conclusion}s in
 * {@link TracingInferenceSet}s
 * 
 * @author Yevgeny Kazakov
 *
 */
public class TracingInferenceStatistics {

	/**
	 * true if all premises used in the proofs can be derived
	 */
	private boolean provable_ = true;

	/**
	 * counts the total number of different conclusions used in all proofs
	 */
	private int countConclusions_ = 0;

	/**
	 * counts the total number of different axioms used in all proofs
	 */
	private int countAxioms_ = 0;

	/**
	 * counts the total number of (not necessarily different) inferences used in
	 * proofs by summing up the number of inferences for every conclusion
	 */
	private int countInferences_ = 0;

	/**
	 * @return {@code true} if all premises used in the proofs can be derived
	 */
	boolean isProvable() {
		return provable_;
	}

	/**
	 * @return the total number of different conclusions used in all proofs
	 */
	int getCountConclusions() {
		return countConclusions_;
	}

	/**
	 * @return the total number of different axioms used in all proofs
	 */
	int getCountAxioms() {
		return countAxioms_;
	}

	/**
	 * @return the total number of (not necessarily different) inferences used
	 *         in proofs; this is the sum of the numbers of inferences for every
	 *         conclusion
	 */
	int getCountInferences() {
		return countInferences_;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(TracingInferenceStatistics.class,
				provable_, countConclusions_, countInferences_, countAxioms_);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof TracingInferenceStatistics) {
			TracingInferenceStatistics other = (TracingInferenceStatistics) obj;
			return provable_ == other.provable_
					&& countConclusions_ == other.countConclusions_
					&& countInferences_ == other.countInferences_
					&& countAxioms_ == other.countAxioms_;
		}
		// else
		return false;
	}

	@Override
	public String toString() {
		return "[ provable: " + provable_ + "; conclusions: "
				+ countConclusions_ + "; inferences: " + countInferences_
				+ "; axioms: " + countAxioms_ + " ]";
	}

	/**
	 * @param inferences
	 * @param conclusion
	 * @return the statistics about the proofs for the given
	 *         {@linkplain Conclusion} in the given {@link TracingInferenceSet}
	 */
	public static TracingInferenceStatistics getStatistics(
			TracingInferenceSet inferences, Conclusion conclusion) {
		Computation comp = new Computation(inferences);
		comp.toDo(conclusion);
		comp.process();
		return comp.statistics_;
	}

	static class Computation {

		private final TracingInferenceSet inferences_;

		Queue<Conclusion> toDo_ = new LinkedList<Conclusion>();

		Set<Conclusion> conclusions_ = new HashSet<Conclusion>();

		Set<ElkAxiom> axioms_ = new HashSet<ElkAxiom>();

		TracingInferenceStatistics statistics_ = new TracingInferenceStatistics();

		TracingInference.Visitor<Void> infVisitor_ = new TracingInferencePremiseVisitor<Void>(
				new InferencePremiseProcessor(), new InferenceAxiomProcessor());

		Computation(TracingInferenceSet inferences) {
			this.inferences_ = inferences;
		}

		private void toDo(Conclusion conclusion) {
			if (conclusions_.add(conclusion)) {
				toDo_.add(conclusion);
				statistics_.countConclusions_++;
			}
		}

		private void toDo(ElkAxiom axiom) {
			if (axioms_.add(axiom)) {
				statistics_.countAxioms_++;
			}
		}

		private void process() {
			Conclusion next;
			while ((next = toDo_.poll()) != null) {
				boolean provable = false;
				for (TracingInference inf : inferences_.getInferences(next)) {
					statistics_.countInferences_++;
					inf.accept(infVisitor_);
					provable = true;
				}
				if (!provable) {
					statistics_.provable_ = false;
				}
			}
		}

		class InferencePremiseProcessor extends DummyConclusionVisitor<Void> {

			@Override
			protected Void defaultVisit(Conclusion conclusion) {
				toDo(conclusion);
				return null;
			}

		}

		class InferenceAxiomProcessor extends DummyElkAxiomVisitor<Void> {

			@Override
			protected Void defaultLogicalVisit(ElkAxiom axiom) {
				toDo(axiom);
				return null;
			}

		}

	}

}
