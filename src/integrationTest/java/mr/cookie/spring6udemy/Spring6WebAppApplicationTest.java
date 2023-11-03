package mr.cookie.spring6udemy;

import static org.assertj.core.api.Assertions.assertThat;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(profiles = {"test"})
class Spring6WebAppApplicationTest {

    @Test
    void contextLoads(@NotNull ApplicationContext context) {
        assertThat(context).isNotNull();
    }

}
