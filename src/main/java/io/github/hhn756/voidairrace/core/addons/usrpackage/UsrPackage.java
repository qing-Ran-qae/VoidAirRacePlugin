package io.github.hhn756.voidairrace.core.addons.usrpackage;

import org.jspecify.annotations.NonNull;

import java.nio.file.Path;

/**
 * 代表一个用户包并记录其基本信息
 * */
public class UsrPackage {
    /** 用户包的Id，重复Id不可加载 */
    private final @NonNull String id;
    /** 入口点脚本路径，相对包位置 */
    private final @NonNull String entryPoint;
    /** 包的位置，包文件（.zip）相对服务器包目录的路径 */
    private final @NonNull Path path;

    /**
     * 由包管理器自动管理，通过包管理器获取实例
     * */
    UsrPackage(
            @NonNull String id,
            @NonNull String entryPoint,
            @NonNull Path path) {
        this.id = id;
        this.entryPoint = entryPoint;
        this.path = path;
    }

    /**
     * @return 包的id，不会与其他包重复
     * */
    public @NonNull String id() {
        return id;
    }

    /**
     * @return 包的入口点脚本
     * */
    public @NonNull String entryPoint() {
        return entryPoint;
    }

    /**
     * 获取包的位置
     *
     * @return 包文件（.zip）相对服务器包目录的路径
     * */
    public @NonNull Path root() {
        return path;
    }
}
