package com.shinemo.share.api.service.impl;

import com.shinemo.share.api.service.StudentService;
import com.shinemo.share.client.domain.Student;
import org.springframework.stereotype.Service;

@Service("studentAgeService")
public class StudentAgeServiceImpl implements StudentService {
    /**
     * 年龄大于20的学生
     * @param student
     * @return
     */
    @Override
    public boolean test(Student student) {
        return student.getAge() > 20;
    }
}
