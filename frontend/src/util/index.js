import axios from "axios";

export const spring_core = axios.create({
    baseURL: import.meta.env.VITE_CORE_API
});

export const spring_auth = axios.create({
    baseURL: import.meta.env.VITE_AUTH_API
});

export const removeDuplicates = (arr) => {
    return [...new Set(arr)];
  }
