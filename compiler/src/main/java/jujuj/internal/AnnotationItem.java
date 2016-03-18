package jujuj.internal;


final class AnnotationItem{

    /**
     * the name of field or method
     */
    private final String elementName;

    /**
     * the value of the annotation
     */
    private final String value;

    /**
     * the type of annotation
     */
    private final Type type;

    AnnotationItem(String name, Type type, String value) {
        this.elementName = name;
        this.value = value;
        this.type = type;
    }

    public String getElementName() {
        return elementName;
    }

    public Type getType(){
        return type;
    }

    public String getValue(){
        return value;
    }

    public enum Type{
        ViewInj, ViewValueInj, DependentInj
    }
}
