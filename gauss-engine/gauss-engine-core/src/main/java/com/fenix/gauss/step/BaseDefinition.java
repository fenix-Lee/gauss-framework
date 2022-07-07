package com.fenix.gauss.step;

import com.fenix.gauss.basis.BeanFactory;
import com.fenix.gauss.infrastructure.Binomial;
import com.fenix.gauss.infrastructure.GaussDistribution;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.ClassUtils;
import org.junit.Assert;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@CucumberContextConfiguration
public class BaseDefinition {

    private Binomial po;

    @Given("insert following {string} in database by using {string}")
    @SuppressWarnings("unchecked")
    public void insertData(String beanName, String name, List<List<String>> table)
            throws Exception {
        Class<?> clazz = BeanFactory.getBean(beanName).getClass();
        GaussDistribution<?, Binomial> repository = (GaussDistribution<?, Binomial>) BeanFactory.getBean(name);
        List<String> columNames = table.get(0);
        for (int i = 1; i < table.size(); i++) {
            List<String> data = table.get(i);
            Binomial base = (Binomial) getInstance(clazz);
            for (int j = 0; j < columNames.size(); j++) {
                assert base != null;
                ReflectionTestUtils.invokeSetterMethod(base, columNames.get(j),
                        getValue(clazz, columNames.get(j), data.get(j)));
            }
            ReflectionTestUtils.invokeMethod(repository, "insert", base);
        }

    }

    @When("I search this flow, which id is {long} with {string}")
    @SuppressWarnings("unchecked")
    public void searchFlow(long id, String name) {
        GaussDistribution<?, Binomial> repository = (GaussDistribution<?, Binomial>) BeanFactory.getBean(name);
        Optional<?> po = ReflectionTestUtils.invokeMethod(repository, "queryEntity", id);
        Assert.assertTrue(null != po && po.isPresent());
        this.po = (Binomial) po.get();
    }

    @Then("I am being told the field {string} is {string}")
    public void beToldAssertion(String field, String value) {
        Assert.assertEquals(ReflectionTestUtils.getField(po,field), value);
    }

    private Object getInstance(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    private Object getValue(Class<?> clazz, String fieldName, String value) throws Exception {
        Class<?> typeClazz;
        Field field = ReflectionUtils.findField(clazz, fieldName);
        assert field != null;
        if ((typeClazz = field.getType()).isPrimitive()) {
            Class<?> wrapper = ClassUtils.primitiveToWrapper(typeClazz);
            return ConvertUtils.convert(value, wrapper);
        }

        if (typeClazz.equals(String.class)) {
            return value;
        }

        if (!ObjectUtils.isEmpty(ClassUtils.wrapperToPrimitive(typeClazz))) {
            return ConvertUtils.convert(value, typeClazz);
        }

        // another converter e.g. java.util.Date .....
        return clazz.getDeclaredConstructor(String.class).newInstance(value);
    }
}
