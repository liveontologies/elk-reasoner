package org.semanticweb.elk.reasoner.incremental;

import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpressionChange;
import org.semanticweb.elk.reasoner.rules.RuleApplicationEngine;
import org.semanticweb.elk.reasoner.rules.RuleApplicationListener;
import org.semanticweb.elk.reasoner.rules.SaturatedClassExpression;
import org.semanticweb.elk.util.collections.LazySetIntersection;

public class RuleUnApplicationEngine extends RuleApplicationEngine {

	final Map<IndexedClassExpression, IndexedClassExpressionChange> indexDeletions;

	public RuleUnApplicationEngine(OntologyIndex ontologyIndex,
			RuleApplicationListener listener) {
		super(ontologyIndex, listener, true, true);
		indexDeletions = ontologyIndex.getIndexChange().getIndexDeletions();
	}

	public RuleUnApplicationEngine(OntologyIndex ontologyIndex) {
		this(ontologyIndex, new RuleApplicationListener() {
			public void notifyCanProcess() {

			}
		});
	}

	void processContextDeletions(SaturatedClassExpression context) {
		final QueueProcessor queueProcessor = new QueueProcessor(context);

		// reverting inferences for all changed indexed class expressions of the
		// context
		for (IndexedClassExpression changedIndexedClassExpression : new LazySetIntersection<IndexedClassExpression>(
				indexDeletions.keySet(), context.getSuperClassExpressions())) {
			IndexedClassExpressionChange change = indexDeletions
					.get(changedIndexedClassExpression);
			queueProcessor.processClass(change);
		}
	}
}
