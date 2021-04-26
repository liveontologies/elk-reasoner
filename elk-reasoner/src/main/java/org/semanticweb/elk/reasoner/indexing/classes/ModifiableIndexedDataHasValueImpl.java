/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing.classes;

import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedSubObject;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Implements {@link ModifiableIndexedDataHasValue}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class ModifiableIndexedDataHasValueImpl extends
		StructuralIndexedComplexClassExpressionEntryImpl<ModifiableIndexedDataHasValueImpl>
		implements ModifiableIndexedDataHasValue {

	private final ElkDataProperty relation_;

	private final ElkLiteral filler_;

	private ModifiableIndexedDataHasValueImpl(ElkDataProperty relation,
			ElkLiteral filler) {
		super(structuralHashCode(relation, filler));
		this.relation_ = relation;
		this.filler_ = filler;
	}

	ModifiableIndexedDataHasValueImpl(ElkDataHasValue elkDataHasValue) {
		this((ElkDataProperty) elkDataHasValue.getProperty(),
				elkDataHasValue.getFiller());
	}

	@Override
	public final ElkDataProperty getRelation() {
		return relation_;
	}

	@Override
	public final ElkLiteral getFiller() {
		return filler_;
	}

	static int structuralHashCode(ElkDataProperty relation, ElkLiteral filler) {
		return HashGenerator.combinedHashCode(
				ModifiableIndexedDataHasValueImpl.class, relation.getIri(),
				filler.getLexicalForm());
	}

	@Override
	public ModifiableIndexedDataHasValueImpl structuralEquals(Object second) {
		if (this == second) {
			return this;
		}
		if (second instanceof ModifiableIndexedDataHasValueImpl) {
			ModifiableIndexedDataHasValueImpl secondEntry = (ModifiableIndexedDataHasValueImpl) second;
			if (getRelation().getIri()
					.equals(secondEntry.getRelation().getIri())
					&& getFiller().getLexicalForm()
							.equals(secondEntry.getFiller().getLexicalForm()))
				return secondEntry;
		}
		// else
		return null;
	}

	@Override
	public final <O> O accept(
			IndexedComplexClassExpression.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(StructuralIndexedSubObject.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}