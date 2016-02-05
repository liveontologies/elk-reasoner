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

import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link ClassConclusion.Visitor} that marks the {@link Context} for the root
 * returned by {@link ClassConclusion#getTraceRoot()} for the visited
 * {@link ClassConclusion}s as not saturated if the {@link ClassConclusion} can
 * potentially be re-derived. The visit method returns always {@link true}.
 * 
 * @see ClassConclusion#getTraceRoot()
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ClassConclusionTracingContextUnsaturationVisitor
		extends
			DummyClassConclusionVisitor<Boolean> {

	private final SaturationStateWriter<?> writer_;

	public ClassConclusionTracingContextUnsaturationVisitor(
			SaturationStateWriter<?> writer) {
		this.writer_ = writer;
	}

	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion) {
		writer_.markAsNotSaturated(conclusion.getTraceRoot());
		return true;
	}

	Boolean defaultVisit(SubClassInclusion conclusion) {
		// if the super-class does not occur in the ontology anymore, it cannot
		// be
		// re-derived, and thus, the context should not be modified
		// TODO: extend this check to other types of conclusions
		if (conclusion.getSubsumer().occurs()) {
			return defaultVisit((ClassConclusion) conclusion);
		}
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionComposed conclusion) {
		return defaultVisit(conclusion);
	}

	@Override
	public Boolean visit(SubClassInclusionDecomposed conclusion) {
		return defaultVisit(conclusion);
	}
}
