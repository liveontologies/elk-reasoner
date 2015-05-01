/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkSubDataPropertyOfAxiom.java 273 2011-08-04 15:33:14Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkSubDataPropertyOfAxiom.java $
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
/**
 * @author Yevgeny Kazakov, Apr 8, 2011
 */
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataPropertyAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.owl.visitors.ElkSubDataPropertyOfAxiomVisitor;

/**
 * ELK implementation of ElkSubDataPropertyOfAxiom.
 *
 * @author Markus Kroetzsch
 */
public class ElkSubDataPropertyOfAxiomImpl extends ElkObjectImpl implements
		ElkSubDataPropertyOfAxiom {

	private final ElkDataPropertyExpression subProperty_;
	private final ElkDataPropertyExpression superProperty_;

	ElkSubDataPropertyOfAxiomImpl(ElkDataPropertyExpression subProperty,
			ElkDataPropertyExpression superProperty) {
		this.subProperty_ = subProperty;
		this.superProperty_ = superProperty;
	}

	@Override
	public ElkDataPropertyExpression getSubDataPropertyExpression() {
		return subProperty_;
	}

	@Override
	public ElkDataPropertyExpression getSuperDataPropertyExpression() {
		return superProperty_;
	}

	@Override
	public <O> O accept(ElkDataPropertyAxiomVisitor<O> visitor) {
		return accept((ElkSubDataPropertyOfAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return accept((ElkSubDataPropertyOfAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkSubDataPropertyOfAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkSubDataPropertyOfAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
