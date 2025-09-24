export const AppConfig = {
  googleId: String(import.meta.env.VITE_GOOGLE_CLIENT_ID),
  backendUrl: String(import.meta.env.VITE_BACKEND_BASEURL),
  secret: String(import.meta.env.VITE_SECRET_CODE)
};
