package io.github.qingranqae.voidairrace.service.config.files;

/** 配置文件枚举的共同接口 */
public interface ConfigFiles {
    /**
     * 获取配置文件名
     *
     * @return 文件名（不含扩展名）
     */
    String getFileName();

    /**
     * 获取该配置文件中定义的所有配置键
     *
     * @return 配置键数组
     */
    ConfigKeys[] getKeys();
}