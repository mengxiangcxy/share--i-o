package com.shinemo.share.api;

import com.shinemo.share.client.domain.Demo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestDemo {

    @Test
    public void test1() {
        System.out.println("test");
    }


    @Test
    public void testStatic() {
        Demo demo = Demo.builder()
                .i(5)
                .build();
        demo = demo.toBuilder()
                .i(10)
                .build();
        System.out.println(demo.getI());
    }
}
