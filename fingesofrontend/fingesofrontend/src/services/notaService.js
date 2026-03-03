import axios from 'axios';

const API_URL = 'http://localhost:8081/api/notas'; 

export default {
  obtenerPorAlumno(usuarioId) {
    return axios.get(`${API_URL}/alumno/${usuarioId}`);
  },
  obtenerPorClase(claseId) {
    return axios.get(`${API_URL}/clase/${claseId}`);
  },
  crearNota(notaData) {
    return axios.post(API_URL, notaData);
  },
  actualizarNota(id, notaData) {
    return axios.put(`${API_URL}/${id}`, notaData);
  },
  eliminarNota(id) {
    return axios.delete(`${API_URL}/${id}`);
  }
};