/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Modelo.Producto;
import Modelo.Venta;
import java.sql.*;
import util.ConnectionMySQL;


/**
 *
 * @author aalex
 */
public class VentaDAO implements Interface.IVentaDAO{
    
    
    @Override
    public int insertar(Connection con, Venta v) throws SQLException {
        String sql = "INSERT INTO venta(id_cliente, total) VALUES(?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getIdcliente());
            ps.setDouble(2, v.getTotal());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    @Override
    public boolean registrarSalidaStock(Connection con, Producto producto, int cantidad) throws SQLException {
        String sqlUpdate = "UPDATE inventario SET stock_actual = stock_actual - ? WHERE id_producto = ? AND stock_actual >= ?";
        try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
            psUpdate.setInt(1, cantidad);
            psUpdate.setInt(2, producto.getIdproducto());
            psUpdate.setInt(3, cantidad);
            int filas = psUpdate.executeUpdate();
            if (filas == 0) return false;

            String sqlSelect = "SELECT id_inventario FROM inventario WHERE id_producto = ?";
            try (PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, producto.getIdproducto());
                ResultSet rs = psSelect.executeQuery();
                if (rs.next()) {
                    int idInventario = rs.getInt("id_inventario");
                    String sqlInsert = "INSERT INTO movimiento_inventario (id_inventario, tipo_movimiento, cantidad) VALUES (?, 'SALIDA', ?)";
                    try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                        psInsert.setInt(1, idInventario);
                        psInsert.setInt(2, cantidad);
                        psInsert.executeUpdate();
                    }
                    return true;
                }
                return false;
            }
        }
    }
}
