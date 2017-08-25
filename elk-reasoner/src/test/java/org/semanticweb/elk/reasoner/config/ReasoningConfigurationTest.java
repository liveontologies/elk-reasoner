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
package org.semanticweb.elk.reasoner.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.elk.config.ConfigurationException;
import org.semanticweb.elk.util.collections.Evictor;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public class ReasoningConfigurationTest {

	@SuppressWarnings("static-method")
	@Test
	public void defaultConfig() {
		ReasonerConfiguration config = ReasonerConfiguration.getConfiguration();

		assertTrue(config.getParameterNames().contains(
				ReasonerConfiguration.NUM_OF_WORKING_THREADS));
		assertTrue(config.getParameterNames().contains(
				ReasonerConfiguration.UNSUPPORTED_FEATURE_TREATMENT));
		assertTrue(config.getParameterNames().contains(
				ReasonerConfiguration.INCREMENTAL_MODE_ALLOWED));
		assertTrue(config.getParameterNames().contains(
				ReasonerConfiguration.TRACING_CONTEXT_EVICTOR));
		assertTrue(config.getParameterNames().contains(
				ReasonerConfiguration.TRACING_CONCLUSION_EVICTOR));
		assertTrue(config.getParameterNames().contains(
				ReasonerConfiguration.CLASS_EXPRESSION_QUERY_EVICTOR));
		assertTrue(config.getParameterNames().contains(
				ReasonerConfiguration.ENTAILMENT_QUERY_EVICTOR));
		assertEquals(
				Runtime.getRuntime().availableProcessors(),
				config.getParameterAsInt(ReasonerConfiguration.NUM_OF_WORKING_THREADS));
		assertEquals(
				UnsupportedFeatureTreatment.IGNORE,
				config.getParameter(ReasonerConfiguration.UNSUPPORTED_FEATURE_TREATMENT));
		assertEquals(
				true,
				config.getParameterAsBoolean(ReasonerConfiguration.INCREMENTAL_MODE_ALLOWED));
		Object value = config.getParameter(ReasonerConfiguration.TRACING_CONTEXT_EVICTOR);
		assertTrue(value instanceof Evictor.Builder);
		value = config.getParameter(ReasonerConfiguration.TRACING_CONTEXT_EVICTOR);
		assertTrue(value instanceof Evictor.Builder);
		value = config.getParameter(ReasonerConfiguration.CLASS_EXPRESSION_QUERY_EVICTOR);
		assertTrue(value instanceof Evictor.Builder);
		value = config.getParameter(ReasonerConfiguration.ENTAILMENT_QUERY_EVICTOR);
		assertTrue(value instanceof Evictor.Builder);
	}

	@SuppressWarnings("static-method")
	@Test(expected = ConfigurationException.class)
	public void wrongIntParameterValue() {
		ReasonerConfiguration config = ReasonerConfiguration.getConfiguration();

		config.setParameter(
				ReasonerConfiguration.UNSUPPORTED_FEATURE_TREATMENT,
				"something unsupported here");
	}

	@SuppressWarnings("static-method")
	@Test(expected = ConfigurationException.class)
	public void wrongEnumParameterValue() {
		ReasonerConfiguration config = ReasonerConfiguration.getConfiguration();

		config.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS,
				"not an integer");
	}

}