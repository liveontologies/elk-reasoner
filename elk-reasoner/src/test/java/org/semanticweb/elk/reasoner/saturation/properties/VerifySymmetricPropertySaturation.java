/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.properties;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class VerifySymmetricPropertySaturation {

	/**
	 * checking that all compositions of this property to the left are computed
	 * in the saturations for the respective left properties
	 * 
	 * @param ipc
	 */
	public static void testLeftCompositions(IndexedPropertyChain ipc, AsymmetricCompositionHook hook) {
		SaturatedPropertyChain saturation = ipc.getSaturated();
		Multimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByLeft = saturation
				.getCompositionsByLeftSubProperty();
		if (compositionsByLeft == null)
			return;
		for (IndexedPropertyChain left : compositionsByLeft.keySet()) {
			for (IndexedPropertyChain composition : compositionsByLeft
					.get(left)) {
				SaturatedPropertyChain leftSaturation = left.getSaturated();
				Multimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByRight = leftSaturation
						.getCompositionsByRightSubProperty();
				if (compositionsByRight == null
						|| !compositionsByRight.contains(ipc, composition)) {
					hook.error(left, ipc, composition, ipc);
				}
			}
		}
	}

	/**
	 * checking that all compositions of this property to the right are computed
	 * in the saturations for the respective right properties
	 * 
	 * @param ipc
	 */
	public static void testRightCompositions(IndexedPropertyChain ipc, AsymmetricCompositionHook hook) {
		SaturatedPropertyChain saturation = ipc.getSaturated();
		Multimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByRight = saturation
				.getCompositionsByRightSubProperty();
		if (compositionsByRight == null)
			return;
		for (IndexedPropertyChain right : compositionsByRight.keySet()) {
			for (IndexedPropertyChain composition : compositionsByRight
					.get(right)) {
				SaturatedPropertyChain rightSaturation = right.getSaturated();
				Multimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByLeft = rightSaturation
						.getCompositionsByLeftSubProperty();
				if (compositionsByLeft == null
						|| !compositionsByLeft.contains(ipc, composition)) {
					hook.error(ipc, right, composition, ipc);
				}
			}
		}
	}
	
	public interface AsymmetricCompositionHook {
		
		public void error(IndexedPropertyChain left, IndexedPropertyChain right, IndexedPropertyChain composition, IndexedPropertyChain computed);
	}
}


