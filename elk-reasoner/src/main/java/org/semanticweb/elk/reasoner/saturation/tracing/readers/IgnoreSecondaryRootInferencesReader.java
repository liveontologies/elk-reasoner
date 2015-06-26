/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.readers;

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

import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.DelegatingTraceReader;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;

/**
 * Ignores all inferences for the root of a context (except of the
 * initialization) when reading the trace.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IgnoreSecondaryRootInferencesReader extends DelegatingTraceReader {

	public IgnoreSecondaryRootInferencesReader(TraceStore.Reader r) {
		super(r);
	}

	@Override
	public void accept(final Conclusion inferredConclusion,
			final ClassInferenceVisitor<IndexedContextRoot, ?> visitor) {
		reader.accept(
				inferredConclusion,
				new AbstractClassInferenceVisitor<IndexedContextRoot, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(
							ClassInference conclusion,
							IndexedContextRoot contextRoot) {
						conclusion.acceptTraced(visitor, null);

						return true;
					}

					@Override
					public Boolean visit(SubClassOfSubsumer<?> conclusion,
							IndexedContextRoot contextRoot) {
						if (conclusion.getExpression() != contextRoot) {
							defaultTracedVisit(conclusion, contextRoot);
						}

						return true;
					}

					@Override
					public Boolean visit(ComposedConjunction conclusion,
							IndexedContextRoot contextRoot) {
						if (conclusion.getExpression() != contextRoot) {
							defaultTracedVisit(conclusion, contextRoot);
						}

						return true;
					}

					@Override
					public Boolean visit(DecomposedConjunction conclusion,
							IndexedContextRoot contextRoot) {
						if (conclusion.getExpression() != contextRoot) {
							defaultTracedVisit(conclusion, contextRoot);
						}

						return true;
					}

					@Override
					public Boolean visit(PropagatedSubsumer conclusion,
							IndexedContextRoot contextRoot) {
						if (conclusion.getExpression() != contextRoot) {
							defaultTracedVisit(conclusion, contextRoot);
						}

						return true;
					}

				});
	}

}
