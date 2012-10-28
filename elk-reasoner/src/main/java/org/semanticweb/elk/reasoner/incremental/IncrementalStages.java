/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

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
	
	CONTEXT_INIT {

		@Override
		public String toString() {
			return "Incremental Context Initialization";
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
			return "Incremental Saturation";
		}
		
	},	
	
	CONTEXT_CLEANING {

		@Override
		public String toString() {
			return "Incremental Context Cleaning";
		}
		
	};	

	@Override
	public abstract String toString();	
}
