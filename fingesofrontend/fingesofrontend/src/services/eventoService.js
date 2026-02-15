import axios from 'axios';

const API_URL = 'http://localhost:8080/api/eventos';

export default {
  obtenerTodos() {
    return axios.get(API_URL);
  }
};