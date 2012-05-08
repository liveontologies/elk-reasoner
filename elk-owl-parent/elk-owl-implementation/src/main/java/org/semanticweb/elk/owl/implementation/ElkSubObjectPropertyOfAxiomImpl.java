/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkSubObjectPropertyOfAxiom.java 273 2011-08-04 15:33:14Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkSubObjectPropertyOfAxiom.java $
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

import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Object_Subproperties">Object Subproperty
 * Axiom<a> in the OWL 2 specification.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * 
 */
public class ElkSubObjectPropertyOfAxiomImpl extends ElkObjectImpl implements
		ElkSubObjectPropertyOfAxiom {

	protected final ElkSubObjectPropertyExpression subObjectPropertyExpression;
	protected final ElkObjectPropertyExpression superObjectPropertyExpression;

	/* package-private */ElkSubObjectPropertyOfAxiomImpl(
			ElkSubObjectPropertyExpression subObjectPropertyExpression,
			ElkObjectPropertyExpression superObjectPropertyExpression) {
		this.subObjectPropertyExpression = subObjectPropertyExpression;
		this.superObjectPropertyExpression = superObjectPropertyExpression;
	}

	@Override
	public ElkSubObjectPropertyExpression getSubObjectPropertyExpression() {
		return subObjectPropertyExpression;
	}

	@Override
	public ElkObjectPropertyExpression getSuperObjectPropertyExpression() {
		return superObjectPropertyExpression;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("SubObjectPropertyOf(");
		result.append(subObjectPropertyExpression.toString());
		result.append(" ");
		result.append(superObjectPropertyExpression.toString());
		result.append(")");
		return result.toString();
	}

	@Override
	public <O> O accept(ElkObjectPropertyAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
