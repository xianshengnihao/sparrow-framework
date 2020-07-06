package com.sina.sparrowframework.metadata;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PageTest {
    @Test
    public void testCreatePage() {
        List<Foo> records = new ArrayList<>(2);
        records.add(new Foo("1", 2L));
        records.add(new Foo("2", 1L));

        Page<Foo> page = new Page<>(records, 10, 10, 1);
        Assert.assertEquals(page.getTotalPage(), 1);
    }

    private static class Foo {
        private String a;
        private Long b;

        public Foo(String a, Long b) {
            this.a = a;
            this.b = b;
        }

        public String getA() {
            return a;
        }

        public Foo setA(String a) {
            this.a = a;
            return this;
        }

        public Long getB() {
            return b;
        }

        public Foo setB(Long b) {
            this.b = b;
            return this;
        }
    }
}
