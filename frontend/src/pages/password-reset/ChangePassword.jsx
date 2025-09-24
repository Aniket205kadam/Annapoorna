import React, { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { IntroVideo } from "../../assets/assets";
import "./ForgetPassword.css";
import { toast } from "react-toastify";
import { changePassword } from "../../service/AuthService";
import BtnLoader from "../../components/util/BtnLoader";

const ChangePassword = () => {
  const [searchParams] = useSearchParams();
  const uid = searchParams.get("uid");
  const token = searchParams.get("token");
  const [firstPassword, setFirstPassword] = useState("");
  const [secondPassword, setSecondPassword] = useState("");
  const [isDisable, setIsDisable] = useState(true);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (firstPassword.length >= 8 && firstPassword === secondPassword) {
      setIsDisable(false);
    } else {
      setIsDisable(true);
    }
  }, [firstPassword, secondPassword]);

  const changePasswordHandler = async (e) => {
    setLoading(true);
    e.preventDefault();
    if (firstPassword !== secondPassword || !(firstPassword.length >= 8)) {
      toast.warn("First enter password.");
      return;
    }

    try {
      await changePassword(uid, token, firstPassword);
      toast.info("Successfully update your password.");
      navigate("/login");
    } catch (error) {
      if (error?.response?.data.validationError) {
        error.response.data.validationError.forEach((message) => {
          toast.error(message);
        });
      } else if (error?.response?.data?.message) {
        toast.error(error.response.data.message);
      } else {
        toast.error("Failed to change password.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="reset-password-container">
      <video autoPlay loop muted className="background-video">
        <source src={IntroVideo} type="video/mp4" />
        Your browser does not support the video tag.
      </video>
      <div className="reset-password-content">
        <div className="logo-section">
          <h3>Create A Strong Password</h3>
          <p className="tagline">
            Your password must be at least 8 characters and should include a
            combination of numbers, letters, and special characters (!$@%)
          </p>
        </div>

        <form className="email-form" onSubmit={changePasswordHandler}>
          <div className="form-group">
            <input
              type="password"
              placeholder="New password"
              value={firstPassword}
              onChange={(e) => setFirstPassword(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <input
              type="password"
              placeholder="New password, again"
              value={secondPassword}
              onChange={(e) => setSecondPassword(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <button
              type="submit"
              className={`send-link ${isDisable ? "disable-btn" : ""}`}
              disabled={isDisable}
            >
              {loading ? <BtnLoader color={"#ffffff"} /> : "Rest Password"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ChangePassword;
