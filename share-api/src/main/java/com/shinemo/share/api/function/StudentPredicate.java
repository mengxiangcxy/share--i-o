package com.shinemo.share.api.function;

/**
 * 自定义函数式接口
 * @param <T>
 */
@FunctionalInterface
public interface StudentPredicate<T> {

    boolean test(T t);

}
