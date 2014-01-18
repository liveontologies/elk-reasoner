/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ConclusionByContextStore {

	private final Map<IndexedClassExpression, ConclusionStore> conclusionStoreMap_;
	
	public ConclusionByContextStore() {
		conclusionStoreMap_ = new HashMap<IndexedClassExpression, ConclusionStore>();
	}
	
	public boolean add(Context context, Conclusion conclusion) {
		ConclusionStore store = conclusionStoreMap_.get(context.getRoot());
		
		if (store == null) {
			store = new ConclusionStore();
			
			store.add(conclusion);
			conclusionStoreMap_.put(context.getRoot(), store);
		
			return true;
		}
		else {
			return store.add(conclusion);
		}
	}
	
	public boolean delete(Context context, Conclusion conclusion) {
		ConclusionStore store = conclusionStoreMap_.get(context.getRoot());
		
		if (store != null) {
			boolean removed = store.remove(conclusion);
			
			if (store.size() == 0) {
				conclusionStoreMap_.remove(context.getRoot());
			}
			
			return removed;
		}
		else {
			return false;
		}
	}
	
	public boolean contains(Context context, Conclusion conclusion) {
		ConclusionStore store = conclusionStoreMap_.get(context.getRoot());
		
		if (store != null) {
			return store.contains(conclusion);
		}
		else {
			return false;
		}
	}
	
	public int getContextNumber() {
		return conclusionStoreMap_.keySet().size();
	}
	
	public int getTotalSize() {
		int total = 0;
		
		for (ConclusionStore store : conclusionStoreMap_.values()) {
			total += store.size();
		}
		
		return total;
	}
	
}
