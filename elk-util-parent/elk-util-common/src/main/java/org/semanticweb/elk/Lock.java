/*-
 * #%L
 * ELK Common Utilities
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk;

/**
 * This interface represents a lock that may be locked multiple times
 * (internally). It becomes unlocked only if it is unlocked the same number of
 * times.
 * 
 * @author Peter Skocovsky
 */
public interface Lock {

	/**
	 * @return {@code true} iff the lock is locked.
	 */
	boolean isLocked();

	/**
	 * Decrements the number of times this lock is locked. It becomes unlocked
	 * when this method is called at least as many times the lock is locked.
	 * 
	 * @return {@code true} if the lock was unlocked by this call, {@code false}
	 *         otherwise.
	 */
	boolean unlock();

}
