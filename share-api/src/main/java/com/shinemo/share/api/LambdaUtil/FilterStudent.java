package com.shinemo.share.api.LambdaUtil;

import com.shinemo.share.api.function.StudentPredicate;
import com.shinemo.share.client.domain.Student;

import java.util.ArrayList;
import java.util.List;

public class FilterStudent {

    public static List<Student> filter(List<Student> studentList, StudentPredicate<Student> p) {
        List<Student> result = new ArrayList<>();
        for (Student student : studentList) {
            if (p.test(student)) {
                result.add(student);
            }
        }
        return result;
    }

    public static boolean isYounger(Student student) {
        return student.getAge() > 10;
    }

}
