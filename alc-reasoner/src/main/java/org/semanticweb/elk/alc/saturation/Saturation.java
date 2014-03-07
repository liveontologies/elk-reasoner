package org.semanticweb.elk.alc.saturation;
/*
 * #%L
 * ALC Reasoner
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

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContextInitializationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;

public class Saturation {

	private final static Conclusion CONTEXT_INIT_ = new ContextInitializationImpl();

	private final SaturationState saturationState_;

	private final ConclusionVisitor<Context, Void> ruleApplicationVisitor_;

	public Saturation(SaturationState saturationState) {
		this.saturationState_ = saturationState;
		this.ruleApplicationVisitor_ = new RuleApplicationVisitor(
				saturationState);
	}

	public void submit(IndexedClassExpression expression) {
		Root root = new Root(expression);
		saturationState_.produce(root, CONTEXT_INIT_);
	}

	public void process() {
		for (;;) {
			Context context = saturationState_.pollActiveContext();
			if (context == null)
				return;
			for (;;) {
				Conclusion unprocessed = context.takeToDo();
				if (unprocessed == null)
					break;
				if (context.addConclusion(unprocessed))
					unprocessed.accept(ruleApplicationVisitor_, context);
			}
		}
	}

}
