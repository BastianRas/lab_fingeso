import axios from 'axios';

// Asegúrate que el puerto coincida con tu backend (8080)
const API_URL = 'http://localhost:8080/api/eventos';

export default {
  obtenerTodos() {
    return axios.get(API_URL);
  },
  // Agregamos estos por si los usamos luego en el Admin
  crear(evento) {
    return axios.post(API_URL, evento);
  },
  eliminar(id) {
    return axios.delete(`${API_URL}/${id}`);
  }
};