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
package org.semanticweb.elk.reasoner.incremental;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.rules.RuleApplicationEngine;
import org.semanticweb.elk.reasoner.rules.RuleApplicationListener;
import org.semanticweb.elk.reasoner.rules.SaturatedClassExpression;

public class RuleCleaningEngine extends RuleApplicationEngine {

	public RuleCleaningEngine(OntologyIndex ontologyIndex,
			RuleApplicationListener listener) {
		super(ontologyIndex, listener, true, false);
	}

	public RuleCleaningEngine(OntologyIndex ontologyIndex) {
		this(ontologyIndex, new RuleApplicationListener() {
			public void notifyCanProcess() {

			}
		});
	}

	void processContextCleaning(SaturatedClassExpression context) {
		initContext(context);
	}
}
