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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;

/**
 * The visitor which uses {@link SideConditionLookup} to expose {@link ElkAxiom}
 * s used as side conditions of the rules.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SideConditionVisitor extends
		AbstractClassInferenceVisitor<IndexedClassExpression, Void> {

	private final SideConditionLookup lookup_;

	private final ElkAxiomVisitor<?> axiomVisitor_;

	SideConditionVisitor(ElkAxiomVisitor<?> visitor) {
		lookup_ = new SideConditionLookup();
		axiomVisitor_ = visitor;
	}

	@Override
	protected Void defaultTracedVisit(ClassInference inference,
			IndexedClassExpression root) {

		ElkAxiom sideCondition = lookup_.lookup(inference);

		if (sideCondition != null) {
			sideCondition.accept(axiomVisitor_);
		}

		return null;
	}

}
