import React from "react";
import { Logo } from "../../assets/assets";
import Select from "react-select";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faArrowCircleLeft,
  faArrowCircleRight,
  faCircleCheck,
} from "@fortawesome/free-solid-svg-icons";
import useIsMobile from "../../hooks/useIsMobile";
import BtnLoader from "../util/BtnLoader";

const PhoneRoleForm = ({
  options,
  phoneStatus,
  setPhoneStatus,
  handleInputChange,
  signupRequest,
  setSignupRequest,
  signupWithGoogle,
  cancelSignupWithGoogle,
  sendOtpOnPhone
}) => {

  const isMobile = useIsMobile();

  return (
    <div className="signup-content">
      <div className="logo-section">
        <div className="logo-container">
          <img src={Logo} alt="Annapoorna Logo" className="logo" />
        </div>
        <p className="tagline">
          Complete a few more steps to get started with Annapoorna
        </p>
      </div>

      <form className="signup-form">
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

        <div className="btn-row">
          <div className="btn">
            <button type="button" className="back-btn" onClick={cancelSignupWithGoogle}>
              <FontAwesomeIcon icon={faArrowCircleLeft} /> Back
            </button>
          </div>

          <div className="btn">
            <button type="button" className="continue-btn" onClick={signupWithGoogle}>
              Continue <FontAwesomeIcon icon={faArrowCircleRight} />
            </button>
          </div>
        </div>
      </form>
    </div>
  );
};

export default PhoneRoleForm;
