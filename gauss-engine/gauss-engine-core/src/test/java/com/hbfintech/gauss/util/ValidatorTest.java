package com.hbfintech.gauss.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ValidatorTest {

    @Test
    public void factoryValidationTest() {
        boolean flag = Validator.checkIfFactory(MultiLayersFactory.class);
        Assert.assertTrue(flag);
    }
}
