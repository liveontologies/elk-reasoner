/*
 * Copyright 2012 Department of Computer Science, University of Oxford.
 *
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
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.reasoner.datatypes.DatatypeRestriction;
import org.semanticweb.elk.reasoner.datatypes.DatatypeToolkit;
import org.semanticweb.elk.reasoner.datatypes.DatatypeToolkit.Domain;
import org.semanticweb.elk.reasoner.datatypes.intervals.IntervalTree;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedDataPropertyVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 *
 * @author Pospishnyi Olexandr
 */
public class IndexedDataProperty {

	protected ElkDataProperty property;
	protected Set<IndexedDataSomeValuesFrom> negExistential;
	protected IntervalTree numericIntervalTree;
	protected IntervalTree temporalIntervalTree;
	protected Map<String, IndexedDataSomeValuesFrom> stringCache;

	public IndexedDataProperty(ElkDataProperty property) {
		this.property = property;
		negExistential = new HashSet<IndexedDataSomeValuesFrom>(5);
		numericIntervalTree = DatatypeToolkit.makeNewIntervalTree();
		temporalIntervalTree = DatatypeToolkit.makeNewIntervalTree();
		stringCache = new HashMap<String, IndexedDataSomeValuesFrom>();
	}

	public ElkIri getIri() {
		return property.getIri();
	}

	public ElkDataProperty getProperty() {
		return property;
	}

	public <O> O accept(IndexedDataPropertyVisitor<O> visitor) {
		return visitor.visit(this);
	}

	void addNegExistential(IndexedDataSomeValuesFrom dataSomeValuesFrom) {
		negExistential.add(dataSomeValuesFrom);
		Domain restrictionDomain = dataSomeValuesFrom.getRestrictionDomain();
		switch (restrictionDomain) {
			case N:
			case Z:
			case R:
				numericIntervalTree.insert(
						DatatypeToolkit.convertRestrictionToInterval(dataSomeValuesFrom.getRestrictions(), restrictionDomain)
						,dataSomeValuesFrom);
				break;
			case TIME:
			case DATE:
			case DATETIME:
				temporalIntervalTree.insert(
						DatatypeToolkit.convertRestrictionToInterval(dataSomeValuesFrom.getRestrictions(), restrictionDomain)
						,dataSomeValuesFrom);
				break;
			case TEXT:
				for (DatatypeRestriction dr : dataSomeValuesFrom.getRestrictions()) {
					stringCache.put(dr.getValueAsString(), dataSomeValuesFrom);
				}
				break;
		}
	}

	void removeNegExistential(IndexedDataSomeValuesFrom dataSomeValuesFrom) {
		negExistential.remove(dataSomeValuesFrom);
		//TODO: remove this ugly code - make proper corrections to data stuctures
		numericIntervalTree = DatatypeToolkit.makeNewIntervalTree();
		temporalIntervalTree = DatatypeToolkit.makeNewIntervalTree();
		stringCache.clear();
		for (IndexedDataSomeValuesFrom negExt : negExistential) {
			addNegExistential(negExt);
		}
		//TODO: no realy, remove it. ASAP.
	}

	public Set<IndexedDataSomeValuesFrom> getAllNegExistentials() {
		return negExistential;
	}

	public Set<IndexedDataSomeValuesFrom> getSatisfyingNegExistentials(IndexedDatatypeExpression ide) {
		switch (ide.getRestrictionDomain()) {
			case N:
			case Z:
			case R:
				return DatatypeToolkit.findSatisfyingExpressions(ide, numericIntervalTree);
			case TIME:
			case DATE:
			case DATETIME:
				return DatatypeToolkit.findSatisfyingExpressions(ide, temporalIntervalTree);
			case TEXT:
				return DatatypeToolkit.findSatisfyingExpressions(ide, stringCache);
			default:
				return null;
		}
	}
	/**
	 * This counts how often this object occurred in the ontology.
	 */
	protected int occurrenceNo = 0;

	protected void updateOccurrenceNumber(int increment) {
		occurrenceNo += increment;
	}

	public boolean occurs() {
		return occurrenceNo > 0;
	}

	@Override
	public String toString() {
		return '<' + property.getIri().asString() + '>';
	}
	/**
	 * Hash code for this object.
	 */
	private final int hashCode_ = HashGenerator.generateNextHashCode();

	/**
	 * Get an integer hash code to be used for this object.
	 *
	 * @return Hash code.
	 */
	@Override
	public final int hashCode() {
		return hashCode_;
	}
}
