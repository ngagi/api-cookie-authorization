import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.util.Date;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$x;
import static io.restassured.RestAssured.given;

public class AuthorizeWithRestCookiesTest {

    @Test
    public void authorizeWithRestCookiesTest() {
        Selenide.open("https://at-sandbox.workbench.lanit.ru/tickets");
        String csrfToken = WebDriverRunner.getWebDriver().manage().getCookieNamed("csrftoken").getValue();

        String sessionId =
        given()
                .contentType(ContentType.MULTIPART)
                .cookie("csrftoken", csrfToken)
                .multiPart("username", "admin")
                .multiPart("password", "adminat")
                .multiPart("next", "/")
                .multiPart("csrfmiddlewaretoken", csrfToken)
                .post("https://at-sandbox.workbench.lanit.ru/login/")
        .then()
                .log().all().extract().cookie("sessionid");

        Date expirationDate = new Date();
        expirationDate.setTime(expirationDate.getTime() + (10000 * 10000)); // added enough time not to expire cookie

        Cookie cookie = new Cookie("sessionid", sessionId,
                "at-sandbox.workbench.lanit.ru", "/", expirationDate);

        WebDriverRunner.getWebDriver().manage().addCookie(cookie);
        Selenide.refresh(); // to apply cookie
        $x("//a[@id='userDropdown']").shouldHave(text("admin"));
    }
}
