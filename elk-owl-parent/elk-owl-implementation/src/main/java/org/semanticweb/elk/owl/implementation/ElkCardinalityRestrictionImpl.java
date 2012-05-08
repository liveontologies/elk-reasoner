/*
 * #%L
 * ELK OWL Model Implementation
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
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkCardinalityRestriction;

/**
 * Implementation of {@link ElkCardinalityRestriction}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <P>
 *            the type of the property of this restriction
 */
public abstract class ElkCardinalityRestrictionImpl<P> extends
		ElkPropertyRestrictionImpl<P> implements ElkCardinalityRestriction<P> {

	protected final int cardinality;

	ElkCardinalityRestrictionImpl(P property, int cardinality) {
		super(property);
		this.cardinality = cardinality;
	}

	@Override
	public int getCardinality() {
		return this.cardinality;
	}

}
