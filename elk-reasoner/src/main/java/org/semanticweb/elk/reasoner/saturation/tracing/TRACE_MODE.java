package org.semanticweb.elk.reasoner.saturation.tracing;

/**
 * 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public enum TRACE_MODE {
	NO_TRACING, // do not trace contexts
	NON_RECURSIVE, // trace only the input contexts
	/*
	 * trace all contexts which directly or indirectly produce conclusions which
	 * belong to the input contexts
	 */
	RECURSIVE
}