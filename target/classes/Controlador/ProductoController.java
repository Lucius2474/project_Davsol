/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;


import Interface.IProductoDAO;
import Modelo.Producto;
import Vista.Sistema;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author aalex
 */
public class ProductoController {

    private Sistema vista;
    IProductoDAO productoDAO;

    public ProductoController(Sistema vista, IProductoDAO productoDAO) {
        this.vista = vista;
        this.productoDAO = productoDAO;
    }

    public void registrarProducto() {
        Producto p = new Producto();
        p.setNombreProducto(vista.txtF_nombreproduct.getText());
        p.setDescripcion(vista.txtF_descripcion.getText());
        p.setPrecio(Double.parseDouble(vista.txtF_precioProduct.getText()));

        if (productoDAO.insertar(p)) {
            listarProductos();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(null, "Error al registrar producto");
        }
    }

    public void listarProductos() {
        DefaultTableModel modelo = (DefaultTableModel) vista.t_product.getModel();
        modelo.setRowCount(0);
        for (Producto p : productoDAO.listar()) {
            modelo.addRow(new Object[]{p.getIdproducto(), p.getNombreProducto(), p.getDescripcion(), p.getPrecio()});
        }
    }

    public void actualizarProducto() {
        int fila = vista.t_product.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un producto");
            return;
        }
        int id = (int) vista.t_product.getValueAt(fila, 0);
        Producto p = new Producto();
        p.setIdproducto(id);
        p.setNombreProducto(vista.txtF_nombreproduct.getText());
        p.setDescripcion(vista.txtF_descripcion.getText());
        p.setPrecio(Double.parseDouble(vista.txtF_precioProduct.getText()));

        if (productoDAO.actualizar(p)) {
            listarProductos();
            limpiarCampos();
            JOptionPane.showMessageDialog(null, "Producto actualizado");
        } else {
            JOptionPane.showMessageDialog(null, "Error al actualizar");
        }
    }

    public void eliminarProducto() {
        int fila = vista.t_product.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un producto");
            return;
        }
        int id = (int) vista.t_product.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(null, "¿Eliminar?") == JOptionPane.YES_OPTION) {
            if (productoDAO.eliminar(id)) {
                listarProductos();
                limpiarCampos();
                JOptionPane.showMessageDialog(null, "Producto eliminado");
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo eliminar");
            }
        }
    }

    public void seleccionarProductoTabla() {
        int fila = vista.t_product.getSelectedRow();
        if (fila != -1) {
            vista.txtF_nombreproduct.setText(vista.t_product.getValueAt(fila, 1).toString());
            vista.txtF_descripcion.setText(vista.t_product.getValueAt(fila, 2).toString());
            vista.txtF_precioProduct.setText(vista.t_product.getValueAt(fila, 3).toString());
        }
    }

    private void limpiarCampos() {
        vista.txtF_nombreproduct.setText("");
        vista.txtF_descripcion.setText("");
        vista.txtF_precioProduct.setText("");
    }

    public Producto getProductoPorNombre(String nombre) {
        for (Producto p : productoDAO.listar()) {
            if (p.getNombreProducto().equals(nombre)) return p;
        }
        return null;
    }
}
