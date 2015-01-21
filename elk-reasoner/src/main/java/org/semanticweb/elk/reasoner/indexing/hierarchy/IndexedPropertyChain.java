/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
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

package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.Collection;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitorEx;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;

/**
 * Represents occurrences of an {@link ElkSubObjectPropertyExpression} in an
 * ontology.
 * 
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * 
 */
public interface IndexedPropertyChain extends IndexedObject {

	/**
	 * @return The representations of all {@link ElkObjectProperty}s occurring
	 *         in {@link ElkSubObjectPropertyOfAxiom}s, where the sub property
	 *         {@link ElkSubObjectPropertyExpression} is represented by this
	 *         {@link IndexedPropertyChain}
	 * 
	 * @see ElkSubObjectPropertyOfAxiom#getSubObjectPropertyExpression()
	 * @see ElkSubObjectPropertyOfAxiom#getSuperObjectPropertyExpression()
	 * @see IndexedObjectProperty#getToldSubProperties()
	 */
	public List<IndexedObjectProperty> getToldSuperProperties();

	/**
	 * @return All {@link IndexedComplexPropertyChain}s in which this
	 *         {@link IndexedPropertyChain} is a right property
	 * 
	 * @see IndexedComplexPropertyChain#getSuffixChain()
	 */
	public Collection<IndexedComplexPropertyChain> getRightChains();

	/**
	 * @return The corresponding {@code SaturatedObjecProperty} assigned to this
	 *         {@link IndexedPropertyChain}; should never be {@code null}
	 */
	public SaturatedPropertyChain getSaturated();

	public <O> O accept(IndexedPropertyChainVisitor<O> visitor);

	public <O, P> O accept(IndexedPropertyChainVisitorEx<O, P> visitor,
			P parameter);

}
