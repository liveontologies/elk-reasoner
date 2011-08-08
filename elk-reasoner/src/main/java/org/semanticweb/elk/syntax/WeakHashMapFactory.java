/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
package org.semanticweb.elk.syntax;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import org.semanticweb.elk.syntax.interfaces.ElkObject;

// TODO reimplement with ArrayHashSet when it supports removals

/**
 * @author Frantisek
 *
 */
public class WeakHashMapFactory implements ElkObjectFactory {
	private static class WeakWrapper extends WeakReference<ElkObject> {
        private int hash_;

        private WeakWrapper(ElkObject object, ReferenceQueue<ElkObject> queue) {
        	super(object, queue);
        	hash_ = object.structuralHashCode();
        }
        
        public boolean equals(Object object) {
        	if (this == object)
        		return true;
        	
        	if (object instanceof WeakWrapper) {
        		ElkObject a = this.get();
        		ElkObject b = ((WeakWrapper) object).get();
        	
        		if (a == b) 
        			return true;
        		if (a != null && b != null)
        			return a.structuralEquals(b);
        	}
        		        		
        	return false;
       	}

       	public int hashCode() {
       		return hash_;
       	}
	}
	
    private HashMap<WeakWrapper, WeakWrapper> wrapperCache_ = new HashMap<WeakWrapper, WeakWrapper> ();
    private ReferenceQueue<ElkObject> referenceQueue_ = new ReferenceQueue<ElkObject> ();
    
    public ElkObject put(ElkObject object) {
    	processQueue();
    	
    	if (object == null)
    		return null;
    	
    	WeakWrapper key = new WeakWrapper(object, referenceQueue_);
    	WeakWrapper value = wrapperCache_.get(key);
    	
    	if (value != null) {
    		ElkObject result = value.get();
    		if (result != null)
    			return result;
    	}
    	
    	wrapperCache_.put(key, key);
    	return object;
    }
    
    private final void processQueue() {
        WeakWrapper w = null;

        while ((w = (WeakWrapper) referenceQueue_.poll()) != null) {
            wrapperCache_.remove(w);
        }
    }
    
    public int size() {
    	return wrapperCache_.size();
    }
}
