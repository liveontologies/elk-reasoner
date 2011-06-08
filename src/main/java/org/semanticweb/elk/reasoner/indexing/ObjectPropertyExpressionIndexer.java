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
package org.semanticweb.elk.reasoner.indexing;

import org.semanticweb.elk.syntax.ElkObjectInverseOf;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpressionVisitor;

/**
 * For indexing object properties.
 * 
 * @author Frantisek Simancik
 * 
 */
class ObjectPropertyExpressionIndexer implements
		ElkObjectPropertyExpressionVisitor<IndexedObjectProperty> {
	
	protected AxiomIndexer axiomIndexer;
	
	public ObjectPropertyExpressionIndexer(AxiomIndexer axiomIndexer) {
		this.axiomIndexer = axiomIndexer;
	}

	public IndexedObjectProperty visit(ElkObjectProperty elkObjectProperty) {
		return axiomIndexer.ontologyIndex.getCreateIndexedObjectProperty(elkObjectProperty);
	}

	public IndexedObjectProperty visit(ElkObjectInverseOf elkObjectInverseOf) {
		throw new UnsupportedOperationException();
	}
}