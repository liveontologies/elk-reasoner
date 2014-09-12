/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;

/**
 * An extension of {@link IndexedObjectProperty} with an explicit binding to
 * axioms for told sub-property chains.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IndexedObjectPropertyWithBinding extends IndexedObjectProperty {

	private List<ElkObjectPropertyAxiom> chainAxioms_ = null;
	
	private ElkReflexiveObjectPropertyAxiom reflexivityAxiom_;

	protected IndexedObjectPropertyWithBinding(
			ElkObjectProperty elkObjectProperty) {
		super(elkObjectProperty);
	}

	@Override
	protected void addToldSubPropertyChain(
			IndexedPropertyChain subObjectProperty) {
		throw new IllegalStateException("SubObjectPropertyOf axiom has to be provided");
	}

	@Override
	protected boolean removeToldSubObjectProperty(
			IndexedPropertyChain subChain) {
		if (toldSubProperties == null) {
			return false;
		}
		
		for (int i = 0; i < toldSubProperties.size(); i++) {
			if (subChain == toldSubProperties.get(i)) {
				toldSubProperties.remove(i);
				chainAxioms_.remove(i);
				
				if (toldSubProperties.isEmpty()) {
					toldSubProperties = null;
				}
				
				return true;
			}
		}

		return false;
	}

	protected void addToldSubPropertyChain(
			IndexedPropertyChain subObjectProperty,
			ElkObjectPropertyAxiom axiom) {
		super.addToldSubPropertyChain(subObjectProperty);

		if (chainAxioms_ == null) {
			chainAxioms_ = new ArrayList<ElkObjectPropertyAxiom>(1);
		}

		chainAxioms_.add(axiom);
	}

	public ElkObjectPropertyAxiom getSubChainAxiom(
			IndexedPropertyChain subChain) {
		if (chainAxioms_ == null) {
			// shouldn't happen
			return null;
		}

		for (int i = 0; i < toldSubProperties.size(); i++) {
			if (subChain == toldSubProperties.get(i)) {
				return chainAxioms_.get(i);
			}
		}

		return null;
	}

	public ElkReflexiveObjectPropertyAxiom getReflexivityAxiom() {
		return reflexivityAxiom_;
	}
	
	public void setAxiom(ElkReflexiveObjectPropertyAxiom axiom) {
		reflexivityAxiom_ = axiom;
	}
}
