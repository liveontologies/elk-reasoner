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

import java.util.Collections;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataPropertyListRestrictionQualifiedVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataSomeValuesFromVisitor;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;

/**
 * Implements the {@link ElkDataSomeValuesFrom} interface by wrapping instances
 * of {@link OWLDataSomeValuesFrom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkDataSomeValuesFromWrap<T extends OWLDataSomeValuesFrom> extends
		ElkClassExpressionWrap<T> implements ElkDataSomeValuesFrom {

	public ElkDataSomeValuesFromWrap(T owlDataSomeValuesFrom) {
		super(owlDataSomeValuesFrom);
	}

	@Override
	public List<? extends ElkDataPropertyExpression> getDataPropertyExpressions() {
		return Collections.singletonList(converter.convert(getProperty(owlObject)));
	}

	@Override
	public ElkDataRange getDataRange() {
		return converter.convert(getFiller(owlObject));
	}

	@Override
	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(
			ElkDataPropertyListRestrictionQualifiedVisitor<O> visitor) {
		return accept((ElkDataSomeValuesFromVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkDataSomeValuesFromVisitor<O> visitor) {
		return visitor.visit(this);
	}
}