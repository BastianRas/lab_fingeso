import axios from 'axios';

const API_URL = 'http://localhost:8081/api/usuario';

export default {
    obtenerTodos() {
        return axios.get(API_URL);
    },
    crear(usuario) {
        return axios.post(`${API_URL}/crearUsuario`, usuario);
    },
    actualizar(id, usuario) {
        return axios.put(`${API_URL}/${id}`, usuario);
    },
    eliminar(id) {
        return axios.delete(`${API_URL}/${id}`);
    }       
};