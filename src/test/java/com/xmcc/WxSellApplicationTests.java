package com.xmcc;

import com.google.common.collect.Lists;
import com.xmcc.entity.Student;
import com.xmcc.repository.StudentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WxSellApplicationTests {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void contextLoads() {
        /*List<Student> all = studentRepository.findAll();
        all.stream().forEach(System.out::println);*/

       /* studentRepository.save(new Student(null,"xoxo","女",98));*/

        /*ArrayList<Integer> integers = Lists.newArrayList(1);
        List<Student> allByIdIn = studentRepository.findAllByIdIn(integers);
        allByIdIn.stream().forEach(System.out::println);*/

       /* Student student = studentRepository.queryStudentByStudentId(1);
        System.out.println(student);*/

        Student studentByStudentId = studentRepository.getStudentByStudentId(2);
        System.out.println(studentByStudentId);
    }

}
