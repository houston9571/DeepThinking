package com.deepthinking;

import cn.hutool.core.date.SystemClock;
import org.junit.After;
import org.junit.Before;

public class BaseTest {

    long start;

    @Before
    public void befor(){
        start = SystemClock.now();
    }

    @After
    public void after(){
        System.out.println("耗时:" + (SystemClock.now() - start) + "ms");
    }

}
