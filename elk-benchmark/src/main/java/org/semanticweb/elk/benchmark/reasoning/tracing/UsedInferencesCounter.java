package org.semanticweb.elk.benchmark.reasoning.tracing;

import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.BaseTracedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.TracedConclusion;

/**
 * Counts the number of visited inferences (instances of {@link TracedConclusion}).
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class UsedInferencesCounter extends BaseTracedConclusionVisitor<Void, Context> {

	private int infCounter_ = 0;
	
	//private Set<TracedConclusion> inferences = new HashSet<TracedConclusion>();
	
	@Override
	protected Void defaultTracedVisit(TracedConclusion conclusion,
			Context parameter) {
		infCounter_++;
		//inferences.add(conclusion);
		//System.out.println(parameter + ": " + conclusion + ": " + InferencePrinter.print(conclusion));
		
		return super.defaultTracedVisit(conclusion, parameter);
	}
	
	public void resetCounter() {
		infCounter_ = 0;
	}
	
	public int getInferenceCount() {
		return infCounter_;
		//return inferences.size();
	}

	
}