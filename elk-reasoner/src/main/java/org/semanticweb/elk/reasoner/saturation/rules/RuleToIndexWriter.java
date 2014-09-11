/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;

/**
 * A facade for adding or removing some particular types of rules to the index. The rules
 * may store references to {@link ElkAxiom}s asserted in the ontology.
 * 
 * TODO shall we also create other rules via this facade for uniformity?
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface RuleToIndexWriter {

	public void addSuperClassFromSubClassRule(
			IndexedSubClassOfAxiom indexedAxiom, ModifiableOntologyIndex index,
			ElkAxiom originalAxiom);

	public void addContradictionFromDisjointnessRule(IndexedDisjointnessAxiom indexedAxiom,
			ModifiableOntologyIndex index, ElkDisjointClassesAxiom originalAxiom);
	
	public void addDisjointSubsumerRule(IndexedDisjointnessAxiom indexedAxiom,
			ModifiableOntologyIndex index, ElkDisjointClassesAxiom originalAxiom);
	
	public void removeSuperClassFromSubClassRule(
			IndexedSubClassOfAxiom indexedAxiom, ModifiableOntologyIndex index);

	public void removeContradictionFromDisjointnessRule(IndexedDisjointnessAxiom indexedAxiom,
			ModifiableOntologyIndex index);
	
	public void removeDisjointSubsumerRule(IndexedDisjointnessAxiom indexedAxiom,
			ModifiableOntologyIndex index);	
}
