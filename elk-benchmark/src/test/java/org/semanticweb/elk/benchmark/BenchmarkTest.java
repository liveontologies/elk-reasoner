/**
 * 
 */
package org.semanticweb.elk.benchmark;
/*
 * #%L
 * ELK Benchmarking Package
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.semanticweb.elk.benchmark.reasoning.AllFilesClassificationTask;
import org.semanticweb.elk.benchmark.reasoning.AllFilesIncrementalClassificationTask;
import org.semanticweb.elk.benchmark.reasoning.ClassificationTask;
import org.semanticweb.elk.benchmark.reasoning.IncrementalClassificationMultiDeltas;
import org.semanticweb.elk.benchmark.reasoning.IncrementalClassificationTask;
import org.semanticweb.elk.benchmark.reasoning.RandomWalkIncrementalClassificationTask;

/**
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class BenchmarkTest {

	@Rule public TestName testName_ = new TestName();
	private static Set<String> testsToRun_ = null;

	@BeforeClass
	public static void before() {
		String tests = System.getProperty("tests");
		
		if (tests != null) {
			testsToRun_ = new HashSet<String>(Arrays.asList(tests.split(",")));
		}
	}
	
	@Test
	public void classification() throws Exception {
		Assume.assumeTrue(testsToRun_ == null || testsToRun_.contains(testName_.getMethodName()));
		
		BenchmarkUtils.runTask(ClassificationTask.class.getName(),
				Integer.valueOf(System.getProperty(Constants.WARM_UPS)),
				Integer.valueOf(System.getProperty(Constants.RUNS)),
				new String[]{System.getProperty("classification.ontology")});
	}
	
	@Test
	public void classificationAll() throws Exception {
		Assume.assumeTrue(testsToRun_ == null || testsToRun_.contains(testName_.getMethodName()));
		
		BenchmarkUtils.runTaskCollection(AllFilesClassificationTask.class.getName(),
				Integer.valueOf(System.getProperty(Constants.WARM_UPS)),
				Integer.valueOf(System.getProperty(Constants.RUNS)),
				new String[]{System.getProperty("classification.dir")});
	}

	@Test
	public void incrementalClassification() throws Exception {
		Assume.assumeTrue(testsToRun_ == null
				|| testsToRun_.contains(testName_.getMethodName()));

		BenchmarkUtils.runTask(
				IncrementalClassificationTask.class.getName(),
				Integer.valueOf(System.getProperty(Constants.WARM_UPS)),
				Integer.valueOf(System.getProperty(Constants.RUNS)),
				new String[] { System.getProperty("incremental.ontology"), "1" });
	}	
	
	@Test
	public void incrementalClassificationRandomWalk() throws Exception {
		Assume.assumeTrue(testsToRun_ == null
				|| testsToRun_.contains(testName_.getMethodName()));

		BenchmarkUtils.runTask(
				RandomWalkIncrementalClassificationTask.class.getName(),
				Integer.valueOf(System.getProperty(Constants.WARM_UPS)),
				Integer.valueOf(System.getProperty(Constants.RUNS)),
				new String[] { System.getProperty("incremental.ontology") });
	}

	@Test
	public void incrementalClassificationMultiDeltas() throws Exception {
		Assume.assumeTrue(testsToRun_ == null || testsToRun_.contains(testName_.getMethodName()));
		
		BenchmarkUtils.runTaskCollection2(IncrementalClassificationMultiDeltas.class.getName(),
				Integer.valueOf(System.getProperty(Constants.WARM_UPS)),
				Integer.valueOf(System.getProperty(Constants.RUNS)),
				new String[]{System.getProperty("incremental.dir")});
	}	
	
	@Test
	public void incrementalClassificationAll() throws Exception {
		Assume.assumeTrue(testsToRun_ == null || testsToRun_.contains(testName_.getMethodName()));
		
		BenchmarkUtils.runTaskCollection(AllFilesIncrementalClassificationTask.class.getName(),
				Integer.valueOf(System.getProperty(Constants.WARM_UPS)),
				Integer.valueOf(System.getProperty(Constants.RUNS)),
				new String[]{System.getProperty("incremental.dir")});
	}
}
