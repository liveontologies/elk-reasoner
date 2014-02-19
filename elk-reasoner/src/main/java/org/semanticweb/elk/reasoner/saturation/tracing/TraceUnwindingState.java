package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Pair;

/**
 * The state of the recursive trace unwinding procedure for some
 * {@link Conclusion} in some {@link Context} identified by its root.
 * 
 * This state is not thread-safe.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TraceUnwindingState {

	private final Queue<Pair<Conclusion, IndexedClassExpression>> toUnwind_;

	private final Set<Inference> processedInferences_;

	TraceUnwindingState() {
		toUnwind_ = new LinkedList<Pair<Conclusion, IndexedClassExpression>>();
		processedInferences_ = new ArrayHashSet<Inference>();
	}

	void addToUnwindingQueue(Conclusion conclusion, IndexedClassExpression rootWhereStored) {
		toUnwind_.add(new Pair<Conclusion, IndexedClassExpression>(conclusion, rootWhereStored));
	}

	Pair<Conclusion, IndexedClassExpression> pollFromUnwindingQueue() {
		return toUnwind_.poll();
	}

	boolean addToProcessed(Inference inference) {
		return processedInferences_.add(inference);
	}
}
