package org.semanticweb.elk.reasoner.saturation.conclusions.model;
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
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;

/**
 * A {@code SubConclusion} indicating that a {@link SubContext} of a
 * {@link Context} where it is stored should be initialized.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface SubContextInitialization extends SubClassConclusion {

	public static final String NAME = "Sub-Context Initialization";
	
	public <I, O> O accept(Visitor<I, O> visitor, I input);
	
	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		SubContextInitialization getSubContextInitialization(
				IndexedContextRoot root, IndexedObjectProperty subRoot);

	}
	
	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <I>
	 *            the type of the input
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<I, O> {

		public O visit(SubContextInitialization conclusion, I input);

	}


}
