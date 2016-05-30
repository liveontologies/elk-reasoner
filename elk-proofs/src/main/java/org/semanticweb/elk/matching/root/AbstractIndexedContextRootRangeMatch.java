package org.semanticweb.elk.matching.root;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkPropertyRestrictionQualified;

abstract class AbstractIndexedContextRootRangeMatch<V extends ElkPropertyRestrictionQualified<ElkObjectPropertyExpression, ?>>
		extends AbstractIndexedContextRootMatch<V>
		implements IndexedContextRootRangeMatch {

	public AbstractIndexedContextRootRangeMatch(V value) {
		super(value);
	}

	@Override
	public ElkObjectPropertyExpression getPropertyMatch() {
		return getValue().getProperty();
	}

	abstract ElkClassExpression getFillerAsElkClassExpression(
			ElkObject.Factory factory);

	@Override
	public ElkClassExpression toElkExpression(ElkObject.Factory factory) {
		// TODO: provide full support
		return getFillerAsElkClassExpression(factory);
	}

	@Override
	public final <O> O accept(IndexedContextRootMatch.Visitor<O> visitor) {
		return accept((IndexedContextRootRangeMatch.Visitor<O>) visitor);
	}

}
