package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.SaturationState.Writer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.logging.CachedTimeThread;

public class DecompositionRuleApplicationTimerVisitor implements
		DecompositionRuleApplicationVisitor {

	/**
	 * the visitor whose methods to be timed
	 */
	private final DecompositionRuleApplicationVisitor visitor_;

	/**
	 * timer used to time the visitor
	 */
	private final DecompositionRuleApplicationTimer timer_;

	/**
	 * Creates a new {@link DecompositionRuleApplicationVisitor} that executes
	 * the corresponding methods of the given
	 * {@link DecompositionRuleApplicationVisitor} and measures the time spent
	 * within the corresponding methods using the given
	 * {@link DecompositionRuleApplicationTimer}.
	 * 
	 * @param visitor
	 *            the {@link DecompositionRuleApplicationVisitor} used to
	 *            execute the methods
	 * @param timer
	 *            the {@link DecompositionRuleApplicationTimer} used to mesure
	 *            the time spent within the methods
	 */
	public DecompositionRuleApplicationTimerVisitor(
			DecompositionRuleApplicationVisitor visitor,
			DecompositionRuleApplicationTimer timer) {
		this.timer_ = timer;
		this.visitor_ = visitor;
	}

	@Override
	public void visit(IndexedClass ice, Writer writer, Context context) {
		timer_.timeIndexedClass -= CachedTimeThread.currentTimeMillis;
		visitor_.visit(ice, writer, context);
		timer_.timeIndexedClass += CachedTimeThread.currentTimeMillis;
	}

	@Override
	public void visit(IndexedObjectIntersectionOf ice, Writer writer,
			Context context) {
		timer_.timeIndexedObjectIntersectionOf -= CachedTimeThread.currentTimeMillis;
		visitor_.visit(ice, writer, context);
		timer_.timeIndexedObjectIntersectionOf += CachedTimeThread.currentTimeMillis;
	}

	@Override
	public void visit(IndexedObjectSomeValuesFrom ice, Writer writer,
			Context context) {
		timer_.timeIndexedObjectSomeValuesFrom -= CachedTimeThread.currentTimeMillis;
		visitor_.visit(ice, writer, context);
		timer_.timeIndexedObjectSomeValuesFrom += CachedTimeThread.currentTimeMillis;
	}

	@Override
	public void visit(IndexedDataHasValue ice, Writer writer, Context context) {
		timer_.timeIndexedDataHasValue -= CachedTimeThread.currentTimeMillis;
		visitor_.visit(ice, writer, context);
		timer_.timeIndexedDataHasValue += CachedTimeThread.currentTimeMillis;
	}

}
