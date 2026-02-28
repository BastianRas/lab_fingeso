import axios from 'axios';

const API_URL = 'http://localhost:8081/api/clases';

export default {
  async getClasesByProfesor(usuarioId) {
    const response = await axios.get(`${API_URL}/profesor/${usuarioId}`);
    return response.data;
  }
};
