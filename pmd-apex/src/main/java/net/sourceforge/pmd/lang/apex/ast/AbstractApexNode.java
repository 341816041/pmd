/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import apex.jorje.data.Loc;
import apex.jorje.data.Loc.RealLoc;
import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.exception.UnexpectedCodePathException;

public abstract class AbstractApexNode<T extends AstNode> extends AbstractNode implements ApexNode<T> {

    protected final T node;

    public AbstractApexNode(T node) {
        super(node.getClass().hashCode());
        this.node = node;
    }

    /**
     * Accept the visitor. *
     */
    public Object childrenAccept(ApexParserVisitor visitor, Object data) {
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                @SuppressWarnings("unchecked")
                // we know that the children here are all ApexNodes
                ApexNode<T> apexNode = (ApexNode<T>) children[i];
                apexNode.jjtAccept(visitor, data);
            }
        }
        return data;
    }

    public T getNode() {
        return node;
    }

    @Override
    public int getBeginLine() {
    	try {
            Loc loc = node.getLoc();
            if (loc instanceof RealLoc) {
                return ((RealLoc) loc).line;
            }
        } catch (UnexpectedCodePathException e) {
            // some nodes are artificially generated by the compiler and are not
            // really existing in source code
        }

    	return 1;
    }
    
// TODO: figure out the correct line
    @Override
    public int getEndLine() {
        return getBeginLine();
    }

// TODO: figure out the correct column
    @Override
    public int getBeginColumn() {
        try {
            Loc loc = node.getLoc();
            if (loc instanceof RealLoc) {
                return ((RealLoc) loc).column;
            }
        } catch (UnexpectedCodePathException e) {
            // some nodes are artificially generated by the compiler and are not
            // really existing in source code
        }

        return 1;
    }

// TODO: figure out the correct column
    @Override
    public int getEndColumn() {
        try {
            Loc loc = node.getLoc();
            if (loc instanceof RealLoc) {
                RealLoc realLoc = (RealLoc) loc;
                return realLoc.endIndex - realLoc.startIndex + realLoc.column;
            }
        } catch (UnexpectedCodePathException e) {
            // some nodes are artificially generated by the compiler and are not
            // really existing in source code
        }
        return super.getEndColumn();
    }

    public String toString() {
        return this.getClass().getSimpleName().replaceFirst("^AST", "");
    }
}
