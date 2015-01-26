/*
 * #%L
 * ELK OWL API Binding
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
package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkCardinalityRestrictionVisitor;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataMaxCardinalityUnqualifiedVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataMaxCardinalityVisitor;
import org.semanticweb.elk.owl.visitors.ElkPropertyRestrictionVisitor;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;

/**
 * Implements the {@link ElkDataMaxCardinalityQualified} interface by wrapping
 * instances of {@link OWLDataMaxCardinality}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkDataMaxCardinalityWrap<T extends OWLDataMaxCardinality> extends
		ElkClassExpressionWrap<T> implements ElkDataMaxCardinalityUnqualified {

	public ElkDataMaxCardinalityWrap(T owlDataMaxCardinality) {
		super(owlDataMaxCardinality);
	}

	@Override
	public int getCardinality() {
		return this.owlObject.getCardinality();
	}

	@Override
	public ElkDataPropertyExpression getProperty() {
		return converter.convert(this.owlObject.getProperty());
	}

	@Override
	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return accept((ElkDataMaxCardinalityUnqualifiedVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkDataMaxCardinalityVisitor<O> visitor) {
		return accept((ElkDataMaxCardinalityUnqualifiedVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkCardinalityRestrictionVisitor<O> visitor) {
		return accept((ElkDataMaxCardinalityUnqualifiedVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkPropertyRestrictionVisitor<O> visitor) {
		return accept((ElkDataMaxCardinalityUnqualifiedVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkDataMaxCardinalityUnqualifiedVisitor<O> visitor) {
		return visitor.visit(this);
	}
}