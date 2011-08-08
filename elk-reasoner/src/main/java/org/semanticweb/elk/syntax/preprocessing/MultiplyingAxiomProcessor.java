/*
 * #%L
 * elk-reasoner
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
package org.semanticweb.elk.syntax.preprocessing;

import org.semanticweb.elk.syntax.ElkAxiomProcessor;
import org.semanticweb.elk.syntax.interfaces.ElkAxiom;

public class MultiplyingAxiomProcessor implements ElkAxiomProcessor {
	
	protected ElkAxiomProcessor subProcessor;
	protected int multiplicity;
	protected RenamingExpressionVisitor renamingVisitor;
	
	public MultiplyingAxiomProcessor(ElkAxiomProcessor subProcessor, int multiplicity) {
		this.subProcessor = subProcessor;
		this.multiplicity = multiplicity;
		renamingVisitor = new RenamingExpressionVisitor("");
	}

	public void process(ElkAxiom elkAxiom) {
		subProcessor.process(elkAxiom);
		for (int i=1; i<multiplicity; ++i) {
			renamingVisitor.setPostfix("X" + i);
			subProcessor.process(elkAxiom.accept(renamingVisitor));
		}
	}

}
