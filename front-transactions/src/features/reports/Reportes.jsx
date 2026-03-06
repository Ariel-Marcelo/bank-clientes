import React, { useState, useEffect } from 'react';
import { reporteService, clienteService } from '../../services/api';

function Reportes() {
  const [clientes, setClientes] = useState([]);
  const [reporteData, setReporteData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);
  
  const [filters, setFilters] = useState({
    clienteId: '',
    startDate: '',
    endDate: '',
  });

  useEffect(() => {
    loadClientes();
  }, []);

  const loadClientes = async () => {
    try {
      const data = await clienteService.getAll();
      setClientes(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Error cargando clientes:', error);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFilters({ ...filters, [name]: value });
  };

  const handleGenerateReport = async (e) => {
    e.preventDefault();
    if (!filters.clienteId || !filters.startDate || !filters.endDate) {
      setErrorMessage('Por favor complete todos los filtros');
      return;
    }

    setLoading(true);
    setErrorMessage(null);
    try {
      const selectedCliente = clientes.find(c => c.id.toString() === filters.clienteId);
      const res = await reporteService.getReporte(selectedCliente.clienteId, filters.startDate, filters.endDate);
      
      if (res.status === false) {
        setErrorMessage(res.message || 'Error al generar el reporte');
        setReporteData(null);
      } else {
        setReporteData(res.data);
      }
    } catch (error) {
      setErrorMessage('Error de conexión con el servidor');
    } finally {
      setLoading(false);
    }
  };

  const handleDownloadPDF = () => {
    if (!reporteData || !reporteData.pdfContent) {
      setErrorMessage('No hay contenido PDF disponible para descargar');
      return;
    }

    try {
      const byteCharacters = atob(reporteData.pdfContent);
      const byteNumbers = new Array(byteCharacters.length);
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      const byteArray = new Uint8Array(byteNumbers);
      const blob = new Blob([byteArray], { type: 'application/pdf' });
      
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `Reporte_${reporteData.nombreCliente.replace(/\s+/g, '_')}_${filters.startDate}_${filters.endDate}.pdf`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Error al descargar el PDF:', error);
      setErrorMessage('Ocurrió un error al intentar descargar el archivo PDF');
    }
  };

  const formatFecha = (fechaStr) => {
    if (!fechaStr) return '';
    return new Date(fechaStr).toLocaleDateString();
  };

  return (
    <div>
      <h1 className="content-title">Estado de Cuenta</h1>
      
      <div className="toolbar" style={{ flexWrap: 'wrap', gap: '10px' }}>
        <div className="form-group" style={{ marginBottom: 0 }}>
          <label style={{ fontSize: '12px' }}>Cliente</label>
          <select name="clienteId" value={filters.clienteId} onChange={handleInputChange} className="search-input" style={{ width: '200px' }}>
            <option value="">Seleccione un cliente</option>
            {clientes.map(c => (
              <option key={c.id} value={c.id}>{c.nombre}</option>
            ))}
          </select>
        </div>
        
        <div className="form-group" style={{ marginBottom: 0 }}>
          <label style={{ fontSize: '12px' }}>Fecha Inicio</label>
          <input type="date" name="startDate" value={filters.startDate} onChange={handleInputChange} className="search-input" style={{ width: '150px' }} />
        </div>
        
        <div className="form-group" style={{ marginBottom: 0 }}>
          <label style={{ fontSize: '12px' }}>Fecha Fin</label>
          <input type="date" name="endDate" value={filters.endDate} onChange={handleInputChange} className="search-input" style={{ width: '150px' }} />
        </div>

        <button className="btn-nuevo" onClick={handleGenerateReport} disabled={loading} style={{ alignSelf: 'flex-end' }}>
          {loading ? 'Generando...' : 'Generar Reporte'}
        </button>

        {reporteData && reporteData.pdfContent && (
          <button 
            className="btn-nuevo" 
            onClick={handleDownloadPDF} 
            style={{ alignSelf: 'flex-end', backgroundColor: '#e74c3c' }}
          >
            Descargar PDF
          </button>
        )}
      </div>

      {errorMessage && (
        <div className="error-footer" style={{ marginBottom: '20px' }}>
          <strong>Error:</strong> {errorMessage}
        </div>
      )}

      {reporteData ? (
        <div className="report-container">
          <div className="report-header" style={{ marginBottom: '20px', padding: '15px', backgroundColor: '#f9f9f9', border: '1px solid #ddd' }}>
            <p><strong>Cliente:</strong> {reporteData.nombreCliente}</p>
            <p><strong>Periodo:</strong> {reporteData.rangoFechasSolicitado}</p>
          </div>

          {reporteData.cuentas && reporteData.cuentas.length > 0 ? (
            reporteData.cuentas.map((cuenta, idx) => (
              <div key={idx} style={{ marginBottom: '40px' }}>
                <h3 style={{ marginBottom: '10px', color: '#1a2b56' }}>
                  Cuenta: {cuenta.numeroCuenta} ({cuenta.tipoCuenta}) - Saldo Actual: ${cuenta.saldoActual}
                </h3>
                
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Fecha</th>
                      <th>Tipo Movimiento</th>
                      <th>Valor</th>
                      <th>Saldo</th>
                    </tr>
                  </thead>
                  <tbody>
                    {cuenta.movimientos && cuenta.movimientos.length > 0 ? (
                      cuenta.movimientos.map((mov, midx) => (
                        <tr key={midx}>
                          <td>{formatFecha(mov.fecha)}</td>
                          <td>{mov.tipoMovimiento}</td>
                          <td style={{ color: mov.valor < 0 ? 'red' : 'green' }}>{mov.valor}</td>
                          <td>{mov.saldo}</td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan="4" style={{ textAlign: 'center' }}>No hay movimientos en este periodo</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            ))
          ) : (
            <p style={{ textAlign: 'center' }}>El cliente no tiene cuentas asociadas.</p>
          )}
        </div>
      ) : (
        !loading && <p style={{ textAlign: 'center', marginTop: '40px', color: '#666' }}>Seleccione los filtros para generar el estado de cuenta.</p>
      )}
    </div>
  );
}

export default Reportes;
