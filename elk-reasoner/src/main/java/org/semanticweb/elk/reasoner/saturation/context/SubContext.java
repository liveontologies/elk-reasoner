package org.semanticweb.elk.reasoner.saturation.context;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;

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

/**
 * The object representing an elementary unit of computations for
 * {@link Conclusion}s that can be used as premises of inferences associated
 * with the same {@link IndexedPropertyChain} sub-root in addition to the same
 * {@link IndexedClassExpression} root. Each {@link SubContext} is accessible
 * from the respective {@link Context} for the corresponding
 * {@link IndexedPropertyChain} sub-root.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface SubContext extends SubContextPremises, SubConclusionSet {

	/**
	 * @return the representation of all derived {@link Propagation}s with
	 *         relations {@link Propagation#getRelation()} to be sub-root of
	 *         this {@link SubContextPremises}
	 */
	Set<? extends IndexedObjectSomeValuesFrom> getPropagatedSubsumers();

}
