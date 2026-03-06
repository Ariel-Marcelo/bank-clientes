import React, { useState, useEffect, useMemo } from 'react';
import { cuentaService, clienteService } from '../../services/api';

function Cuentas() {
  const [cuentas, setCuentas] = useState([]);
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingCuenta, setEditingCuenta] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);
  const [formData, setFormData] = useState({
    numeroCuenta: '',
    tipoCuenta: 'Ahorro',
    saldoInicial: 0,
    estado: true,
    clienteId: '',
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    setErrorMessage(null);
    try {
      const response = await cuentaService.getAll();
      if (response.status === false) {
        setErrorMessage(response.message || 'Error desconocido al cargar cuentas');
        setCuentas([]);
      } else {
        setCuentas(Array.isArray(response.data) ? response.data : []);
      }
      
      const dataClientes = await clienteService.getAll();
      setClientes(Array.isArray(dataClientes) ? dataClientes : []);
    } catch (error) {
      setErrorMessage('Error de conexión con el servidor');
      console.error('Error cargando datos:', error);
    } finally {
      setLoading(false);
    }
  };

  const filteredCuentas = useMemo(() => {
    if (!searchTerm) return cuentas;
    const lowerSearch = searchTerm.toLowerCase();
    return cuentas.filter(c => 
      (c.numeroCuenta && c.numeroCuenta.toLowerCase().includes(lowerSearch)) ||
      (c.tipoCuenta && c.tipoCuenta.toLowerCase().includes(lowerSearch)) ||
      (c.nombreCliente && c.nombreCliente.toLowerCase().includes(lowerSearch)) ||
      (c.saldoInicial.toString().includes(lowerSearch)) ||
      ((c.estado ? 'activa' : 'inactiva').includes(lowerSearch))
    );
  }, [searchTerm, cuentas]);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : (name === 'clienteId') ? parseFloat(value) : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage(null);
    try {
      // Siempre forzamos saldoInicial a 0 según requerimiento
      const dataToSend = { ...formData, saldoInicial: 0 };
      
      let result;
      if (editingCuenta) {
        result = await cuentaService.update(editingCuenta.id, dataToSend);
      } else {
        result = await cuentaService.create(dataToSend);
      }

      if (result.status === false) {
        setErrorMessage(result.message || 'Error al procesar la solicitud');
      } else {
        setShowModal(false);
        resetForm();
        loadData();
      }
    } catch (error) {
      setErrorMessage('Error de red al intentar guardar');
    }
  };

  const resetForm = () => {
    setFormData({
      numeroCuenta: '',
      tipoCuenta: 'Ahorro',
      saldoInicial: 0,
      estado: true,
      clienteId: '',
    });
    setEditingCuenta(null);
    setErrorMessage(null);
  };

  const handleEdit = (cuenta) => {
    setEditingCuenta(cuenta);
    setFormData({
      numeroCuenta: cuenta.numeroCuenta || '',
      tipoCuenta: cuenta.tipoCuenta || 'Ahorro',
      saldoInicial: 0, // Siempre mandamos 0 por defecto
      estado: cuenta.estado !== undefined ? cuenta.estado : true,
      clienteId: cuenta.clienteId || '',
    });
    setErrorMessage(null);
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar esta cuenta?')) {
      setErrorMessage(null);
      try {
        await cuentaService.delete(id);
        loadData();
      } catch (error) {
        setErrorMessage('Error de red al intentar eliminar');
      }
    }
  };

  return (
    <div>
      <h1 className="content-title">Cuentas</h1>
      
      <div className="toolbar">
        <input 
          type="text" 
          className="search-input" 
          placeholder="Buscar por número, tipo o cliente..." 
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <button className="btn-nuevo" onClick={() => { resetForm(); setShowModal(true); }}>Nueva Cuenta</button>
      </div>

      {loading ? (
        <p>Cargando...</p>
      ) : (
        <>
          <table className="data-table">
            <thead>
              <tr>
                <th>Número</th>
                <th>Tipo</th>
                <th>Saldo Inicial</th>
                <th>Estado</th>
                <th>Cliente</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredCuentas.length > 0 ? (
                filteredCuentas.map((cuenta) => (
                  <tr key={cuenta.id}>
                    <td>{cuenta.numeroCuenta}</td>
                    <td>{cuenta.tipoCuenta}</td>
                    <td>{cuenta.saldoInicial}</td>
                    <td>{cuenta.estado ? 'Activa' : 'Inactiva'}</td>
                    <td>{cuenta.nombreCliente}</td>
                    <td>
                      <button className="btn-action btn-edit" onClick={() => handleEdit(cuenta)}>Actualizar</button>
                      <button className="btn-action btn-delete" onClick={() => handleDelete(cuenta.id)}>Eliminar</button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center' }}>No se encontraron cuentas</td>
                </tr>
              )}
            </tbody>
          </table>
          
          {errorMessage && (
            <div className="error-footer">
              <strong>Error:</strong> {errorMessage}
            </div>
          )}
        </>
      )}

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2>{editingCuenta ? 'Actualizar Cuenta' : 'Nueva Cuenta'}</h2>
            
            {errorMessage && (
              <div className="error-footer" style={{ marginBottom: '15px', borderRadius: '4px' }}>
                {errorMessage}
              </div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Número de Cuenta</label>
                <input 
                  type="text" 
                  name="numeroCuenta" 
                  value={formData.numeroCuenta} 
                  onChange={handleInputChange} 
                  required 
                  minLength="6" 
                  disabled={!!editingCuenta}
                  style={editingCuenta ? { backgroundColor: '#e9ecef', cursor: 'not-allowed' } : {}}
                />
              </div>
              <div className="form-group">
                <label>Tipo de Cuenta</label>
                <select name="tipoCuenta" value={formData.tipoCuenta} onChange={handleInputChange}>
                  <option value="Ahorro">Ahorro</option>
                  <option value="Corriente">Corriente</option>
                </select>
              </div>
              <div className="form-group">
                <label>Cliente</label>
                <select name="clienteId" value={formData.clienteId} onChange={handleInputChange} required>
                  <option value="">Seleccione un cliente</option>
                  {clientes.map(c => (
                    <option key={c.id} value={c.id}>{c.nombre} ({c.identificacion})</option>
                  ))}
                </select>
              </div>
              <div className="form-group-checkbox">
                <label>
                  <input type="checkbox" name="estado" checked={formData.estado} onChange={handleInputChange} />
                  Estado Activo
                </label>
              </div>
              <div className="modal-actions">
                <button type="button" className="btn-cancel" onClick={() => setShowModal(false)}>Cancelar</button>
                <button type="submit" className="btn-save">{editingCuenta ? 'Actualizar' : 'Guardar'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Cuentas;
