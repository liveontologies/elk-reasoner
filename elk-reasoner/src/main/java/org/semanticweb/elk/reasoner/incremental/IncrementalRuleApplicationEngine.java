/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner.incremental;

import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpressionChange;
import org.semanticweb.elk.reasoner.rules.RuleApplicationEngine;
import org.semanticweb.elk.reasoner.rules.SaturatedClassExpression;
import org.semanticweb.elk.util.collections.LazySetIntersection;

/**
 * The rule application engine used for updating saturation after incremental
 * changes in the ontology index. This engine can be used both for de-applying
 * inferences and for re-applying inferences. De-application of inferences is
 * typically done after removal of the entries from the ontology index and takes
 * the removed part of the index changes into account. Re-application of
 * inferences is done after additions of entries to the ontology indexed and,
 * likewise, takes the addition part of the index changes into account.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class IncrementalRuleApplicationEngine extends RuleApplicationEngine {

	/**
	 * The map assigning relevant index changes (deletions or additions) to
	 * indexed class expressions in the ontology.
	 */
	final Map<IndexedClassExpression, IndexedClassExpressionChange> indexChanges;

	/**
	 * Creating the rule application engine for a given ontology, listener, and
	 * the flags for deletion and modification modes.
	 * 
	 * @param ontologyIndex
	 *            the index used to apply the inference rules of this engine
	 * @param listener
	 *            the listener for context modifications
	 * @param deletionMode
	 *            if <tt>true</tt> the rules will delete the conclusions of the
	 *            inferences from saturations instead of adding them; in this
	 *            case, the inferences will take into account only the deletion
	 *            part of the index change.
	 * 
	 * @param modificationMode
	 *            if <tt>true</tt>, inferences applied to saturated contexts
	 *            will be performed and such contexts will be marked as not
	 *            saturated and reported to the listener
	 */
	public IncrementalRuleApplicationEngine(OntologyIndex ontologyIndex,
			ContextModificationListener listener, boolean deletionMode,
			boolean modificationMode) {
		super(ontologyIndex, listener, deletionMode, modificationMode);
		indexChanges = deletionMode ? ontologyIndex.getIndexChange()
				.getIndexDeletions() : ontologyIndex.getIndexChange()
				.getIndexAdditions();
	}

	/**
	 * Initialize inferences for the index changes that are applicable to the
	 * given context; the conclusions of relevant inferences will be added to
	 * the relevant context queues, so that the can be processed during
	 * saturations when calling {@link #process()}.
	 * 
	 * @param context
	 *            the context for which to initialize inferences for the index
	 *            changes
	 */
	protected void initChanges(SaturatedClassExpression context) {
		final QueueProcessor queueProcessor = new QueueProcessor(context);

		for (IndexedClassExpression changedIndexedClassExpression : new LazySetIntersection<IndexedClassExpression>(
				indexChanges.keySet(), context.getSuperClassExpressions())) {
			IndexedClassExpressionChange change = indexChanges
					.get(changedIndexedClassExpression);
			queueProcessor.processClass(change);
		}
	}

	protected void initContext(SaturatedClassExpression context) {
		super.initContext(context);
	}
}
