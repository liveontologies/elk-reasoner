package org.semanticweb.elk.reasoner.saturation;

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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * An implementation of {@link SaturationStateWriter} that just mirrors all
 * methods of the given {@link SaturationStateWriter}. This class can be used
 * for convenience if some methods of a {@link SaturationStateWriter} should be
 * redefined.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SaturationStateWriterWrap<C extends Context> implements
		SaturationStateWriter<C> {

	protected final SaturationStateWriter<? extends C> mainWriter;

	public SaturationStateWriterWrap(
			SaturationStateWriter<? extends C> mainWriter) {
		this.mainWriter = mainWriter;
	}

	@Override
	public void produce(ClassConclusion conclusion) {
		mainWriter.produce(conclusion);
	}

	@Override
	public Context pollForActiveContext() {
		return mainWriter.pollForActiveContext();
	}

	@Override
	public boolean markAsNotSaturated(IndexedContextRoot root) {
		return mainWriter.markAsNotSaturated(root);
	}

	@Override
	public void resetContexts() {
		mainWriter.resetContexts();
	}

	@Override
	public SaturationState<? extends C> getSaturationState() {
		return mainWriter.getSaturationState();
	}

}
