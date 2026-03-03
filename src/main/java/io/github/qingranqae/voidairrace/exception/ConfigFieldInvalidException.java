package io.github.qingranqae.voidairrace.exception;

/**
 * 当配置文件中的字段值无效时抛出的运行时异常。
 * 例如，期望的整数字段却包含字符串，或字段值为 null 但需要非空。
 * 该异常通常由配置验证逻辑（如 {@link io.github.qingranqae.voidairrace.core.matchsystem.MatchConfig#validate()}）抛出。
 */
public class ConfigFieldInvalidException extends RuntimeException {
    /** 导致异常的字段路径。 */
    private final String fieldPath;

    /** 详细错误原因。 */
    private final String reason;

    /**
     * 构造一个配置字段无效异常。
     *
     * @param fieldPath 字段路径（例如 "selectedMapId"）
     * @param reason    错误原因描述
     */
    public ConfigFieldInvalidException(String fieldPath, String reason) {
        super("配置字段无效: '" + fieldPath + "' - " + reason);
        this.fieldPath = fieldPath;
        this.reason = reason;
    }

    /**
     * 获取导致异常的字段路径。
     *
     * @return 字段路径
     */
    public String getFieldPath() {
        return fieldPath;
    }

    /**
     * 获取详细错误原因。
     *
     * @return 错误原因
     */
    public String getReason() {
        return reason;
    }
}