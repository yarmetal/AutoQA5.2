package ru.netology.app_ibank;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class AuthTest {
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @BeforeEach
    void setUpEach() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Valid login and password, status active")
    void shouldLogin() {
        val user = DataGenerator.Registration.registerUser("en-US", "active");
        $("span[data-test-id='login'] input").setValue(user.getLogin());
        $("span[data-test-id='password'] input").setValue(user.getPassword());

        $("button[data-test-id='action-login']").click();
        $("body div#root h2").shouldHave(text("Личный кабинет"));
    }

    @Test
    @DisplayName("Valid login and invalid password, status active")
    void shouldNotifyThatPasswordIsIncorrectForActiveUser() {
        val user = DataGenerator.Registration.registerUser("en-US", "active");
        $("span[data-test-id='login'] input").setValue(user.getLogin());
        $("span[data-test-id='password'] input").setValue(user.getPassword() + "wrong");

        $("button[data-test-id='action-login']").click();
        $("div[data-test-id='error-notification']").shouldHave(text("Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Valid login and password, status blocked")
    void shouldNotifyThatUserBlocked() {
        val user = DataGenerator.Registration.registerUser("en-US", "blocked");
        $("span[data-test-id='login'] input").setValue(user.getLogin());
        $("span[data-test-id='password'] input").setValue(user.getPassword());

        $("button[data-test-id='action-login']").click();
        $("div[data-test-id='error-notification']").shouldHave(text("Пользователь заблокирован"));
    }

    @Test
    @DisplayName("Valid login and invalid password, status blocked")
    void shouldNotifyThatPasswordIsIncorrectForBlockedUser() {
        val user = DataGenerator.Registration.registerUser("en-US", "blocked");
        $("span[data-test-id='login'] input").setValue(user.getLogin());
        $("span[data-test-id='password'] input").setValue(user.getPassword() + "wrong");

        $("button[data-test-id='action-login']").click();
        $("div[data-test-id='error-notification']").shouldHave(text("Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Invalid login and password")
    void shouldNotifyThatLoginIsIncorrect() {
        val user = DataGenerator.Registration.registerUser("en-US", "active");
        $("span[data-test-id='login'] input").setValue("wronguser");
        $("span[data-test-id='password'] input").setValue("wrongpassword");

        $("button[data-test-id='action-login']").click();
        $("div[data-test-id='error-notification']").shouldHave(text("Неверно указан логин или пароль"));
    }
}
