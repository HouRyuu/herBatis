package jp.ac.jec.herBatis;

import ognl.Ognl;
import ognl.OgnlException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class HerBatisApplicationTests {

	@Test
	void contextLoads() throws OgnlException {
		Map<String, Object> map = new HashMap<>();
		map.put("name", null);
		System.out.println(Ognl.getValue("name != null", map));

	}

}
