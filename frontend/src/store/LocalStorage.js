import { AppConfig } from "../config/AppConfig";

export const saveAuthInfo = (state) => {
    const serializedState = JSON.stringify(state);
    localStorage.setItem(AppConfig.secret, serializedState);
}

export const loadAuthInfo = () => {
    const serializedState = localStorage.getItem(AppConfig.secret);
    return JSON.parse(serializedState);
}