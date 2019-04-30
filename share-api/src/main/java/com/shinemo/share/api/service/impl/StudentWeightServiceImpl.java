package com.shinemo.share.api.service.impl;

import com.shinemo.share.api.service.StudentService;
import com.shinemo.share.client.domain.Student;
import org.springframework.stereotype.Service;

@Service("studentWeightService")
public class StudentWeightServiceImpl implements StudentService {
    /**
     * 体重大于100斤的学生
     * @param student
     * @return
     */
    @Override
    public boolean test(Student student) {
        return student.getWeight() > 100;
    }
}
