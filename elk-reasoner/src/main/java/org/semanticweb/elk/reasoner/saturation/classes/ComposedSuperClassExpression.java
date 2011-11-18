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
package org.semanticweb.elk.reasoner.saturation.classes;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.markers.Marked;



/**
 * Wrapper used for indicating that this IndexedClassExpression has been
 * derived by a composition rule. Decomposition rules need not be applied
 * to such expression.
 *  
 * 
 * @author Frantisek Simancik
 *
 */
public class ComposedSuperClassExpression implements Queueable {
	protected final Marked<IndexedClassExpression> markedClassExpression;

	public ComposedSuperClassExpression(Marked<IndexedClassExpression> markedClassExpression) {
		this.markedClassExpression = markedClassExpression;
	}
	
	public Marked<IndexedClassExpression> getClassExpression() {
		return markedClassExpression;
	}

	public <O> O accept(QueueableVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
