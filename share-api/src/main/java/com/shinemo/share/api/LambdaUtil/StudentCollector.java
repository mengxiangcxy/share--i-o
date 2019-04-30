package com.shinemo.share.api.LambdaUtil;

import com.shinemo.share.client.domain.Student;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * 自定义收集器
 */
public class StudentCollector implements Collector<Student, Map<Integer, Student>, Map<Integer, String>> {
    //初始化累加器
    @Override
    public Supplier<Map<Integer, Student>> supplier() {
        return HashMap::new;
    }
    //规约
    @Override
    public BiConsumer<Map<Integer, Student>, Student> accumulator() {
        return (m1, s1) -> m1.put(s1.getAge(), s1);
    }
    //并行时处理 合并
    @Override
    public BinaryOperator<Map<Integer, Student>> combiner() {
        return (m1, m2) -> {
            m1.putAll(m2);
            return m1;
        };
    }
    //转换函数
    @Override
    public Function<Map<Integer, Student>, Map<Integer, String>> finisher() {
        Map<Integer, String> map = new HashMap<>();
        return m1 -> {
            m1.forEach((key, value) -> map.put(key, value.getName()));
            return map;
        };
//        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.CONCURRENT, Characteristics.UNORDERED));
    }
}
