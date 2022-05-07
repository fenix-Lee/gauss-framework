package com.hbfintech.gauss.step;

import com.hbfintech.gauss.util.BeanFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.junit.Assert.assertEquals;

//@CucumberContextConfiguration
//@SpringBootTest
public class BaseDefinition {
    private String today;

    private String answer;

    @Given("today is {string}")
    public void today_is(String today) {
        Object obj = BeanFactory.acquireBean("repayFlowRepository");
        this.today = today;
    }

    @When("I ask whether it's Friday yet")
    public void i_ask_whether_it_s_Friday_yet() {
        answer = "Friday".equals(today) ? "TGIF" : "Nope";
    }

    @Then("I should be told {string}")
    public void i_should_be_told(String expectedAnswer) {
        assertEquals(expectedAnswer, answer);
    }
}
