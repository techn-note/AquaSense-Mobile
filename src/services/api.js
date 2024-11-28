import axios from 'axios';

const api = axios.create({
  baseURL: "http://localhost:5000",
  timeout: 5000,
});

export const getLatestAtualizacao = async (tanque) => {
  try {
    const response = await api.get('/atualizacoes/latest', {
      params: { tanque },
    });
    return response.data;
  } catch (error) {
    console.error('Erro ao buscar a última atualização:', error.response?.data || error.message);
    throw error;
  }
};

export const createAtualizacao = async (tanque) => {
  try {
    const response = await api.post('/atualizacoes', null, {
      params: { tanque },
    });
    return response.data;
  } catch (error) {
    console.error('Erro ao criar atualização:', error.response?.data || error.message);
    throw error;
  }
};


export const getLatestSensorData = async (tipo, tanque) => {
  try {
    const response = await api.get('/sensores/latest', {
      params: { tipo, tanque },
    });
    return response.data;
  } catch (error) {
    console.error('Erro ao buscar dados do sensor:', error.response?.data || error.message);
    throw error;
  }
};


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


export const getUserName = async (token) => {
  try {
    const response = await api.get("/profile", {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data.data.name;
  } catch (error) {
    console.error("Erro ao buscar perfil:", error.response?.data || error.message);
    throw error;
  }
};

export default api;
