package functional.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import models.Role;
import models.User;

import org.junit.Test;

public class AdminLoginControllerTest extends AbstractLoginControllerTest {

	@Override
	public String getControllerPath() {
		return "/admin/";
	}
	
	@Override
	public String getRedirPathOnLogout() {
		return "/admin";
	}	
	
	@Test
	public void testUserLogin() {
		final User u = User.find.byEmail("test@test.test");
		assertNotNull(u);
		assertEquals(Role.user, u.getRole());
		assertInvalidPost(u.getEmail(), "test");
	}
	
}
