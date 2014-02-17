/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.MutableBoolean;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionEqualityChecker;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.Condition;

/**
 * A stateless procedure to check if the given inference is cyclic.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IsInferenceCyclic {

	private final static ConclusionEqualityChecker conclusionEqualityChecker_ = new ConclusionEqualityChecker();
	
	/**
	 * 
	 * @param inference
	 * @param targetContext The context which this inference is being produced to (i.e. the target)
	 * @return
	 */
	static Conclusion check(final TracedConclusion inference, final Context targetContext, final TraceStore.Reader inferenceReader) {
		// the inference is cyclic if at least one of the premises has been
		// derived only through this inference's conclusion
		final Context inferenceContext = inference.getInferenceContext(targetContext);
		
		Conclusion cyclicPremise = PremiseUtils.find(inference, new Condition<Conclusion>(){

			@Override
			public boolean holds(Conclusion premise) {
				return derivedOnlyViaGivenConclusion(premise, inference, inferenceContext, targetContext, inferenceReader);
			}
			
		});
		
		return cyclicPremise;
	}
	
	/**
	 * Returns true if all inferences of the given premise use the given conclusion.
	 * 
	 * @param premise
	 * @param conclusion
	 * @param premiseContext
	 * @param conclusionContext 
	 * @return
	 */
	static boolean derivedOnlyViaGivenConclusion(final Conclusion premise, final Conclusion conclusion, final Context premiseContext, final Context conclusionContext, final TraceStore.Reader inferenceReader) {
		final MutableBoolean foundAlternative = new MutableBoolean(false);
		final MutableBoolean anyInference = new MutableBoolean(false);

		inferenceReader.accept(premiseContext.getRoot(), premise, new BaseTracedConclusionVisitor<Void, Void>(){

			@Override
			protected Void defaultTracedVisit(TracedConclusion premiseInference, Void ignored) {
				anyInference.set(true);
				
				if (isAlternative(premiseInference, conclusion, conclusionContext)) {
					foundAlternative.set(true);
				}
				
				return null;
			}
			
		});
		
		return anyInference.get() && !foundAlternative.get();
	}
	
	/**
	 * Returns true if all premises of the inference are NOT equivalent to the
	 * given conclusion (i.e. if the inference derives its conclusion NOT via
	 * the given conclusion). It is assumed that the premises are stored in the
	 * same context as the conclusion.
	 */
	static boolean isAlternative(final TracedConclusion inference, final Conclusion conclusion, final Context conclusionContext) {
		// if the premise is produced in a context different
		// from where the conclusion is stored, then it must be
		// produced by an alternative inference.
		if (inference.getInferenceContext(conclusionContext).getRoot() != conclusionContext.getRoot()) {
			return true;
		}
		
		Conclusion equivalentPremise = PremiseUtils.find(inference, new Condition<Conclusion>(){

			@Override
			public boolean holds(Conclusion premise) {
				return premise.accept(conclusionEqualityChecker_, conclusion);
			}
			
		});
		
		return equivalentPremise == null;
	}
}
