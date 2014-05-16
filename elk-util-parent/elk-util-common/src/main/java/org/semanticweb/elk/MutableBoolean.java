/**
 * 
 */
package org.semanticweb.elk;
/*
 * #%L
 * ELK Common Utilities
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
