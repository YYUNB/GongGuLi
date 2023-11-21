package back.handler;

import java.net.Socket;

import back.ResponseCode;
import back.dao.UserDAO;
import back.request.account.Find_UserId_Request;
import back.request.account.Find_UserPassword_Request;
import back.request.account.Login_Request;
import back.request.account.SignUp_Request;
import back.response.account.Find_UserId_Response;
import back.response.account.Find_UserPassword_Response;
import back.response.account.Login_Response;

import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.UUID;

public class Account_Handler extends Thread {
	private Socket clientSocket = null;

	private InputStream inputStream = null;
	private ObjectInputStream objectInputStream = null;
	private OutputStream outputStream = null;
	private ObjectOutputStream objectOutputStream = null;
	private final UserDAO userDAO = new UserDAO();

	public Account_Handler(Socket clientSocket) {
		try {
			this.clientSocket = clientSocket;

			inputStream = clientSocket.getInputStream();
			objectInputStream = new ObjectInputStream(inputStream);

			outputStream = clientSocket.getOutputStream();
			objectOutputStream = new ObjectOutputStream(outputStream);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			Object readObj = objectInputStream.readObject();

			if (readObj instanceof SignUp_Request signUpRequest) {
				SignUpMethod(signUpRequest);
			} else if (readObj instanceof Login_Request loginRequest) {
				System.out.println("3");
				LoginMethod(loginRequest);
				System.out.println("4");
			} else if (readObj instanceof Find_UserId_Request findUserIdRequest) {
				FindUserIdMethod(findUserIdRequest);
			} else if (readObj instanceof Find_UserPassword_Request findUserPasswordRequest) {
				FindUserPasswordMethod(findUserPasswordRequest);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void SignUpMethod(SignUp_Request signUpRequest) {
		try {
			//각 조건들을 비교하여 클라이언트에 응답을 보낸다.
			if (signUpRequest.userId().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.ID_MISSING);
			} else if (signUpRequest.password().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.PASSWORD_MISSING);
			} else if (!signUpRequest.password().equals(String.valueOf(signUpRequest.passwordCheck()))) {
				objectOutputStream.writeObject(ResponseCode.PASSWORD_MISMATCH);
			} else if (signUpRequest.password().length() < 8 ||
					!signUpRequest.password().matches(".*[a-zA-Z].*") ||
					!signUpRequest.password().matches(".*\\d.*") ||
					!signUpRequest.password().matches(".*[@#$%^&*+_=!].*")) {
				objectOutputStream.writeObject(ResponseCode.PASSWORD_CONDITIONS_NOT_MET);
			} else if (signUpRequest.name().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.NAME_MISSING);
			} else if (signUpRequest.birth().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.BIRTHDAY_MISSING);
			} else if (!signUpRequest.birth().matches("\\d{6}")) {
				objectOutputStream.writeObject(ResponseCode.BIRTHDAY_CONDITIONS_NOT_MET);
			} else if (signUpRequest.phoneNumber().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.PHONE_NUMBER_MISSING);
			} else if (!signUpRequest.phoneNumber().matches("\\d{11}")) {
				objectOutputStream.writeObject(ResponseCode.PHONE_NUMBER_CONDITIONS_NOT_MET);
			} else if (signUpRequest.nickName().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.NICKNAME_MISSING);
			} else if (signUpRequest.region().equals("거주 지역")) {
				objectOutputStream.writeObject(ResponseCode.RESIDENCE_AREA_NOT_SELECTED);
			} else {
				String uuid = UUID.randomUUID().toString();
				userDAO.signUp(signUpRequest, uuid);
				objectOutputStream.writeObject(ResponseCode.SIGNUP_SUCCESS);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void LoginMethod(Login_Request loginRequest) {
		try {
			//각 조건들을 비교하여 클라이언트에 응답을 보낸다.
			if (loginRequest.userId().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.ID_MISSING);
			} else if (loginRequest.password().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.PASSWORD_MISSING);
			} else {
				String logInCheckResult = userDAO.logInCheck(loginRequest);
				if (logInCheckResult.equals("0")) {
					objectOutputStream.writeObject(ResponseCode.PASSWORD_MISMATCH_LOGIN);
				} else if (logInCheckResult.equals("-1")) {
					objectOutputStream.writeObject(ResponseCode.ID_NOT_EXIST);
				} else if (!logInCheckResult.equals("-2")) {
					objectOutputStream.writeObject(ResponseCode.LOGIN_SUCCESS);
					objectOutputStream.writeObject(new Login_Response(logInCheckResult));
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void FindUserIdMethod(Find_UserId_Request findUserIdRequest) {
		try {
			//각 조건들을 비교하여 클라이언트에 응답을 보낸다.
			if (findUserIdRequest.name().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.NAME_MISSING);
			} else if (findUserIdRequest.birth().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.BIRTHDAY_MISSING);
			} else if (findUserIdRequest.phoneNumber().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.PHONE_NUMBER_MISSING);
			} else {
				Find_UserId_Response findUserIdResponse = new Find_UserId_Response(userDAO.findID(findUserIdRequest));

				if (!findUserIdResponse.userId().isEmpty()) {
					objectOutputStream.writeObject(ResponseCode.FIND_ID_SUCCESS);
					objectOutputStream.writeObject(findUserIdResponse);
				} else {
					objectOutputStream.writeObject(ResponseCode.NO_MATCHING_USER);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void FindUserPasswordMethod(Find_UserPassword_Request findUserPasswordRequest) {
		try {
			//각 조건들을 비교하여 클라이언트에 응답을 보낸다.
			if (findUserPasswordRequest.name().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.NAME_MISSING);
			} else if (findUserPasswordRequest.userId().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.ID_MISSING);
			} else if (findUserPasswordRequest.birth().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.BIRTHDAY_MISSING);
			} else if (findUserPasswordRequest.phoneNumber().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.PHONE_NUMBER_MISSING);
			} else {
				Find_UserPassword_Response findUserPasswordResponse = new Find_UserPassword_Response(userDAO.findPassword(findUserPasswordRequest));

				if (!findUserPasswordResponse.password().isEmpty()) {
					objectOutputStream.writeObject(ResponseCode.FIND_PASSWORD_SUCCESS);
					objectOutputStream.writeObject(findUserPasswordResponse);
				} else {
					objectOutputStream.writeObject(ResponseCode.NO_MATCHING_USER);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}