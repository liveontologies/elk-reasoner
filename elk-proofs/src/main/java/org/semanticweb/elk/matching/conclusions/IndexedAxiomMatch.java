package org.semanticweb.elk.matching.conclusions;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

public interface IndexedAxiomMatch extends ConclusionMatch {

	<O> O accept(Visitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory extends IndexedDisjointClassesAxiomMatch1.Factory,
			IndexedDisjointClassesAxiomMatch2.Factory,
			IndexedSubClassOfAxiomMatch1.Factory,
			IndexedSubClassOfAxiomMatch2.Factory,
			IndexedDefinitionAxiomMatch1.Factory,
			IndexedDefinitionAxiomMatch2.Factory,
			IndexedSubObjectPropertyOfAxiomMatch1.Factory,
			IndexedSubObjectPropertyOfAxiomMatch2.Factory,
			IndexedObjectPropertyRangeAxiomMatch1.Factory,
			IndexedObjectPropertyRangeAxiomMatch2.Factory {

		// combined interface

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> extends IndexedDisjointClassesAxiomMatch1.Visitor<O>,
			IndexedDisjointClassesAxiomMatch2.Visitor<O>,
			IndexedSubClassOfAxiomMatch1.Visitor<O>,
			IndexedSubClassOfAxiomMatch2.Visitor<O>,
			IndexedDefinitionAxiomMatch1.Visitor<O>,
			IndexedDefinitionAxiomMatch2.Visitor<O>,
			IndexedSubObjectPropertyOfAxiomMatch1.Visitor<O>,
			IndexedSubObjectPropertyOfAxiomMatch2.Visitor<O>,
			IndexedObjectPropertyRangeAxiomMatch1.Visitor<O>,
			IndexedObjectPropertyRangeAxiomMatch2.Visitor<O> {

		// combined interface

	}

}
