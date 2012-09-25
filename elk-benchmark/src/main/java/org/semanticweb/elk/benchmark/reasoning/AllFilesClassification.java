/**
 * 
 */
package org.semanticweb.elk.benchmark.reasoning;

import org.semanticweb.elk.benchmark.AllFilesMultiTask;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskFactory;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class AllFilesClassification extends AllFilesMultiTask {

	public AllFilesClassification(String[] args) {
		super(args);
	}

	@Override
	public String getName() {
		return "Multi classification";
	}

	@Override
	public Task instantiateSubTask(String[] args) {
		return TaskFactory.create(ClassificationTask.class.getName(), args);
	}

}
