import React, { useState, useEffect, useMemo } from 'react';
import { clienteService } from '../../services/api';

function Clientes() {
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingCliente, setEditingCliente] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);
  const [formData, setFormData] = useState({
    nombre: '',
    genero: 'Masculino',
    edad: 0,
    identificacion: '',
    direccion: '',
    telefono: '',
    clienteId: '',
    contrasenia: '',
    estado: true,
  });

  useEffect(() => {
    loadClientes();
  }, []);

  const loadClientes = async () => {
    setLoading(true);
    setErrorMessage(null);
    try {
      const data = await clienteService.getAll();
      setClientes(Array.isArray(data) ? data : []);
    } catch (error) {
      setErrorMessage('Error de conexión con el servidor');
      console.error('Error cargando clientes:', error);
    } finally {
      setLoading(false);
    }
  };

  const filteredClientes = useMemo(() => {
    if (!searchTerm) return clientes;
    const lowerSearch = searchTerm.toLowerCase();
    return clientes.filter(c => 
      (c.nombre && c.nombre.toLowerCase().includes(lowerSearch)) ||
      (c.identificacion && c.identificacion.toLowerCase().includes(lowerSearch)) ||
      (c.direccion && c.direccion.toLowerCase().includes(lowerSearch)) ||
      (c.telefono && c.telefono.toLowerCase().includes(lowerSearch)) ||
      ((c.estado ? 'activo' : 'inactivo').includes(lowerSearch))
    );
  }, [searchTerm, clientes]);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : type === 'number' ? parseInt(value) : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage(null);
    try {
      let result;
      if (editingCliente) {
        result = await clienteService.update(editingCliente.id, formData);
      } else {
        result = await clienteService.create(formData);
      }

      if (result.status === false) {
        setErrorMessage(result.message || 'Error al procesar la solicitud');
      } else {
        setShowModal(false);
        resetForm();
        loadClientes();
      }
    } catch (error) {
      setErrorMessage('Error de red al intentar guardar');
      console.error('Error guardando cliente:', error);
    }
  };

  const resetForm = () => {
    setFormData({
      nombre: '',
      genero: 'Masculino',
      edad: 0,
      identificacion: '',
      direccion: '',
      telefono: '',
      clienteId: '',
      contrasenia: '',
      estado: true,
    });
    setEditingCliente(null);
    setErrorMessage(null);
  };

  const handleEdit = (cliente) => {
    setEditingCliente(cliente);
    setFormData({
      nombre: cliente.nombre || '',
      genero: cliente.genero || 'Masculino',
      edad: cliente.edad || 0,
      identificacion: cliente.identificacion || '',
      direccion: cliente.direccion || '',
      telefono: cliente.telefono || '',
      clienteId: cliente.clienteId || '',
      contrasenia: '', // No devolvemos contraseña en el get
      estado: cliente.estado !== undefined ? cliente.estado : true,
    });
    setErrorMessage(null);
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar este cliente?')) {
      setErrorMessage(null);
      try {
        await clienteService.delete(id);
        loadClientes();
      } catch (error) {
        setErrorMessage('Error de red al intentar eliminar');
        console.error('Error eliminando cliente:', error);
      }
    }
  };

  return (
    <div>
      <h1 className="content-title">Clientes</h1>
      
      <div className="toolbar">
        <input 
          type="text" 
          className="search-input" 
          placeholder="Buscar cliente..." 
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <button className="btn-nuevo" onClick={() => { resetForm(); setShowModal(true); }}>Nuevo</button>
      </div>

      {loading ? (
        <p>Cargando...</p>
      ) : (
        <>
          <table className="data-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Identificación</th>
                <th>Dirección</th>
                <th>Teléfono</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredClientes.length > 0 ? (
                filteredClientes.map((cliente) => (
                  <tr key={cliente.id}>
                    <td>{cliente.nombre}</td>
                    <td>{cliente.identificacion}</td>
                    <td>{cliente.direccion}</td>
                    <td>{cliente.telefono}</td>
                    <td>{cliente.estado ? 'Activo' : 'Inactivo'}</td>
                    <td>
                      <button className="btn-action btn-edit" onClick={() => handleEdit(cliente)}>Actualizar</button>
                      <button className="btn-action btn-delete" onClick={() => handleDelete(cliente.id)}>Eliminar</button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center' }}>No se encontraron clientes</td>
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
            <h2>{editingCliente ? 'Actualizar Cliente' : 'Nuevo Cliente'}</h2>
            
            {errorMessage && (
              <div className="error-footer" style={{ marginBottom: '15px', borderRadius: '4px' }}>
                {errorMessage}
              </div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Nombre</label>
                <input type="text" name="nombre" value={formData.nombre} onChange={handleInputChange} required />
              </div>
              <div className="form-group">
                <label>Género</label>
                <select name="genero" value={formData.genero} onChange={handleInputChange}>
                  <option value="Masculino">Masculino</option>
                  <option value="Femenino">Femenino</option>
                  <option value="Otro">Otro</option>
                </select>
              </div>
              <div className="form-group">
                <label>Edad</label>
                <input type="number" name="edad" value={formData.edad} onChange={handleInputChange} min="18" required />
              </div>
              <div className="form-group">
                <label>Identificación</label>
                <input type="text" name="identificacion" value={formData.identificacion} onChange={handleInputChange} required />
              </div>
              <div className="form-group">
                <label>Dirección</label>
                <input type="text" name="direccion" value={formData.direccion} onChange={handleInputChange} required />
              </div>
              <div className="form-group">
                <label>Teléfono</label>
                <input type="text" name="telefono" value={formData.telefono} onChange={handleInputChange} required />
              </div>
              <div className="form-group">
                <label>Cliente ID (Username)</label>
                <input type="text" name="clienteId" value={formData.clienteId} onChange={handleInputChange} required />
              </div>
              {!editingCliente && (
                <div className="form-group">
                  <label>Contraseña</label>
                  <input type="password" name="contrasenia" value={formData.contrasenia} onChange={handleInputChange} required />
                </div>
              )}
              <div className="form-group-checkbox">
                <label>
                  <input type="checkbox" name="estado" checked={formData.estado} onChange={handleInputChange} />
                  Estado Activo
                </label>
              </div>
              <div className="modal-actions">
                <button type="button" className="btn-cancel" onClick={() => setShowModal(false)}>Cancelar</button>
                <button type="submit" className="btn-save">{editingCliente ? 'Actualizar' : 'Guardar'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Clientes;
