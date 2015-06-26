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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.SubConclusionVisitor;

/**
 * An implementation for {@link BackwardLink}
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
public class BackwardLinkImpl extends AbstractConclusion implements
		BackwardLink {

	/**
	 * the source {@link IndexedContextRoot} of this {@link BackwardLinkImpl};
	 * the root of the source implies this link.
	 */
	private final IndexedContextRoot source_;

	/**
	 * the {@link IndexedObjectProperty} in the existential restriction
	 * corresponding to this link
	 */
	private final IndexedObjectProperty relation_;

	public BackwardLinkImpl(IndexedContextRoot root, IndexedContextRoot source,
			IndexedObjectProperty relation) {
		super(root);
		this.relation_ = relation;
		this.source_ = source;
	}

	@Override
	public IndexedContextRoot getSourceRoot() {
		return source_;
	}

	@Override
	public IndexedObjectProperty getSubRoot() {
		return relation_;
	}

	@Override
	public String toString() {
		return (relation_ + "<-" + source_);
	}

	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return accept((SubConclusionVisitor<I, O>) visitor, input);
	}

	@Override
	public <I, O> O accept(SubConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public IndexedObjectProperty getRelation() {
		return relation_;
	}

	@Override
	public IndexedContextRoot getSource() {
		return source_;
	}

}
