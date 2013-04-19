package controllers.forms;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

public class LoginForm {

	@Required
	@Email
	private String email;
	@Required
	@MinLength(4)
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String _email) {
		email = _email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String _password) {
		password = _password;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
