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

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ConclusionVisitor} that localizes the input {@link Context} within
 * the given {@link SaturationState} and passes it to the given internal
 * {@link ConclusionVisitor}. Localization means that the {@link Context} is
 * converted to the corresponding {@link Context} within the given
 * {@link SaturationState} (i.e., with the same root). If the localized context
 * does not exist, the visitor returns {@code false}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class LocalizedConclusionVisitor extends
		AbstractConclusionVisitor<Context, Boolean> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(LocalizedConclusionVisitor.class);

	/**
	 * the {@link ConclusionVisitor} to be localized
	 */
	ConclusionVisitor<Context, Boolean> visitor_;

	/**
	 * the {@link SaturationState} used to localize {@link Context}s
	 */
	SaturationState<?> state_;

	public LocalizedConclusionVisitor(
			ConclusionVisitor<Context, Boolean> visitor,
			SaturationState<?> state) {
		this.visitor_ = visitor;
		this.state_ = state;
	}

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context input) {
		Context localContext = state_.getContext(input.getRoot());
		if (localContext == null) {
			LOGGER_.trace("{}: local context does not exist", input);
			return false;
		}
		return conclusion.accept(visitor_, localContext);
	}
}
