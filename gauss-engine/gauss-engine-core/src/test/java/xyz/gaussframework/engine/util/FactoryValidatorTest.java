package xyz.gaussframework.engine.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FactoryValidatorTest {

    @Test
    public void factoryValidationTest() {
        boolean flag = FactoryValidator.checkIfFactory(MultiLayersFactory.class);
        Assert.assertTrue(flag);
    }
}
