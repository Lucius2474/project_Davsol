/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.*;


/**
 *
 * @author aalex
 */
public class MovimientoInventarioDAO implements Interface.IMovimientoInventarioDAO{
    
    @Override
    public void insertar(Connection con, int idInventario, String tipo, int cantidad) throws SQLException {
        String sql = "INSERT INTO movimiento_inventario(id_inventario, tipo_movimiento, cantidad) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInventario);
            ps.setString(2, tipo);
            ps.setInt(3, cantidad);
            ps.executeUpdate();
        }
    }
}
