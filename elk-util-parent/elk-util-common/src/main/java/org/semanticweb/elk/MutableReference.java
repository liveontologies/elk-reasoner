/**
 * 
 */
package org.semanticweb.elk;

/**
 * A mutable reference to an object.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class MutableReference<R> {

	private R object_;
	
	public MutableReference() {
		this(null);
	}
	
	public MutableReference(R object) {
		object_ = object;
	}

	public R get() {
		return object_;
	}
	
	public void set(R object) {
		object_ = object;
	}
	
	@Override
	public String toString() {
		return object_.toString();
	}

	@Override
	public int hashCode() {
		return object_.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MutableReference<?> other = (MutableReference<?>) obj;
		if (!object_.equals(other.object_))
			return false;
		return true;
	}
	
	
}
