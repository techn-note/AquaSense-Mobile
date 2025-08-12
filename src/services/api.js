import { IP, PORT } from "@env";
import axios from "axios";


const api = axios.create({
  baseURL: "http://3.141.23.81:5000",
  timeout: 15000,
});

export async function getTanques() {
  try {
    const response = await api.get("/tanks");
    return response.data.data;
  } catch (error) {
    console.error("Erro ao buscar tanques:", error);
    throw error;
  }
}

export async function getPeixes() {
  try {
    const response = await api.get("/peixes");
    return response;
  } catch (error) {
    console.error("Erro ao buscar tanques:", error);
    throw error;
  }
}

export const getLatestAtualizacao = async (tanque) => {
  try {
    const response = await api.get("/atualizacoes/latest", {
      params: { tanque },
    });
    return response.data;
  } catch (error) {
    console.error(
      "Erro ao buscar a última atualização:",
      error.response?.data || error.message
    );
    throw error;
  }
};


export const createAtualizacao = async (tanque) => {
  try {
    const response = await api.post("/atualizacoes", null, {
      params: { tanque },
    });
    return response.data;
  } catch (error) {
    console.error(
      "Erro ao criar atualização:",
      error.response?.data || error.message
    );
    throw error;
  }
};

export const getLatestSensorData = async (tipo, tanque) => {
  try {
    const response = await api.get("/sensores/latest", {
      params: { tipo, tanque },
    });
    return response.data;
  } catch (error) {
    console.error(
      "Erro ao buscar dados do sensor:",
      error.response?.data || error.message
    );
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
    console.error(
      "Erro ao buscar perfil:",
      error.response?.data || error.message
    );
    throw error;
  }
};


export const createPeixe = async (peixeData) => {
  try {
    const response = await api.post("/peixes", peixeData);
    return response.data;
  } catch (error) {
    console.error("Erro ao criar peixe:", error.response?.data || error.message);
    throw error
  }
};


export const createTank = async (tankData) => {
  try {
    const response = await api.post('/tanks', tankData);
    return response.data;
  } catch (error) {
    console.error("Erro ao criar tanque:", error.response?.data || error.message);
    throw error;
  }
};


export const getLast10SensorData = async (tipo, tanque) => {
  try {
    const response = await api.get('/sensores/latest/10', {
      params: { tipo, tanque },
    });
    return response.data.data;
  } catch (error) {
    console.error('Erro ao buscar os últimos 10 dados do sensor:', error.response?.data || error.message);
    throw error;
  }
};


export const updateTank = async (tankId, updateData) => {
  try {
    const response = await api.put(`/tanks/${tankId}`, updateData);
    return response.data;
  } catch (error) {
    console.error("Erro ao atualizar tanque:", error.response?.data || error.message);
    throw error;
  }
};


export const updatePeixe = async (peixeId, updateData) => {
  try {
    console.log("Enviando dados para a API:", peixeId, updateData);
    const response = await api.put(`/peixes/${peixeId}`, updateData);
    console.log("Resposta da API:", response.data);
    return response.data;
  } catch (error) {
    console.error("Erro ao atualizar peixe:", error.response?.data || error.message);
    throw error;
  }
};



export const getTankByName = async (name) => {
  try {
    const response = await api.get(`/tanks/name/${name}`);
    return response.data.data;
  } catch (error) {
    console.error("Erro ao buscar tanque pelo nome:", error.response?.data || error.message);
    throw error;
  }
};


export const getPeixeByName = async (name) => {
  try {
    const response = await api.get(`/peixes/name/${name}`);
    return response.data.data;
  } catch (error) {
    console.error("Erro ao buscar peixe pelo nome:", error.response?.data || error.message);
    throw error;
  }
};


export const predictIA = async (payload) => {
  try {
    const response = await api.post("/predict", payload);
    return response.data;
  } catch (error) {
    console.error("Erro ao chamar IA /predict:", error.response?.data || error.message);
    throw error;
  }
};

export default api;
