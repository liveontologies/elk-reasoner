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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkDataOneOf;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Implements the {@link ElkDataOneOf} interface by wrapping instances of
 * {@link OWLDataOneOf}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkDataOneOfWrap<T extends OWLDataOneOf> extends
		ElkDataRangeWrap<T> implements ElkDataOneOf {

	public ElkDataOneOfWrap(T refOWLDataOneOf) {
		super(refOWLDataOneOf);
	}

	@Override
	public List<? extends ElkLiteral> getLiterals() {
		List<ElkLiteral> result = new ArrayList<ElkLiteral>();
		for (OWLLiteral ltr : this.owlObject.getValues()) {
			result.add(converter.convert(ltr));
		}
		return result;
	}

	@Override
	public <O> O accept(ElkDataRangeVisitor<O> visitor) {
		return visitor.visit(this);
	}
}