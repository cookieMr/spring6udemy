package mr.cookie.spring6udemy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class Spring6WebAppApplicationTests {

	@Test
	void contextLoads(ApplicationContext context) {
		Assertions.assertThat(context).isNotNull();
	}

}
