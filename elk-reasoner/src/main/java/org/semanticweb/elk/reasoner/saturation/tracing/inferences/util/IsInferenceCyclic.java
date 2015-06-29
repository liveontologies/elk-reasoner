/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.util;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.semanticweb.elk.MutableBoolean;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionEqualityChecker;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
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
	 * @param targetContextRoot The root of the context to which this inference should be produced to (i.e. the target)
	 * @return
	 */
	public static Conclusion check(final ClassInference inference, final TraceStore.Reader inferenceReader) {
		// the inference is cyclic if at least one of the premises has been
		// derived only through this inference's conclusion
		final IndexedContextRoot inferenceContext = inference.getInferenceRoot();
		
		Conclusion cyclicPremise = Premises.find(inference, new Condition<Conclusion>(){

			@Override
			public boolean holds(Conclusion premise) {
				return derivedOnlyViaGivenConclusion(premise, inference, inferenceReader);
			}
			
		});
		
		return cyclicPremise;
	}
	
	/**
	 * Returns true if all inferences of the given premise use the given conclusion.
	 * 
	 * @param premise
	 * @param conclusion
	 * @return
	 */
	static boolean derivedOnlyViaGivenConclusion(final Conclusion premise,
			final Conclusion conclusion,
			final TraceStore.Reader inferenceReader) {
		final MutableBoolean foundAlternative = new MutableBoolean(false);
		final MutableBoolean anyInference = new MutableBoolean(false);

		inferenceReader.accept(premise, new AbstractClassInferenceVisitor<IndexedContextRoot, Void>(){

			@Override
			protected Void defaultTracedVisit(ClassInference premiseInference, IndexedContextRoot ignored) {
				anyInference.set(true);
				
				if (isAlternative(premiseInference, conclusion)) {
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
	public static boolean isAlternative(final ClassInference inference, final Conclusion conclusion) {
		// if the premise is produced in a context different
		// from where the conclusion is stored, then it must be
		// produced by an alternative inference.
		if (inference.getInferenceRoot() != conclusion.getConclusionRoot()) {
			return true;
		}
		
		Conclusion equivalentPremise = Premises.find(inference, new Condition<Conclusion>(){

			@Override
			public boolean holds(Conclusion premise) {
				return premise.accept(conclusionEqualityChecker_, conclusion);
			}
			
		});
		
		return equivalentPremise == null;
	}
}
