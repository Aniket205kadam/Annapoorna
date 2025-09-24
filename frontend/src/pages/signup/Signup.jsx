import React, { useEffect, useState } from "react";
import "./Signup.css";
import { IntroVideo, Logo } from "../../assets/assets";
import { GoogleLogin, GoogleOAuthProvider } from "@react-oauth/google";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCircleCheck,
  faEye,
  faEyeSlash,
} from "@fortawesome/free-solid-svg-icons";
import VerificationCodeInput from "../../components/verification/VerificationCodeInput";
import Select from "react-select";
import { useNavigate } from "react-router-dom";
import { AppConfig } from "../../config/AppConfig";
import PhoneRoleForm from "../../components/auth/PhoneRoleForm";
import { toast } from "react-toastify";
import {
  sendVerificationCodeOnEmail,
  signup,
  signupWithGoogleAuth,
  verifyEmailCode,
} from "../../service/AuthService";
import BtnLoader from "../../components/util/BtnLoader";
import useIsMobile from "../../hooks/useIsMobile";
import { useDispatch } from "react-redux";
import { setCredentials } from "../../store/authSlice";

const Signup = () => {
  const [signupRequest, setSignupRequest] = useState({
    firstname: "",
    lastname: "",
    email: "",
    phone: "",
    password: "",
    role: "",
  });
  const [showPasswordRules, setShowPasswordRules] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const options = [
    {
      value: "CUSTOMER",
      label: (
        <Option
          label={"Customer"}
          imageUrl={
            "https://res.cloudinary.com/dqfdqxtvo/image/upload/v1758449034/Generated_Image_September_21_2025_-_12_30PM_1_sovclz.png"
          }
        />
      ),
    },
    {
      value: "SELLER",
      label: (
        <Option
          label={"Seller"}
          imageUrl={
            "https://res.cloudinary.com/dqfdqxtvo/image/upload/v1758449025/Generated_Image_September_21_2025_-_12_30PM_2_q05pnv.png"
          }
        />
      ),
    },
    {
      value: "RIDER",
      label: (
        <Option
          label={"Rider"}
          imageUrl={
            "https://res.cloudinary.com/dqfdqxtvo/image/upload/v1758449038/Generated_Image_September_21_2025_-_12_32PM_ufof3v.png"
          }
        />
      ),
    },
  ];
  const navigate = useNavigate();
  const [emailStatus, setEmailStatus] = useState({
    showVerificationInput: false,
    verified: false,
    loading: false,
  });
  const [phoneStatus, setPhoneStatus] = useState({
    showVerificationInput: false,
    verified: false,
    loading: false,
  });
  const [isOAuthUsed, setIsOAuthUsed] = useState(false);
  const [showSignupComp, setShowSignupComp] = useState(true);
  const [signupState, setSignupState] = useState(false);
  const [credential, setCredential] = useState(null);
  const isMobile = useIsMobile();
  const dispatch = useDispatch();

  useEffect(() => {
    if (
      !emailStatus.showVerificationInput &&
      !phoneStatus.showVerificationInput &&
      !isOAuthUsed
    ) {
      setShowSignupComp(true);
    } else {
      setShowSignupComp(false);
    }
  }, [emailStatus, phoneStatus, isOAuthUsed]);

  useEffect(() => {
    setEmailStatus({ ...emailStatus, verified: false });
  }, [signupRequest.email]);

  useEffect(() => {
    setPhoneStatus({ ...phoneStatus, verified: false });
  }, [signupRequest.phone]);

  const handleInputChange = (e) => {
    setSignupRequest({ ...signupRequest, [e.target.name]: e.target.value });
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const googleSignupSuccess = (credentialResponse) => {
    if (credentialResponse?.credential) {
      setCredential(credentialResponse.credential);
      setIsOAuthUsed(true);
    }
  };

  const sendOtpOnEmail = async () => {
    try {
      if (!signupRequest.email) {
        toast.warn("Please enter your email address first.");
        return;
      }
      setEmailStatus({
        showVerificationInput: false,
        verified: false,
        loading: true,
      });
      await sendVerificationCodeOnEmail(signupRequest.email);
      toast.success(
        `Verification code sent successfully to ${signupRequest.email}`
      );
    } catch (error) {
      toast.error(`Failed to send verification code to ${signupRequest.email}`);
      setEmailStatus({
        showVerificationInput: false,
        verified: false,
        loading: false,
      });
      return;
    }
    setEmailStatus({
      showVerificationInput: true,
      verified: false,
      loading: false,
    });
  };

  const verifyEmail = async (code) => {
    try {
      const response = await verifyEmailCode(signupRequest.email, code);
      if (response?.isVerified) {
        toast.success(
          "You entered the correct code. Your email has been verified."
        );
        setEmailStatus({
          showVerificationInput: false,
          verified: true,
          loading: false,
        });
      } else {
        toast.warn(response.message);
      }
    } catch (error) {
      toast.error("Failed to verify your email. Please try again!");
    }
  };

  const sendOtpOnPhone = async () => {
    if (!signupRequest.phone) {
      toast.warn("Please enter your phone number first.");
      return;
    }

    try {
      setPhoneStatus({
        showVerificationInput: false,
        verified: false,
        loading: true,
      });

      // Simulate OTP sending delay
      setTimeout(() => {
        setPhoneStatus({
          showVerificationInput: true,
          verified: false,
          loading: false,
        });
      }, 4000);
    } catch (error) {
      console.error("Error sending OTP:", error);
    }
  };

  const verifyPhone = async (code) => {
    try {
      if (signupRequest.phone.substring(4) === code) {
        toast.success(
          "You entered the correct code. Your phone number has been verified."
        );
        setPhoneStatus({
          showVerificationInput: false,
          verified: true,
          loading: false,
        });
      } else {
        toast.warn("Enterd correct otp, which recide on the phone number");
      }
    } catch (error) {}
  };

  const signupHandler = async () => {
    if (!emailStatus.verified || !phoneStatus.verified) {
      toast.warn(
        "Please verify your email and phone number before signing up."
      );
      return;
    }
    setSignupState(true);
    try {
      const response = await signup(signupRequest);
      // stored user details in redux
      dispatch(
        setCredentials({
          id: response.id,
          fullName: response.fullName,
          accessToken: response.accessToken,
          isAuthenticated: true,
        })
      );
      toast.success("Signup successful! Welcome aboard.");
      setSignupRequest({
        firstname: "",
        lastname: "",
        email: "",
        phone: "",
        password: "",
        role: "",
      });
    } catch (error) {
      if (error?.response?.data.validationError) {
        error.response.data.validationError.forEach((message) => {
          toast.error(message);
        });
      } else if (error?.response?.data?.message) {
        toast.error(error.response.data.message);
      } else {
        toast.error("Failed to sign up. Please try again.");
      }
    } finally {
      setSignupState(false);
    }
  };

  const signupWithGoogle = async () => {
    if (!phoneStatus.verified) {
      toast.warn("First verify your phone number.");
      return;
    }
    try {
      const request = {
        phone: signupRequest.phone,
        role: signupRequest.role,
        token: credential,
      };
      const response = await signupWithGoogleAuth(request);
      // stored user details in redux
      dispatch(
        setCredentials({
          id: response.id,
          fullName: response.fullName,
          accessToken: response.accessToken,
          isAuthenticated: true,
        })
      );
      toast.success("Signup successful! Welcome aboard.");
      setSignupRequest({
        firstname: "",
        lastname: "",
        email: "",
        phone: "",
        password: "",
        role: "",
      });
    } catch (error) {
      if (error?.response?.data?.message) {
        toast.error(error.response.data.message);
      } else {
        toast.error("Failed to sign up. Please try again.");
      }
    } finally {
      setSignupState(false);
    }
  };

  const cancelSignupWithGoogle = () => {
    setCredential(null);
    setSignupRequest({
      firstname: "",
      lastname: "",
      email: "",
      phone: "",
      password: "",
      role: "",
    });
    setIsOAuthUsed(false);
    navigate("/login");
  };

  return (
    <div className="signup-container">
      <video autoPlay loop muted className="background-video">
        <source src={IntroVideo} type="video/mp4" />
        Your browser does not support the video tag.
      </video>

      <div className="verification-code-input">
        {emailStatus.showVerificationInput && (
          <VerificationCodeInput
            resendCode={sendOtpOnEmail}
            verifyCode={verifyEmail}
            close={() =>
              setEmailStatus({
                showVerificationInput: false,
                verified: false,
                loading: false,
              })
            }
            cardTitle={"We’ve sent a code to your email address to confirm it."}
            cardPrompt={"Enter the 6-digit code you received in your email."}
          />
        )}

        {phoneStatus.showVerificationInput && (
          <VerificationCodeInput
            resendCode={sendOtpOnPhone}
            verifyCode={verifyPhone}
            close={() =>
              setPhoneStatus({
                showVerificationInput: false,
                verified: false,
                loading: false,
              })
            }
            cardTitle={"We’ve sent a code to your number via SMS."}
            cardPrompt={
              "Enter the 6-digit code you received on number via SMS."
            }
          />
        )}
      </div>

      {isOAuthUsed && !phoneStatus.showVerificationInput && (
        <PhoneRoleForm
          options={options}
          phoneStatus={phoneStatus}
          setPhoneStatus={setPhoneStatus}
          handleInputChange={handleInputChange}
          signupRequest={signupRequest}
          setSignupRequest={setSignupRequest}
          signupWithGoogle={signupWithGoogle}
          cancelSignupWithGoogle={cancelSignupWithGoogle}
          sendOtpOnPhone={sendOtpOnPhone}
        />
      )}

      {showSignupComp && (
        <div className="signup-content">
          <div className="logo-section">
            <div className="logo-container">
              <img src={Logo} alt="Annapoorna Logo" className="logo" />
            </div>
            <p className="tagline">
              Create your account to get started with delicious food deliveries
            </p>
          </div>

          <form className="signup-form" onSubmit={(e) => e.preventDefault()}>
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="firstname">First Name:</label>
                <input
                  type="text"
                  name="firstname"
                  id="firstname"
                  placeholder="Enter your firstname"
                  value={signupRequest.firstname}
                  onChange={handleInputChange}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="lastname">Last Name:</label>
                <input
                  type="text"
                  name="lastname"
                  id="lastname"
                  placeholder="Enter your lastname"
                  value={signupRequest.lastname}
                  onChange={handleInputChange}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="email">Email:</label>
              <div className="input-with-button">
                <input
                  type="email"
                  name="email"
                  id="email"
                  placeholder="Enter your email address"
                  value={signupRequest.email}
                  onChange={handleInputChange}
                  readOnly={emailStatus.verified}
                  required
                />
                <button
                  type="button"
                  disabled={emailStatus.verified || emailStatus.loading}
                  style={
                    emailStatus.verified || emailStatus.loading
                      ? { cursor: "not-allowed" }
                      : {}
                  }
                  className={`${
                    emailStatus.verified ? "verified-btn" : "verify-btn"
                  }`}
                  onClick={() => {
                    sendOtpOnEmail();
                  }}
                >
                  {emailStatus.loading ? (
                    <BtnLoader color={"#ffffff"} />
                  ) : (
                    <>
                      {emailStatus.verified ? (
                        <>
                          <FontAwesomeIcon
                            icon={faCircleCheck}
                            style={{ color: "#ffffff" }}
                            size="lg"
                          />
                          {isMobile ? "" : " Verified"}
                        </>
                      ) : (
                        "Verify"
                      )}
                    </>
                  )}
                </button>
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="phone">Mobile Number:</label>
              <div className="mobile-input-container">
                <div className="country-code">
                  <img
                    src="https://upload.wikimedia.org/wikipedia/en/thumb/4/41/Flag_of_India.svg/500px-Flag_of_India.svg.png"
                    alt="India flag"
                    className="flag"
                  />
                  <span>+91</span>
                </div>
                <input
                  type="tel"
                  name="phone"
                  id="phone"
                  placeholder="Enter your mobile number"
                  value={signupRequest.phone}
                  onChange={handleInputChange}
                  maxLength={10}
                  readOnly={phoneStatus.verified}
                  required
                />
                <button
                  type="button"
                  disabled={phoneStatus.verified || phoneStatus.loading}
                  style={
                    phoneStatus.verified || phoneStatus.loading
                      ? { cursor: "not-allowed" }
                      : {}
                  }
                  className={`${
                    phoneStatus.verified ? "verified-btn" : "verify-btn"
                  }`}
                  onClick={sendOtpOnPhone}
                >
                  {phoneStatus.loading ? (
                    <BtnLoader color={"#ffffff"} />
                  ) : (
                    <>
                      {phoneStatus.verified ? (
                        <>
                          <FontAwesomeIcon
                            icon={faCircleCheck}
                            style={{ color: "#ffffff" }}
                            size="lg"
                          />
                          {isMobile ? "" : " Verified"}
                        </>
                      ) : (
                        "Verify"
                      )}
                    </>
                  )}
                </button>
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="role">Choose Your Profile:</label>
              <Select
                options={options}
                onChange={(e) =>
                  setSignupRequest({ ...signupRequest, role: e.value })
                }
              />
            </div>

            <div className="form-group password-container">
              <label htmlFor="password">Password:</label>
              <div className="password-input-wrapper">
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  id="password"
                  className="password"
                  placeholder="Create a strong password"
                  value={signupRequest.password}
                  onChange={handleInputChange}
                  onFocus={() => setShowPasswordRules(true)}
                  onBlur={() => setShowPasswordRules(false)}
                  required
                />
                <button
                  type="button"
                  className="toggle-password"
                  onClick={togglePasswordVisibility}
                >
                  {showPassword ? (
                    <FontAwesomeIcon icon={faEye} />
                  ) : (
                    <FontAwesomeIcon icon={faEyeSlash} />
                  )}
                </button>
              </div>

              {showPasswordRules && (
                <div className="password-rules">
                  <p>Password must include:</p>
                  <ul>
                    <li>At least 1 uppercase letter (A–Z)</li>
                    <li>At least 1 lowercase letter (a–z)</li>
                    <li>At least 1 number (0–9)</li>
                    <li>At least 1 special symbol (!@#$%^&*)</li>
                    <li>Minimum 8 characters</li>
                  </ul>
                </div>
              )}
            </div>

            <div className="form-group">
              <button
                type="submit"
                className="signup-button"
                onClick={signupHandler}
              >
                {signupState ? <BtnLoader color={"#ffffff"} /> : "Sign Up Now"}
              </button>
            </div>

            <div className="oauth-section">
              <GoogleOAuthProvider clientId={AppConfig.googleId}>
                <GoogleLogin
                  onSuccess={googleSignupSuccess}
                  onError={() => {}}
                  type="standard"
                  theme="outline"
                  shape="rectangular"
                  size="large"
                  text="signup_with"
                  width="100%"
                  logo_alignment="center"
                />
              </GoogleOAuthProvider>
            </div>
          </form>

          <p className="login-redirect">
            Already have an account?{" "}
            <button className="login-link" onClick={() => navigate("/login")}>
              Login
            </button>
          </p>
        </div>
      )}
    </div>
  );
};

const Option = ({ label, imageUrl }) => {
  return (
    <div className="customize-option">
      <div className="customize-option-img">
        <img src={imageUrl} alt={label} />
      </div>
    </div>
  );
};

export default Signup;
