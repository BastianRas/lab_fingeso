import axios from 'axios';

const API_URL = 'http://localhost:8081/api/eventos';

export default {
  obtenerTodos() {
    return axios.get(API_URL);
  },
  // Agregamos estos por si los usamos luego en el Admin
  crear(evento) {
    return axios.post(API_URL, evento);
  },
  // Actualizar como Admin 
  actualizar(id, evento) {
    return axios.put(`${API_URL}/${id}`, evento);
  },
  eliminar(id) {
    return axios.delete(`${API_URL}/${id}`);
  }
};