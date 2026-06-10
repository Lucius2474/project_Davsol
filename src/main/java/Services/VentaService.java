/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Services;

import Interface.*;
import Modelo.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import util.ConnectionMySQL;

public class VentaService {
    private IVentaDAO ventaDAO;
    private IDetalleVentaDAO detalleDAO;
    private IInventarioDAO inventarioDAO;
    private IMovimientoInventarioDAO movimientoDAO;

    public VentaService(IVentaDAO ventaDAO, IDetalleVentaDAO detalleDAO, 
                        IInventarioDAO inventarioDAO, IMovimientoInventarioDAO movimientoDAO) {
        this.ventaDAO = ventaDAO;
        this.detalleDAO = detalleDAO;
        this.inventarioDAO = inventarioDAO;
        this.movimientoDAO = movimientoDAO;
    }

    public int procesarVenta(Venta venta, List<DetalleVenta> detalles) throws SQLException {
        Connection con = null;
        try {
            con = ConnectionMySQL.getConexion();
            con.setAutoCommit(false);

            int idVenta = ventaDAO.insertar(con, venta);
            if (idVenta == -1) throw new SQLException("Error al insertar venta");

            for (DetalleVenta detalle : detalles) {
                detalle.setIdVenta(idVenta);
                detalleDAO.insertar(con, detalle);

                boolean ok = ventaDAO.registrarSalidaStock(con, 
                    new Producto() {{ setIdproducto(detalle.getIdProducto()); }}, 
                    detalle.getCantidad());
                if (!ok) throw new SQLException("Stock insuficiente para producto id: " + detalle.getIdProducto());
            }

            con.commit();
            return idVenta;
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) con.close();
        }
    }
}
