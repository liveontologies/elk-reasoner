/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.alc.indexing.hierarchy;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.alc.indexing.visitors.IndexedAxiomFilter;
import org.semanticweb.elk.alc.indexing.visitors.IndexedObjectFilter;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;

/**
 * An object that indexes axioms into a given ontology index. Each instance can
 * either only add or only remove axioms.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class MainAxiomIndexerVisitor extends AbstractElkAxiomIndexerVisitor
		implements ElkAxiomIndexingVisitor {

	/**
	 * 1 if adding axioms, -1 if removing axioms
	 */
	private final int multiplicity_;

	/**
	 * {@link IndexObjectConverter}s used for indexing a neutral, a positive,
	 * and a negative occurrence of {@link IndexedClassExpression}s.
	 */
	private final IndexObjectConverter neutralIndexer, positiveIndexer,
			negativeIndexer;

	/**
	 * An {@link IndexedAxiomFilter} used to update occurrences of
	 * {@link IndexedAxiom}s
	 */
	private final IndexedAxiomFilter axiomUpdateFilter_;

	/**
	 * @param index
	 *            the {@link ModifiableOntologyIndex} used for indexing axioms
	 * @param insert
	 *            specifies whether this objects inserts or deletes axioms
	 */
	public MainAxiomIndexerVisitor(final OntologyIndex index, boolean insert) {
		this.multiplicity_ = insert ? 1 : -1;
		IndexedObjectFilter neutralFilter = new ObjectOccurrenceUpdateFilter(
				index, multiplicity_, 0, 0);
		this.neutralIndexer = new IndexObjectConverter(neutralFilter);
		this.axiomUpdateFilter_ = neutralFilter;
		IndexObjectConverterFactory negativeIndexerFactory = new IndexObjectConverterFactory() {
			@Override
			public IndexObjectConverter create(
					IndexObjectConverter complementary) {
				return new IndexObjectConverter(
						new ObjectOccurrenceUpdateFilter(index, multiplicity_,
								0, multiplicity_), complementary);
			}
		};
		this.positiveIndexer = new IndexObjectConverter(
				new ObjectOccurrenceUpdateFilter(index, multiplicity_,
						multiplicity_, 0), negativeIndexerFactory);
		this.negativeIndexer = positiveIndexer.getComplementaryConverter();
	}

	@Override
	public int getMultiplicity() {
		return multiplicity_;
	}

	@Override
	public void indexSubClassOfAxiom(ElkClassExpression subElkClass,
			ElkClassExpression superElkClass) {

		IndexedClassExpression subIndexedClass = subElkClass
				.accept(negativeIndexer);

		IndexedClassExpression superIndexedClass = superElkClass
				.accept(positiveIndexer);

		axiomUpdateFilter_.visit(new IndexedSubClassOfAxiom(subIndexedClass,
				superIndexedClass));
	}
	
	

	@Override
	public void indexDisjointClassesAxiom(
			List<? extends ElkClassExpression> disjointClasses) {
		List<IndexedClassExpression> members = new ArrayList<IndexedClassExpression>(disjointClasses.size());
		
		for (ElkClassExpression clazz : disjointClasses) {
			members.add(clazz.accept(negativeIndexer));
		}
		
		axiomUpdateFilter_.visit(new IndexedDisjointnessAxiom(members));
	}

	@Override
	public IndexedClass indexClassDeclaration(ElkClass ec) {
		return (IndexedClass) ec.accept(neutralIndexer);
	}

	@Override
	public IndexedObjectProperty indexObjectPropertyDeclaration(
			ElkObjectProperty ep) {
		return ep.accept(neutralIndexer);
	}

	@Override
	public void indexSubObjectPropertyOfAxiom(ElkObjectProperty subElkProperty,
			ElkObjectProperty superElkProperty) {
		
		IndexedObjectProperty subIndexedProperty = subElkProperty
				.accept(negativeIndexer);

		IndexedObjectProperty superIndexedProperty = superElkProperty
				.accept(positiveIndexer);

		if (multiplicity_ == 1) {
			subIndexedProperty.addToldSuperObjectProperty(superIndexedProperty);
		} else {
			subIndexedProperty.removeToldSuperObjectProperty(superIndexedProperty);
		}
		
	}

	@Override
	public void indexTransitiveProperty(ElkObjectProperty property) {
		IndexedObjectProperty indexedProperty = property.accept(neutralIndexer);
		
		if (multiplicity_ == 1) {
			indexedProperty.setTransitive();
		}
		else {
			indexedProperty.setNotTransitive();
		}
	}

}
