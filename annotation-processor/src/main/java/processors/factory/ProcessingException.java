package processors.factory;

import javax.lang.model.element.Element;

/**
 * Created by iaktas on 26.09.2017 at 17:28.
 */

public class ProcessingException extends Exception {

    Element element;

    public ProcessingException(Element element, String msg, Object... args) {
        super(String.format(msg, args));
        this.element = element;
    }

    public Element getElement() {
        return element;
    }
}
