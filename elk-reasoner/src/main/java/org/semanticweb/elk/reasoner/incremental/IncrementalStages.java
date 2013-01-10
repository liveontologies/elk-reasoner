/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public enum IncrementalStages {

	ADDITIONS_INIT {

		@Override
		public String toString() {
			return "Incremental Additions Initialization";
		}
		
	},

	DELETIONS_INIT {

		@Override
		public String toString() {
			return "Incremental Deletions Initialization";
		}
		
	},
	
	CONTEXT_AFTER_DEL_INIT {

		@Override
		public String toString() {
			return "Post-Deletion Context Initialization";
		}
		
	},	
	
	CONTEXT_AFTER_CLEAN_INIT {

		@Override
		public String toString() {
			return "Post-Cleaning Context Initialization";
		}
		
	},	
	
	DESATURATION {

		@Override
		public String toString() {
			return "Incremental Desaturation";
		}
		
	},
	
	SATURATION {

		@Override
		public String toString() {
			return "Incremental Re-saturation";
		}
		
	},	
	
	CONTEXT_CLEANING {

		@Override
		public String toString() {
			return "Incremental Context Cleaning";
		}
	},
		
	COMPLETION {

		@Override
		public String toString() {
			return "Incremental Context Completion";
		}
	},
		
	TAXONOMY_CLEANING {

		@Override
		public String toString() {
			return "Incremental Taxonomy Cleaning";
		}
	},
	
	TAXONOMY_CONSTRUCTION {

		@Override
		public String toString() {
			return "Incremental Taxonomy Construction";
		}
	};

	@Override
	public abstract String toString();	
}
