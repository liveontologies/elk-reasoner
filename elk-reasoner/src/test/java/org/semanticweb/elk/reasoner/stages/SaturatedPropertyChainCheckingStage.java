package org.semanticweb.elk.reasoner.stages;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.properties.VerifySymmetricPropertySaturation;
import org.semanticweb.elk.reasoner.saturation.properties.VerifySymmetricPropertySaturation.AsymmetricCompositionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checking invariants for {@code SaturatedPropertyChain}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SaturatedPropertyChainCheckingStage extends
		BasePostProcessingStage {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SaturatedPropertyChainCheckingStage.class);

	private final OntologyIndex index_;

	public SaturatedPropertyChainCheckingStage(AbstractReasonerState reasoner) {
		this.index_ = reasoner.ontologyIndex;
	}

	@Override
	public String getName() {
		return "Checking Saturation for Properties";
	}

	@Override
	public void execute() throws ElkException {

		for (IndexedPropertyChain ipc : index_.getPropertyChains()) {
			AsymmetricCompositionHook hook = new VerifySymmetricPropertySaturation.AsymmetricCompositionHook() {

				@Override
				public void error(IndexedPropertyChain left,
						IndexedPropertyChain right,
						IndexedPropertyChain composition,
						IndexedPropertyChain computed) {
					LOGGER_.error("Composition " + left + " o " + right
							+ " => " + composition + " is computed for "
							+ (left == computed ? left : right)
							+ " but not for "
							+ (left == computed ? right : left));
				}
			};

			VerifySymmetricPropertySaturation.testLeftCompositions(ipc, hook);
			if (ipc instanceof IndexedObjectProperty)
				VerifySymmetricPropertySaturation.testRightCompositions(
						(IndexedObjectProperty) ipc, hook);
		}

	}
}
