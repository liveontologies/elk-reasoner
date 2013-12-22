/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.LocalSaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.util.collections.Condition;
import org.semanticweb.elk.util.collections.Operations;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TracingSaturationState extends LocalSaturationState {

	public TracingSaturationState(OntologyIndex index) {
		super(index);
	}

	@Override
	public BasicSaturationStateWriter getWriter(
			ConclusionVisitor<?, Context> conclusionVisitor) {
		return getExtendedWriter(conclusionVisitor,
				SaturationState.DEFAULT_INIT_RULE_APP_VISITOR);
	}

	@Override
	public ExtendedSaturationStateWriter getExtendedWriter(
			ConclusionVisitor<?, Context> conclusionVisitor,
			CompositionRuleApplicationVisitor initRuleAppVisitor) {
		return new TracingWriter(conclusionVisitor, initRuleAppVisitor,
				new Condition<Context>() {
					// by default trace everything
					@Override
					public boolean holds(Context element) {
						return true;
					}
				});
	}

	public TracingWriter getTracingWriter(
			ConclusionVisitor<?, Context> conclusionVisitor,
			CompositionRuleApplicationVisitor initRuleAppVisitor,
			Condition<Context> traceCondition) {
		return new TracingWriter(conclusionVisitor, initRuleAppVisitor,
				traceCondition);
	}

	/**
	 * 
	 * @param context
	 * @return true if the context has been traced
	 */
	public boolean isTraced(Context context) {
		Context localContext = getContext(context.getRoot());

		return localContext != null && localContext.isSaturated();
	}

	public Iterable<Context> getTracedContexts() {
		return Operations.filter(getContexts(), new Condition<Context>() {

			@Override
			public boolean holds(Context cxt) {
				return cxt.isSaturated();
			}
		});
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class TracingWriter extends LocalSaturationState.LocalWriter {

		private final Condition<Context> traceCondition_;

		public TracingWriter(ConclusionVisitor<?, Context> visitor,
				CompositionRuleApplicationVisitor ruleAppVisitor,
				Condition<Context> traceCondition) {
			super(visitor, ruleAppVisitor, new TracingConclusionFactory());
			traceCondition_ = traceCondition;
		}

		@Override
		public void produceLocally(Context context, Conclusion conclusion) {
			Context sourceContext = conclusion.getSourceContext(context);
			// no need to iterate over conclusions which belong to non-traced
			// contexts
			if (traceCondition_.holds(sourceContext)) {
				super.produceLocally(context, conclusion);
			}
		}
	}
}
