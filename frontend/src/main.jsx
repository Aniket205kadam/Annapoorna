import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import Signup from "./pages/signup/Signup.jsx";
import Login from "./pages/login/Login.jsx";
import { Bounce, ToastContainer } from "react-toastify";
import { Provider } from "react-redux";
import store from "./store/store.js";
import SecureRoutes from "./security/SecureRoutes.jsx";
import ForgetPassword from "./pages/password-reset/ForgetPassword.jsx";
import ChangePassword from "./pages/password-reset/ChangePassword.jsx";

const rounter = createBrowserRouter([
  {
    path: "/signup",
    element: (
      <SecureRoutes required={false}>
        <Signup />
      </SecureRoutes>
    ),
  },
  {
    path: "/login",
    element: (
      <SecureRoutes required={false}>
        <Login />
      </SecureRoutes>
    ),
  },
  {
    path: "/accounts/password/reset/",
    element: (
      <SecureRoutes required={false}>
        <ForgetPassword />
      </SecureRoutes>
    )
  },
  {
    path: "/rest-password",
    element: (
      <SecureRoutes required={false}>
        <ChangePassword />
      </SecureRoutes>
    )
  },
  {
    path: "",
    element: (
      <SecureRoutes required={true}>
        <App />
      </SecureRoutes>
    ),
  },
]);

createRoot(document.getElementById("root")).render(
  <>
    <Provider store={store}>
      <RouterProvider router={rounter} />
    </Provider>
    <ToastContainer
      position="bottom-right"
      autoClose={5000}
      closeButton={true}
      hideProgressBar={false}
      newestOnTop
      closeOnClick
      rtl={false}
      pauseOnFocusLoss
      draggable
      pauseOnHover
      transition={Bounce}
    />
  </>
);
