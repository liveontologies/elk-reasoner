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
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
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
	 * @param hook 
	 */
	public static void testLeftCompositions(IndexedPropertyChain ipc,
			AsymmetricCompositionHook hook) {
		SaturatedPropertyChain saturation = ipc.getSaturated();
		Multimap<IndexedObjectProperty, IndexedComplexPropertyChain> compositionsByLeft = saturation
				.getNonRedundantCompositionsByLeftSubProperty();
		for (IndexedObjectProperty left : compositionsByLeft.keySet()) {
			for (IndexedComplexPropertyChain composition : compositionsByLeft
					.get(left)) {
				SaturatedPropertyChain leftSaturation = left.getSaturated();
				Multimap<IndexedPropertyChain, IndexedComplexPropertyChain> compositionsByRight = leftSaturation
						.getNonRedundantCompositionsByRightSubProperty();
				if (!compositionsByRight.contains(ipc, composition)) {
					hook.error(left, ipc, composition, ipc);
				}
			}
		}
	}

	/**
	 * checking that all compositions of this property to the right are computed
	 * in the saturations for the respective right properties
	 * 
	 * @param ip
	 * @param hook 
	 */
	public static void testRightCompositions(IndexedObjectProperty ip,
			AsymmetricCompositionHook hook) {
		SaturatedPropertyChain saturation = ip.getSaturated();
		Multimap<IndexedPropertyChain, IndexedComplexPropertyChain> compositionsByRight = saturation
				.getNonRedundantCompositionsByRightSubProperty();
		for (IndexedPropertyChain right : compositionsByRight.keySet()) {
			for (IndexedComplexPropertyChain composition : compositionsByRight
					.get(right)) {
				SaturatedPropertyChain rightSaturation = right.getSaturated();
				Multimap<IndexedObjectProperty, IndexedComplexPropertyChain> compositionsByLeft = rightSaturation
						.getNonRedundantCompositionsByLeftSubProperty();
				if (!compositionsByLeft.contains(ip, composition)) {
					hook.error(ip, right, composition, ip);
				}
			}
		}
	}

	public interface AsymmetricCompositionHook {

		public void error(IndexedPropertyChain left,
				IndexedPropertyChain right, IndexedPropertyChain composition,
				IndexedPropertyChain computed);
	}
}
