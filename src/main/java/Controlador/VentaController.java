/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import DAO.*;
import Interface.*;
import Modelo.*;
import Services.VentaService;
import Vista.Sistema;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aalex
 */
public class VentaController {

    private Sistema vista;
    private ProductoController productoController;
    private IClienteDAO clienteDAO;
    private VentaService ventaService;
    private IInventarioDAO inventarioDAO; 
    private List<DetalleVenta> detallesTemp;
    private Cliente clienteActual;
    private VentaPDF ventaPDF;
    private double totalPagar;
    

    
    public VentaController(Sistema vista, ProductoController productoController,
                           IClienteDAO clienteDAO, IVentaDAO ventaDAO,
                           IDetalleVentaDAO detalleDAO, IInventarioDAO inventarioDAO,
                           IMovimientoInventarioDAO movimientoDAO) {
        this.vista = vista;
        this.productoController = productoController;
        this.clienteDAO = clienteDAO;
        this.ventaService = new VentaService(ventaDAO, detalleDAO, inventarioDAO, movimientoDAO);
        this.inventarioDAO = inventarioDAO;
        this.detallesTemp = new ArrayList<>();
    }

    public void cargarProductosEnCombo() {
        productoController.listarProductos(); // llena el combo indirectamente
        // pero mejor: llenar cbox_producto1 con nombres de productos
        var productos = productoController.productoDAO.listar(); // necesitas acceso, ajusta
        vista.cbox_producto1.removeAllItems();
        for (Producto p : productos) {
            vista.cbox_producto1.addItem(p.getNombreProducto());
        }
    }

    public Producto getProductoSeleccionado() {
        String nombre = (String) vista.cbox_producto1.getSelectedItem();
        return productoController.getProductoPorNombre(nombre);
    }

    public void productoSeleccionado() {
        Producto p = getProductoSeleccionado();
        if (p == null) return;
        vista.txtF_precio.setText(String.valueOf(p.getPrecio()));
        // Obtener stock desde inventario
        // (deberías tener un método en InventarioController o acceder a inventarioDAO)
        // Simplificamos: asumimos que tienes inventarioDAO
        int stock = inventarioDAO.obtenerStockActual(p.getIdproducto());
        vista.txtF_stockDis.setText(String.valueOf(stock));
        vista.txtF_cantidad.setText("");
        vista.btn_agregarVenta.setEnabled(true);
    }

    public void agregarProducto() {
        if (vista.txtF_cantidad.getText().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese cantidad");
            return;
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(vista.txtF_cantidad.getText());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Cantidad inválida");
            return;
        }

        Producto prod = getProductoSeleccionado();
        if (prod == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione un producto");
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(vista.txtF_precio.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Precio inválido");
            return;
        }

        int stockActual = inventarioDAO.obtenerStockActual(prod.getIdproducto());
        if (cantidad > stockActual) {
            JOptionPane.showMessageDialog(vista, 
                "Stock insuficiente. Disponible: " + stockActual,
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double subtotal = cantidad * precio;

        DefaultTableModel model = (DefaultTableModel) vista.t_regVent.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            Producto pTabla = (Producto) model.getValueAt(i, 1);
            if (pTabla.getIdproducto() == prod.getIdproducto()) {
                JOptionPane.showMessageDialog(vista, "El producto ya está en la lista");
                return;
            }
        }
        
        vista.btn_deleteVenta.setEnabled(true);
        model.addRow(new Object[]{cantidad, prod, precio, subtotal});
        detallesTemp.add(new DetalleVenta(cantidad, 0, 0, prod.getIdproducto(), precio, subtotal));
        recalcularTotal();
        limpiarCamposProducto();
    }

    public void eliminarVenta() {
        int fila = vista.t_regVent.getSelectedRow();
        if (fila >= 0) {
            DefaultTableModel model = (DefaultTableModel) vista.t_regVent.getModel();
            model.removeRow(fila);
            detallesTemp.remove(fila);
            recalcularTotal();
        }
    }

    private void recalcularTotal() {
        totalPagar = detallesTemp.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        vista.txtF_total.setText(String.format("%.2f", totalPagar));
    }

    private void limpiarCamposProducto() {
        vista.txtF_cantidad.setText("");
        vista.txtF_precio.setText("");
        vista.txtF_stockDis.setText("");
    }

    public void autocompletarCliente() {
        String dni = vista.txtF_ruc.getText();
        if (dni.length() >= 8) {
            Cliente c = clienteDAO.buscarPorRUC(dni);
            if (c != null) {
                clienteActual = c;
                vista.txtF_nombre.setText(c.getNombres());
            } else {
                clienteActual = null;
                vista.txtF_nombre.setText("");
            }
        }
    }

    public void procesarVentaCompleta() {
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(null, "Cliente no válido");
            return;
        }
        if (detallesTemp.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay productos en la venta");
            return;
        }

        Venta venta = new Venta();
        venta.setIdcliente(clienteActual.getIdcliente());
        venta.setTotal(totalPagar);

        try {
            int idVenta = ventaService.procesarVenta(venta, detallesTemp);
            JOptionPane.showMessageDialog(null, "Venta registrada con ID: " + idVenta);
            ventaPDF.generarFacturaPDF(clienteActual);
            // Limpiar carrito
            DefaultTableModel model = (DefaultTableModel) vista.t_regVent.getModel();
            model.setRowCount(0);
            detallesTemp.clear();
            totalPagar = 0;
            vista.txtF_total.setText("");
            vista.txtF_ruc.setText("");
            vista.txtF_nombre.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar venta: " + e.getMessage());
        }
    }
    
}
