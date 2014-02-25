/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * Creates instances of {@link ContextImpl}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class MainContextFactory implements ContextFactory<ExtendedContext> {

	@Override
	public ExtendedContext createContext(IndexedClassExpression root) {
		return new ContextImpl(root);
	}

}
