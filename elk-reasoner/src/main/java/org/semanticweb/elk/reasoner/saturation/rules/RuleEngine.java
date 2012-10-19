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
import org.semanticweb.elk.reasoner.indexing.hierarchy.RuleStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionsCounter;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A common interface for engines used in the saturation for
 * {@link IndexedClassExpression}s.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface RuleEngine {

	/**
	 * @return the {@link IndexedClassExpression} corresponding to
	 *         {@code owl:Nothing}.
	 */
	public IndexedClassExpression getOwlNothing();

	/**
	 * Return the {@link Context} which has the given
	 * {@link IndexedClassExpression} as the root. In case no such context
	 * exists, a new one is created with the given root and is returned. It is
	 * ensured that no two different {@link Context}s are created with the same
	 * root.
	 * 
	 * @param root
	 *            the root of the {@link Context} that should be returned by
	 *            this method
	 * @return the {@link Context} with the given root
	 * 
	 */
	public Context getCreateContext(IndexedClassExpression root);

	/**
	 * Produce a {@link Conclusion} in the given {@link Context}, which will be
	 * further processed by this {@link RuleEngine}.
	 * 
	 * @param context
	 *            the {@link Context} for which the {@link Conclusion} has been
	 *            produced
	 * @param conclusion
	 *            the {@link Conclusion} produced in the {@link Context}
	 * 
	 */
	public void produce(Context context, Conclusion conclusion);

	/**
	 * @return the {@link ConclusionsCounter} of this {@link RuleEngine}.
	 */
	public ConclusionsCounter getConclusionsCounter();

	/**
	 * @return the {@link ConclusionsCounter} of this {@link RuleEngine}.
	 */
	public RuleStatistics getRulesTimer();
}
