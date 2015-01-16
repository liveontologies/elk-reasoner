/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.classes;
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

import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.AbstractInference;
import org.semanticweb.elk.proofs.inferences.ClassInferenceVisitor;

/**
 * The base class for class inferences whose conclusions are always subsumption
 * axioms.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
abstract class AbstractClassInference extends AbstractInference {

	final DerivedExpression conclusion;

	AbstractClassInference(DerivedAxiomExpression<? extends ElkClassAxiom> c) {//(ElkSubClassOfAxiom c, DerivedExpressionFactory factory) {
		//conclusion = factory.create(c);
		conclusion = c;
	}
	
	AbstractClassInference(LemmaExpression c) {
		conclusion = c;
	}

	@Override
	public DerivedExpression getConclusion() {
		return conclusion;
	}

	public abstract <I, O> O accept(ClassInferenceVisitor<I, O> visitor, I input);
}
