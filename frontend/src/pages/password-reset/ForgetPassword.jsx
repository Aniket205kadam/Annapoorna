import { useState } from "react";
import { IntroVideo, Logo } from "../../assets/assets";
import "./ForgetPassword.css";
import { useNavigate } from "react-router-dom";
import { sendResetPasswordLinkOnEmail } from "../../service/AuthService";
import { toast } from "react-toastify";

const ForgetPassword = () => {
  const [email, setEmail] = useState("");
  const navigate = useNavigate();

  const sendLinkOnEmail = async (e) => {
    e.preventDefault()
    
    try {
      await sendResetPasswordLinkOnEmail(email);
      toast.success("Reset password link sent to your email.");
    } catch (error) {
      console.error("Failed to send reset password link:", error);
      if (error?.response?.data?.message) {
        toast.error(error.response.data.message);
        return;
      } 
      toast.error("Failed to send reset password link on your email.");
    }
  }

  return (
    <div className="reset-password-container">
      <video autoPlay loop muted className="background-video">
        <source src={IntroVideo} type="video/mp4" />
        Your browser does not support the video tag.
      </video>
      <div className="reset-password-content">
        <div className="logo-section">
          <div className="logo-container">
            <img src={Logo} alt="Annapoorna Logo" className="logo" />
          </div>
          <h3>Trouble logging in?</h3>
          <p className="tagline">
            Enter your email, we'll send you a link to get back into your
            account.
          </p>
        </div>

        <form className="email-form" onSubmit={sendLinkOnEmail}>
          <div className="form-group">
            <div className="input-with-button">
              <input
                type="email"
                name="email"
                id="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
          </div>
          <div className="form-group">
            <button
              type="submit"
              className="send-link"
            >
              Send login link
            </button>
          </div>
          <div className="form-group">
            <button
              type="button"
              className="back-login"
              onClick={() => navigate("/login")}
            >
              Back to login
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ForgetPassword;