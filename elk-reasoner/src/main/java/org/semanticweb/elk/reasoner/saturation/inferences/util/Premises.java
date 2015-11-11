/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences.util;
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

import org.semanticweb.elk.reasoner.saturation.conclusions.classes.AbstractClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferencePremiseVisitor;
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
	public static ClassConclusion find(ClassInference inference, final Condition<ClassConclusion> premiseCondition) {
		final AtomicReference<ClassConclusion> found = new AtomicReference<ClassConclusion>();
		
		inference.accept(new ClassInferencePremiseVisitor<Void, Void>(new AbstractClassConclusionVisitor<Void, Void>() {
			@Override
			protected Void defaultVisit(ClassConclusion premise, Void cxt) {
				if (found.get() == null && premiseCondition.holds(premise)) {
					found.set(premise);
				}
				
				return null;
			}
		}), null);
		
		return found.get();
	}
}
