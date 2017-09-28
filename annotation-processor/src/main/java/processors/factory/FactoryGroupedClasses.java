package processors.factory;

import annotations.Factory;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by iaktas on 26.09.2017 at 17:23.
 */

public class FactoryGroupedClasses {

    /**
     * Will be added to the name of the generated factory class
     */
    private static final String SUFFIX = "Factory";
    private final Messager messager;

    private String qualifiedClassName;

    private Map<String, FactoryAnnotatedClass> itemsMap =
            new LinkedHashMap<String, FactoryAnnotatedClass>();

    public FactoryGroupedClasses(String qualifiedClassName, Messager messager) {
        this.qualifiedClassName = qualifiedClassName;
        this.messager = messager;

        log(qualifiedClassName + " group is created.");
    }

    public void add(FactoryAnnotatedClass toInsert) throws ProcessingException  {

        FactoryAnnotatedClass existing = itemsMap.get(toInsert.getId());
        if (existing != null) {
            // Alredy existing
            throw new ProcessingException(toInsert.getTypeElement(),
                    "Conflict: The class %s is annotated with @%s with id ='%s' but %s already uses the same id",
                    toInsert.getTypeElement().getQualifiedName().toString(), Factory.class.getSimpleName(),
                    toInsert.getId(), existing.getTypeElement().getQualifiedName().toString());
        }

        itemsMap.put(toInsert.getId(), toInsert);

        log(toInsert.getTypeElement().getSimpleName() + " class is added into "+qualifiedClassName);
    }

    public void generateCode(Elements elementUtils, Filer filer) throws IOException {
        TypeElement superClassName = elementUtils.getTypeElement(qualifiedClassName);
        String factoryClassName = superClassName.getSimpleName() + SUFFIX;
        String qualifiedFactoryClassName = qualifiedClassName + SUFFIX;
        PackageElement pkg = elementUtils.getPackageOf(superClassName);
        String packageName = pkg.isUnnamed() ? "" : pkg.getQualifiedName().toString();

        MethodSpec.Builder method = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "id")
                .returns(TypeName.get(superClassName.asType()));

        // check if id is null
        method.beginControlFlow("if (id == null)")
                .addStatement("throw new IllegalArgumentException($S)", "id is null!")
                .endControlFlow();

        // Generate items map

        for (FactoryAnnotatedClass item : itemsMap.values()) {
            method.beginControlFlow("if ($S.equals(id))", item.getId())
                    .addStatement("return new $L()", item.getTypeElement().getQualifiedName().toString())
                    .endControlFlow();
        }

        method.addStatement("throw new IllegalArgumentException($S + id)", "Unknown id = ");

        TypeSpec typeSpec = TypeSpec.classBuilder(factoryClassName).addMethod(method.build()).build();

        // Write file
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
    }

    private void log(String message){
        messager.printMessage(
                Diagnostic.Kind.WARNING,message);
    }
}