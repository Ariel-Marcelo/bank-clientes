import React, { useState } from 'react';
import './App.css';
import Clientes from './features/clients/Clientes';
import Cuentas from './features/accounts/Cuentas';
import Movimientos from './features/movements/Movimientos';
import Reportes from './features/reports/Reportes';

function App() {
  const [activeTab, setActiveTab] = useState('Clientes');

  const renderContent = () => {
    switch (activeTab) {
      case 'Clientes':
        return <Clientes />;
      case 'Cuentas':
        return <Cuentas />;
      case 'Movimientos':
        return <Movimientos />;
      case 'Reportes':
        return <Reportes />;
      default:
        return <Clientes />;
    }
  };

  return (
    <div className="app-container">
      {/* Cabecera superior */}
      <header className="header">
        <span className="header-icon">💸</span>
        <span className="header-title">BANCO</span>
      </header>

      <div className="main-layout">
        {/* Menú lateral */}
        <nav className="sidebar">
          <ul>
            <li 
              className={activeTab === 'Clientes' ? 'active' : ''} 
              onClick={() => setActiveTab('Clientes')}
            >
              Clientes
            </li>
            <li 
              className={activeTab === 'Cuentas' ? 'active' : ''} 
              onClick={() => setActiveTab('Cuentas')}
            >
              Cuentas
            </li>
            <li 
              className={activeTab === 'Movimientos' ? 'active' : ''} 
              onClick={() => setActiveTab('Movimientos')}
            >
              Movimientos
            </li>
            <li 
              className={activeTab === 'Reportes' ? 'active' : ''} 
              onClick={() => setActiveTab('Reportes')}
            >
              Reportes
            </li>
          </ul>
        </nav>

        {/* Contenido principal */}
        <main className="content">
          {renderContent()}
        </main>
      </div>
    </div>
  );
}

export default App;