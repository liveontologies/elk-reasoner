package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.SaturationState.Writer;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link DecompositionRuleApplicationVisitor} wrapper for a given
 * {@link DecompositionRuleApplicationVisitor} that additionally records the
 * number of invocations of the methods using the given
 * {@link DecompositionRuleApplicationCounter}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class DecompositionRuleApplicationCounterVisitor implements
		DecompositionRuleApplicationVisitor {

	/**
	 * the visitor whose method applications to be counted
	 */
	private final DecompositionRuleApplicationVisitor visitor_;
	/**
	 * the counter used to count the number of method applications of the
	 * visitor
	 */
	private final DecompositionRuleApplicationCounter counter_;

	/**
	 * Creates a new {@link DecompositionRuleApplicationVisitor} that executes
	 * the corresponding methods of the given
	 * {@link DecompositionRuleApplicationVisitor} and counts the number of
	 * invocations of the corresponding methods using the given
	 * {@link DecompositionRuleApplicationCounter}.
	 * 
	 * @param visitor
	 *            the {@link DecompositionRuleApplicationVisitor} used to
	 *            execute the methods
	 * @param counter
	 *            the {@link DecompositionRuleApplicationCounter} used to count
	 *            the number of method invocations
	 */
	public DecompositionRuleApplicationCounterVisitor(
			DecompositionRuleApplicationVisitor visitor,
			DecompositionRuleApplicationCounter counter) {
		this.visitor_ = visitor;
		this.counter_ = counter;
	}

	@Override
	public void visit(IndexedClass ice, Writer writer, Context context) {
		counter_.countIndexedClass++;
		visitor_.visit(ice, writer, context);
	}

	@Override
	public void visit(IndexedObjectIntersectionOf ice, Writer writer,
			Context context) {
		counter_.countIndexedObjectIntersectionOf++;
		visitor_.visit(ice, writer, context);
	}

	@Override
	public void visit(IndexedObjectSomeValuesFrom ice, Writer writer,
			Context context) {
		counter_.countIndexedObjectSomeValuesFrom++;
		visitor_.visit(ice, writer, context);
	}

	@Override
	public void visit(IndexedDataHasValue ice, Writer writer, Context context) {
		counter_.countIndexedDataHasValue++;
		visitor_.visit(ice, writer, context);
	}

}
