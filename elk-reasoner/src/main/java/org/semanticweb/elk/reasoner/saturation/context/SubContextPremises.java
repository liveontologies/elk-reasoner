package org.semanticweb.elk.reasoner.saturation.context;

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

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;

/**
 * A finer representation for a set of {@link Conclusion}s that can be used as
 * premises of inference rules associated with the same sub-root
 * {@link IndexedPropertyChain} in addition to the same root
 * {@link IndexedClassExpression}. Each {@link SubContextPremises} is stored
 * within the corresponding {@link ContextPremises} for the respective sub-root
 * {@link IndexedPropertyChain}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface SubContextPremises {

	/**
	 * @return the sources of all derived {@link BackwardLink}s with relations
	 *         {@link BackwardLink#getBackwardRelation()} to be sub-root of this
	 *         {@link SubContextPremises}
	 */
	Set<IndexedContextRoot> getLinkedRoots();

	/**
	 * @return the representation of all derived {@link Propagation}s with
	 *         relations {@link Propagation#getRelation()} to be sub-root of
	 *         this {@link SubContextPremises}
	 */
	// Set<? extends IndexedObjectSomeValuesFrom> getPropagatedSubsumers();

	boolean isInitialized();

}
