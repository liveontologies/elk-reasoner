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

import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDataUnionOf;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataUnionOfVisitor;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataUnionOf;

/**
 * Implements the {@link ElkDataUnionOf} interface by wrapping instances of
 * {@link OWLDataUnionOf}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkDataUnionOfWrap<T extends OWLDataUnionOf> extends
		ElkDataRangeWrap<T> implements ElkDataUnionOf {

	public ElkDataUnionOfWrap(T owlDataUnionOf) {
		super(owlDataUnionOf);
	}

	@Override
	public List<? extends ElkDataRange> getDataRanges() {
		List<ElkDataRange> result = new ArrayList<ElkDataRange>();
		for (OWLDataRange ran : this.owlObject.getOperands()) {
			result.add(converter.convert(ran));
		}
		return result;
	}

	@Override
	public <O> O accept(ElkDataRangeVisitor<O> visitor) {
		return accept((ElkDataUnionOfVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkDataUnionOfVisitor<O> visitor) {
		return visitor.visit(this);
	}
}