import axios from "axios";
import { AppConfig } from "../config/AppConfig";

const API_URL = AppConfig.backendUrl;

export const sendVerificationCodeOnEmail = async (email) => {
  try {
    const response = await axios.patch(
      `${API_URL}/api/v1/authentication/verification/${email}`
    );
    if (response.status !== 200) {
      throw new Error("Failed to send verification code");
    }
  } catch (error) {
    console.error("Error sending verification code:", error);
    throw error;
  }
};

export const verifyEmailCode = async (email, code) => {
  try {
    const response = await axios.patch(
      `${API_URL}/api/v1/authentication/verification/${email}/${code}`
    );
    return { isVerified: response.data };
  } catch (error) {
    console.error("Error verifying email code:", error);
    if (error.response) {
      return error.response.data;
    }
  }
};

export const signup = async (request) => {
  try {
    const response = await axios.post(
      `${API_URL}/api/v1/authentication/signup`,
      request,
      {
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error during signup:", error);
    throw error;
  }
};

export const signupWithGoogleAuth = async (request) => {
  try {
    const response = await axios.post(
      `${API_URL}/api/v1/authentication/signup/google`,
      request,
      {
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error during signup with Google:", error);
    throw error;
  }
};

export const signin = async (request) => {
  try {
    const response = await axios.post(
      `${API_URL}/api/v1/authentication/login`,
      request,
      {
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error during login:", error);
    throw error;
  }
};

export const sendResetPasswordLinkOnEmail = async (email) => {
  try {
    const response = await axios.patch(
      `${API_URL}/api/v1/authentication/password-reset/${email}`
    );
    if (response.data !== true) {
      throw new Error("Failed to send reset password link");
    }
  } catch (error) {
    console.error("Error sending reset password link:", error);
    throw error;
  }
};

export const changePassword = async (uid, token, password) => {
  try {
    const response = await axios.patch(
      `${API_URL}/api/v1/authentication/password/change`,
      { password },
      { params: { uid, token } },
      {
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
    if (response.data !== true) {
      throw new Error("Failed to change password");
    }
  } catch (error) {
    console.error("Error changing password:", error);
    throw error;
  }
};
