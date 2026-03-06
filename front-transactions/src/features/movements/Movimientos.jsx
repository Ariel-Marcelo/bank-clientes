import React, { useState, useEffect, useMemo } from 'react';
import { movimientoService, cuentaService } from '../../services/api';

function Movimientos() {
  const [movimientos, setMovimientos] = useState([]);
  const [cuentas, setCuentas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingMovimiento, setEditingMovimiento] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);
  const [formData, setFormData] = useState({
    numeroCuenta: '',
    tipoMovimiento: 'DEPOSITO',
    valor: 0,
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    setErrorMessage(null);
    try {
      const response = await movimientoService.getAll();
      if (response.status === false) {
        setErrorMessage(response.message || 'Error al cargar movimientos');
        setMovimientos([]);
      } else {
        setMovimientos(Array.isArray(response.data) ? response.data : []);
      }
      
      const resCuentas = await cuentaService.getAll();
      setCuentas(Array.isArray(resCuentas.data) ? resCuentas.data : []);
    } catch (error) {
      setErrorMessage('Error de conexión con el servidor');
    } finally {
      setLoading(false);
    }
  };

  const formatFecha = (fechaStr) => {
    if (!fechaStr) return '';
    return new Date(fechaStr).toLocaleString();
  };

  const filteredMovimientos = useMemo(() => {
    if (!searchTerm) return movimientos;
    const lowerSearch = searchTerm.toLowerCase();
    return movimientos.filter(m => 
      (m.numeroCuenta && m.numeroCuenta.toLowerCase().includes(lowerSearch)) ||
      (m.tipoMovimiento && m.tipoMovimiento.toLowerCase().includes(lowerSearch)) ||
      (m.valor.toString().includes(lowerSearch)) ||
      (m.saldo.toString().includes(lowerSearch)) ||
      (formatFecha(m.fecha).toLowerCase().includes(lowerSearch))
    );
  }, [searchTerm, movimientos]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name === 'valor' ? parseFloat(value) : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage(null);
    try {
      let result;
      if (editingMovimiento) {
        result = await movimientoService.update(editingMovimiento.id, formData);
      } else {
        result = await movimientoService.create(formData);
      }

      if (result.status === false) {
        setErrorMessage(result.message || 'Error al procesar el movimiento');
      } else {
        setShowModal(false);
        resetForm();
        loadData();
      }
    } catch (error) {
      setErrorMessage('Error de red al intentar guardar el movimiento');
    }
  };

  const resetForm = () => {
    setFormData({
      numeroCuenta: '',
      tipoMovimiento: 'DEPOSITO',
      valor: 0,
    });
    setEditingMovimiento(null);
  };

  const handleEdit = (mov) => {
    setEditingMovimiento(mov);
    setFormData({
      numeroCuenta: mov.numeroCuenta || '',
      tipoMovimiento: mov.tipoMovimiento || 'DEPOSITO',
      valor: mov.valor || 0,
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar este movimiento?')) {
      setErrorMessage(null);
      try {
        await movimientoService.delete(id);
        loadData();
      } catch (error) {
        setErrorMessage('Error de red al intentar eliminar el movimiento');
      }
    }
  };

  return (
    <div>
      <h1 className="content-title">Movimientos</h1>
      
      <div className="toolbar">
        <input 
          type="text" 
          className="search-input" 
          placeholder="Buscar movimientos..." 
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <button className="btn-nuevo" onClick={() => { resetForm(); setShowModal(true); }}>Nuevo Movimiento</button>
      </div>

      {loading ? (
        <p>Cargando...</p>
      ) : (
        <>
          <table className="data-table">
            <thead>
              <tr>
                <th>Fecha</th>
                <th>Cuenta</th>
                <th>Tipo</th>
                <th>Valor</th>
                <th>Saldo</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredMovimientos.length > 0 ? (
                filteredMovimientos.map((mov) => (
                  <tr key={mov.id}>
                    <td>{formatFecha(mov.fecha)}</td>
                    <td>{mov.numeroCuenta}</td>
                    <td>{mov.tipoMovimiento}</td>
                    <td style={{ color: mov.valor < 0 ? 'red' : 'green' }}>{mov.valor}</td>
                    <td>{mov.saldo}</td>
                    <td>
                      <button className="btn-action btn-edit" onClick={() => handleEdit(mov)}>Actualizar</button>
                      <button className="btn-action btn-delete" onClick={() => handleDelete(mov.id)}>Eliminar</button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center' }}>No se encontraron movimientos</td>
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
            <h2>{editingMovimiento ? 'Actualizar Movimiento' : 'Nuevo Movimiento'}</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Número de Cuenta</label>
                <select name="numeroCuenta" value={formData.numeroCuenta} onChange={handleInputChange} required>
                  <option value="">Seleccione una cuenta</option>
                  {cuentas.map(c => (
                    <option key={c.id} value={c.numeroCuenta}>{c.numeroCuenta} - {c.nombreCliente}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Tipo de Movimiento</label>
                <select name="tipoMovimiento" value={formData.tipoMovimiento} onChange={handleInputChange}>
                  <option value="DEPOSITO">Depósito</option>
                  <option value="RETIRO">Retiro</option>
                </select>
              </div>
              <div className="form-group">
                <label>Valor</label>
                <input type="number" name="valor" value={formData.valor} onChange={handleInputChange} step="0.01" required />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn-cancel" onClick={() => setShowModal(false)}>Cancelar</button>
                <button type="submit" className="btn-save">{editingMovimiento ? 'Actualizar' : 'Guardar'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Movimientos;
