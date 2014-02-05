package org.semanticweb.elk.reasoner.saturation;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * An implementation of {@link SaturationStateWriter} that just mirrors all
 * methods of the given {@link SaturationStateWriter}. This class can be used
 * for convenience if some methods of a {@link SaturationStateWriter} should be
 * redefined.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <W>
 *            the type of the wrapped {@link SaturationStateWriter}
 */
public class SaturationStateWriterWrap<W extends SaturationStateWriter>
		implements SaturationStateWriter {

	protected final W mainWriter;

	public SaturationStateWriterWrap(W mainWriter) {
		this.mainWriter = mainWriter;
	}

	@Override
	public void produce(IndexedClassExpression root, Conclusion conclusion) {
		mainWriter.produce(root, conclusion);
	}

	@Override
	public Context pollForActiveContext() {
		return mainWriter.pollForActiveContext();
	}

	@Override
	public boolean markAsNotSaturated(Context context) {
		return mainWriter.markAsNotSaturated(context);
	}

	@Override
	public void clearNotSaturatedContexts() {
		mainWriter.clearNotSaturatedContexts();
	}

	@Override
	public void resetContexts() {
		mainWriter.resetContexts();
	}

}
