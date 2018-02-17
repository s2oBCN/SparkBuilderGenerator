package com.helospark.spark.builder.handlers.codegenerator.component.fragment.builderclass.constructor;

import static com.helospark.spark.builder.preferences.PluginPreferenceList.CREATE_BUILDER_COPY_METHOD;
import static org.eclipse.jdt.core.dom.Modifier.ModifierKeyword.PRIVATE_KEYWORD;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.helospark.spark.builder.handlers.codegenerator.component.fragment.constructor.FieldSetterAdderFragment;
import com.helospark.spark.builder.handlers.codegenerator.component.helper.TypeDeclarationToVariableNameConverter;
import com.helospark.spark.builder.handlers.codegenerator.domain.BuilderField;
import com.helospark.spark.builder.preferences.PreferencesManager;

/**
 * Creates a private constructor that copies all fields from the given class.
 * <pre>
 * private Builder(OriginalType originalType) {
 *   this.field1 = originalType.field1;
 *   this.field2 = originalType.field2;
 * }
 * </pre>
 * @author helospark
 */
public class RegularBuilderCopyMethodAdderFragment {
    private FieldSetterAdderFragment fieldSetterAdderFragment;
    private TypeDeclarationToVariableNameConverter typeDeclarationToVariableNameConverter;
    private PreferencesManager preferencesManager;

    public RegularBuilderCopyMethodAdderFragment(FieldSetterAdderFragment fieldSetterAdderFragment,
            TypeDeclarationToVariableNameConverter typeDeclarationToVariableNameConverter, PreferencesManager preferencesManager) {
        this.fieldSetterAdderFragment = fieldSetterAdderFragment;
        this.typeDeclarationToVariableNameConverter = typeDeclarationToVariableNameConverter;
        this.preferencesManager = preferencesManager;
    }

    public void addCopyMethodIfNeeded(AST ast, TypeDeclaration builderType, TypeDeclaration originalType, List<BuilderField> builderFields) {
        if (preferencesManager.getPreferenceValue(CREATE_BUILDER_COPY_METHOD)) {
            createCopyConstructor(ast, builderType, originalType, builderFields);
        }
    }

    private void createCopyConstructor(AST ast, TypeDeclaration builderType, TypeDeclaration originalType, List<BuilderField> builderFields) {
        String originalTypeParameterName = typeDeclarationToVariableNameConverter.convert(originalType);

        Block methodBody = createMethodBody(ast, builderFields, originalTypeParameterName);
        MethodDeclaration copyConstructor = createCopyConstructorWithBody(ast, builderType, originalType, originalTypeParameterName, methodBody);
        builderType.bodyDeclarations().add(copyConstructor);
    }

    private Block createMethodBody(AST ast, List<BuilderField> builderFields, String originalTypeParameterName) {
        Block methodBody = ast.newBlock();
        fieldSetterAdderFragment.populateBodyWithFieldSetCalls(ast, originalTypeParameterName, methodBody, builderFields);
        return methodBody;
    }

    private MethodDeclaration createCopyConstructorWithBody(AST ast, TypeDeclaration builderType, TypeDeclaration originalType, String parameterName,
            Block body) {
        MethodDeclaration copyConstructor = ast.newMethodDeclaration();
        copyConstructor.setBody(body);
        copyConstructor.setConstructor(true);
        copyConstructor.setName(ast.newSimpleName(builderType.getName().toString()));
        copyConstructor.modifiers().add(ast.newModifier(PRIVATE_KEYWORD));
        copyConstructor.parameters().add(createParameter(ast, originalType, parameterName));
        return copyConstructor;
    }

    private SingleVariableDeclaration createParameter(AST ast, TypeDeclaration originalType, String parameterName) {
        SingleVariableDeclaration methodParameterDeclaration = ast.newSingleVariableDeclaration();
        methodParameterDeclaration.setType(ast.newSimpleType(ast.newName(originalType.getName().toString())));
        methodParameterDeclaration.setName(ast.newSimpleName(parameterName));
        return methodParameterDeclaration;
    }

}
