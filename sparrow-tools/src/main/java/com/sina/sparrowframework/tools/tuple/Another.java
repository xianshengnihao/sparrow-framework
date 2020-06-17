package com.sina.sparrowframework.tools.tuple;

/**
 * 工具
 */
public class Another {

    private static final int INDEX_NOT_FOUND = -1;


    /**
     * <p>数组中是否包含着执行的因子</p>
     * @param array 数组
     * @param objectToFind  因子
     * @return
     */
    public static boolean contains(final Object[] array, final Object objectToFind) {
        return indexOf(array, objectToFind ,0) != INDEX_NOT_FOUND;
    }


    private static final int indexOf(final Object[] array, final Object objectToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for (int i = startIndex; i < array.length; i++) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }
}
