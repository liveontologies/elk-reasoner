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

import org.semanticweb.elk.owl.interfaces.ElkDataIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataRange;

/**
 * Implements the {@link ElkDataIntersectionOf} interface by wrapping instances
 * of {@link OWLDataIntersectionOf}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ElkDataIntersectionOfWrap<T extends OWLDataIntersectionOf> extends
		ElkDataRangeWrap<T> implements ElkDataIntersectionOf {

	public ElkDataIntersectionOfWrap(T owlDataIntersectionOf) {
		super(owlDataIntersectionOf);
	}

	public List<? extends ElkDataRange> getDataRanges() {
		List<ElkDataRange> result = new ArrayList<ElkDataRange>();
		for (OWLDataRange dr : this.owlObject.getOperands()) {
			result.add(converter.convert(dr));
		}
		return result;
	}

	@Override
	public <O> O accept(ElkDataRangeVisitor<O> visitor) {
		return visitor.visit(this);
	}
}