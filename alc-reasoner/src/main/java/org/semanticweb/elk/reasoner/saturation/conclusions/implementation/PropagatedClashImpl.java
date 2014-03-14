package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

/*
 * #%L
 * ALC Reasoner
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

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.saturation.Root;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Clash;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PropagatedConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;

public class PropagatedClashImpl extends AbstractConclusion implements Clash,
		PropagatedConclusion {

	private final IndexedObjectProperty relation_;

	private final Root inconsistentRoot_;

	public PropagatedClashImpl(IndexedObjectProperty relation,
			Root inconsistentRoot) {
		this.relation_ = relation;
		this.inconsistentRoot_ = inconsistentRoot;
	}

	@Override
	public IndexedObjectProperty getRelation() {
		return this.relation_;
	}

	@Override
	public Root getSourceRoot() {
		return this.inconsistentRoot_;
	}

	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public String toString() {
		return "Propagated" + Clash.NAME + "(" + relation_ + " "
				+ inconsistentRoot_ + ")";
	}
}
