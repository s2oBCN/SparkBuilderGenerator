package com.helospark.spark.builder.handlers.codegenerator.component.remover;

import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

/**
 * Chain item to remove parts of the previously generated builder.
 * @author helospark
 */
public interface BuilderRemoverChainItem {

    void remove(ASTRewrite rewriter, TypeDeclaration mainType);

}
