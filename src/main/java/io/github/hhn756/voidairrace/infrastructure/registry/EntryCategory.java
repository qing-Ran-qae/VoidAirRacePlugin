package io.github.hhn756.voidairrace.infrastructure.registry;

/**
 * 用于定义一个注册项类别，包含项目类型和键类型信息
 *
 * @param <I> 该类别下所有注册项的类型，必须实现 {@link Entry} 接口
 * @param <K> 主键类型
 */
public class EntryCategory<I extends Entry<K>, K> {}
