package io.github.hhn756.voidairrace.core.playerstatemanager;

import io.github.hhn756.voidairrace.VoidAirRace;
import io.github.hhn756.voidairrace.infrastructure.util.ClassScanner;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;

public class StateRegistry {
    private static StateRegistry instance;

    static void load() {
        instance = new StateRegistry();
    }

    static void unload() {
        instance = null;
    }

    public static StateRegistry getInstance() {
        if (instance == null) throw new NullPointerException("玩家状态注册表实例不存在");
        return instance;
    }

    // ------

    /**
     * 键为状态体系名称，值为对应状态体系的元数据
     * */
    private final HashMap<String, StateSystemMeta> nameToSystemMeta = new HashMap<>();

    /**
     * 键为状态id（包含所在体系名称），值为对应状态类的单例对象
     * */
    private final HashMap<NamespacedKey, PlayerState> idToStateInstance = new HashMap<>();

    StateRegistry() {
        scanStates();
    }

    /**
     * 扫描所有状态类
     * */
    private void scanStates() {
        VoidAirRace mainClass = VoidAirRace.getInstance();
        Collection<Class<PlayerState>> stateClasses = ClassScanner.scanSubclasses(
                PlayerState.class,
                "io.github.hhn756.voidairrace.core.playerstatemanager.systems"
        );
        stateClasses.forEach(clazz -> {
            try {
                PlayerState stateInst = clazz.getConstructor().newInstance();
                NamespacedKey stateId = stateInst.getId();
                String system = stateId.getNamespace();
                String key = stateId.getKey();

                // 记录当前状态所属的体系
                if (DefaultState.class.isAssignableFrom(clazz)) {
                    if (nameToSystemMeta.containsKey(stateId.getNamespace())) {
                        mainClass.getLogger().warning(
                                "状态体系 '" + system + "' 中有重复的默认状态：" + clazz.getName()
                        );
                    } else {
                        nameToSystemMeta.put(system, new StateSystemMeta(system, key));
                    }
                }

                // 记录状态实例
                idToStateInstance.put(stateId, stateInst);

                // 如果是监听器，则注册
                if (stateInst instanceof Listener) {
                    Bukkit.getPluginManager().registerEvents((Listener) stateInst, mainClass);
                }
            } catch (NoSuchMethodException | InvocationTargetException
                     | InstantiationException| IllegalAccessException
                     | NullPointerException e) {
                mainClass.getLogger().warning(
                        "记录状态类型 \"" + clazz.getName() + "\" 时发生了异常：" + e.getMessage()
                );
            }
        });
    }

    /**
     * 获取所有状态体系<br>
     * 键为状态体系名称，值状态体系的元数据
     * */
    public HashMap<String, StateSystemMeta> getAllSystems() {
        return new HashMap<>(nameToSystemMeta);
    }

    /**
     * 获取 id 为 {@code id} 的玩家状态实例
     *
     * @param id 指定状态 id
     *
     * @return 存在对应id的状态时返回状态实例，否则返回{@code null}
     * */
    public PlayerState getStateInstance(NamespacedKey id) {
        return idToStateInstance.get(id);
    }
}
