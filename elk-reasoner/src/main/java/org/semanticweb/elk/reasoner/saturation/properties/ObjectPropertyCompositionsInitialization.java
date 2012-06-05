/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Pair;

//TODO: Document this class
//TODO: Add progress monitor
/**
 * Sets up multimaps for fast retrieval of property compositions.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class ObjectPropertyCompositionsInitialization {

	/**
	 * the ontology index used to compute property chains
	 */
	protected final OntologyIndex ontologyIndex;

	public ObjectPropertyCompositionsInitialization(OntologyIndex ontologyIndex) {
		this.ontologyIndex = ontologyIndex;
	}

	public Map<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>> getCompositions() {
		// set up property compositions
		Map<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>> compositions = new HashMap<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>>();

		for (IndexedBinaryPropertyChain chain : Operations.filter(
				ontologyIndex.getIndexedPropertyChains(),
				IndexedBinaryPropertyChain.class))
			for (IndexedPropertyChain rightSubProperty : chain
					.getRightProperty().getSaturated().getSubProperties())
				for (IndexedPropertyChain leftSubProperty : chain
						.getLeftProperty().getSaturated().getSubProperties()) {

					// SaturatedPropertyChain right = rightSubProperty
					// .getSaturated();
					// if (right.compositionsByLeftSubProperty == null)
					// right.compositionsByLeftSubProperty = new
					// HashListMultimap<IndexedPropertyChain,
					// IndexedBinaryPropertyChain>();
					// right.compositionsByLeftSubProperty.add(
					// leftSubProperty, chain);
					//
					// SaturatedPropertyChain left = leftSubProperty
					// .getSaturated();
					// if (left.compositionsByRightSubProperty == null)
					// left.compositionsByRightSubProperty = new
					// HashListMultimap<IndexedPropertyChain,
					// IndexedBinaryPropertyChain>();
					// left.compositionsByRightSubProperty.add(
					// rightSubProperty, chain);

					Pair<IndexedPropertyChain, IndexedPropertyChain> key = new Pair<IndexedPropertyChain, IndexedPropertyChain>(
							leftSubProperty, rightSubProperty);
					Vector<IndexedPropertyChain> value = compositions.get(key);

					if (value == null) {
						value = new Vector<IndexedPropertyChain>();
						compositions.put(key, value);
					}

					if (chain.occursAuxiliarily())
						value.add(chain);
					else
						for (IndexedObjectProperty superProperty : chain
								.getToldSuperProperties())
							value.add(superProperty);
				}

		return compositions;

	}

}
