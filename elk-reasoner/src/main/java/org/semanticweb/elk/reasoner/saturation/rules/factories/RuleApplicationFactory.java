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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * A common interface for factories for worker engines that process a
 * {@link SaturationState} in parallel. Each engine has an exclusive read-write
 * access to the {@link Context} of the {@link SaturationState} in which the
 * current {@link Conclusion} is processed, so it can modify this
 * {@link Context} and apply the rules using conclusions saved in the
 * {@link Context}, which can possibly produce {@link Conclusion}s for other
 * {@link Context}s.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface RuleApplicationFactory {

	/**
	 * @return the {@link SaturationState} with which this
	 *         {@link RuleApplicationFactory} is working.
	 */
	public SaturationState getSaturationState();

	/**
	 * Create a new {@link InputProcessor} that can perform computations for
	 * input {@link IndexedClassExpression}s within a working thread, typically,
	 * computing the closure under the rules for the {@link Context}s
	 * initialized with the given {@link IndexedClassExpression}s. Since the
	 * {@link SaturationState} is shared by all workers, the computation is
	 * finished only when all concurrent workers finish the processing.
	 * 
	 * @param creationListener
	 *            the {@link ContextCreationListener} that registers all
	 *            {@link Context}s created by this {@link InputProcessor}
	 * @param modificationListener
	 *            the {@link ContextModificationListener} that registers all
	 *            {@link Context}s that become not saturated by operations of
	 *            this {@link InputProcessor}
	 * @return the new {@link InputProcessor} for {@link IndexedClassExpression}
	 *         that can perform computation in parallel.
	 */
	public InputProcessor<IndexedClassExpression> getEngine(
			ContextCreationListener creationListener,
			ContextModificationListener modificationListener);

	/**
	 * @return {@link SaturationStatistics} aggregating the statistics for all
	 *         engines of this {@link RuleApplicationFactory}
	 */
	public SaturationStatistics getSaturationStatistics();

	/**
	 * free the resources used by this {@link RuleApplicationFactory}
	 */
	public void dispose();

}
