/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.AbstractObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;

/**
 * A utility class to create inference visitors which look up side conditions
 * using {@link SideConditionLookup} and then pass them to the user-provided
 * {@link ElkAxiomVisitor}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SideConditions {

	public static <O> ClassInferenceVisitor<IndexedContextRoot, O> getClassSideConditionVisitor(
			final ElkAxiomVisitor<O> visitor) {
		return new AbstractClassInferenceVisitor<IndexedContextRoot, O>() {

			@Override
			protected O defaultTracedVisit(ClassInference conclusion,
					IndexedContextRoot input) {
				ElkAxiom sideCondition = new SideConditionLookup()
						.lookup(conclusion);

				if (sideCondition != null) {
					return sideCondition.accept(visitor);
				}

				return null;
			}

		};
	}

	public static <O> ObjectPropertyInferenceVisitor<Void, O> getPropertySideConditionVisitor(
			final ElkAxiomVisitor<O> visitor) {
		return new AbstractObjectPropertyInferenceVisitor<Void, O>() {

			@Override
			protected O defaultTracedVisit(ObjectPropertyInference conclusion,
					Void input) {
				ElkAxiom sideCondition = new SideConditionLookup()
						.lookup(conclusion);

				if (sideCondition != null) {
					return sideCondition.accept(visitor);
				}

				return null;
			}

		};
	}

}
