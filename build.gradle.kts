plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

group = "io.github.qingranqae"
version = "0.1"

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-api:4.26.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<JavaCompile> {
    options.encoding = "utf-8"
}

val deployDir = System.getenv("DEPLOY_DIR")?.replace("\\", "/") ?: "build/deploy"
val isCI = System.getenv("CI")?.toBoolean() ?: false

// ai写的，用于移动输出到服务端插件目录
tasks.reobfJar {
    val fileName = if (isCI) {
        "${project.name}-${project.version}-${System.getenv("GIT_COMMIT")?.substring(0, 7) ?: "SNAPSHOT"}.jar"
    } else {
        "${project.name}.jar"
    }

    // 添加调试信息
    doFirst {
        println("========================================")
        println("DEPLOY_DIR 环境变量: ${System.getenv("DEPLOY_DIR")}")
        println("处理后路径: $deployDir")
        println("文件名: $fileName")
        println("完整路径: $deployDir/$fileName")
        println("========================================")

        // 确保目录存在
        val targetDir = file(deployDir)
        println("目标目录是否存在: ${targetDir.exists()}")
        if (!targetDir.exists()) {
            println("创建目录: ${targetDir.absolutePath}")
            targetDir.mkdirs()
            println("目录创建成功: ${targetDir.exists()}")
        }
    }

    // 使用 File 构造函数而不是 layout
    outputJar.set(file("$deployDir/$fileName"))

    doLast {
        println("任务执行完成")
        println("输出文件路径: ${outputJar.get().asFile.absolutePath}")
        println("文件是否存在: ${outputJar.get().asFile.exists()}")
        println("文件大小: ${outputJar.get().asFile.length()} 字节")
    }
}