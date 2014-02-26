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
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.SubConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.ContextInitRule;

/**
 * A {@link ConclusionInsertionVisitor} that additionally initializes
 * {@link Context}s and {@link SubContext}s to which {@link Conclusion}s are
 * inserted.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ConclusionInitializingInsertionVisitor extends
		ConclusionInsertionVisitor {

	/**
	 * The producer for {@link ContextInitialization}s and
	 * {@link SubContextInitialization}s
	 */
	private final ConclusionProducer producer_;

	/**
	 * The {@link Conclusion} used to initialize contexts using
	 * {@link ContextInitRule}s
	 */
	private final ContextInitialization contextInitConclusion_;

	public ConclusionInitializingInsertionVisitor(SaturationStateWriter writer) {
		super(writer);
		this.producer_ = writer;
		this.contextInitConclusion_ = new ContextInitialization(writer
				.getSaturationState().getOntologyIndex());
	}

	@Override
	Boolean defaultVisit(Conclusion conclusion, Context context) {
		if (!context.containsConclusion(contextInitConclusion_))
			producer_.produce(context.getRoot(), contextInitConclusion_);
		if (conclusion instanceof SubConclusion) {
			IndexedObjectProperty subRoot = ((SubConclusion) conclusion)
					.getSubRoot();
			SubConclusion subContextInit = new SubContextInitialization(subRoot);
			if (!context.containsConclusion(subContextInit))
				producer_.produce(context.getRoot(), subContextInit);
		}
		return super.defaultVisit(conclusion, context);
	}
}
