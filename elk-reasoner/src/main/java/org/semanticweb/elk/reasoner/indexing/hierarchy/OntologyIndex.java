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
/**
 * @author Yevgeny Kazakov, May 13, 2011
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.caching.IndexedObjectCache;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.LinkedContextInitRule;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * An object representing the logical structure of the ontology that enables
 * execution of reasoning inference rules. There are two type of rules: the
 * global rules stored for the ontology that can be obtained using
 * {@link #getContextInitRuleHead()}, and the local rules associated with
 * specific {@link IndexedObject}s, such as {@link IndexedClassExpression}s and
 * {@link IndexedPropertyChain}s. The methods of this class provide access to
 * such objects.
 * 
 * @see {@link IndexedObject}
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * 
 */
public interface OntologyIndex extends IndexedObjectCache {

	/**
	 * @return the assignment of {@link IndexedObjectProperty}s to
	 *         {@link ElkAxiom}s stating reflexivity of the corresponding
	 *         {@link ElkObjectProperty}.
	 */
	Multimap<IndexedObjectProperty, ElkAxiom> getReflexiveObjectProperties();

	/**
	 * @return the first context initialization rule assigned to this
	 *         {@link OntologyIndex}, or {@code null} if there no such rules;
	 *         all other rules can be obtained by traversing over
	 *         {@link LinkRule#next()}
	 */
	LinkedContextInitRule getContextInitRuleHead();

	/**
	 * @return {@code true} if {@code owl:Thing} occurs negatively in the
	 *         ontology represented by this {@link OntologyIndex}
	 */
	boolean hasNegativeOwlThing();

	/**
	 * @return {@code true} if {@code owl:Nothing} occurs positively in the
	 *         ontology represented by this {@link OntologyIndex}
	 */
	boolean hasPositivelyOwlNothing();

}