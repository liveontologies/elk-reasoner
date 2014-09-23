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

import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.PremiseVisitor;
import org.semanticweb.elk.util.collections.Condition;

/**
 * Utilities to work with premises of inferences.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class Premises {

	/**
	 * Returns the first premise of the inference which satisfies the condition,
	 * or null if no such premise exists.
	 * 
	 * @param inference
	 * @param condition
	 * @return
	 */
	public static Conclusion find(ClassInference inference, final Condition<Conclusion> premiseCondition) {
		final AtomicReference<Conclusion> found = new AtomicReference<Conclusion>();
		
		inference.acceptTraced(new PremiseVisitor<Void, Void>(new AbstractConclusionVisitor<Void, Void>() {
			@Override
			protected Void defaultVisit(Conclusion premise, Void cxt) {
				if (found.get() == null && premiseCondition.holds(premise)) {
					found.set(premise);
				}
				
				return null;
			}
		}), null);
		
		return found.get();
	}
}
