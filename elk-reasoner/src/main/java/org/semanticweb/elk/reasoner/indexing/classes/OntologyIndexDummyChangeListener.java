package org.semanticweb.elk.reasoner.indexing.classes;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.LinkedContextInitRule;

/**
 * An {@link OntologyIndex.ChangeListener} that does nothing; can be used as a
 * prototype to implement other listeners
 * 
 * @author Yevgeny Kazakov
 *
 */
public class OntologyIndexDummyChangeListener
		extends IndexedObjectCacheDummyChangeListener
		implements OntologyIndex.ChangeListener {

	@Override
	public void reflexiveObjectPropertyAddition(IndexedObjectProperty property,
			ElkAxiom reason) {
		// does nothing by default
	}

	@Override
	public void reflexiveObjectPropertyRemoval(IndexedObjectProperty property,
			ElkAxiom reason) {
		// does nothing by default
	}

	@Override
	public void contextInitRuleHeadSet(LinkedContextInitRule rule) {
		// does nothing by default
	}

}
