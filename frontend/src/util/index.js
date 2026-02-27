import axios from "axios";

export const spring_core = axios.create({
    baseURL: "/api"
});

export const spring_auth = axios.create({
    baseURL: "/api/auth"
});

export const removeDuplicates = (arr) => {
    return [...new Set(arr)];
  }
