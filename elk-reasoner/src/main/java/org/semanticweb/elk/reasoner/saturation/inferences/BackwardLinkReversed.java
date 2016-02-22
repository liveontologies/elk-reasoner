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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;

/**
 * A {@link ClassInference} producing a {@link BackwardLink} from a
 * {@link ForwardLink} with an {@link IndexedObjectProperty} chain:<br>
 * 
 * <pre>
 *  [C] ⊑ <∃R>.D
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *  C ⊑ <∃R>.[D]
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getOrigin()} = {@link #getSource()} <br>
 * R = {@link #getRelation()}<br>
 * D = {@link #getDestination()}<br>
 * 
 * @see ForwardLink#getRelation()
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class BackwardLinkReversed extends AbstractBackwardLinkInference {

	public BackwardLinkReversed(ForwardLink premise) {
		super(premise.getTarget(),
				(IndexedObjectProperty) premise.getRelation(),
				premise.getDestination());
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getTraceRoot();
	}

	public ForwardLink getPremise(ForwardLink.Factory factory) {
		return factory.getForwardLink(getOrigin(), getRelation(),
				getDestination());
	}

	@Override
	public final <O> O accept(BackwardLinkInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	public static interface Visitor<O> {

		public O visit(BackwardLinkReversed inference);

	}

}
