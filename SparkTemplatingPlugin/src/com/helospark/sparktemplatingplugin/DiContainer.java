package com.helospark.sparktemplatingplugin;

import java.util.ArrayList;
import java.util.List;

import com.helospark.sparktemplatingplugin.execute.templater.GlobalConfiguration;
import com.helospark.sparktemplatingplugin.execute.templater.ScriptExposed;
import com.helospark.sparktemplatingplugin.execute.templater.ScriptExposedObjectProvider;
import com.helospark.sparktemplatingplugin.execute.templater.ScriptExposedProvider;
import com.helospark.sparktemplatingplugin.execute.templater.ScriptInterpreter;
import com.helospark.sparktemplatingplugin.execute.templater.ScriptPreProcessor;
import com.helospark.sparktemplatingplugin.execute.templater.Templater;
import com.helospark.sparktemplatingplugin.execute.templater.TemplatingResultFactory;
import com.helospark.sparktemplatingplugin.execute.templater.helper.CompilationUnitCreator;
import com.helospark.sparktemplatingplugin.execute.templater.helper.PackageRootFinder;
import com.helospark.sparktemplatingplugin.execute.templater.provider.CompilationUnitProvider;
import com.helospark.sparktemplatingplugin.execute.templater.provider.CurrentClassProvider;
import com.helospark.sparktemplatingplugin.execute.templater.provider.CurrentProjectProvider;
import com.helospark.sparktemplatingplugin.repository.CommandNameToFilenameMapper;
import com.helospark.sparktemplatingplugin.repository.EclipseRootFolderProvider;
import com.helospark.sparktemplatingplugin.repository.FileSystemBackedScriptRepository;
import com.helospark.sparktemplatingplugin.repository.ScriptRepository;
import com.helospark.sparktemplatingplugin.repository.zip.ScriptUnzipper;
import com.helospark.sparktemplatingplugin.repository.zip.ScriptZipper;
import com.helospark.sparktemplatingplugin.support.classpath.ClassInClasspathLocator;
import com.helospark.sparktemplatingplugin.ui.editor.DocumentationProvider;
import com.helospark.sparktemplatingplugin.ui.editor.cache.EditorCacheInitializer;
import com.helospark.sparktemplatingplugin.ui.editor.completition.CompletitionChain;
import com.helospark.sparktemplatingplugin.ui.editor.completition.ProposalToDocumentationConverter;
import com.helospark.sparktemplatingplugin.ui.editor.completition.TemplatingToolCompletionProcessor;
import com.helospark.sparktemplatingplugin.ui.editor.completition.chain.ImportedPackageClassCompletitionChainItem;
import com.helospark.sparktemplatingplugin.ui.editor.completition.chain.MethodCallCompletitionChainItem;
import com.helospark.sparktemplatingplugin.ui.editor.completition.chain.ScriptExposedObjectCompletitionChainItem;

public class DiContainer {
    private static List<Object> diContainer = new ArrayList<>();

    public static void clearDiContainer() {
        diContainer.clear();
    }

    public static void initializeDiContainer() {
        addDependency(new ScriptPreProcessor());
        addDependency(new GlobalConfiguration());
        addDependency(new CompilationUnitCreator());
        addDependency(new PackageRootFinder());
        addDependency(new CompilationUnitProvider());
        addDependency(new ClassInClasspathLocator());
        addDependency(
                new TemplatingResultFactory(getDependency(CompilationUnitProvider.class),
                        getDependency(CompilationUnitCreator.class), getDependency(PackageRootFinder.class)));
        addDependency(new CurrentProjectProvider());
        addDependency(new CurrentClassProvider(getDependency(CompilationUnitProvider.class)));
        addDependency(new ScriptExposedObjectProvider(getDependency(TemplatingResultFactory.class),
                getDependencyList(ScriptExposed.class),
                getDependencyList(ScriptExposedProvider.class)));
        addDependency(new ScriptInterpreter(getDependency(ScriptExposedObjectProvider.class)));
        addDependency(new DocumentationProvider(getDependencyList(IDocumented.class)));
        addDependency(new ProposalToDocumentationConverter());
        addDependency(new MethodCallCompletitionChainItem(getDependency(ProposalToDocumentationConverter.class)));
        addDependency(new ScriptExposedObjectCompletitionChainItem(getDependency(ScriptExposedObjectProvider.class)));
        addDependency(new ImportedPackageClassCompletitionChainItem(getDependency(ClassInClasspathLocator.class)));
        addDependency(new TemplatingToolCompletionProcessor(getDependencyList(CompletitionChain.class)));
        addDependency(new Templater(getDependency(ScriptPreProcessor.class), getDependency(ScriptInterpreter.class)));

        addDependency(new EclipseRootFolderProvider());
        addDependency(new CommandNameToFilenameMapper());
        addDependency(new ScriptZipper(getDependency(CommandNameToFilenameMapper.class)));
        addDependency(new ScriptUnzipper());
        addDependency(new FileSystemBackedScriptRepository(getDependency(EclipseRootFolderProvider.class),
                getDependency(CommandNameToFilenameMapper.class)));
        addDependency(new TemplatingEditorOpener(getDependency(ScriptRepository.class)));
        addDependency(new EditorCacheInitializer(getDependency(ClassInClasspathLocator.class)));
    }

    // Visible for testing
    public static void addDependency(Object dependency) {
        boolean alreadyHasDependency = diContainer.stream()
                .filter(value -> isSameMockitoMockDependency(dependency.getClass().toString(), value.getClass().toString()))
                .findFirst()
                .isPresent();

        if (!alreadyHasDependency) {
            diContainer.add(dependency);
        } else {
            System.out.println("[INFO] Skipping " + dependency + " because diContainer already contains it");
        }
    }

    private static boolean isSameMockitoMockDependency(String newDependency, String oldDependency) {
        int mockitoClassNameStartIndex = oldDependency.indexOf("$$EnhancerByMockitoWithCGLIB");
        if (mockitoClassNameStartIndex != -1) {
            String mockitolessClassName = oldDependency.substring(0, mockitoClassNameStartIndex);
            return newDependency.equals(mockitolessClassName);
        } else {
            return false;
        }
    }

    /**
     * Probably will be deprecated after I will be able to create e4 plugin.
     * 
     * @param clazz
     *            type to get
     * @return dependency of that class
     */
    @SuppressWarnings("unchecked")
    public static <T> T getDependency(Class<T> clazz) {
        return (T) diContainer.stream()
                .filter(value -> clazz.isAssignableFrom(value.getClass()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unable to initialize " + clazz.getName() + " not found"));
    }

    public static <T> List<T> getDependencyList(Class<T> classToFind) {
        List<Object> result = new ArrayList<>();
        for (Object o : diContainer) {
            if (classToFind.isAssignableFrom(o.getClass())) {
                result.add(o);
            }
        }
        return (List<T>) result;
    }

}
