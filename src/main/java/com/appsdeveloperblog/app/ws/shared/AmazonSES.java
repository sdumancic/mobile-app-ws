package com.appsdeveloperblog.app.ws.shared;


import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;

@Service
public class AmazonSES {

	// This email must be verified with Amazon SES
	final String FROM = "sanjin.dumancic@yahoo.com";
	final String SUBJECT = "One last step to complete your registration with PhotoApp";
	
	final String PASSWORD_RESET_SUBJECT = "Password reset request";

	final String HTMLBODY = "<h1> Please verify your email address</h1>"
			+ "<p> Thank you for registering with our mobile app. To complete the registration and be able to log in you must verify this email address"
			+ " by clicking on the following link:"
			+ "<a href='http://localhost:8080/verification-service/email-verification.html?token=$tokenValue'>"
			+ "Final step to complete your registration" + "</a><br/><br/>"
			+ "Thank you! And we are waiting for you inside";

	final String TEXTBODY = "Please verify your email address "
			+ "Thank you for registering with our mobile app. To complete the registration and be able to log in you must verify this email address"
			+ " by opening the following link in your browser window: "
			+ "http://localhost:8080/verification-service/email-verification.html?token=$tokenValue"
			+ "Final step to complete your registration" + " Thank you! And we are waiting for you inside";
	
	final String PASSWORD_RESET_HTMLBODY = "<h1> A request to reset your password</h1>"
			+ "<p> Hi, $firstName! </p>"
			+ "<p> Someone has requested to reset your password with our project. If it were not you please ignore it."
			+ " Otherwise please click on the link below to set a new password: "
			+ "<a href='http://localhost:8080/verification-service/password-reset.html?token=$tokenValue'>"
			+ " Click this link to Reset Password"
			+ "</a><br/><br/>"
			+ "Thank you!";

	final String PASSWORD_RESET_TEXTBODY = "A request to reset your password"
			+ "Hi, $firstName!"
			+ "Someone has requested to reset your password with our project. If it were not you please ignore it."
			+ " Otherwise please click on the link below to set a new password: "
			+ "http://localhost:8080/verification-service/password-reset.html?token=$tokenValue"
			+ " Click this link to Reset Password"
			+ "Thank you!";

	public void verifyEmail(UserDto userDto) {
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_WEST_1)
				.build();
		String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", userDto.getEmailVerificationToken());
		String textBodyWithToken = TEXTBODY.replace("$tokenValue", userDto.getEmailVerificationToken());

		SendEmailRequest request = new SendEmailRequest()
				.withDestination(new Destination().withToAddresses(userDto.getEmail()))
				.withMessage(new Message()
								.withBody(new Body()
											.withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
											.withText(new Content().withCharset("UTF-8").withData(textBodyWithToken))
										 )
								.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
				.withSource(FROM);
		client.sendEmail(request);
		
		System.out.println("email sent");
	}

	public boolean sendPasswordResetRequest(String firstName, String email, String token) {
		
		boolean returnValue = false;
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
					.standard()
					.withRegion(Regions.EU_WEST_1)
					.build();
		String  htmlBodyWithToken = PASSWORD_RESET_HTMLBODY.replace("$tokenValue", token);
				htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", firstName);
		String  textBodyWithToken = PASSWORD_RESET_TEXTBODY.replace("$tokenValue", token);
				textBodyWithToken = textBodyWithToken.replace("$firstName", firstName);
				
		SendEmailRequest request = new SendEmailRequest()
				.withDestination(new Destination().withToAddresses(email))
				.withMessage(new Message()
								.withBody(new Body()
											.withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
											.withText(new Content().withCharset("UTF-8").withData(textBodyWithToken))
										 )
								.withSubject(new Content().withCharset("UTF-8").withData(PASSWORD_RESET_SUBJECT)))
				.withSource(FROM);		

		SendEmailResult result = client.sendEmail(request);
		if (result != null && result.getMessageId()!= null && !result.getMessageId().isEmpty()){
			returnValue = true;
		}
		
		return returnValue;
		
	}

}
