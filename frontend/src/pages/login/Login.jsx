import React, { useState } from "react";
import "./Login.css";
import { IntroVideo, Logo } from "../../assets/assets";
import { GoogleLogin, GoogleOAuthProvider } from "@react-oauth/google";
import { AppConfig } from "../../config/AppConfig";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { signin } from "../../service/AuthService";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEye, faEyeSlash } from "@fortawesome/free-solid-svg-icons";
import BtnLoader from "../../components/util/BtnLoader";
import { useDispatch } from "react-redux";
import { setCredentials } from "../../store/authSlice";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const loginHandler = async (e) => {
    e.preventDefault();
    if (!email || !password) {
      toast.warn("First enter your credentials.");
      return;
    }
    setLoading(true);
    try {
      const response = await signin({ email, password });
      // stored user details in redux
      dispatch(
        setCredentials({
          id: response.id,
          fullName: response.fullName,
          accessToken: response.accessToken,
          isAuthenticated: true,
        })
      );
    } catch (error) {
      if (error?.response?.data?.message) {
        toast.error(error.response.data.message);
      } else {
        toast.error("Failed to sign in. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <video autoPlay loop muted className="background-video">
        <source src={IntroVideo} type="video/mp4" />
        Your browser does not support the video tag.
      </video>
      <div className="login-content">
        <div className="logo-section">
          <div className="logo-container">
            <img src={Logo} alt="Annapoorna Logo" className="logo" />
          </div>
          <p className="tagline">
            Welcome back! Please sign in to continue enjoying delicious food
            deliveries.
          </p>
        </div>

        <form className="login-form" onSubmit={loginHandler}>
          <div className="form-group">
            <label htmlFor="email">Email:</label>
            <div className="input-with-button">
              <input
                type="email"
                name="email"
                id="email"
                placeholder="Enter your email address"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
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
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              <button
                type="button"
                className="toggle-password"
                onClick={() => setShowPassword((prev) => !prev)}
              >
                {showPassword ? (
                  <FontAwesomeIcon icon={faEye} />
                ) : (
                  <FontAwesomeIcon icon={faEyeSlash} />
                )}
              </button>
            </div>
          </div>

          <div
            className="forgot-password"
            onClick={() => navigate("/accounts/password/reset/")}
          >
            Forgot Password?
          </div>

          <div className="form-group">
            <button type="submit" className="login-button">
              {loading ? <BtnLoader color="#ffffff" /> : "Sign In"}
            </button>
          </div>

          <div className="oauth-section">
            <GoogleOAuthProvider clientId={AppConfig.googleId}>
              <GoogleLogin
                // onSuccess={handleOauthSuccess}
                onError={() => console.log("Login Failed")}
                type="standard"
                theme="outline"
                shape="rectangular"
                size="large"
                text="signin_with"
                width="100%"
                logo_alignment="center"
              />
            </GoogleOAuthProvider>
          </div>
        </form>

        <p className="signup-redirect">
          Don't have an account?{" "}
          <button className="signup-link" onClick={() => navigate("/signup")}>
            Sign Up
          </button>
        </p>
      </div>
    </div>
  );
};

export default Login;
