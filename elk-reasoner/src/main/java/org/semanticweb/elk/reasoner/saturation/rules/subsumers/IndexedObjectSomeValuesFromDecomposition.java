package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ForwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A {@link SubsumerDecompositionRule} that processes
 * {@link IndexedObjectSomeValuesFrom} and produces the corresponding
 * {@link BackwardLink}s in the context corresponding to its filler (or range
 * filler) pointing to the {@link Context} in which the {@link Conclusion} was
 * processed using its relation
 * 
 * @see IndexedObjectSomeValuesFrom#getFiller()
 * @see IndexedObjectSomeValuesFrom#getRangeFiller()
 * @see IndexedObjectSomeValuesFrom#getProperty()
 * 
 * @author "Yevgeny Kazakov"
 */
public class IndexedObjectSomeValuesFromDecomposition extends
		AbstractSubsumerDecompositionRule<IndexedObjectSomeValuesFrom> {

	public static final String NAME = "IndexedObjectSomeValuesFrom Decomposition";

	private static SubsumerDecompositionRule<IndexedObjectSomeValuesFrom> INSTANCE_ = new IndexedObjectSomeValuesFromDecomposition();

	public static SubsumerDecompositionRule<IndexedObjectSomeValuesFrom> getInstance() {
		return INSTANCE_;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void apply(IndexedObjectSomeValuesFrom premise,
			ContextPremises premises, ConclusionProducer producer) {
		ForwardLinkImpl.produceDecomposedExistentialLink(producer,
				premises.getRoot(), premise);
	}

	@Override
	public void accept(SubsumerDecompositionRuleVisitor visitor,
			IndexedObjectSomeValuesFrom premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

}
