/**
 * 
 */
package org.semanticweb.elk.util.collections;

import java.util.Collection;
import java.util.Map;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface MultimapQueue<Key, Value> extends Multimap<Key, Value> {
	
	/**
	 * 
	 * @return the removed entry or {@code null} if the queue is empty
	 */
	public Map.Entry<Key, Collection<Value>> takeEntry();

}
