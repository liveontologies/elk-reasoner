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
package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubClassConclusion;

/**
 * An implementation of {@link BackwardLink}
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
public class BackwardLinkImpl extends AbstractSubClassConclusion implements
		BackwardLink {

	/**
	 * the source {@link IndexedContextRoot} of this {@link BackwardLinkImpl};
	 * the root of the source implies this link.
	 */
	private final IndexedContextRoot origin_;

	protected BackwardLinkImpl(IndexedContextRoot root,
			IndexedObjectProperty relation, IndexedContextRoot source) {
		super(root, relation);
		this.origin_ = source;
	}

	@Override
	public IndexedContextRoot getOriginRoot() {
		return origin_;
	}

	@Override
	public IndexedObjectProperty getOriginSubRoot() {
		return null;
	}

	@Override
	public IndexedObjectProperty getBackwardRelation() {
		return getConclusionSubRoot();
	}

	@Override
	public <I, O> O accept(ClassConclusion.Visitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public <I, O> O accept(SubClassConclusion.Visitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}	

	@Override
	public <I, O> O accept(BackwardLink.Visitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
