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
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore.Reader;

/**
 * Recursively visits all conclusions which were used to produce a given
 * conclusion. It can notify the caller if some visited conclusion has not been
 * traced (this is useful for testing).
 * 
 * TODO it's a bit kludgy that this class extends the recursive unwinder and
 * thus requires clients to pass visitors which return boolean (to know when
 * stop the recursion). Since this class never stops recursion, it's better to
 * encompass the recursive visitor and accept any visitors.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TestTraceUnwinder extends RecursiveTraceUnwinder {

	private final UntracedConclusionListener listener_;

	public TestTraceUnwinder(Reader reader, UntracedConclusionListener listener) {
		super(reader);
		listener_ = listener;
	}

	@Override
	protected void handleUntraced(Conclusion untraced, IndexedContextRoot root) {
		listener_.notifyUntraced(untraced, root);
	}

	@Override
	protected void handleUntraced(ObjectPropertyConclusion untraced) {
		listener_.notifyUntraced(untraced);
	}

}
