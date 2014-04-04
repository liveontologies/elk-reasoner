/**
 * 
 */
package org.semanticweb.elk.alc.saturation.reduction;
/*
 * #%L
 * ALC Reasoner
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.alc.saturation.Context;
import org.semanticweb.elk.alc.saturation.SaturationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A very simplistic, unoptimized transitive reduction computation of the atomic
 * subsumption relation for atomic classes.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SubsumptionTransitiveReduction {

	private static final Logger LOGGER_ = LoggerFactory.getLogger(SubsumptionTransitiveReduction.class);
	
	public Map<IndexedClass, SubsumptionReduct> compute(Collection<IndexedClass> classes, SaturationState saturationState) {
		Map<IndexedClass, SubsumptionReduct> reduct = new HashMap<IndexedClass, SubsumptionReduct>(classes.size());
		// assume that everything has been saturated
		for (IndexedClass next : classes) {
			Context cxt = saturationState.getContext(next);
			
			assertSaturated(cxt);
			computeDirectSubsumers(cxt, saturationState, reduct);
		}
		
		return reduct;
	}

	private void computeDirectSubsumers(Context context, SaturationState saturationState, Map<IndexedClass, SubsumptionReduct> reduct) {
		LOGGER_.trace("{}: transitive reduction started", context);
		
		Iterator<IndexedClass> subsumerIterator = context.getSaturatedContext().getAtomicSubsumers().iterator();
		IndexedClass root = (IndexedClass) context.getRoot().getPositiveMember();
		SubsumptionReduct classReduct = new SubsumptionReduct();
		
		while(subsumerIterator.hasNext()) {
			boolean isCandidateDirect = true;
			IndexedClass candidate = subsumerIterator.next();
			// adding itself, is it necessary?
			if (candidate == root) {
				classReduct.equivalent.add(candidate);
				continue;
			}
			
			Context candidateCxt = saturationState.getContext(candidate);
			
			assertSaturated(candidateCxt);
			
			Set<IndexedClass> candidateSupers = candidateCxt.getSaturatedContext().getAtomicSubsumers();
			Iterator<IndexedClass> directSubsumerIterator = classReduct.directSubsumers.iterator();
			
			if (candidateSupers.contains(root)) {
				//root subsumes the candidate and vice versa -- they're equivalent
				classReduct.equivalent.add(candidate);
				continue;
			}
			
			while (directSubsumerIterator.hasNext()) {
				IndexedClass directSubsumer = directSubsumerIterator.next();
				Context directSubsumerCxt = saturationState.getContext(directSubsumer);
				
				assertSaturated(directSubsumerCxt);
				// checking if the candidate subsumes some current direct subsumer, then it can't be direct
				if (directSubsumerCxt.getSaturatedContext().getAtomicSubsumers().contains(candidate)) {
					isCandidateDirect = false;
					
					if (candidateSupers.contains(directSubsumer)) {
						// the candidate is equivalent to the direct subsumer
						SubsumptionReduct directSubsumerReduct = reduct.get(directSubsumer);
						
						if (directSubsumerReduct == null) {
							directSubsumerReduct = new SubsumptionReduct();
							reduct.put(directSubsumer, directSubsumerReduct);
						}
						
						directSubsumerReduct.equivalent.add(candidate);
					}
					
					break;
				}
				// checking if some current direct subsumer subsumes the candidate, then it's not direct anymore
				if (candidateSupers.contains(directSubsumer)) {
					directSubsumerIterator.remove();
				}
			}
			
			if (isCandidateDirect) {
				classReduct.directSubsumers.add(candidate);
			}
		}
		
		reduct.put(root, classReduct);
		
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("{}: transitive reduction finished", context);
			LOGGER_.trace("{}: equivalent classes: {}", context, classReduct.equivalent);
			LOGGER_.trace("{}: direct super-classes: {}", context, classReduct.directSubsumers);
		}
	}
	
	private void assertSaturated(Context cxt) {
		if (cxt == null || cxt.getSaturatedContext() == null) {
			throw new IllegalStateException("Cannot compute transitive reduction until the class " + cxt.getRoot() + " has been saturated");
		}
	}
}
