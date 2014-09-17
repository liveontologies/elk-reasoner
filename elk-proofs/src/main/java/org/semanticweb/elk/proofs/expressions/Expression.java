/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface Expression<E extends ElkAxiom> {

	public Collection<E> getAxioms();
}
