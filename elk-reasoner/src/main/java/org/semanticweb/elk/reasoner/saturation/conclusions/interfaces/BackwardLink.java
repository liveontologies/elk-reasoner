package org.semanticweb.elk.reasoner.saturation.conclusions.interfaces;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;

/**
 * A {@link Conclusion} representing derived existential restrictions from a
 * source {@link IndexedClassExpression} to this target
 * {@link IndexedClassExpression}. Intuitively, if a subclass axiom
 * {@code SubClassOf(:A ObjectSomeValuesFrom(:r :B))} is derived by inference
 * rules, then a {@link BackwardLinkImpl} with the source {@code :A} and the
 * relation {@code :r} can be produced for the target {@code :B}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public interface BackwardLink extends SubConclusion {

	public static final String NAME = "Backward Link";

	/**
	 * @return the {@link IndexedPropertyChain} that is the relation of this
	 *         {@link BackwardLink}
	 */
	public IndexedObjectProperty getBackwardRelation();

	/**
	 * @return the source {@link IndexedContextRoot} of this
	 *         {@link BackwardLink}, that is, the {@link IndexedClassExpression}
	 *         from which the existential restriction corresponding to this
	 *         {@link BackwardLink} follows
	 */
	@Override
	public IndexedContextRoot getOriginRoot();

}
