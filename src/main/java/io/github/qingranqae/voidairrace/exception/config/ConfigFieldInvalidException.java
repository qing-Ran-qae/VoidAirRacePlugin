package io.github.qingranqae.voidairrace.exception.config;

public class ConfigFieldInvalidException extends RuntimeException {
    private final String fieldPath;
    private final String reason; // 详细错误原因

    public ConfigFieldInvalidException(String fieldPath, String reason) {
        super("配置字段无效: '" + fieldPath + "' - " + reason);
        this.fieldPath = fieldPath;
        this.reason = reason;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public String getReason() {
        return reason;
    }
}
