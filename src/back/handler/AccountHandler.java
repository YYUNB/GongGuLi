package back.handler;

import java.io.*;
import java.net.Socket;

import back.ResponseCode;
import back.dao.user.FindUserDAO;
import back.dao.user.LogInDAO;
import back.dao.user.SignUpDAO;
import back.request.account.FindUserIdRequest;
import back.request.account.FindUserPasswordRequest;
import back.request.account.LoginRequest;
import back.request.account.SignUpRequest;
import back.response.account.FindUserIdResponse;
import back.response.account.FindUserPasswordResponse;
import back.response.account.LoginResponse;

public class AccountHandler extends Thread {
	private ObjectInputStream objectInputStream = null;
	private ObjectOutputStream objectOutputStream = null;

	public AccountHandler(Socket clientSocket) {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);

            OutputStream outputStream = clientSocket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                objectInputStream.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
	}

	/*사용자 Request를 받는 메소드*/
	@Override
	public void run() {
		try {
			Object readObj = objectInputStream.readObject();

			if (readObj instanceof SignUpRequest signUpRequest) {
				SignUpMethod(signUpRequest);
			} else if (readObj instanceof LoginRequest loginRequest) {
				LoginMethod(loginRequest);
			} else if (readObj instanceof FindUserIdRequest findUserIdRequest) {
				FindUserIdMethod(findUserIdRequest);
			} else if (readObj instanceof FindUserPasswordRequest findUserPasswordRequest) {
				FindUserPasswordMethod(findUserPasswordRequest);
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	/*회원가입 Response를 보내는 메소드*/
	private void SignUpMethod(SignUpRequest signUpRequest) {
		try {
			SignUpDAO signUpDAO = new SignUpDAO();

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
				signUpDAO.signUp(signUpRequest);
				objectOutputStream.writeObject(ResponseCode.SIGNUP_SUCCESS);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	/*로그인 Response를 보내는 메소드*/
	private void LoginMethod(LoginRequest loginRequest) {
		try {
			LogInDAO logInDAO = new LogInDAO();

			if (loginRequest.userId().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.ID_MISSING);
			} else if (loginRequest.password().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.PASSWORD_MISSING);
			} else {
				String logInCheckResult = logInDAO.logIn(loginRequest);
				if (logInCheckResult.equals("Password Does Not Match")) {
					objectOutputStream.writeObject(ResponseCode.PASSWORD_MISMATCH_LOGIN);
				} else if (logInCheckResult.equals("Id Does Not Exist")) {
					objectOutputStream.writeObject(ResponseCode.ID_NOT_EXIST);
				} else if (!logInCheckResult.equals("Database or SQL Error")) {
					objectOutputStream.writeObject(ResponseCode.LOGIN_SUCCESS);
					objectOutputStream.writeObject(new LoginResponse(logInCheckResult));
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	/*아이디 찾기 Response를 보내는 메소드*/
	private void FindUserIdMethod(FindUserIdRequest findUserIdRequest) {
		try {
			FindUserDAO findUserDAO = new FindUserDAO();

			if (findUserIdRequest.name().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.NAME_MISSING);
			} else if (findUserIdRequest.birth().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.BIRTHDAY_MISSING);
			} else if (findUserIdRequest.phoneNumber().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.PHONE_NUMBER_MISSING);
			} else {
				FindUserIdResponse findUserIdResponse = new FindUserIdResponse(findUserDAO.findID(findUserIdRequest));

				if (!findUserIdResponse.userId().isEmpty()) {
					objectOutputStream.writeObject(ResponseCode.FIND_ID_SUCCESS);
					objectOutputStream.writeObject(findUserIdResponse);
				} else {
					objectOutputStream.writeObject(ResponseCode.NO_MATCHING_USER);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	/*비밀번호 찾기 Response를 보내는 메소드*/
	private void FindUserPasswordMethod(FindUserPasswordRequest findUserPasswordRequest) {
		try {
			FindUserDAO findUserDAO = new FindUserDAO();

			if (findUserPasswordRequest.name().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.NAME_MISSING);
			} else if (findUserPasswordRequest.userId().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.ID_MISSING);
			} else if (findUserPasswordRequest.birth().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.BIRTHDAY_MISSING);
			} else if (findUserPasswordRequest.phoneNumber().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.PHONE_NUMBER_MISSING);
			} else {
				FindUserPasswordResponse findUserPasswordResponse = new FindUserPasswordResponse(findUserDAO.findPassword(findUserPasswordRequest));

				if (!findUserPasswordResponse.password().isEmpty()) {
					objectOutputStream.writeObject(ResponseCode.FIND_PASSWORD_SUCCESS);
					objectOutputStream.writeObject(findUserPasswordResponse);
				} else {
					objectOutputStream.writeObject(ResponseCode.NO_MATCHING_USER);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
}