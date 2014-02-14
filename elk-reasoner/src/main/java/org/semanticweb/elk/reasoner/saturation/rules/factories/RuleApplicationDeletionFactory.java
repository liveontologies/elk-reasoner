/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules.factories;

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
import org.semanticweb.elk.reasoner.saturation.ContextExistenceCheckingWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AllRuleApplicationConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ComposedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionDeletionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionOccurrenceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionSourceContextUnsaturationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * A {@link RuleApplicationFactory} that deletes the produced {@link Conclusion}
 * s from the respective {@link Context} and applies all (redundant and
 * non-redundant) rules, which in turn produce {@link Conclusion} s for which
 * this process repeats if they have not been processed already. This
 * {@link RuleApplicationFactory} never creates new {@link Context}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class RuleApplicationDeletionFactory extends
		AbstractRuleApplicationFactory implements RuleApplicationFactory {

	public RuleApplicationDeletionFactory(SaturationState saturationState) {
		super(saturationState);
	}

	@Override
	public InputProcessor<IndexedClassExpression> getEngine(
			RuleVisitor ruleVisitor, SaturationStateWriter writer,
			SaturationStatistics localStatistics) {
		writer = wrapWriter(writer);
		return super.getEngine(getConclusionProcessor(ruleVisitor, writer),
				writer, localStatistics);
	}

	SaturationStateWriter wrapWriter(SaturationStateWriter writer) {
		// only write to exiting contexts
		return new ContextExistenceCheckingWriter(writer, getSaturationState());
	}

	/**
	 * @param ruleVisitor
	 *            A {@link RuleVisitor} used for rule application
	 * @param writer
	 *            A {@link SaturationStateWriter} to be used for rule
	 *            applications
	 * @return {@link ConclusionVisitor} that applies all rules to the
	 *         {@link Conclusion} if it occurs in the {@link Context}, and
	 *         deletes this {@link Conclusion} from the {@link Context}
	 */
	@SuppressWarnings("unchecked")
	static ConclusionVisitor<Context, Boolean> getConclusionProcessor(
			RuleVisitor ruleVisitor, SaturationStateWriter writer) {
		return new ComposedConclusionVisitor<Context>(
		// check if conclusion occurs in the context
				new ConclusionOccurrenceCheckingVisitor(),
				// if so, apply all rules, including those that are
				// redundant
				new AllRuleApplicationConclusionVisitor(ruleVisitor, writer),
				// after processing, delete the conclusion
				new ConclusionDeletionVisitor(),
				// and mark the source context of the conclusion as
				// non-saturated
				new ConclusionSourceContextUnsaturationVisitor(writer));
	}

}
