import axios from 'axios';

const API_URL = 'http://localhost:8080/api/pius';

export default {
  obtenerTodos() {
    return axios.get(API_URL);
  },
  crear(piu) {
    return axios.post(API_URL, piu);
  },
  actualizar(id, piu){
    return axios.put(`${API_URL}/${id}`, piu);
  },
  eliminar(id) {
    return axios.delete(`${API_URL}/${id}`);
  }
};