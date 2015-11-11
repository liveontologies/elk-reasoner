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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PropagationImpl;

/**
 * A {@link SubClassConclusion} representing that a filler of
 * {@link IndexedObjectSomeValuesFrom} has been derived as a subsumer, and
 * therefore {@link IndexedObjectSomeValuesFrom} should be propagated to all
 * predecessors over the sub-property of the property of this
 * {@link IndexedObjectSomeValuesFrom}
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public interface Propagation extends SubClassConclusion {

	public static final String NAME = "Propagation";

	// TODO: this is not needed anymore as it can be obtained from #getCarry()
	/**
	 * @return the {@link IndexedObjectProperty} that is the relation over which
	 *         this {@link PropagationImpl} is applied
	 */
	public IndexedObjectProperty getRelation();

	/**
	 * @return the {@link IndexedObjectSomeValuesFrom} that is propagated by
	 *         this {@link PropagationImpl}
	 */
	public IndexedObjectSomeValuesFrom getCarry();
	
	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		Propagation getPropagation(IndexedContextRoot root,
				IndexedObjectProperty relation,
				IndexedObjectSomeValuesFrom carry);

	}

}
