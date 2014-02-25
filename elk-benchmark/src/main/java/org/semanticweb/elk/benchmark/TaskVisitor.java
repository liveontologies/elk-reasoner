/**
 * 
 */
package org.semanticweb.elk.benchmark;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface TaskVisitor {

	public void visit(Task task) throws TaskException;
}
