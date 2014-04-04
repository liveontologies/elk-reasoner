package org.semanticweb.elk.alc.saturation.conclusions.interfaces;

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

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectSomeValuesFrom;

/**
 * A {@link SubLocalConclusion} representing that a given
 * {@link IndexedClassExpression} should be produced as a subsumer of all
 * neighbors over the given {@link IndexedObjectProperty}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public interface Propagation extends LocalDeterministicConclusion {

	public static final String NAME = "Propagation";

	/**
	 * @return the {@link IndexedObjectProperty} that is the relation over which
	 *         this {@link Propagation} is applied
	 */
	public IndexedObjectProperty getRelation();

	/**
	 * @return the {@link IndexedObjectSomeValuesFrom} that is propagated by
	 *         this {@link Propagation}
	 */
	public IndexedObjectSomeValuesFrom getCarry();
}
