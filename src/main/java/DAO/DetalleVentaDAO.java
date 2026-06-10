/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Modelo.DetalleVenta;
import java.sql.*;
import util.ConnectionMySQL;


/**
 *
 * @author aalex
 */
public class DetalleVentaDAO implements Interface.IDetalleVentaDAO{

    @Override
    public void insertar(Connection con, DetalleVenta d) throws SQLException {
        String sql = "INSERT INTO detalle_venta(id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, d.getIdVenta());
            ps.setInt(2, d.getIdProducto());
            ps.setInt(3, d.getCantidad());
            ps.setDouble(4, d.getPrecioUnitario());
            ps.setDouble(5, d.getCantidad() * d.getPrecioUnitario()); // subtotal calculado
            ps.executeUpdate();
        }
    }
}
