import axios from 'axios';

const API_URL = 'http://localhost:8080';

export default {
  async login(correo, numeroCredencial, rol) {
    try {
      const response = await axios.post(`${API_URL}/usuario/login`, {
        correo: correo,
        numeroCredencial: numeroCredencial,
        rol: rol
      });
      
      if (response.data) {
        localStorage.setItem('user', JSON.stringify(response.data));
      }

      return response.data;
    } catch (error) {
      console.error("Error en login:", error);
      throw error;
    }
  },

  logout() {
    localStorage.removeItem('user');
  }
};