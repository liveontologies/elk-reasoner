package org.semanticweb.elk.reasoner.incremental;

import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpressionChange;
import org.semanticweb.elk.reasoner.rules.RuleApplicationEngine;
import org.semanticweb.elk.reasoner.rules.RuleApplicationListener;
import org.semanticweb.elk.reasoner.rules.SaturatedClassExpression;
import org.semanticweb.elk.util.collections.LazySetIntersection;

public class RuleReApplicationEngine extends RuleApplicationEngine {

	final Map<IndexedClassExpression, IndexedClassExpressionChange> indexAdditions;

	public RuleReApplicationEngine(OntologyIndex ontologyIndex,
			RuleApplicationListener listener) {
		super(ontologyIndex, listener, false, true);
		indexAdditions = ontologyIndex.getIndexChange().getIndexAdditions();
	}

	public RuleReApplicationEngine(OntologyIndex ontologyIndex) {
		this(ontologyIndex, new RuleApplicationListener() {
			public void notifyCanProcess() {

			}
		});
	}

	void processContextAdditions(SaturatedClassExpression context) {
		final QueueProcessor queueProcessor = new QueueProcessor(context);

		// applying inferences for all changed indexed class expressions of the
		// context
		for (IndexedClassExpression changedIndexedClassExpression : new LazySetIntersection<IndexedClassExpression>(
				indexAdditions.keySet(), context.getSuperClassExpressions())) {
			IndexedClassExpressionChange change = indexAdditions
					.get(changedIndexedClassExpression);
			queueProcessor.processClass(change);
		}
	}

	void processContext(SaturatedClassExpression context) {
//		final QueueProcessor queueProcessor = new QueueProcessor(context);
		// re-initializing context just in case
		context.clear();
		initContext(context);

		// re-applying inferences for all indexed class expressions of the
		// context
//		for (IndexedClassExpression derived : context
//				.getSuperClassExpressions()) {
//			queueProcessor.processClass(derived);
//		}
	}
}
