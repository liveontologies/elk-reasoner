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

/**
 * A {@link ClassConclusion} representing a subsumer {@link IndexedClassExpression}
 * of the root. Intuitively, if a subclass axiom {@code SubClassOf(:A :B)} is
 * derived by inference rules, then this axiom corresponds to a {@link Subsumer}
 * with root {@code :A} and expression {@code :B}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public interface Subsumer extends ClassConclusion {

	/**
	 * @return the {@code IndexedClassExpression} represented by this
	 *         {@link Subsumer}
	 */
	public IndexedClassExpression getExpression();
	
	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory extends ComposedSubsumer.Factory, DecomposedSubsumer.Factory {

		// combined interface

	}
	

}
