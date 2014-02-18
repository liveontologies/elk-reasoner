/**
 * 
 */
package org.semanticweb.elk;

/**
 * A mutable boolean which is helpful, for example, when one wants to set a flag
 * from inside an anonymous inner class (e.g., a visitor).
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class MutableBoolean {

	private boolean value_;
	
	public MutableBoolean() {
		this(false);
	}

	public MutableBoolean(boolean i) {
		value_ = i;
	}

	public boolean get() {
		return value_;
	}
	
	public void set(boolean i) {
		value_ = i;
	}
	
	public boolean flip() {
		value_ = !value_;
		
		return value_;
	}
	
	public boolean and(boolean i) {
		value_ &= i;
		
		return value_;
	}
	
	public boolean or(boolean i) {
		value_ |= i;
		
		return value_;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value_);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (value_ ? 1231 : 1237);
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
		MutableBoolean other = (MutableBoolean) obj;
		if (value_ != other.value_)
			return false;
		return true;
	}
	
}
