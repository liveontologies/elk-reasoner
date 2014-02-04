/**
 * 
 */
package org.semanticweb.elk.benchmark.reasoning.owlapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.benchmark.TaskVisitor;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.saturation.tracing.ComprehensiveSubsumptionTracingTests;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingTestVisitor;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SampleSubsumptionsForJustifications extends FindJustificationsForAllSubsumptions {

	private final int sampleSize_;
	
	public SampleSubsumptionsForJustifications(String... args) {
		super(args);
		sampleSize_ = Integer.valueOf(args[2]);
	}
	
	@Override
	public void visitTasks(TaskVisitor visitor) throws TaskException {
		Taxonomy<ElkClass> taxonomy = loadAndClassify(ontologyFile_);
		
		//collect all tasks for sampling
		final List<GenerateJustifications> tasks = new ArrayList<GenerateJustifications>(taxonomy.getNodes().size() * 2);
		
		System.err.println("Classified, creating tasks..");
		
		try {
			initOWLOntology();
			
			new ComprehensiveSubsumptionTracingTests(taxonomy).accept(new TracingTestVisitor() {
				
				@Override
				public boolean visit(ElkClassExpression subsumee, 	ElkClassExpression subsumer) throws Exception {
					
					tasks.add(createSpecificTask(subsumee, subsumer));
					
					return true;
				}
			});
			
			System.err.println("Taking a random sample of size " + sampleSize_);
			
			List<GenerateJustifications> sample = null;
			
			if (sampleSize_ < tasks.size()) {
				Collections.shuffle(tasks);
				sample = new ArrayList<GenerateJustifications>(tasks.subList(0, sampleSize_));
				tasks.clear();
			}
			else {
				sample = tasks;
			}

			for (GenerateJustifications task : sample) {
				visitor.visit(task);
			}
		} 
		catch (TaskException e) {
			throw e;
		}
		catch (Exception e) {
			throw new TaskException(e);
		}
	}

	
}
