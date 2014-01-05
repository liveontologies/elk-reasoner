/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DelegatingBasicSaturationStateWriter implements
		BasicSaturationStateWriter {

	protected final BasicSaturationStateWriter writer;
	
	/**
	 * 
	 */
	public DelegatingBasicSaturationStateWriter(BasicSaturationStateWriter writer) {
		this.writer = writer;
	}

	@Override
	public IndexedClassExpression getOwlThing() {
		return writer.getOwlThing();
	}

	@Override
	public IndexedClassExpression getOwlNothing() {
		return writer.getOwlNothing();
	}

	@Override
	public Context pollForActiveContext() {
		return writer.pollForActiveContext();
	}

	@Override
	public void produce(Context context, Conclusion conclusion) {
		writer.produce(context, conclusion);
	}

	@Override
	public boolean markAsNotSaturated(Context context) {
		return writer.markAsNotSaturated(context);
	}

	@Override
	public void clearNotSaturatedContexts() {
		writer.clearNotSaturatedContexts();
	}

	@Override
	public void resetContexts() {
		writer.resetContexts();
	}

	@Override
	public ConclusionFactory getConclusionFactory() {
		return writer.getConclusionFactory();
	}

	@Override
	public ConclusionVisitor<Boolean, Context> getConclusionInserter() {
		return writer.getConclusionInserter();
	}

}
