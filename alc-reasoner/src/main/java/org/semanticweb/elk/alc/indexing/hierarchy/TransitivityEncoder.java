/**
 * 
 */
package org.semanticweb.elk.alc.indexing.hierarchy;

import java.util.Collection;

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes transitivity axioms syntactically by doing the following for each transitive property R:
 * 
 * i)   For each negatively occurring "S some C", where R => S and some C, it indexes "R some C" negatively
 * ii)  Adds "S some C" to told subsumers of "R some C"
 * iii) Adds "R some C" to negative existentials of "S some C"
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TransitivityEncoder {

	private static final Logger LOGGER_ = LoggerFactory.getLogger(TransitivityEncoder.class);
	
	// TODO this can optimized in many, many ways
	public void encode(OntologyIndex index, Collection<IndexedObjectProperty> propsWithTransitiveSubProps) {
		
		if (propsWithTransitiveSubProps.isEmpty()) {
			return;
		}
		
		IndexObjectConverter converter = new IndexObjectConverter(index.getIndexedObjectCache());
		ObjectOccurrenceUpdateFilter indexer = new ObjectOccurrenceUpdateFilter(index, 1, 0, 1);
		ClassExpressionDeindexer deIndexer = new ClassExpressionDeindexer();
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		Multimap<IndexedObjectSomeValuesFrom, ElkObjectSomeValuesFrom> toIndex = new HashListMultimap<IndexedObjectSomeValuesFrom, ElkObjectSomeValuesFrom>();
		// first, creating all ELK existentials which will need to be indexed
		for (IndexedObjectSomeValuesFrom e : Operations.map(index.getIndexedClassExpressions(), new Transformation<IndexedClassExpression, IndexedObjectSomeValuesFrom>(){
			// iterating over negatively occurring existentials
			@Override
			public IndexedObjectSomeValuesFrom transform(IndexedClassExpression element) {
				if (element instanceof IndexedObjectSomeValuesFrom && element.occursNegatively()) {
					return (IndexedObjectSomeValuesFrom) element;
				}
				
				return null;
				
			}})) {
			
			ElkClassExpression deIndexedFiller = e.getFiller().accept(deIndexer);
			IndexedObjectProperty propertyUnderQuantifier = e.getRelation();
			// now go through all transitive sub-properties of this property
			for (IndexedObjectProperty transitive : propertyUnderQuantifier.getSaturatedProperty().getTransitiveSubProperties()) {
				ElkObjectSomeValuesFrom existentialToIndex = factory.getObjectSomeValuesFrom(transitive.getElkObjectProperty(), deIndexedFiller);
				
				toIndex.add(e, existentialToIndex);
			}
		}
		// second, index them, i.e. add R some R some C => R some C and R some C => T some C (if R => T)
		for (IndexedObjectSomeValuesFrom existingExistential : toIndex.keySet()) {
			for (ElkObjectSomeValuesFrom toBeIndexed : toIndex.get(existingExistential)) {
				ElkObjectSomeValuesFrom nested = factory.getObjectSomeValuesFrom(toBeIndexed.getProperty(), toBeIndexed);
				IndexedObjectSomeValuesFrom indexedNested = (IndexedObjectSomeValuesFrom) nested.accept(converter);
				IndexedObjectSomeValuesFrom indexedExistential = (IndexedObjectSomeValuesFrom) indexedNested.getFiller();
				
				//IndexedObjectSomeValuesFrom indexedExistential = (IndexedObjectSomeValuesFrom) toBeIndexed.accept(converter);
				
				// update the counters
				indexer.update(indexedExistential);
				indexer.update(indexedNested);
				
				/*if (existingExistential.negativeExistentials_ == null) {
					existingExistential.negativeExistentials_ = new ArrayHashSet<IndexedObjectSomeValuesFrom>(
							16);
				}
				
				LOGGER_.trace("Adding {} as a negative existential for {}", indexedExistential, existingExistential);
				existingExistential.negativeExistentials_.add(indexedExistential);*/
				if (indexedNested.toldSuperClasses_ == null) {
					indexedNested.toldSuperClasses_ = new ArrayHashSet<IndexedClassExpression>(
							16);
				}
				
				indexedNested.toldSuperClasses_.add(indexedExistential);
				
				if (indexedExistential.toldSuperClasses_ == null) {
					indexedExistential.toldSuperClasses_ = new ArrayHashSet<IndexedClassExpression>(
							16);
				}
				
				LOGGER_.trace("Adding {} as a super class of {}", existingExistential, indexedExistential);
				indexedExistential.toldSuperClasses_.add(existingExistential);
			}
		}
	}
}
