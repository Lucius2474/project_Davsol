/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Interface.IClienteDAO;
import Modelo.Cliente;
import Vista.Sistema;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author aalex
 */
public class ClienteController {

    private Sistema vista;
    private IClienteDAO clienteDAO;

    public ClienteController(Sistema vista, IClienteDAO clienteDAO) {
        this.vista = vista;
        this.clienteDAO = clienteDAO;
    }

    // Los métodos son similares, solo cambia que usan clienteDAO (interfaz)
    public void agregarCliente() {
        if (!Validador.esNumerico(vista.txtF_dni.getText())) {
            JOptionPane.showMessageDialog(vista, "El campo no admite el dni","Error",JOptionPane.ERROR_MESSAGE);
            limpiarCampos();
            return;
        }
        if (!Validador.esNumerico(vista.txtF_telefono.getText())) {
            JOptionPane.showMessageDialog(vista, "El campo no admite el telefono","Error",JOptionPane.ERROR_MESSAGE);
            limpiarCampos();
            return;
        }
        if (!Validador.esCorreo(vista.txtF_correo.getText())) {
            JOptionPane.showMessageDialog(vista, "El campo no admite el correo","Error",JOptionPane.ERROR_MESSAGE);
            limpiarCampos();
            return;
        }
        Cliente c = new Cliente();
        c.setDniRUC(vista.txtF_dni.getText());
        c.setNombres(vista.txtF_name.getText());
        c.setTelefono(vista.txtF_telefono.getText());
        c.setCorreo(vista.txtF_correo.getText());

        if (clienteDAO.insertar(c)) {
            listarClientes();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(null, "Error al registrar cliente");
        }
    }

    public void actualizarCliente() {
        int fila = vista.t_cliente.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un cliente");
            return;
        }
        if (!Validador.esNumerico(vista.txtF_dni.getText())) {
            JOptionPane.showMessageDialog(vista, "El campo no admite el contenido","Error",JOptionPane.ERROR_MESSAGE);
            limpiarCampos();
            return;
        }
        if (!Validador.esNumerico(vista.txtF_telefono.getText())) {
            JOptionPane.showMessageDialog(vista, "El campo no admite el contenido","Error",JOptionPane.ERROR_MESSAGE);
            limpiarCampos();
            return;
        }
        if (!Validador.esCorreo(vista.txtF_correo.getText())) {
            JOptionPane.showMessageDialog(vista, "El campo no admite el contenido","Error",JOptionPane.ERROR_MESSAGE);
            limpiarCampos();
            return;
        }
        int id = (int) vista.t_cliente.getValueAt(fila, 0);
        Cliente c = new Cliente();
        c.setIdcliente(id);
        c.setDniRUC(vista.txtF_dni.getText());
        c.setNombres(vista.txtF_name.getText());
        c.setTelefono(vista.txtF_telefono.getText());
        c.setCorreo(vista.txtF_correo.getText());

        if (clienteDAO.actualizar(c)) {
            listarClientes();
            limpiarCampos();
            JOptionPane.showMessageDialog(null, "Cliente actualizado");
        } else {
            JOptionPane.showMessageDialog(null, "Error al actualizar");
        }
    }

    public void eliminarCliente() {
        int fila = vista.t_cliente.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un cliente");
            return;
        }
        int id = (int) vista.t_cliente.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(null, "¿Eliminar?") == JOptionPane.YES_OPTION) {
            if (clienteDAO.eliminar(id)) {
                listarClientes();
                limpiarCampos();
                JOptionPane.showMessageDialog(null, "Cliente eliminado");
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo eliminar");
            }
        }
    }

    public void seleccionarClienteTabla() {
        int fila = vista.t_cliente.getSelectedRow();
        if (fila != -1) {
            vista.txtF_dni.setText(vista.t_cliente.getValueAt(fila, 1).toString());
            vista.txtF_name.setText(vista.t_cliente.getValueAt(fila, 2).toString());
            vista.txtF_telefono.setText(vista.t_cliente.getValueAt(fila, 3).toString());
            vista.txtF_correo.setText(vista.t_cliente.getValueAt(fila, 4).toString());
        }
    }

    public void listarClientes() {
        DefaultTableModel modelo = (DefaultTableModel) vista.t_cliente.getModel();
        modelo.setRowCount(0);
        for (Cliente c : clienteDAO.listar()) {
            modelo.addRow(new Object[]{c.getIdcliente(), c.getDniRUC(), c.getNombres(), c.getTelefono(), c.getCorreo()});
        }
    }

    private void limpiarCampos() {
        vista.txtF_dni.setText("");
        vista.txtF_name.setText("");
        vista.txtF_telefono.setText("");
        vista.txtF_correo.setText("");
    }
}
