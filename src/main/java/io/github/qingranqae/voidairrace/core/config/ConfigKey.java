package io.github.qingranqae.voidairrace.core.config;

/**
 * 配置键的抽象接口，用于标识配置项的唯一路径及其期望的数据类型。
 * 具体配置键由枚举实现，例如 {@link GameSettingKey} 和 {@link FlagsKey}。
 */
public interface ConfigKey {
    /**
     * 获取配置项在YAML文件中的路径（例如 "selectedMapId" 或 "SpawnLocation.x"）。
     *
     * @return 配置项路径
     */
    String getPath();

    /**
     * 获取配置项期望的Java类型，用于类型安全的取值和验证。
     *
     * @return 配置项的类型 Class 对象
     */
    Class<?> getType();
}