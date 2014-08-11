package org.semanticweb.elk.benchmark.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;

/**
 * Counts the number of visited inferences (instances of {@link ClassInference}).
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class UsedInferencesCounter extends
		AbstractClassInferenceVisitor<IndexedClassExpression, Void> {

	private int infCounter_ = 0;

	// private Set<TracedConclusion> inferences = new
	// HashSet<TracedConclusion>();

	@Override
	protected Void defaultTracedVisit(ClassInference conclusion,
			IndexedClassExpression parameter) {
		infCounter_++;
		// inferences.add(conclusion);
		// System.out.println(parameter + ": " + conclusion + ": " +
		// InferencePrinter.print(conclusion));

		return null;
	}

	public void resetCounter() {
		infCounter_ = 0;
	}

	public int getInferenceCount() {
		return infCounter_;
		// return inferences.size();
	}

}