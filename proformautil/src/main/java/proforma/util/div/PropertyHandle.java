package proforma.util.div;

import java.lang.reflect.InvocationTargetException;

/**
 * An instance of this class can access a {@code propertyType}-typed property named "{@code propertyName}"
 * of a given {@code target} object
 * using it's getters and setters.
 */
public class PropertyHandle {

    private Object target;
    private Class<?> propertyType;
    private String getterName;
    private String setterName;
    private String propertyName;

    public PropertyHandle(Object target, String propertyName, Class<?> propertyType) {
        this.target = target;
        if (target == null) throw new AssertionError(this.getClass() + ": file shouldn't be null");
        this.propertyType = propertyType;
        String getPrefix = "get";
        if (boolean.class.equals(propertyType) || Boolean.class.equals(propertyType)) {
            getPrefix = "is";
        }
        this.getterName = getPrefix + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        this.setterName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }

    public Object get() {
        try {
            return propertyType.cast(target.getClass().getDeclaredMethod(getterName).invoke(target));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
            | SecurityException e) {
            throw new AssertionError(e);
        }
    }

    public void set(Object value) {
        try {
            target.getClass().getDeclaredMethod(setterName, propertyType).invoke(target, value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
            | SecurityException e) {
            throw new AssertionError(e);
        }
    }

    public void createAndSet() {
        try {
            Object f = propertyType.getDeclaredConstructor().newInstance();
            set(f);
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }

    public Object getTarget() {
        return target;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void assertNotNull(String whatToDo, Object embracingObject) throws NullPointerException {
        if (get() == null)
            throw new NullPointerException("Cannot " + whatToDo + " because " + embracingObject.getClass() + " embraces a null value. You should call createAndSet() first.");
    }

}
