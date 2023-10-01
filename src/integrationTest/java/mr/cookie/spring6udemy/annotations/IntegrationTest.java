package mr.cookie.spring6udemy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(
        printOnlyOnFailure = false,
        webClientEnabled = false,
        webDriverEnabled = false
)
@ActiveProfiles(profiles = {"test"})
public @interface IntegrationTest {
}
