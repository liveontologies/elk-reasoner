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
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkCardinalityRestrictionVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataExactCardinalityUnqualifiedVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataExactCardinalityVisitor;

/**
 * Implementation of {@link ElkDataExactCardinalityUnqualified}.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * 
 */
public class ElkDataExactCardinalityUnqualifiedImpl extends
		ElkCardinalityRestrictionImpl<ElkDataPropertyExpression> implements
		ElkDataExactCardinalityUnqualified {

	ElkDataExactCardinalityUnqualifiedImpl(
			ElkDataPropertyExpression dataPropertyExpression, int cardinality) {
		super(dataPropertyExpression, cardinality);
	}

	@Override
	public <O> O accept(ElkCardinalityRestrictionVisitor<O> visitor) {
		return accept((ElkDataExactCardinalityUnqualifiedVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkDataExactCardinalityVisitor<O> visitor) {
		return accept((ElkDataExactCardinalityUnqualifiedVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkDataExactCardinalityUnqualifiedVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
