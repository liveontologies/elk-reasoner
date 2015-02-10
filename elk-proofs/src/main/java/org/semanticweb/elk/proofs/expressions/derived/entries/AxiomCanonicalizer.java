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
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.AbstractElkAxiomVisitor;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;

/**
 * Transforms axiom to a canonical form for structural equivalence checking and structural hashing.
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
class AxiomCanonicalizer extends AbstractElkAxiomVisitor<ElkAxiom> {

	private static AxiomCanonicalizer INSTANCE_ = new AxiomCanonicalizer(new ElkObjectFactoryImpl());
	
	private final ElkObjectFactory factory_;
	
	AxiomCanonicalizer(ElkObjectFactory f) {
		factory_ = f;
	}
	
	static ElkAxiom canonicalize(ElkAxiom axiom) {
		return axiom.accept(INSTANCE_);
	}

	@Override
	protected ElkAxiom defaultLogicalVisit(ElkAxiom axiom) {
		return axiom;
	}

	@Override
	protected ElkAxiom defaultNonLogicalVisit(ElkAxiom axiom) {
		return axiom;
	}

	@Override
	public ElkAxiom visit(ElkClassAssertionAxiom axiom) {
		return factory_.getSubClassOfAxiom(factory_.getObjectOneOf(axiom.getIndividual()), axiom.getClassExpression());
	}
	
	
}
