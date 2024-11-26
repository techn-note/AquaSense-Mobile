import axios from 'axios';

const api = axios.create({
  baseURL: "http://localhost:5000",
  timeout: 5000,
});

// Endpoints
export const login = async (email, password) => {
  try {
    const response = await api.post("/login", { email, password });
    return response.data;
  } catch (error) {
    console.error("Erro ao logar:", error.response?.data || error.message);
    throw error;
  }
};

export const register = async (userData) => {
    try {
      const response = await api.post("/register", userData);
      return response.data;
    } catch (error) {
      console.error("Erro ao registrar:", error.response?.data || error.message);
      throw error;
    }
  };
  

export const getUserProfile = async (token) => {
  try {
    const response = await api.get("/profile", {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  } catch (error) {
    console.error("Erro ao buscar perfil:", error.response?.data || error.message);
    throw error;
  }
};

export default api;
