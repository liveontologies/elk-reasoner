/**
 * 
 */
package org.semanticweb.elk.reasoner.datatypes;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;

/**
 * The central entry-point to datatype reasoning. Answers queries of the form:
 * give me all (negatively occurring) indexed datatype expressions which subsume
 * the given datatype expression.
 * 
 * Internally manages a set of datatype stores for each data property and (comparable)
 * datatypes which occur in datatype expressions with that property.
 * 
 * It is not specified but advisable that this class does not use a single,
 * global registry of datatype stores but maintains them locally, each next to
 * the indexed data property (for performance reasons). In other words, each
 * property has a set of root datatypes (often just one) each of which is mapped
 * to its datatype store.
 * 
 * It seems possible to create all value spaces during loading/indexing and fill
 * up all datatype stores during a dedicate reasoning stage.
 * 
 * This class should not have static members.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface DatatypeEngine2 {

	public Iterable<IndexedDatatypeExpression> getSubsumingNegExistentials(
			IndexedDatatypeExpression datatypeExpression);
}
