package org.semanticweb.elk.reasoner.saturation.rules.factories;

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

import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriterWrap;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link SaturationStateWriter} that produces {@link Conclusions} to the
 * {@link WorkerLocalTodo} if they are produced for the root of the currently
 * processed {@link Context} as determined by
 * {@link WorkerLocalTodo#getActiveRoot()}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class WorkerLocalizedSaturationStateWriter<C extends Context> extends
		SaturationStateWriterWrap<C> {

	private final WorkerLocalTodo localTodo_;

	public WorkerLocalizedSaturationStateWriter(
			SaturationStateWriter<? extends C> mainWriter,
			WorkerLocalTodo localTodo) {
		super(mainWriter);
		this.localTodo_ = localTodo;
	}

	@Override
	public void produce(ClassConclusion conclusion) {
		if (localTodo_.isActivated()
				&& conclusion.getConclusionRoot() == localTodo_.getActiveRoot()) {
			localTodo_.add(conclusion);
		} else {
			mainWriter.produce(conclusion);
		}
	}

	/**
	 * @return the {@link WorkerLocalTodo} to which this
	 *         {@link WorkerLocalizedSaturationStateWriter} produces the
	 *         {@link Conclusions} for the current {@link Context}
	 */
	WorkerLocalTodo getLocalTodo() {
		return localTodo_;
	}

}
