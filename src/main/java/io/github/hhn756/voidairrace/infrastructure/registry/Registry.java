package io.github.hhn756.voidairrace.infrastructure.registry;

import io.github.hhn756.voidairrace.exception.RegistryException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 能够记录和分类项目，支持键值唯一约束和键索引查询
 */
public class Registry {
    private static Registry instance;

    // 类别 → 项目映射表（键 → 项目对象）
    private final ConcurrentHashMap<EntryCategory<?, ?>, ConcurrentHashMap<Object, Object>> categoryMaps
            = new ConcurrentHashMap<>();

    static void load() {
        instance = new Registry();
    }

    static void unload() {
        instance = null;
    }

    /**
     * @return 注册表单例对象
     *
     * @throws NullPointerException 如果注册表实例不存在
     */
    public static @NonNull Registry getInstance() throws NullPointerException {
        if (instance == null) throw new NullPointerException("注册表实例不存在");
        return instance;
    }

    private Registry() {}

    /**
     * 定义一个项目类别<br>
     * 注册后此类别将拥有独立于其他类别的子表，用于记录该类的所有已注册项目<br>
     * 若类别已存在，则无操作
     *
     * @param category 要定义的类别
     */
    public <I extends Entry<K>, K> void createCategory(@NonNull EntryCategory<I, K> category) {
        categoryMaps.computeIfAbsent(category, k -> new ConcurrentHashMap<>());
    }

    /**
     * 增加一项到指定类别中<br>
     * 根据类别的键提取器提取键，若键已存在，则不做任何操作（即不覆盖）
     *
     * @param category 目标类别
     * @param entry    要添加的项目，应和类别定义对象中的键类型一致
     */
    public <I extends Entry<K>, K> void add(@NonNull EntryCategory<I, K> category, I entry) {
        K key = entry.getKey();
        if (key == null) throw new RegistryException("项目的主键为 null", null);

        ConcurrentHashMap<Object, Object> map =
                categoryMaps.computeIfAbsent(category, k -> new ConcurrentHashMap<>());
        // putIfAbsent 保证键唯一且不覆盖已有值
        map.putIfAbsent(key, entry);
    }

    /**
     * 根据键删除指定类别中的项目<br>
     * 项目本就不在类别中时方法不产生效果
     *
     * @param category 指定类别
     * @param key      要删除项目的键，应和类别定义对象中的键类型一致
     */
    public <I extends Entry<K>, K> void remove(@NonNull EntryCategory<I, K> category, K key) {
        ConcurrentHashMap<Object, Object> map = categoryMaps.get(category);
        if (map != null) {
            map.remove(key);
        }
    }

    /**
     * 根据键获取指定类别中的项目
     *
     * @param category 指定类别
     * @param key      查询键，应和类别定义对象中的键类型一致
     *
     * @return 项目对象，若未注册则返回{@code null}
     */
    @SuppressWarnings("unchecked")
    public <I extends Entry<K>, K> @Nullable I get(@NonNull EntryCategory<I, K> category, K key) {
        ConcurrentHashMap<Object, Object> map = categoryMaps.get(category);
        if (map == null) return null;
        return (I) map.get(key);
    }

    /**
     * 检查指定项目在指定类别中是否已注册
     *
     * @param category 指定类别
     * @param key      查询键，应和类别定义对象中的键类型一致
     *
     * @return 如果项目在指定类别已注册将返回{@code true}，否则返回{@code false}
     */
    public <I extends Entry<K>, K> boolean isRegistered(@NonNull EntryCategory<I, K> category, K key) {
        return categoryMaps.get(category).containsKey(key);
    }

    /**
     * 列出指定类别中的所有项目
     *
     * @param category 类别
     *
     * @return 该类别下所有项目的集合（副本），若类别未定义则返回空集合
     */
    @SuppressWarnings("unchecked")
    public <I extends Entry<K>, K> @NonNull Collection<@NonNull I> list(@NonNull EntryCategory<I, K> category) {
        ConcurrentHashMap<Object, Object> map = categoryMaps.get(category);
        if (map == null) return Collections.emptySet();
        // 返回副本以避免外部修改内部结构
        return (Collection<I>) (Collection<?>) new HashSet<>(map.values());
    }
}