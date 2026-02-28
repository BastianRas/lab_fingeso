import axios from 'axios';

const API_URL = 'http://localhost:8081/api/matricula';

export default {
  async obtenerMatricula(usuarioId) {
    const response = await axios.get(`${API_URL}/alumno/${usuarioId}`);
    return response.data;
  }
};
