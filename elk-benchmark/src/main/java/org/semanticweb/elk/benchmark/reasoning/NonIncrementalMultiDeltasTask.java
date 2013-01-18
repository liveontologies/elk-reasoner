/**
 * 
 */
package org.semanticweb.elk.benchmark.reasoning;

import java.io.File;

import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;

/**
 * Same as the superclass but all changes are applied non-incrementally (for
 * comparison reasons)
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class NonIncrementalMultiDeltasTask extends
		IncrementalClassificationMultiDeltasTask {

	public NonIncrementalMultiDeltasTask(String[] args) {
		super(args);
	}

	
	@Override
	protected Task getIncrementalClassificationTask(File source) {
		return new ClassifyNonIncrementally(source);
	}


	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class ClassifyNonIncrementally extends IncrementalClassificationMultiDeltasTask.ClassifyIncrementally {

		ClassifyNonIncrementally(File dir) {
			super(dir);
		}

		@Override
		public String getName() {
			return "Classify from scratch";
		}

		@Override
		public void prepare() throws TaskException {
			reasoner_.setIncrementalMode(false);
			loadChanges(reasoner_);
		}
		
	}
}
