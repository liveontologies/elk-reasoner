/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owlapi.query;

import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.Node;

/**
 * ensures that the test results can be compared with {@link #equals(Object)}
 * 
 * @author Peter Skocovsky
 */
public class OwlEquivalentClassesTestOutput extends
		OwlEquivalentEntitiesTestOutput<OWLClass, OwlEquivalentClassesTestOutput> {

	public OwlEquivalentClassesTestOutput(
			IncompleteResult<Node<OWLClass>> equivalent) {
		super(equivalent.map(Node::getEntities));
	}

	public OwlEquivalentClassesTestOutput(Node<OWLClass> equivalent) {
		super(equivalent.getEntities());
	}

	public OwlEquivalentClassesTestOutput(ElkReasoner reasoner,
			OWLClassExpression query) {
		this(reasoner.computeEquivalentClasses(query));
	}

}
