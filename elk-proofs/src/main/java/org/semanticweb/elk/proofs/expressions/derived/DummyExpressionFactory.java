/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived;
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
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.inferences.readers.InferenceReader;

/**
 * Always creates new expression objects
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DummyExpressionFactory implements DerivedExpressionFactory {

	@Override
	public <E extends ElkAxiom> DerivedAxiomExpressionImpl<E> create(E axiom) {
		return new DerivedAxiomExpressionImpl<E>(axiom, InferenceReader.DUMMY);
	}

	@Override
	public <L extends ElkLemma> LemmaExpressionImpl<L> create(L lemma) {
		return new LemmaExpressionImpl<L>(lemma, InferenceReader.DUMMY);
	}

	@Override
	public <E extends ElkAxiom> DerivedAxiomExpressionImpl<E> createAsserted(E axiom) {
		return new DerivedAxiomExpressionImpl<E>(axiom, InferenceReader.DUMMY, true);
	}

}
