import axios from 'axios';

const API_URL = 'http://localhost:8081/api/lugares';

export default {
  obtenerTodos() {
    return axios.get(API_URL);
  },
  crear(lugar) {
    return axios.post(API_URL, lugar);
  },
  actualizar(id, lugar) {
    return axios.put(`${API_URL}/${id}`, lugar);
  },
  eliminar(id) {
    return axios.delete(`${API_URL}/${id}`);
  }
};