package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContextInitializationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.SubContextInitializationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A {@link ConclusionInsertionVisitor} that initializes the {@link Context} and
 * the {@link SubContext} if it is the first conclusion inserted there.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ContextInitializingConclusionInsertionVisitor extends
		ConclusionInsertionVisitor {

	/**
	 * The producer for {@link ContextInitialization}s and
	 * {@link SubContextInitialization}s
	 */
	private final ConclusionProducer producer_;

	private final OntologyIndex index_;

	public ContextInitializingConclusionInsertionVisitor(
			SaturationStateWriter<?> writer) {
		super(writer);
		this.producer_ = writer;
		this.index_ = writer.getSaturationState().getOntologyIndex();
	}

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context context) {
		IndexedContextRoot root = context.getRoot();
		if (context.isEmpty()) {
			producer_.produce(new ContextInitializationImpl(root, index_));
		}
		if (conclusion instanceof SubConclusion) {
			IndexedObjectProperty subRoot = ((SubConclusion) conclusion)
					.getConclusionSubRoot();
			if (context.isEmpty(subRoot)) {
				producer_.produce(new SubContextInitializationImpl(root,
						subRoot));
			}
		}
		return super.defaultVisit(conclusion, context);
	}
}
