/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Interface.*;
import Modelo.Producto;
import Services.InventarioService;
import Vista.Sistema;
import util.ConnectionMySQL;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aalex
 */
public class InventarioController {

    private Sistema vista;
    private IInventarioDAO inventarioDAO;
    private IProductoDAO productoDAO;
    private InventarioService inventarioService;
    private Map<String, Producto> mapaProductos;

    public InventarioController(Sistema vista, IInventarioDAO inventarioDAO, IProductoDAO productoDAO, IMovimientoInventarioDAO movimientoDAO) {
        this.vista = vista;
        this.inventarioDAO = inventarioDAO;
        this.productoDAO = productoDAO;
        this.inventarioService = new InventarioService(inventarioDAO, movimientoDAO);
        this.mapaProductos = new HashMap<>();
    }

    public void cargarProductos() {
        List<Producto> lista = productoDAO.listar();
        vista.cbox_producto.removeAllItems();
        mapaProductos.clear();
        for (Producto p : lista) {
            vista.cbox_producto.addItem(p.getNombreProducto());
            mapaProductos.put(p.getNombreProducto(), p);
        }
    }

    public Producto getProductoSeleccionado() {
        String nombre = (String) vista.cbox_producto.getSelectedItem();
        return mapaProductos.get(nombre);
    }

    public void cargarStockProducto() {
        Producto p = getProductoSeleccionado();
        if (p == null) return;
        var inv = inventarioDAO.obtenerPorProducto(p.getIdproducto());
        vista.txtF_stock.setText(inv != null ? String.valueOf(inv.getStockActual()) : "0");
    }

    public void procesarMovimiento() {
        try {
            Producto p = getProductoSeleccionado();
            if (p == null) {
                JOptionPane.showMessageDialog(null, "Seleccione un producto");
                return;
            }
            int cantidad = Integer.parseInt(vista.txtF_cantidadInventario.getText());
            if (cantidad <= 0) throw new NumberFormatException();
            String tipo = vista.cbox_movimiento.getSelectedItem().toString();

            inventarioService.procesarMovimiento(p, tipo, cantidad);
            JOptionPane.showMessageDialog(null, "Movimiento realizado");
            cargarStockProducto();
            listarMovimientos();
            vista.txtF_cantidadInventario.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Cantidad inválida");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void listarMovimientos() {
        DefaultTableModel modelo = (DefaultTableModel) vista.t_inventario.getModel();
        modelo.setRowCount(0);
        String sql = """
            SELECT m.id_movimiento, m.fecha, p.nombre, m.tipo_movimiento, m.cantidad, i.stock_actual
            FROM movimiento_inventario m
            INNER JOIN inventario i ON m.id_inventario = i.id_inventario
            INNER JOIN producto p ON i.id_producto = p.id_producto
            ORDER BY m.fecha DESC
        """;
        try (var con = ConnectionMySQL.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt(1), rs.getTimestamp(2), rs.getString(3),
                    rs.getString(4), rs.getInt(5), rs.getInt(6)
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar movimientos");
        }
    }
}

