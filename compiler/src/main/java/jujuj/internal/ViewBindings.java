package jujuj.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ViewBindings {
    //the id could be an Integer or String
    //Integer is the id of resource,
    //while String is the indicator of the resource(android:id="")
    private final String id;
    private final Set<FieldViewBinding> fieldBindings = new LinkedHashSet<>();

    ViewBindings(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Collection<FieldViewBinding> getFieldBindings() {
        return fieldBindings;
    }

    public void addFieldBinding(FieldViewBinding fieldBinding) {
        fieldBindings.add(fieldBinding);
    }

}
