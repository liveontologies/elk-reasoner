package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.LinkedContextInitRule;

/**
 * An implementation of {@link ContextInitialization}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContextInitializationImpl extends AbstractClassConclusion implements
		ContextInitialization {

	// actually we just need only context initialization rules,
	// but they can change after creating this object
	private final OntologyIndex ontologyIndex_;

	protected ContextInitializationImpl(IndexedContextRoot root,
			OntologyIndex ontologyIndex) {
		super(root);
		this.ontologyIndex_ = ontologyIndex;
	}	

	@Override
	public LinkedContextInitRule getContextInitRuleHead() {
		return ontologyIndex_.getContextInitRuleHead();
	}
	
	@Override
	public <I, O> O accept(ClassConclusion.Visitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public <I, O> O accept(ContextInitialization.Visitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
