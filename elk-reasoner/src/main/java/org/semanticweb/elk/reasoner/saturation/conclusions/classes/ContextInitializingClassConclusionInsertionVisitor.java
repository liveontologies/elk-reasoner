package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

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

import org.semanticweb.elk.Reference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;
import org.semanticweb.elk.reasoner.saturation.inferences.ContextInitializationNoPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.SubContextInitializationNoPremises;

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
	private final SaturationStateWriter<?> writer_;

	public ContextInitializingClassConclusionInsertionVisitor(
			SaturationStateWriter<?> writer) {
		super(writer);
		this.writer_ = writer;
	}

	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion) {
		IndexedContextRoot root = conclusion.getDestination();
		Context context = writer_.getSaturationState().getContext(root);
		if (context.isEmpty()) {
			writer_.produce(new ContextInitializationNoPremises(root));
		}
		if (conclusion instanceof SubClassConclusion) {
			IndexedObjectProperty subRoot = ((SubClassConclusion) conclusion)
					.getSubDestination();
			if (context.isEmpty(subRoot)) {
				writer_.produce(
						new SubContextInitializationNoPremises(root, subRoot));
			}
		}
		return super.defaultVisit(conclusion);
	}
}
