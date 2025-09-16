import React, { useState } from "react";
import "./Signup.css";
import { IntroVideo, Logo } from "../../assets/assets";
import { GoogleLogin, GoogleOAuthProvider } from "@react-oauth/google";

const Signup = () => {
  const [showPasswordRules, setShowPasswordRules] = useState(false);

  const googleClientId = "your-google-client-id";

  const handleOauthSuccess = (response) => {
    console.log("OAuth success:", response);
  };

  return (
    <div className="signup-container">
      <video autoPlay loop muted className="background-video">
        <source src={IntroVideo} type="video/mp4" />
        Your browser does not support the video tag.
      </video>

      <div className="signup-content">
        <div className="logo-section">
          <div className="logo-container">
            <img src={Logo} alt="Annapoorna Logo" />
          </div>
          <p className="tagline">
            Create your account to get started with delicious food deliveries
          </p>
        </div>

        <form className="signup-form">
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="firstname">Firstname:</label>
              <input
                type="text"
                name="firstname"
                id="firstname"
                placeholder="Enter your firstname"
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="lastname">Lastname:</label>
              <input
                type="text"
                name="lastname"
                id="lastname"
                placeholder="Enter your lastname"
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="email">Email:</label>
            <input
              type="email"
              name="email"
              id="email"
              placeholder="Enter your email address"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="mobile-number">Mobile Number:</label>
            <div className="mobile-input-container">
              <div className="country-code">
                <img
                  src="https://upload.wikimedia.org/wikipedia/en/thumb/4/41/Flag_of_India.svg/500px-Flag_of_India.svg.png"
                  alt="India flag"
                />
                <span>+91</span>
              </div>
              <input
                type="tel"
                name="mobile-number"
                id="mobile-number"
                placeholder="Enter your mobile number"
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="password">Password:</label>
            <input
              type="password"
              name="password"
              id="password"
              placeholder="Create a strong password"
              required
            />
            {showPasswordRules && (
              <div class="password-rules">
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
            <button className="signup-button">Sign Up</button>
          </div>

          <div className="oauth-section">
            <GoogleOAuthProvider clientId={googleClientId}>
              <div>
                <GoogleLogin
                  onSuccess={() => {}}
                  onError={() => {}}
                  type="standard"
                  theme="outline"
                  shape="rectangular"
                  size="large"
                  text="signup_with"
                  width="100%"
                  logo_alignment="center"
                  className="custom-google-button"
                />
              </div>
            </GoogleOAuthProvider>
          </div>
        </form>

        <p className="login-redirect">
          Already have an account? <button className="login-link">Login</button>
        </p>
      </div>
    </div>
  );
};

export default Signup;
