/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.incremental.AxiomLoadingListener;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverter;

/**
 * A delegating visitor which notifies the provided
 * {@link AxiomLoadingListener} that some axiom represents a change
 * which cannot be processed incrementally by the reasoner
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class NonIncrementalElkAxiomVisitor extends DelegatingElkAxiomVisitor<Void>
		implements ElkAxiomConverter {

	private final AxiomLoadingListener<ElkAxiom> listener_;

	public NonIncrementalElkAxiomVisitor(ElkAxiomConverter visitor,
			AxiomLoadingListener<ElkAxiom> listener) {
		super(visitor);
		listener_ = listener;
	}

	@Override
	public Void visit(ElkEquivalentObjectPropertiesAxiom axiom) {
		listener_.notify(axiom);
		return super.visit(axiom);
	}

	@Override
	public Void visit(ElkReflexiveObjectPropertyAxiom axiom) {
		listener_.notify(axiom);
		return super.visit(axiom);
	}

	@Override
	public Void visit(ElkSubObjectPropertyOfAxiom axiom) {
		listener_.notify(axiom);
		return super.visit(axiom);
	}

	@Override
	public Void visit(ElkTransitiveObjectPropertyAxiom axiom) {
		listener_.notify(axiom);
		return super.visit(axiom);
	}

	@Override
	public Void visit(ElkObjectPropertyRangeAxiom axiom) {
		listener_.notify(axiom);
		return super.visit(axiom);
	}

}
