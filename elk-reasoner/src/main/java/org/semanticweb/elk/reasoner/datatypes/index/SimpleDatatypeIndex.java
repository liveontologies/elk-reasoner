/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.datatypes.index;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import static org.semanticweb.elk.owl.interfaces.ElkDatatype.ELDatatype;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;

/**
 * Simple (and inefficient) storage and retrieval of datatype expressions. All
 * expressions belonging to the same datatype hierarchy (and thus sharing a
 * value space among themselves) are stored in a single collection.
 *
 * @author Pospishnyi Oleksandr
 */
public class SimpleDatatypeIndex implements DatatypeIndex {

	protected EnumMap<ELDatatype, Set<IndexedDatatypeExpression>> datatypeExpressions;

	@Override
	public void addDatatypeExpression(IndexedDatatypeExpression ide) {
		if (datatypeExpressions == null) {
			datatypeExpressions = new EnumMap<ELDatatype, Set<IndexedDatatypeExpression>>(ELDatatype.class);
		}
		ELDatatype rootDatatype =
			ide.getValueSpace().getDatatype().getRootValueSpaceDatatype();
		Set<IndexedDatatypeExpression> expressionsByDatatype =
			datatypeExpressions.get(rootDatatype);
		if (expressionsByDatatype == null) {
			expressionsByDatatype = new HashSet<IndexedDatatypeExpression>();
			datatypeExpressions.put(rootDatatype, expressionsByDatatype);
		}
		expressionsByDatatype.add(ide);
	}

	@Override
	public boolean removeDatatypeExpression(IndexedDatatypeExpression ide) {
		boolean success = false;
		if (ide != null && datatypeExpressions != null) {
			ELDatatype rootDatatype =
				ide.getValueSpace().getDatatype().getRootValueSpaceDatatype();
			Set<IndexedDatatypeExpression> expressionsByDatatype =
				datatypeExpressions.get(rootDatatype);
			if (expressionsByDatatype != null) {
				success = expressionsByDatatype.remove(ide);
				if (expressionsByDatatype.isEmpty()) {
					datatypeExpressions.remove(rootDatatype);
				}
			}
		}
		return success;
	}

	@Override
	public Collection<IndexedDatatypeExpression> getDatatypeExpressionsFor(IndexedDatatypeExpression ide) {
		if (datatypeExpressions != null) {
			ELDatatype rootDatatype =
				ide.getValueSpace().getDatatype().getRootValueSpaceDatatype();
			Set<IndexedDatatypeExpression> expressionsByDatatype =
				datatypeExpressions.get(rootDatatype);
			Set<IndexedDatatypeExpression> resultSet;

			if (expressionsByDatatype != null) {
				resultSet = new HashSet<IndexedDatatypeExpression>(expressionsByDatatype);
			} else {
				resultSet = new HashSet<IndexedDatatypeExpression>();
			}

			if (rootDatatype != ELDatatype.rdfs_Literal) {
				//using all expressions for rdfs:Literal datatype
				Set<IndexedDatatypeExpression> rdfsLiteralExpressions = datatypeExpressions.get(ELDatatype.rdfs_Literal);
				if (rdfsLiteralExpressions != null) {
					resultSet.addAll(rdfsLiteralExpressions);
				}
			}

			Iterator<IndexedDatatypeExpression> iterator = resultSet.iterator();
			while (iterator.hasNext()) {
				IndexedDatatypeExpression expression = iterator.next();
				if (!expression.getValueSpace().contains(ide.getValueSpace())) {
					iterator.remove();
				}
			}
			return resultSet;
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	public void appendTo(DatatypeIndex index) {
		for (Map.Entry<ELDatatype, Set<IndexedDatatypeExpression>> entry : datatypeExpressions.entrySet()) {
			Set<IndexedDatatypeExpression> expressions = entry.getValue();
			for (IndexedDatatypeExpression datatypeExpression : expressions) {
				index.addDatatypeExpression(datatypeExpression);
			}
		}
	}
}
