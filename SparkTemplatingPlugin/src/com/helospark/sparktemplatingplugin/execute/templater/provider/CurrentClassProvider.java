package com.helospark.sparktemplatingplugin.execute.templater.provider;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.helospark.sparktemplatingplugin.execute.templater.ScriptExposedProvider;
import com.helospark.sparktemplatingplugin.wrapper.SttType;

public class CurrentClassProvider implements ScriptExposedProvider {

    public static final String EXPOSED_NAME = "currentClass";
    private CompilationUnitProvider compilationUnitProvider;

    public CurrentClassProvider(CompilationUnitProvider compilationUnitProvider) {
        this.compilationUnitProvider = compilationUnitProvider;
    }

    @Override
    public Object provide(ExecutionEvent event) {
        try {
            IType result = null;
            ICompilationUnit compilationUnit = compilationUnitProvider.provideCurrentICompiltionUnit(event).getRaw();
            ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
            ITextSelection selection = (ITextSelection) currentSelection;
            IJavaElement element = compilationUnit.getElementAt(selection.getOffset());
            if (element != null) {
                result = (IType) element.getAncestor(IJavaElement.TYPE);
            } else {
                result = compilationUnit.findPrimaryType();
            }

            return new SttType(result);
        } catch (Exception e) {
            throw new RuntimeException("Unable to extract current class", e);
        }
    }

    @Override
    public Class<?> getExposedObjectType() {
        return SttType.class;
    }

    @Override
    public String getExposedName() {
        return EXPOSED_NAME;
    }
}
