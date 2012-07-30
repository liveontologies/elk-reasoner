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
package org.semanticweb.elk.reasoner.datatypes;

import java.util.*;
import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;
import org.semanticweb.elk.reasoner.datatypes.handlers.*;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.Pair;

/**
 * DatatypeEngine is a single entry point for external reasoning modules 
 * that carry out incremental reasoning with datatype axioms.
 * DatatypeEngine contains {@link DatatypeHandler} for each supported {@link Datatype} and
 * centralized registry for all negatively occurring datatype expressions. 
 * 
 * @author Pospishnyi Olexandr
 */
public class DatatypeEngine {

	private static final Map<Datatype, DatatypeHandler> datatypeHandlers =
			new EnumMap<Datatype, DatatypeHandler>(Datatype.class);
	private static final Map<IndexedDataProperty, Multimap<Datatype, Pair<ValueSpace, IndexedDatatypeExpression>>> registry =
			new HashMap<IndexedDataProperty, Multimap<Datatype, Pair<ValueSpace, IndexedDatatypeExpression>>>();

	static {
		registerDatatypeHandler(new AnyURIDatatypeHandler());
		registerDatatypeHandler(new BinaryDatatypeHandler());
		registerDatatypeHandler(new DateTimeDatatypeHandler());
		registerDatatypeHandler(new LiteralDatatypeHandler());
		registerDatatypeHandler(new NumericDatatypeHandler());
		registerDatatypeHandler(new PlainLiteralDatatypeHandler());
		registerDatatypeHandler(new XMLLiteralDatatypeHandler());
	}

	private static void registerDatatypeHandler(DatatypeHandler handler) {
		for (Datatype datatype : handler.getSupportedDatatypes()) {
			datatypeHandlers.put(datatype, handler);
		}
	}

	/**
	 * Get corresponding handler for specified datatype.
	 *
	 * @param datatype Supported datatype
	 * @return Handler for this datatype
	 */
	public static DatatypeHandler getDatatypeHandler(Datatype datatype) {
		return datatypeHandlers.get(datatype);
	}

	/**
	 * Register negatively occurring datatype expression 
	 * 
	 * @param property Datatype property used with datatype expression 
	 * @param datatypeExpression negatively occurring datatype expression 
	 */
	public static void register(IndexedDataProperty property, IndexedDatatypeExpression datatypeExpression) {
		Multimap<Datatype, Pair<ValueSpace, IndexedDatatypeExpression>> propertyStore = registry.get(property);
		if (propertyStore == null) {
			propertyStore = new HashListMultimap<Datatype, Pair<ValueSpace, IndexedDatatypeExpression>>();
			registry.put(property, propertyStore);
		}
		DatatypeHandler handler = getDatatypeHandler(datatypeExpression.getDatatype());
		ValueSpace valueSpace = handler.getValueSpace(datatypeExpression);
		if (valueSpace != null) {
			if (valueSpace.getDatatype() == Datatype.rdfs_Literal) {
				/*
				 * This is a very rare and special case when rdfs:Literal is
				 * occurring negatively. Such axiom subsumes all other axioms.
				 * Due to specific organization of propertyStore and datatype
				 * processing we must assign this information to every root
				 * datatype.
				 */
				for (Datatype datatype : Datatype.getRootDatatypes()) {
					propertyStore.add(datatype, new Pair(valueSpace, datatypeExpression));
				}
			} else {
				propertyStore.add(valueSpace.getDatatype().getRootValueSpaceDatatype(),
						new Pair(valueSpace, datatypeExpression));
			}
		}
	}

	/**
	 * Unregister negatively occurring datatype expression 
	 * 
	 * @param property Datatype property used with datatype expression 
	 * @param datatypeExpression negatively occurring datatype expression 
	 */
	public static void unregister(IndexedDataProperty property, IndexedDatatypeExpression datatypeExpression) {
		Multimap<Datatype, Pair<ValueSpace, IndexedDatatypeExpression>> propertyStore = registry.get(property);
		if (propertyStore != null) {
			Collection<Pair<ValueSpace, IndexedDatatypeExpression>> negativeDatatypeExpressions =
					propertyStore.get(datatypeExpression.getDatatype().getRootValueSpaceDatatype());
			Iterator<Pair<ValueSpace, IndexedDatatypeExpression>> iterator = negativeDatatypeExpressions.iterator();
			while (iterator.hasNext()) {
				if (datatypeExpression.equals(iterator.next().getSecond())) {
					iterator.remove();
					return;
				}
			}
		}
	}

	/**
	 * Get a list of all negatively occurring datatype expression that subsume
	 * specified datatype expression
	 *
	 * @param property Indexed datatype property used in datatype expressions
	 * @param datatypeExpression Positively occurring datatype expression
	 * @return A list of all previously registered negatively occurring datatype
	 * expressions that subsume specified datatype expression. The method will
	 * return null if datatypeExpression is an empty value space.
	 */
	public static List<IndexedDatatypeExpression> getSatisfyingNegExistentials(
			IndexedDataProperty property, IndexedDatatypeExpression datatypeExpression) {
		Multimap<Datatype, Pair<ValueSpace, IndexedDatatypeExpression>> propertyStore = registry.get(property);
		if (propertyStore != null) {
			DatatypeHandler handler = getDatatypeHandler(datatypeExpression.getDatatype());
			ValueSpace valueSpace = handler.getValueSpace(datatypeExpression);
			if (valueSpace == EmptyValueSpace.INSTANCE) {
				return null;
			}
			List<IndexedDatatypeExpression> result = new ArrayList<IndexedDatatypeExpression>(3);
			Collection<Pair<ValueSpace, IndexedDatatypeExpression>> negativeDatatypeExpressions =
					propertyStore.get(valueSpace.getDatatype().getRootValueSpaceDatatype());
			for (Pair<ValueSpace, IndexedDatatypeExpression> pair : negativeDatatypeExpressions) {
				if (pair.getSecond() == datatypeExpression || pair.getFirst().contains(valueSpace)) {
					result.add(pair.getSecond());
				}
			}
			return result;
		}
		throw new RuntimeException("Reasoner was not initialized properly!");
	}
}
