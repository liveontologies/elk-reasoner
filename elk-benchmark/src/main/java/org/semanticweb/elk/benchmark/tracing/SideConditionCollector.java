package org.semanticweb.elk.benchmark.tracing;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.util.collections.Pair;

/**
 * Counts the number of distinct SubClassOf axioms in visited inferences.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SideConditionCollector extends UsedInferencesCounter {

	private Set<Pair<IndexedClassExpression, IndexedClassExpression>> subclassAxioms_ = new HashSet<Pair<IndexedClassExpression, IndexedClassExpression>>();
	//private Set<SubClassOfSubsumer> subclassAxioms_ = new HashSet<SubClassOfSubsumer>();
	
	@Override
	public Void visit(SubClassOfSubsumer conclusion, IndexedClassExpression input) {
		subclassAxioms_.add(new Pair<IndexedClassExpression, IndexedClassExpression>(((Subsumer<?>)conclusion.getPremise()).getExpression(), conclusion.getExpression()));
		//subclassAxioms_.add(conclusion);
		
		return super.visit(conclusion, input);
	}
	
	public Set<Pair<IndexedClassExpression, IndexedClassExpression>> getSubClassOfAxioms() {
	//public Set<SubClassOfSubsumer> getSubClassOfAxioms() {
		return subclassAxioms_;
	}
}