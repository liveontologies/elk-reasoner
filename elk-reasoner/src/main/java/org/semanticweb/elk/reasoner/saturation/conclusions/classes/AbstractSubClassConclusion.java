package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;

/**
 * A skeleton for implementation of {@link SubClassConclusion}s.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public abstract class AbstractSubClassConclusion extends AbstractClassConclusion
		implements SubClassConclusion {

	private final IndexedObjectProperty subRoot_;

	protected AbstractSubClassConclusion(IndexedContextRoot root,
			IndexedObjectProperty subRoot) {
		super(root);
		this.subRoot_ = subRoot;
	}

	@Override
	public IndexedObjectProperty getConclusionSubRoot() {
		return this.subRoot_;
	}

	@Override
	public IndexedObjectProperty getOriginSubRoot() {
		return this.subRoot_;
	}

	@Override
	public <I, O> O accept(ClassConclusion.Visitor<I, O> visitor, I input) {
		return accept((SubClassConclusion.Visitor<I, O>) visitor, input);
	}
	
}
