package org.semanticweb.elk.reasoner.saturation;
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

/**
 * A {@link ClassExpressionSaturationListener} that does nothing with finished
 * jobs
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the input jobs of
 *            {@link ClassExpressionSaturationFactory}
 */
public class DummyClassExpressionSaturationListener<J extends SaturationJob<? extends IndexedClassExpression>>
		implements ClassExpressionSaturationListener<J> {

	@Override
	public void notifyFinished(J job) throws InterruptedException {
		// nothing to do
	}

}
