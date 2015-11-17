/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;

/**
 * A {@link BackwardLink} obtained from a {@link ForwardLink} with
 * {@link IndexedObjectProperty} chain.
 * 
 * @see ForwardLink#getForwardChain()
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class BackwardLinkReversed extends AbstractBackwardLinkInference {

	/**
	 * 
	 */
	public BackwardLinkReversed(ForwardLink premise) {
		super(premise.getTarget(), (IndexedObjectProperty) premise
				.getForwardChain(), premise.getConclusionRoot());
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getOriginRoot();
	}

	public ForwardLink getPremise(ForwardLink.Factory factory) {
		return factory.getForwardLink(getInferenceRoot(), getBackwardRelation(),
				getConclusionRoot());
	}

	@Override
	public <O> O accept(BackwardLinkInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}
	
	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O> {
		
		public O visit(BackwardLinkReversed inference);
		
	}

}
