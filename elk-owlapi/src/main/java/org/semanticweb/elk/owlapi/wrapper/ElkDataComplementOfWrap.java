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

import org.semanticweb.elk.owl.interfaces.ElkDataComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.owlapi.model.OWLDataComplementOf;

/**
 * Implements the {@link ElkDataComplementOf} interface by wrapping instances of
 * {@link OWLDataComplementOf}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkDataComplementOfWrap<T extends OWLDataComplementOf> extends
		ElkDataRangeWrap<T> implements ElkDataComplementOf {

	public ElkDataComplementOfWrap(T owlDataComplementOf) {
		super(owlDataComplementOf);
	}

	public ElkDataRange getDataRange() {
		return converter.convert(this.owlObject.getDataRange());
	}

	@Override
	public <O> O accept(ElkDataRangeVisitor<O> visitor) {
		return visitor.visit(this);
	}
}