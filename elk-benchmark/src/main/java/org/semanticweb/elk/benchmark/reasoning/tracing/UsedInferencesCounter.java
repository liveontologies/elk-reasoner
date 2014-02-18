package org.semanticweb.elk.benchmark.reasoning.tracing;

import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.BaseInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.Inference;

/**
 * Counts the number of visited inferences (instances of {@link Inference}).
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class UsedInferencesCounter extends BaseInferenceVisitor<Void, Context> {

	private int infCounter_ = 0;
	
	//private Set<TracedConclusion> inferences = new HashSet<TracedConclusion>();
	
	@Override
	protected Void defaultTracedVisit(Inference conclusion,
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