package org.semanticweb.elk.alc.indexing.hierarchy;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.util.collections.ArrayHashSet;

public class OntologyIndex {

	private final IndexedObjectCache objectCache_ = new IndexedObjectCache();

	private final Set<ElkClass> classes_ = new ArrayHashSet<ElkClass>(1024);

	void addClass(ElkClass elkClass) {
		classes_.add(elkClass);
	}

	void removeClass(ElkClass elkClass) {
		classes_.remove(elkClass);
	}

	public IndexedObjectCache getIndexedObjectCache() {
		return objectCache_;
	}

	/**
	 * Adds the given {@link IndexedObject} to this {@link OntologyIndex}
	 * 
	 * @param newObject
	 *            the object to be added
	 */
	public void add(IndexedObject newObject) {
		newObject.accept(objectCache_.inserter);
	}

	/**
	 * Removes the given {@link IndexedObject} from this {@link OntologyIndex}
	 * 
	 * @param oldObject
	 *            the object to be removed
	 * 
	 * @throws ElkUnexpectedIndexingException
	 *             if the given object does not occur in this
	 *             {@link OntologyIndex}
	 */
	public void remove(IndexedObject oldObject)
			throws ElkUnexpectedIndexingException {
		if (!oldObject.accept(objectCache_.deletor))
			throw new ElkUnexpectedIndexingException(
					"Cannot remove indexed object from the cache " + oldObject);
	}

}
