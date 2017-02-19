package me.hao0.antares.common.util;

import sun.misc.Unsafe;
import java.lang.reflect.Field;

/**
 * The reflection util based on Unsafe
 */
public abstract class Fields {

    private static final Unsafe unsafe = getUnsafe();

    private static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("failed to get unsafe instance, cause");
        }
    }

    /**
     * put field to target object
     * @param target target object
     * @param name field name
     * @param value field valiue
     */
    public static void put(Object target, String name, Object value){
        try {
            Field field = target.getClass().getField(name);
            field.setAccessible(true);
            long fieldOffset = unsafe.objectFieldOffset(field);
            unsafe.putObject(target, fieldOffset, value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * put field to target object
     * @param target target object
     * @param field object field
     * @param value field valiue
     */
    public static void put(Object target, Field field, Object value) {
        try {
            field.setAccessible(true);
            long fieldOffset = unsafe.objectFieldOffset(field);
            unsafe.putObject(target, fieldOffset, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get field of target object
     * @param target target object
     * @param name field name
     * @param <T> generic type
     * @return the field value
     */
    public static <T> T get(Object target, String name) {
        try {
            return get(target, target.getClass().getDeclaredField(name));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get field of target object
     * @param target target object
     * @param field field
     * @param <T> generic type
     * @return the field value
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Object target, Field field) {
        try {
            long fieldOffset = unsafe.objectFieldOffset(field);
            return (T)unsafe.getObject(target, fieldOffset);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}