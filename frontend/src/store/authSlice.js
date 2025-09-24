import { createSlice } from "@reduxjs/toolkit";
import { loadAuthInfo, saveAuthInfo } from "./LocalStorage";

const authSlice = createSlice({
  name: "authentication",
  initialState: loadAuthInfo() || {
    id: null,
    fullName: null,
    accessToken: null,
    isAuthenticated: false,
  },
  reducers: {
    setCredentials: (state, action) => {
      state.id = action.payload.id;
      state.fullName = action.payload.fullName;
      state.accessToken = action.payload.accessToken;
      state.isAuthenticated = true;
      saveAuthInfo({
        id: state.id,
        fullName: state.fullName,
        isAuthenticated: state.isAuthenticated,
      });
    },
    logout: (state) => {
      state.id = null;
      state.fullName = null;
      state.accessToken = null;
      state.isAuthenticated = false;
      saveAuthInfo({
        id: null,
        fullName: null,
        isAuthenticated: false,
      });
    },
  },
});

export const { setCredentials, logout } = authSlice.actions;
export default authSlice.reducer;
