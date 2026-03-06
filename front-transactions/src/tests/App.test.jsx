import React from 'react';
import { render, screen } from '@testing-library/react';
import App from '../App';

describe('App Clientes', () => {
  test('renderiza el título del banco en la cabecera', () => {
    render(<App />);
    const logoElement = screen.getByText(/BANCO/i);
    expect(logoElement).toBeInTheDocument();
  });

  test('renderiza el menú lateral con opciones', () => {
    render(<App />);
    expect(screen.getAllByText('Clientes')[0]).toBeInTheDocument();
    expect(screen.getByText('Cuentas')).toBeInTheDocument();
    expect(screen.getByText('Movimientos')).toBeInTheDocument();
    expect(screen.getByText('Reportes')).toBeInTheDocument();
  });

  test('renderiza el título principal de Clientes', () => {
    render(<App />);
    // Buscamos el h1
    const titleElement = screen.getByRole('heading', { level: 1, name: /Clientes/i });
    expect(titleElement).toBeInTheDocument();
  });

  test('renderiza el input de búsqueda', () => {
    render(<App />);
    const inputElement = screen.getByPlaceholderText(/Buscar/i);
    expect(inputElement).toBeInTheDocument();
  });

  test('renderiza el botón Nuevo', () => {
    render(<App />);
    const buttonElement = screen.getByRole('button', { name: /Nuevo/i });
    expect(buttonElement).toBeInTheDocument();
  });
});