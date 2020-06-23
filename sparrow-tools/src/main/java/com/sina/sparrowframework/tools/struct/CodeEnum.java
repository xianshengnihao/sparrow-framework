package com.sina.sparrowframework.tools.struct;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 所有要持久化的枚举的基接口.
 * 这个设计是要避免 将 枚举的 {@link Enum#ordinal()} 持久化到数据库,以造成代码的改动不变
 * <p>
 * 在 mybatis 中使用时,请注意在 在语句中加上
 * <ol>
 * <li>javaType=你的枚举类全称</li>
 * <li>{@code typeHandler=com.sina.sparrowframework.mybatis.typehandler.SparrowEnumTypeHandler}</li>
 * <li></li>
 * </ol>
 * ,否则将出错.
 * 尤其是 update 和 delete 语句,因为 select 可用 resultMap 返回,insert 可使用 实体作为 parameterType
 * </p>
 */
public interface CodeEnum extends Compare<CodeEnum> {


    Set<Class<?>> subClass = Collections.newSetFromMap(new ConcurrentHashMap(50));

    /**
     * @return 用于持久化到数据库中的 code
     */
    int code();

    /**
     * 枚举的 name
     */
    String name();

    /**
     * @return 用于展示到前端的名称
     */
    String display();


    default CodeEnum family() {
        return this;
    }


    @Override
    default CompareResult compareWith(CodeEnum o) {
        return CompareResult.resolve(this.code() - o.code());
    }

    static CompareResult compare(CodeEnum o1, CodeEnum o2) {
        return CompareResult.resolve(o1.code() - o2.code());
    }

    @SuppressWarnings("unchecked")
    static <T extends Enum<T> & CodeEnum> T getRootFamily(T codeEnum) {
        CodeEnum family = codeEnum;
        int count = 0;
        for (; ; count++) {
            if (family.family() == null
                    || family == family.family()) {
                break;
            }
            family = family.family();
            if (count > 1000) {
                throw new IllegalStateException(String.format
                        ("CodeEnum family Illegal,%s", codeEnum.getClass().getName()));
            }
        }
        return (T) family;
    }

    /**
     * @return true 表示 codeEnum 是  targetFamily 家族的一员
     */
    static boolean isFamily(CodeEnum codeEnum, CodeEnum targetFamily) {
        if (codeEnum == null
                || targetFamily == null
                || targetFamily.getClass() != codeEnum.getClass()) {
            return false;
        }
        boolean match = false;
        CodeEnum family = codeEnum;
        int count = 0;
        for (; ; count++) {
            if (family == targetFamily) {
                match = true;
                break;
            }
            if (family.family() == null
                    || family == family.family()) {
                break;
            }
            family = family.family();
            if (count > 1000) {
                throw new IllegalStateException(String.format
                        ("CodeEnum family Illegal,%s", codeEnum.getClass().getName()));
            }
        }
        return match;
    }


    static <T extends Enum<T> & CodeEnum> Map<Integer, T> createCodeMap(Class<T> clazz) {
        CodeEnumHelper.assertCodeEnum(clazz);

        Map<Integer, T> map = CodeEnumHelper.getMap(clazz);

        if (map != null) {
            return map;
        }

        T[] types = clazz.getEnumConstants();
        map = new HashMap<>((int) (types.length / 0.75f));

        for (T type : types) {
            if (map.containsKey(type.code())) {
                throw new CodeEnumException(String.format("Enum[%s] code[%s]duplicate", clazz.getName(), type.code()));
            }
            map.put(type.code(), type);
        }
        map = Collections.unmodifiableMap(map);
        CodeEnumHelper.addMap(clazz, map);
        return map;
    }

}
