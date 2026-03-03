package io.github.qingranqae.voidairrace.util;

public class Percentage {
    /**
     * 将最大值和当前值转为{@code 0.0f~1.0f}间的百分比值
     *
     * @param max 最大值
     * @param current 当前值
     *
     * @return {@code 0.0f~1.0f}间的百分比值
     * */
    public static float toPercentage(int max, int current) {
        // 处理最大值为0的情况，避免除以0
        if (max <= 0) {
            throw new IllegalArgumentException("最大值小于等于 0");
        }
        // 计算百分比，并限制在 [0,1] 范围内
        return Math.max(
                0.0f,
                Math.min(1.0f, ((float) current) / max)
        );
    }
}