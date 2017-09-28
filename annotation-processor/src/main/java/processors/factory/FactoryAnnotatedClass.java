package processors.factory;

import annotations.Factory;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;

/**
 * Created by iaktas on 26.09.2017 at 17:13.
 */

public class FactoryAnnotatedClass {

    private TypeElement annotatedClassElement;
    private String qualifiedSuperClassName;
    private String simpleTypeName;
    private String id;

    public FactoryAnnotatedClass(TypeElement classElement, Messager messager) throws IllegalArgumentException {
        this.annotatedClassElement = classElement;
        // Here we access the @annotations.Factory annotation and check if the id is not empty.
        Factory annotation = classElement.getAnnotation(Factory.class);
        id = annotation.id();

        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException(
                    String.format("id() in @%s for class %s is null or empty! that's not allowed",
                            Factory.class.getSimpleName(), classElement.getQualifiedName().toString()));
        }

        // Get the full QualifiedTypeName
        try {
            // The class is already compiled:
            // This is the case if a third party .jar contains compiled .class files with @annotations.Factory annotations.
            // In that case we can directly access the Class like we do in the try-block.
            Class<?> clazz = annotation.type();
            qualifiedSuperClassName = clazz.getCanonicalName();
            simpleTypeName = clazz.getSimpleName();
        } catch (MirroredTypeException mte) {
            // The class is not compiled yet: This will be the case if we try to compile our source code
            // which has @annotations.Factory annotations.
            // Trying to access the Class directly throws a MirroredTypeException.
            // Fortunately MirroredTypeException contains a TypeMirror representation of our not yet compiled class.
            // Since we know that it must be type of class
            // (we have already checked that before) we can cast it to DeclaredType
            // and access TypeElement to read the qualified name.
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
            simpleTypeName = classTypeElement.getSimpleName().toString();
        } finally {
            messager.printMessage(Diagnostic.Kind.WARNING, "Type of class in annotation: "+simpleTypeName+ " ("+qualifiedSuperClassName + ")");
        }
    }

    /**
     * Get the id as specified in {@link Factory#id()}.
     * return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Get the full qualified name of the type specified in  {@link Factory#type()}.
     *
     * @return qualified name
     */
    public String getQualifiedFactoryGroupName() {
        return qualifiedSuperClassName;
    }


    /**
     * Get the simple name of the type specified in  {@link Factory#type()}.
     *
     * @return qualified name
     */
    public String getSimpleFactoryGroupName() {
        return simpleTypeName;
    }

    /**
     * The original element that was annotated with @annotations.Factory
     */
    public TypeElement getTypeElement() {
        return annotatedClassElement;
    }
}