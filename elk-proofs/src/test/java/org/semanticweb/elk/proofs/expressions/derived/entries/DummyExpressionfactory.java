/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived.entries;
/*
 * #%L
 * ELK Proofs Package
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
import org.semanticweb.elk.proofs.expressions.derived.AssertedExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.expressions.derived.DerivedLemmaExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.inferences.InferenceReader;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DummyExpressionfactory implements DerivedExpressionFactory {

	@Override
	public DerivedAxiomExpression create(ElkAxiom axiom) {
		return new DerivedAxiomExpression(axiom, InferenceReader.DUMMY);
	}

	@Override
	public DerivedLemmaExpression create(ElkLemma lemma) {
		return new DerivedLemmaExpression(lemma, InferenceReader.DUMMY);
	}

	@Override
	public DerivedAxiomExpression createAsserted(ElkAxiom axiom) {
		return new AssertedExpression<ElkAxiom>(axiom, InferenceReader.DUMMY);
	}

}
