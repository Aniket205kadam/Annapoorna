import React, { use, useEffect, useState } from "react";
import "./VerificationCodeInput.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft } from "@fortawesome/free-solid-svg-icons";

const VerificationCodeInput = ({
  cardTitle,
  cardPrompt,
  verifyCode,
  close,
  resendCode,
}) => {
  const [code, setCode] = useState("");
  const [minute, setMinute] = useState(9);
  const [second, setSecond] = useState(60);

  useEffect(() => {
    if (code?.length === 6) {
      verifyCode(code);
    }
  }, [code]);

  useEffect(() => {
    const timer = setInterval(() => {
      setSecond((prevSecond) => {
        if (prevSecond === 0) {
          if (minute === 0) {
            clearInterval(timer);
            return 0;
          } else {
            setMinute((prevMinute) => prevMinute - 1);
            return 59;
          }
        }
        return prevSecond - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  return (
    <form className="form-card">
      <p className="form-card-title">{cardTitle}</p>
      <p className="form-card-prompt">{cardPrompt}</p>
      <div className="form-card-input-wrapper">
        <input
          className="form-card-input"
          placeholder="______"
          maxlength="6"
          type="tel"
          value={code}
          onChange={(e) => setCode(e.target.value)}
        />
        <div className="form-card-input-bg"></div>
      </div>
      <p className="call-again">
        <span
          className={`underlined ${minute === 0 ? "active-resend-btn" : ""}`}
          onClick={resendCode}
        >
          send again
        </span>{" "}
        in {minute}:{second} seconds
      </p>
      <button type="button" className="back-button" onClick={close}>
        <FontAwesomeIcon icon={faArrowLeft} />
        Back
      </button>
    </form>
  );
};

export default VerificationCodeInput;
