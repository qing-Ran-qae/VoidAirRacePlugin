package io.github.hhn756.voidairrace.infrastructure.util;

import java.lang.reflect.*;

public final class TypeUtil {

    private TypeUtil() {}

    /**
     * 从任意 Type 中提取原始 Class（raw type）。
     * <p>
     * 例如：
     * <ul>
     *   <li>{@code String.class}           → {@code String.class}</li>
     *   <li>{@code List<String>}           → {@code List.class}</li>
     *   <li>{@code Map<String, Integer>}   → {@code Map.class}</li>
     *   <li>{@code String[]}               → {@code String[].class}</li>
     *   <li>{@code ? extends Number}       → {@code Number.class}</li>
     * </ul>
     */
    public static Class<?> getRawClass(Type type) {
        if (type instanceof Class<?> clazz) {
            // 普通类型：String.class, int.class, String[].class ...
            return clazz;

        } else if (type instanceof ParameterizedType pType) {
            // 参数化类型：List<String>, Map<K,V> ...
            // getRawType() 返回的一定是 Class<?>
            return (Class<?>) pType.getRawType();

        } else if (type instanceof GenericArrayType gType) {
            // 泛型数组：T[], List<String>[] ...
            Class<?> componentClass = getRawClass(gType.getGenericComponentType());
            return Array.newInstance(componentClass, 0).getClass();

        } else if (type instanceof WildcardType wType) {
            // 通配符：? extends Number, ? super Integer
            Type[] upperBounds = wType.getUpperBounds();
            return getRawClass(upperBounds.length > 0 ? upperBounds[0] : Object.class);

        } else if (type instanceof TypeVariable<?> tVar) {
            // 类型变量：T, T extends Comparable<T>
            Type[] bounds = tVar.getBounds();
            return getRawClass(bounds.length > 0 ? bounds[0] : Object.class);

        } else {
            throw new IllegalArgumentException(
                    "无法从 Type 中提取原始类型: " + type + " (" + type.getClass().getName() + ")"
            );
        }
    }
}
