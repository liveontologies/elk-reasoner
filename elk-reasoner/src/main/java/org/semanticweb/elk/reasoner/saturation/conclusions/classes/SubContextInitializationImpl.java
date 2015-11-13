package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;

/**
 * An implementation of {@link SubContextInitialization}
 * 
 * @author "Yevgeny Kazakov"
 */
public class SubContextInitializationImpl extends AbstractSubClassConclusion
		implements SubContextInitialization {

	protected SubContextInitializationImpl(IndexedContextRoot root,
			IndexedObjectProperty subRoot) {
		super(root, subRoot);
	}
	
	@Override
	public <I, O> O accept(SubClassConclusion.Visitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public <I, O> O accept(SubContextInitialization.Visitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}