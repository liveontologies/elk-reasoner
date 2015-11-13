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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

/**
 * A common interface for inferences composing {@link BackwardLink} and
 * {@link ForwardLink} using {@link SubPropertyChain} premises.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface LinkComposition extends ClassInference {

	/**
	 * @return the {@link IndexedObjectProperty} that was composed on the left
	 */
	public IndexedObjectProperty getPremiseBackwardRelation();

	/**
	 * @return the {@link IndexedPropertyChain} that was composed on the right
	 */
	public IndexedPropertyChain getPremiseForwardChain();

	/**
	 * @return the {@link IndexedComplexPropertyChain} that is produced as the
	 *         result of the composition
	 */
	public IndexedComplexPropertyChain getComposition();

	/**
	 * @param factory
	 *            a factory for creating instances of {@link BackwardLink}
	 * 
	 * @return the {@link BackwardLink} used as premise in this
	 *         {@link LinkComposition}; its
	 *         {@link BackwardLink#getBackwardRelation()} equals to
	 *         {@link #getPremiseBackwardRelation()}
	 */
	public BackwardLink getFirstPremise(BackwardLink.Factory factory);

	/**
	 * @param factory
	 *            a factory for creating instances of {@link SubPropertyChain}
	 * 
	 * @return the {@link SubPropertyChain} used as premise of this
	 *         {@link LinkComposition}; its
	 *         {@link SubPropertyChain#getSubChain()} equals to
	 *         {@link #getPremiseBackwardRelation()}
	 */
	public SubPropertyChain getSecondPremise(SubPropertyChain.Factory factory);

	/**
	 * @param factory
	 *            a factory for creating instances of {@link ForwardLink}
	 * 
	 * @return the {@link ForwardLink} used as premise of this
	 *         {@link LinkComposition}; its
	 *         {@link ForwardLink#getForwardChain()} equals to
	 *         {@link #getPremiseForwardChain()}
	 */
	public ForwardLink getThirdPremise(ForwardLink.Factory factory);

	/**
	 * @param factory
	 *            a factory for creating instances of {@link SubPropertyChain}
	 * 
	 * @return the {@link SubPropertyChain} used as premise of this
	 *         {@link LinkComposition}; its
	 *         {@link SubPropertyChain#getSubChain()} equals to
	 *         {@link #getPremiseForwardChain()}
	 */
	public SubPropertyChain getFourthPremise(SubPropertyChain.Factory factory);

}
