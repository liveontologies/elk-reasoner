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

import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitorEx;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;

/**
 * Represents all occurrences of an {@link ElkSubObjectPropertyExpression} in an
 * ontology. To this end, objects of this class keeps a list of sub and super
 * property expressions. The data structures are optimized for quickly
 * retrieving the relevant relationships during inferencing.
 * 
 * This class is mainly a data container that provides direct public access to
 * its content. The task of updating index structures consistently in a global
 * sense is left to callers.
 * 
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * 
 */
public interface IndexedPropertyChain extends IndexedObject {

	/**
	 * @return All told super object properties of this
	 *         {@link IndexedBinaryPropertyChain}
	 */
	public List<IndexedObjectProperty> getToldSuperProperties();

	/**
	 * @return All {@link IndexedBinaryPropertyChain}s in which this
	 *         {@link IndexedPropertyChain} occurs on right
	 */
	public Collection<IndexedBinaryPropertyChain> getRightChains();

	/**
	 * @return The corresponding {@code SaturatedObjecProperty} assigned to this
	 *         {@link IndexedPropertyChain}; should not be {@code null}
	 */
	public SaturatedPropertyChain getSaturated();

	public <O> O accept(IndexedPropertyChainVisitor<O> visitor);

	public <O, P> O accept(IndexedPropertyChainVisitorEx<O, P> visitor,
			P parameter);

}
