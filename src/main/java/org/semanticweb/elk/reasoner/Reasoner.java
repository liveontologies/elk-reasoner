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
package org.semanticweb.elk.reasoner;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.semanticweb.elk.syntax.ElkAxiom;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkClassExpression;


public class Reasoner {
	public static int TRANSITIVITY = 0;  
	
	
	final protected Indexer indexer = new Indexer();
	final protected Saturator saturator = new Saturator();
	
	public void add(ElkAxiom elkAxiom) {
		if (elkAxiom != null) {
			indexer.indexAxiom(elkAxiom);
		}
	}
	
	public Taxonomy<ElkClass> classify() {
		indexer.preprocessRBox();
		return new TransitiveReduction<ElkClass> (new AtomicClassifier());
	}
	

	protected class AtomicClassifier implements TransitiveRelation<ElkClass> {
		final List<ElkClass> atomicClasses = new LinkedList<ElkClass> ();
		
		public AtomicClassifier() {
			System.err.println("//classifying");
			int step = 0;
			for (Map.Entry<ElkClassExpression, Concept> entry : indexer.mapClassToConcept.entrySet()) {
				if (entry.getKey() instanceof ElkClass) {
					if (++step % 1000 == 0) System.err.println("//" + step);
					atomicClasses.add((ElkClass) entry.getKey());
					saturator.saturate(entry.getValue());
				}
				
			}
			System.err.println("//done");
		}

		public Iterable<ElkClass> getAllSubObjects(ElkClass object) {
			throw new UnsupportedOperationException();
		}

		public Iterable<ElkClass> getAllSuperObjects(ElkClass object) {
			List<ElkClass> result = new LinkedList<ElkClass> ();
			for (Concept concept :saturator.getContext(indexer.mapClassToConcept.get(object)).derivedConcepts)
				if (concept.getClassExpression() instanceof ElkClass)
						result.add((ElkClass) concept.getClassExpression());
			return result;
		}

		public Iterable<ElkClass> getAllObjects() {
			return atomicClasses;
		}

		public org.semanticweb.elk.reasoner.TransitiveRelation.Direction getImplementedDirection() {
			return TransitiveRelation.Direction.SUPER_OBJECTS;
		}
	}
}