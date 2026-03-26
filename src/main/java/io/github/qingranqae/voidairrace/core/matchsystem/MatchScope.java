package io.github.qingranqae.voidairrace.core.matchsystem;

import io.github.qingranqae.voidairrace.core.arenasystem.ArenaToken;
import org.jspecify.annotations.NonNull;

import javax.lang.model.type.NullType;
import java.util.HashMap;

/**
 * 代表比赛涉及到的一个世界（竞技场）
 * */
public record MatchScope(
        /*
          竞技场借据
          */
        @NonNull ArenaToken token,
        /*
          此 “比赛涉及范围” 的标签，用于标识和操作特地世界

          键为标签名，值用于占位
          */
        @NonNull HashMap<String, NullType> tags
) {}