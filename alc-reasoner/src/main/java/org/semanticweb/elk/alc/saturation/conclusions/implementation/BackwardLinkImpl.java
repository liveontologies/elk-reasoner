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
package org.semanticweb.elk.alc.saturation.conclusions.implementation;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.saturation.Root;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.alc.saturation.conclusions.visitors.ExternalDeterministicConclusionVisitor;

/**
 * An implementation for {@link BackwardLink}
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
public class BackwardLinkImpl extends AbstractExternalDeterministicConclusion
		implements BackwardLink {

	/**
	 * the source {@link Root} of this {@link BackwardLinkImpl}; the root of the
	 * source implies this link.
	 */
	private final Root source_;

	/**
	 * the {@link IndexedObjectProperty} in the existential restriction
	 * corresponding to this link
	 */
	private final IndexedObjectProperty relation_;

	public BackwardLinkImpl(Root source, IndexedObjectProperty relation) {
		this.relation_ = relation;
		this.source_ = source;
	}

	@Override
	public IndexedObjectProperty getRelation() {
		return relation_;
	}

	@Override
	public Root getSource() {
		return source_;
	}

	@Override
	public <I, O> O accept(
			ExternalDeterministicConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public Root getSourceRoot(Root forWhicProduced) {
		return source_;
	}

	@Override
	public String toString() {
		return BackwardLink.NAME + "(" + relation_ + " " + source_ + ")";
	}

}
