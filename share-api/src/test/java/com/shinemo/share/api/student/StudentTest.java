package com.shinemo.share.api.student;

import com.shinemo.share.api.Application;
import com.shinemo.share.api.LambdaUtil.FilterStudent;
import com.shinemo.share.api.LambdaUtil.StudentCollector;
import com.shinemo.share.api.function.StudentPredicate;
import com.shinemo.share.api.service.impl.StudentAgeServiceImpl;
import com.shinemo.share.api.service.impl.StudentWeightServiceImpl;
import com.shinemo.share.client.domain.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class StudentTest {

    @Resource
    private StudentAgeServiceImpl studentAgeService;
    @Resource
    private StudentWeightServiceImpl studentWeightService;

    private static List<Student> studentList = new ArrayList<>();

    static {
        Student student = new Student("tom", 21, 90);
//        Student student1 = new Student("anni", 18, 80);
        Student student2 = new Student("hello", 25, 120);
//        Student student3 = new Student("jone", 15, 101);
        Student student4 = new Student("tony", 22, 99);
        Student student5 = new Student("son", 22, 120);
        Student student6 = new Student("son", 22, 120);
        studentList.add(student);
//        studentList.add(student1);
        studentList.add(student2);
//        studentList.add(student3);
        studentList.add(student4);
        studentList.add(student5);
        studentList.add(student6);
    }

    @Test
    public void testCycle() {
        //内部迭代
        studentList.forEach(student -> {
            boolean test = studentAgeService.test(student);
            System.out.println(test);
        });
        //外部迭代
        for (Student student : studentList) {
            boolean test = studentWeightService.test(student);
            System.out.println(test);
        }
        for (int i = 0; i < 10; i++) {

        }
    }

    @Test
    public void test1() {
//        int[] ints = IntStream.rangeClosed(1, 100000).toArray();
        long time = System.currentTimeMillis();
        IntStream.rangeClosed(1, 100000).parallel().forEach(System.out::println);
        System.out.println(System.currentTimeMillis() - time);
    }

    @Test
    public void test2() {
        int[] ints = IntStream.rangeClosed(1, 100000).toArray();
        long time = System.currentTimeMillis();
        for (int i : ints) {
            System.out.println(i);
        }
        System.out.println(System.currentTimeMillis() - time);
    }

    @Test
    public void testPredicate() {
        //匿名内部类
//        List<Student> list2 = FilterStudent.filter(studentList, new StudentPredicate<Student>(){
//            @Override
//            public boolean test(Student student) {
//                return student.getAge() < 20;
//            }
//        });
        //获取名字中含t的学生
        List<Student> list = FilterStudent.filter(studentList, s -> s.getName().contains("t"));
        list.forEach(System.out::println);
        System.out.println("=========lambda");
        //获取年龄>20体重大于100的学生
        List<Student> list1 = FilterStudent.filter(studentList, s -> s.getAge() > 20 && s.getWeight() > 100);
        list1.forEach(System.out::println);
        System.out.println("=========lambda");
        //方法引用
//        Predicate<Student> isYounger = FilterStudent::isYounger;
//        isYounger.and(FilterStudent.filter())  isYounger.or()   谓词复合
        List<Student> lists = FilterStudent.filter(studentList, FilterStudent::isYounger);
        lists.forEach(System.out::println);
        System.out.println("==========方法引用");
        //java8
//        Predicate   T -> true/false
        studentList.stream().filter(s -> s.getAge() < 20).collect(toList()).forEach(System.out::println);
        System.out.println("==========java8");

    }

    /**
     * 中间操作和终端操作，没有终端操作时中间操作不执行
     */
    @Test
    public void testMiddleOrEnd() {
        List<Student> ss = studentList.stream().filter(s -> {
            System.out.println("执行了");
            return s.getWeight() > 100;
        }).peek(System.out::println).limit(2).collect(toList());
    }

    /**
     * 筛选和切片
     */
    @Test
    public void testFilter() {
        List<Student> collect = studentList.stream()
                .filter(s -> s.getAge() > 20)
                .distinct()
                .peek(System.out::println)
                .collect(toList());
    }

    /**
     * 映射
     */
    @Test
    public void testMapping() {
//        Function  Stream<String>
        List<String> collect = studentList.stream().map(Student::getName).collect(toList());
        collect.forEach(System.out::println);
    }

    /**
     * 查找和匹配
     * allMatch、anyMatch、noneMatch、findFirst和findAny
     */
    @Test
    public void testFindOrMatch() {
        Optional<Student> first = studentList.stream()
                .filter(FilterStudent::isYounger)
                .findFirst();
        Student ss = new Student("test", 10, 100);
        System.out.println("==== init" + ss);
        Student student = first.orElse(ss = new Student());
        System.out.println(student);
        System.out.println("==== new init" + ss);
        System.out.println("=========orElse");
        Optional<Student> any = studentList.stream()
                .filter(FilterStudent::isYounger)
                .findAny();
//        Supplier
        Student testStudent = any.orElseGet(() -> {
            System.out.println("===");
            return new Student();
        });
        System.out.println("========orElseGet");

        boolean b = studentList.stream()
                .anyMatch(s -> s.getName().equals("sss"));
        System.out.println(b);
    }

    /**
     * 规约
     * reduce 三个参数的变形，第三个函数只有在并行的时候会被触发，但要注意并行来带的重复等问题
     */
    @Test
    public void testReduce() {
        String str = studentList.stream()
                .filter(FilterStudent::isYounger)
                .map(Student::getName)
//                .parallel()
                .reduce("init", this::test111, (s1, s2) -> {
                    System.out.println("========BinaryOperator" + (s1 + s2));
                    return s1 + s2 + "====chd";
                });
//        BinaryOperator
//        BiFunction
        System.out.println(str);
//        String reduce = studentList.stream()
//                .reduce("init", this::test11, (a1, a2) -> String.format("%s,%s", a1, a2));
//        System.out.println(reduce);
    }

    private String test11(String s1, Student s2) {
        String s = s1 + "_" + s2.getName();
        System.out.println("====" + s);
        return s;
    }

    private String test111(String s, String s1) {
        String s2 = s + "_" + s1;
        System.out.println("=====s2" + s2);
        return s2;
    }

    /**
     * 分组和分区
     */
    @Test
    public void testGroup() {
        //分组
        Map<Integer, List<Student>> collect = studentList.stream().collect(groupingBy(Student::getAge));
        Map<Integer, Set<String>> collect3 = studentList.stream().collect(groupingBy(Student::getAge, mapping(Student::getName, toSet())));
        Map<Integer, Student> collect1 = studentList.stream().collect(toMap(Student::getAge, Function.identity()));
        Map<Integer, String> collect4 = studentList.stream().collect(toMap(Student::getAge, Student::getName));
        //分区
        Map<Boolean, List<Student>> collect2 = studentList.stream().collect(partitioningBy(FilterStudent::isYounger));
    }

    /**
     * 自定义收集器
     */
    @Test
    public void testCustomCollector() {
        Map<Integer, String> collect = studentList.parallelStream().collect(new StudentCollector());
        collect.forEach((k, v) -> System.out.println("key:" + k + "value:" + v));
    }

    @Test
    public void testOptional() {
//        Student student = null;
        Student student = new Student();
        student.setAge(11);
        Optional.ofNullable(student).ifPresent(System.out::println);
    }

    /**
     * java8实现异步API
     */
    @Test
    public void testAsync() {
//        LocalDate
//        LocalTime
        LocalDateTime startTime = LocalDateTime.now();
//        Runtime.getRuntime().availableProcessors()
        List<CompletableFuture> futureList = IntStream.rangeClosed(1, 5).boxed()
                .map(i -> CompletableFuture.supplyAsync(() -> thread(i)))
                .collect(toList());
        futureList.forEach(future -> System.out.println(future.join()));
        Duration duration = Duration.between(startTime, LocalDateTime.now());
        long second = duration.getSeconds();
        System.out.println(second);
        long millis = duration.toMillis();
        System.out.println(millis);
    }

    /**
     * thenAccept 在获取到异步执行的结果后进行处理，
     * allOf() 接受一个CompletableFuture[]
     */
    @Test
    public void testThenAcceptAllOf() {
        CompletableFuture[] array = IntStream.rangeClosed(1, 5).boxed()
                .map(i -> CompletableFuture.supplyAsync(() -> thread(i)))
                .map(future -> future.thenAccept(System.out::println))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(array).join();

//        Arrays.stream(array).forEach(CompletableFuture::join);

    }

    /**
     * anyOf() 接受一个CompletableFuture[]
     * 这个跟allOf()的区别是，只要有一个结果返回时，就不再获取其他的结果，调用join()方法，返回最快的那一个
     */
    @Test
    public void testThenAcceptAnyOf() {
        CompletableFuture[] array = IntStream.rangeClosed(1, 5).boxed()
                .map(i -> CompletableFuture.supplyAsync(() -> thread(i)))
                .map(future -> future.thenAccept(System.out::println))
                .toArray(CompletableFuture[]::new);
        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(array);
        Object join = anyOf.join();
        System.out.println(join);
    }

    @Test
    public void testCompletable() {
        //thenApply()  对异步调用返回的结果进行转换  参数为一个Function函数
        String str = CompletableFuture.supplyAsync(this::thread)
                .thenApply(String::valueOf).join();
        System.out.println("apply:" + str);
        //thenCompose()  将异步调用返回的结果作为另一个异步的入参
        String join = CompletableFuture.supplyAsync(this::thread)
                .thenCompose(l -> CompletableFuture.supplyAsync(() -> reduce(l)))
                .join();
        System.out.println("compose:" + join);
        //thenCombine()  将两个异步执行的结果都作为参数，交给第二个参数BiFunction<T, U, R>处理
        Long aLong = CompletableFuture.supplyAsync(this::thread)
                .thenCombineAsync(CompletableFuture.supplyAsync(this::thread), (l1, l2) -> l1 - l2).join();
        System.out.println("combine:" + aLong);

    }

    private long thread(Integer i) {
        Random random = new Random();
        long time = 500 + random.nextInt(5000);
        try {
            Thread.sleep(time);
            System.out.println("=====我是第" + i + "个执行的，一共等待了:" + time + "秒");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return time;
    }

    private Long thread() {
        Random random = new Random();
        long time = 500 + random.nextInt(5000);
        try {
            Thread.sleep(time);
            System.out.println("=====我执行了，一共等待了:" + time + "秒");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return time;
    }

    private String reduce(Long arg) {
        return arg * arg + "";
    }


}
