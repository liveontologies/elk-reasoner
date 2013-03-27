/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Creates an engine which works as the de-application engine except that it
 * doesn't modify saturated contexts. The engine is used to "clean" contexts
 * after de-application but if the context is saturated, then cleaning is
 * unnecessary because it's not going to get any extra super-classes after
 * re-application.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ContextCleaningFactory extends RuleDeapplicationFactory {

	public ContextCleaningFactory(final SaturationState saturationState) {
		super(saturationState, false);
	}

	@Override
	public DeapplicationEngine getDefaultEngine(
			ContextCreationListener listener,
			ContextModificationListener modificationListener) {
		return new CleaningEngine();
	}

	/**
	 * A {@link RuleDeapplicationFactory} that its own saturation state riter that does not produce conclusions if
	 * their source is marked as saturated.
	 */
	public class CleaningEngine extends
			RuleDeapplicationFactory.DeapplicationEngine {

		protected CleaningEngine() {
			super(ContextModificationListener.DUMMY);
		}

		@Override
		protected BasicSaturationStateWriter getSaturationStateWriter() {
			ConclusionVisitor<?> visitor = getEngineConclusionVisitor(localStatistics
					.getConclusionStatistics());
			BasicSaturationStateWriter writer = saturationState.getWriter(
					ContextModificationListener.DUMMY, visitor);

			return new SaturationCheckingWriter(writer);
		}
	}

	/**
	 * A writer that does not produce conclusions if their source
	 * context is already saturated.
	 * 
	 * @author Pavel Klinov
	 * 
	 */
	private static class SaturationCheckingWriter implements
			BasicSaturationStateWriter {

		private final BasicSaturationStateWriter writer_;

		SaturationCheckingWriter(BasicSaturationStateWriter writer) {
			writer_ = writer;
		}

		@Override
		public void produce(Context context, Conclusion conclusion) {
			Context sourceContext = conclusion.getSourceContext(context);

			if (sourceContext == null || !sourceContext.isSaturated()) {
				writer_.produce(context, conclusion);
			}
		}

		@Override
		public IndexedClassExpression getOwlThing() {
			return writer_.getOwlThing();
		}

		@Override
		public IndexedClassExpression getOwlNothing() {
			return writer_.getOwlNothing();
		}

		@Override
		public Context pollForActiveContext() {
			return writer_.pollForActiveContext();
		}

		@Override
		public boolean markAsNotSaturated(Context context) {
			return writer_.markAsNotSaturated(context);
		}

		@Override
		public void clearNotSaturatedContexts() {
			writer_.clearNotSaturatedContexts();
		}

		@Override
		public void resetContexts() {
			writer_.resetContexts();
		}
	}
}
