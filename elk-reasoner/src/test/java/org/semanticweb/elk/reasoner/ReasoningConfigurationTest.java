/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
 * 
 */
package org.semanticweb.elk.reasoner;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReasoningConfigurationTest {

	@Test
	public void defaultConfig() {
		
		ReasonerConfiguration config = ReasonerConfiguration.getDefaultConfiguration();
		
		assertTrue(config.getParameterNames().contains(ReasonerConfiguration.NUM_OF_WORKING_THREADS.getName()));
		assertEquals(Runtime.getRuntime().availableProcessors(), config.getParameterAsInt(ReasonerConfiguration.NUM_OF_WORKING_THREADS.getName()));
	}

}
