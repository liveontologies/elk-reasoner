/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.properties;

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

import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.inferences.AbstractInference;
import org.semanticweb.elk.proofs.inferences.PropertyInferenceVisitor;

/**
 * The base class for class inferences whose conclusions are always object
 * property axioms.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
abstract class AbstractPropertyInference extends AbstractInference {

	final DerivedExpression conclusion;

	AbstractPropertyInference(DerivedAxiomExpression<? extends ElkObjectPropertyAxiom> c) {
		conclusion = c;
	}
	
	AbstractPropertyInference(LemmaExpression<? extends ElkLemma> c) {
		conclusion = c;
	}

	@Override
	public DerivedExpression getConclusion() {
		return conclusion;
	}
	
	public abstract <I, O> O accept(PropertyInferenceVisitor<I, O> visitor, I input);
}
