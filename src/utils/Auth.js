import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: 'http://3.141.23.81:5000',
});


export const storeToken = async (token) => {
  try {
    await AsyncStorage.setItem('access_token', token);
  } catch (error) {
    console.error('Erro ao armazenar token', error);
  }
};


export const getToken = async () => {
  try {
    const token = await AsyncStorage.getItem('access_token');
    return token;
  } catch (error) {
    console.error('Erro ao recuperar token', error);
    return null;
  }
};


export const removeToken = async () => {
  try {
    await AsyncStorage.removeItem('access_token');
  } catch (error) {
    console.error('Erro ao remover token', error);
  }
};


axiosInstance.interceptors.request.use(
  async (config) => {
    const token = await getToken();
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);


export const fetchData = async (endpoint) => {
  try {
    const response = await axiosInstance.get(endpoint);
    return response.data;
  } catch (error) {
    console.error('Erro ao buscar dados protegidos', error);
    throw new Error('Erro ao buscar dados.');
  }
};


export default axiosInstance;
