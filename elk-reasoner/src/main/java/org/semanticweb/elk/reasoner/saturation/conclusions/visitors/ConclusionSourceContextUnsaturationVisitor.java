package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link ConclusionVisitor} that marks the source {@link Context} of the
 * {@link Conclusion} as not saturated if the {@link Conclusion} can potentially
 * be re-derived. The visit method returns always {@link true}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ConclusionSourceContextUnsaturationVisitor extends
		AbstractConclusionVisitor<Context, Boolean> {

	private final SaturationStateWriter<?> writer_;

	public ConclusionSourceContextUnsaturationVisitor(
			SaturationStateWriter<?> writer) {
		this.writer_ = writer;
	}

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context context) {
		IndexedContextRoot root = conclusion.getSourceRoot(context.getRoot());
		writer_.markAsNotSaturated(root);
		return true;
	}

	Boolean defaultVisit(Subsumer<?> conclusion, Context context) {
		// if the subsumer does not occur in the ontology anymore, it cannot be
		// re-derived, and thus, the context should not be modified
		// TODO: extend this check to other types of conclusions
		if (conclusion.getExpression().occurs())
			return defaultVisit((Conclusion) conclusion, context);
		return true;
	}

	@Override
	public Boolean visit(ComposedSubsumer<?> conclusion, Context context) {
		return defaultVisit(conclusion, context);
	}

	@Override
	public Boolean visit(DecomposedSubsumer<?> conclusion, Context context) {
		return defaultVisit(conclusion, context);
	}
}
