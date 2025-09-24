import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";

const SecureRoutes = ({ children, required }) => {
  const { isAuthenticated } = useSelector((state) => state.authentication);

  // Redirect to login if not authenticated and route is required
  if (required && !isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Render children if authenticated or route is not required
  if (!required && isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  return children;
};

export default SecureRoutes;
