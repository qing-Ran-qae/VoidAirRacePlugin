package io.github.hhn756.voidairrace.infrastructure.util.schedulingutil;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

/**
 * Bukkit 调度器的简单包装
 * */
public class SchedulingUtil {
    private SchedulingUtil() {}

    private static JavaPlugin mainClass;
    private static BukkitScheduler bukkitScheduler;

    static void init(JavaPlugin mainClass) {
        SchedulingUtil.mainClass = mainClass;
        bukkitScheduler = Bukkit.getScheduler();
    }

    /**
     * 在主线程执行一个任务
     *
     * @param runnable 要执行的任务
     *
     * @return Bukkit 任务
     *
     * @see BukkitTask
     * */
    public static BukkitTask runOnMainThread(Runnable runnable) {
        return bukkitScheduler.runTask(mainClass, runnable);
    }

    /**
     * 异步执行一个任务
     *
     * @param runnable 要执行的任务
     *
     * @return Bukkit 任务
     *
     * @see BukkitTask
     * */
    public static BukkitTask runTaskAsync(Runnable runnable) {
        return bukkitScheduler.runTaskAsynchronously(mainClass, runnable);
    }
}
