package processors.builder;

import annotations.BuilderProperty;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by iaktas on 25.09.2017 at 17:03.
 */

@AutoService(Processor.class)
public class BuilderProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<String>();
        annotataions.add(BuilderProperty.class.getCanonicalName());/*You can add other processors here*/
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        for (TypeElement annotation : annotations) {
            // In this code, we use the RoundEnvironment instance to receive all elements annotated with the @annotations.BuilderProperty annotation.
            // In the case of the Person class, these elements correspond to the setName and setAge methods.
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

            // we use the Collectors.partitioningBy() collector to split annotated methods into two collections:
            // correctly annotated setters and other erroneously annotated methods.
            Map<Boolean, List<Element>> annotatedMethods = annotatedElements.stream().collect(
                    Collectors.partitioningBy(element ->
                            ((ExecutableType) element.asType()).getParameterTypes().size() == 1
                                    && element.getSimpleName().toString().startsWith("set")));

            List<Element> setters = annotatedMethods.get(true);
            List<Element> otherMethods = annotatedMethods.get(false);

            // The following lines will output an error for each erroneously annotated element during the source processing stage
            otherMethods.forEach(element ->
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "@annotations.BuilderProperty must be applied to a setXxx method "
                                    + "with a single argument", element));

            // if the correct setters collection is empty, there is no point of continuing the current type element set iteration
            if (setters.isEmpty()) {
                continue;
            }

            // If the setters collection has at least one element, we’re going to use it
            // to get the fully qualified class name from the enclosing element,
            // which in case of the setter method appears to be the source class itself
            String className = ((TypeElement) setters.get(0)
                    .getEnclosingElement()).getQualifiedName().toString();

            // The last bit of information we need to generate a builder class is a map
            // between the names of the setters and the names of their argument types
            Map<String, String> setterMap = setters.stream().collect(Collectors.toMap(
                    setter -> setter.getSimpleName().toString(),
                    setter -> ((ExecutableType) setter.asType())
                            .getParameterTypes().get(0).toString()
            ));

            // Now we have all the information we need to generate a builder class:
            // the name of the source class, all its setter names, and their argument types.

            try {
                writeBuilderFile(className, setterMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void writeBuilderFile(String className, Map<String, String> setterMap) throws IOException {

        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String builderClassName = className + "Builder";
        String builderSimpleClassName = builderClassName
                .substring(lastDot + 1);

        JavaFileObject builderFile = processingEnv.getFiler()
                .createSourceFile(builderClassName);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.print("public class ");
            out.print(builderSimpleClassName);
            out.println(" {");
            out.println();

            out.print("    private ");
            out.print(simpleClassName);
            out.print(" object = new ");
            out.print(simpleClassName);
            out.println("();");
            out.println();

            out.print("    public ");
            out.print(simpleClassName);
            out.println(" build() {");
            out.println("        return object;");
            out.println("    }");
            out.println();

            setterMap.entrySet().forEach(setter -> {
                String methodName = setter.getKey();
                String argumentType = setter.getValue();

                out.print("    public ");
                out.print(builderSimpleClassName);
                out.print(" ");
                out.print(methodName);

                out.print("(");

                out.print(argumentType);
                out.println(" value) {");
                out.print("        object.");
                out.print(methodName);
                out.println("(value);");
                out.println("        return this;");
                out.println("    }");
                out.println();
            });

            out.println("}");
        }
    }
}