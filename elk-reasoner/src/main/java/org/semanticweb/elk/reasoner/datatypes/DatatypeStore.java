/**
 * 
 */
package org.semanticweb.elk.reasoner.datatypes;

import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;

/**
 * Manages all value spaces for a given datatype (several datatypes connected
 * with parent/child relationships) that occur in indexed datatype expressions
 * and answers queries of the form: give all value spaces (or associated
 * datatype expressions) that subsume the given value space.
 * 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface DatatypeStore<C> {

	/**
	 * 
	 * @param vs
	 *            Value space whose subsumers need to be found
	 * @return An iteration over elements, e.g., indexed datatype expressions,
	 *         associated with value spaces which subsume the given value space
	 */
	public Iterable<C> getSubsumingValueSpaces(ValueSpace vs);
}
