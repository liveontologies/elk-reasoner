/**
 * 
 */
package org.semanticweb.elk;

/**
 * A mutable integer which is helpful, for example, when one wants to count
 * things from inside an anonymous inner class (e.g., a visitor).
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class MutableInteger {

	private int value_;
	
	public MutableInteger() {
		this(0);
	}

	public MutableInteger(int i) {
		value_ = i;
	}

	public int get() {
		return value_;
	}
	
	public void set(int i) {
		value_ = i;
	}
	
	public int increment() {
		return value_++;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value_);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value_;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MutableInteger other = (MutableInteger) obj;
		if (value_ != other.value_)
			return false;
		return true;
	}
	
	
}
