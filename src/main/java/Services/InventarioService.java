/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Services;

import Interface.*;
import Modelo.Inventario;
import Modelo.Producto;
import java.sql.Connection;
import java.sql.SQLException;
import util.ConnectionMySQL;

public class InventarioService {
    private IInventarioDAO inventarioDAO;
    private IMovimientoInventarioDAO movimientoDAO;

    public InventarioService(IInventarioDAO inventarioDAO, IMovimientoInventarioDAO movimientoDAO) {
        this.inventarioDAO = inventarioDAO;
        this.movimientoDAO = movimientoDAO;
    }

    public void procesarMovimiento(Producto producto, String tipo, int cantidad) throws SQLException {
        Connection con = null;
        try {
            con = ConnectionMySQL.getConexion();
            con.setAutoCommit(false);

            Inventario inv = inventarioDAO.obtenerPorProductoConConexion(con, producto.getIdproducto());
            if (inv == null) throw new SQLException("Producto no tiene inventario");

            int nuevoStock;
            if ("ENTRADA".equalsIgnoreCase(tipo)) {
                nuevoStock = inv.getStockActual() + cantidad;
            } else if ("SALIDA".equalsIgnoreCase(tipo)) {
                if (inv.getStockActual() < cantidad) throw new SQLException("Stock insuficiente");
                nuevoStock = inv.getStockActual() - cantidad;
            } else {
                throw new SQLException("Tipo de movimiento inválido");
            }

            inventarioDAO.actualizarStock(con, inv.getIdinventario(), nuevoStock);
            movimientoDAO.insertar(con, inv.getIdinventario(), tipo.toUpperCase(), cantidad);

            con.commit();
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) con.close();
        }
    }
}
