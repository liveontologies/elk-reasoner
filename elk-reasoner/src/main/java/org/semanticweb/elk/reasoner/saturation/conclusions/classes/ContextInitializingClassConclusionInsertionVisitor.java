package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.Reference;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.InitializationConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;

/**
 * A {@link ClassConclusionInsertionVisitor} that initializes the
 * {@link Context} provided in the {@link Reference} and its corresponding
 * {@link SubContext} if the visited {@link ClassConclusion} is the first one
 * inserted there.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ContextInitializingClassConclusionInsertionVisitor
		extends
			ClassConclusionInsertionVisitor {

	/**
	 * The producer for {@link ContextInitialization}s and
	 * {@link SubContextInitialization}s
	 */
	private final ClassConclusionProducer producer_;

	private final OntologyIndex index_;

	private final InitializationConclusion.Factory factory_;

	public ContextInitializingClassConclusionInsertionVisitor(
			Reference<Context> contextRef,
			InitializationConclusion.Factory factory,
			SaturationStateWriter<?> writer) {
		super(contextRef, writer);
		this.producer_ = writer;
		this.index_ = writer.getSaturationState().getOntologyIndex();
		this.factory_ = factory;
	}

	public ContextInitializingClassConclusionInsertionVisitor(
			Reference<Context> contextRef, SaturationStateWriter<?> writer) {
		this(contextRef, new SaturationConclusionBaseFactory(), writer);
	}

	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion) {
		Context context = get();
		IndexedContextRoot root = context.getRoot();
		if (context.isEmpty()) {
			producer_.produce(factory_.getContextInitialization(root, index_));
		}
		if (conclusion instanceof SubClassConclusion) {
			IndexedObjectProperty subRoot = ((SubClassConclusion) conclusion)
					.getConclusionSubRoot();
			if (context.isEmpty(subRoot)) {
				producer_.produce(
						factory_.getSubContextInitialization(root, subRoot));
			}
		}
		return super.defaultVisit(conclusion);
	}
}
