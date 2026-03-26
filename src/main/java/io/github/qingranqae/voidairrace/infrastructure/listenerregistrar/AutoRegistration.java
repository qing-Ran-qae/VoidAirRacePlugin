package io.github.qingranqae.voidairrace.infrastructure.listenerregistrar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动注册此 Bukkit 事件监听器<br>
 * 注：如果一个监听器有多个方式注册到 Bukkit 就不要使用这个注解，因为会重复注册
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoRegistration {}