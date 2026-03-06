const BASE_URL = 'http://localhost:8080/api/v1';

export const clienteService = {
  getAll: async () => {
    const response = await fetch(`${BASE_URL}/clientes`);
    const result = await response.json();
    return result.data || [];
  },
  getById: async (id) => {
    const response = await fetch(`${BASE_URL}/clientes/${id}`);
    const result = await response.json();
    return result.data;
  },
  create: async (cliente) => {
    const response = await fetch(`${BASE_URL}/clientes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cliente),
    });
    return await response.json();
  },
  update: async (id, cliente) => {
    const response = await fetch(`${BASE_URL}/clientes/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cliente),
    });
    return await response.json();
  },
  delete: async (id) => {
    const response = await fetch(`${BASE_URL}/clientes/${id}`, {
      method: 'DELETE',
    });
    return await response.json();
  },
};

export const cuentaService = {
  getAll: async () => {
    const response = await fetch(`${BASE_URL}/cuentas`);
    const result = await response.json();
    return result; // Devolvemos el objeto completo para manejar el status en el componente
  },
  create: async (cuenta) => {
    const response = await fetch(`${BASE_URL}/cuentas`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cuenta),
    });
    return await response.json();
  },
  update: async (id, cuenta) => {
    const response = await fetch(`${BASE_URL}/cuentas/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cuenta),
    });
    return await response.json();
  },
  delete: async (id) => {
    const response = await fetch(`${BASE_URL}/cuentas/${id}`, {
      method: 'DELETE',
    });
    return await response.json();
  },
};

export const movimientoService = {
  getAll: async () => {
    const response = await fetch(`${BASE_URL}/movimientos`);
    const result = await response.json();
    return result;
  },
  create: async (movimiento) => {
    const response = await fetch(`${BASE_URL}/movimientos`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(movimiento),
    });
    return await response.json();
  },
  update: async (id, movimiento) => {
    const response = await fetch(`${BASE_URL}/movimientos/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(movimiento),
    });
    return await response.json();
  },
  delete: async (id) => {
    const response = await fetch(`${BASE_URL}/movimientos/${id}`, {
      method: 'DELETE',
    });
    // Para delete no validamos status según instrucción
    return response;
  },
};

export const reporteService = {
  getReporte: async (clienteId, startDate, endDate) => {
    const response = await fetch(`${BASE_URL}/reportes?startDate=${startDate}&endDate=${endDate}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ clienteId }),
    });
    return await response.json();
  },
};



