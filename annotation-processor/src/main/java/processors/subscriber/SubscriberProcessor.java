package processors.subscriber;

import annotations.SubscriberAnnotation;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by iaktas on 02.10.2017 at 15:45.
 */

@AutoService(Processor.class)
public class SubscriberProcessor extends AbstractProcessor{
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<String>();
        annotataions.add(SubscriberAnnotation.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(o -> log("supported annotations by SubscriberProcessor: "+o.getSimpleName().toString()));
        roundEnv.getElementsAnnotatedWith(SubscriberAnnotation.class).forEach(o -> log("annotated elements of SubscriberAnnotation annotation: "+o.toString()));

        // Itearate over all @annotations.SubscriberAnnotation annotated elements
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(SubscriberAnnotation.class)) {

            // Check if a class has been annotated with @annotations.Factory
            if(!isField(annotatedElement)){
                return false;
            }

            VariableElement variableElement = (VariableElement) annotatedElement;
        }

        return false;
    }


    private boolean isClass(Element annotatedElement){
        // Check if a class has been annotated with @annotations.Factory
        if (annotatedElement.getKind() != ElementKind.CLASS) {
            error(annotatedElement, "Only classes can be annotated with @%s",
                    SubscriberAnnotation.class.getSimpleName());
            return false; // Exit processing
        }
        return true;
    }

    private boolean isField(Element annotatedElement){
        // Check if a class has been annotated with @annotations.Factory
        if (annotatedElement.getKind() != ElementKind.FIELD) {
            error(annotatedElement, "Only fields can be annotated with @%s",
                    SubscriberAnnotation.class.getSimpleName());
            return false; // Exit processing
        }
        return true;
    }

    private void log(String message){
        messager.printMessage(
                Diagnostic.Kind.WARNING,message);
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }
}
