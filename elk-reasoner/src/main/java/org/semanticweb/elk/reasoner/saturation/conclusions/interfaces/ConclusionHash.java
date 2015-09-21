package org.semanticweb.elk.reasoner.saturation.conclusions.interfaces;

import org.semanticweb.elk.owl.comparison.ElkObjectHash;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObject;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

public class ConclusionHash implements ConclusionVisitor<Void, Integer>,
		Hasher<Conclusion> {

	private static final ConclusionHash INSTANCE_ = new ConclusionHash();

	// forbid construction; only static methods should be used
	private ConclusionHash() {

	}

	private static int combinedHashCode(int... hashes) {
		return HashGenerator.combineListHash(hashes);
	}

	private static int hashCode(Class<?> c) {
		return c.hashCode();
	}

	private static int hashCode(IndexedObject o) {
		return o.hashCode();
	}

	private static int hashCode(ElkObject elkObject) {
		return ElkObjectHash.hashCode(elkObject);
	}

	public static int hashCode(Conclusion conclusion) {
		return conclusion == null ? 0 : conclusion.accept(INSTANCE_, null);
	}

	@Override
	public int hash(Conclusion conclusion) {
		return hashCode(conclusion);
	}

	@Override
	public Integer visit(BackwardLink subConclusion, Void input) {
		return combinedHashCode(hashCode(BackwardLink.class),
				hashCode(subConclusion.getConclusionRoot()),
				hashCode(subConclusion.getConclusionSubRoot()),
				hashCode(subConclusion.getOriginRoot()));
	}

	@Override
	public Integer visit(Propagation subConclusion, Void input) {
		return combinedHashCode(hashCode(Propagation.class),
				hashCode(subConclusion.getConclusionRoot()),
				hashCode(subConclusion.getConclusionSubRoot()),
				hashCode(subConclusion.getCarry()));
	}

	@Override
	public Integer visit(SubContextInitialization subConclusion, Void input) {
		return combinedHashCode(hashCode(SubContextInitialization.class),
				hashCode(subConclusion.getConclusionRoot()),
				hashCode(subConclusion.getConclusionSubRoot()));
	}

	@Override
	public Integer visit(ComposedSubsumer conclusion, Void input) {
		return combinedHashCode(hashCode(ComposedSubsumer.class),
				hashCode(conclusion.getConclusionRoot()),
				hashCode(conclusion.getExpression()));
	}

	@Override
	public Integer visit(ContextInitialization conclusion, Void input) {
		return combinedHashCode(hashCode(ContextInitialization.class),
				hashCode(conclusion.getConclusionRoot()));
	}

	@Override
	public Integer visit(Contradiction conclusion, Void input) {
		return combinedHashCode(hashCode(Contradiction.class),
				hashCode(conclusion.getConclusionRoot()));
	}

	@Override
	public Integer visit(DecomposedSubsumer conclusion, Void input) {
		return combinedHashCode(hashCode(DecomposedSubsumer.class),
				hashCode(conclusion.getConclusionRoot()),
				hashCode(conclusion.getExpression()));
	}

	@Override
	public Integer visit(DisjointSubsumer conclusion, Void input) {
		return combinedHashCode(hashCode(DisjointSubsumer.class),
				hashCode(conclusion.getConclusionRoot()),
				hashCode(conclusion.getAxiom()),
				hashCode(conclusion.getMember()),
				hashCode(conclusion.getReason()));
	}

	@Override
	public Integer visit(ForwardLink conclusion, Void input) {
		return combinedHashCode(hashCode(ForwardLink.class),
				hashCode(conclusion.getConclusionRoot()),
				hashCode(conclusion.getForwardChain()),
				hashCode(conclusion.getTarget()));
	}

}
