/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.ConnectionMySQL;

/**
 *
 * @author aalex
 */
public class ProductoDAO implements Interface.IProductoDAO{
    
    @Override
    public boolean insertar(Producto p) {
        Connection con = null;
        try {
            con = ConnectionMySQL.getConexion();
            con.setAutoCommit(false);

            // Insertar producto
            String sqlProd = "INSERT INTO producto(nombre, descripcion, precio) VALUES(?,?,?)";
            PreparedStatement psProd = con.prepareStatement(sqlProd, Statement.RETURN_GENERATED_KEYS);
            psProd.setString(1, p.getNombreProducto());
            psProd.setString(2, p.getDescripcion());
            psProd.setDouble(3, p.getPrecio());
            int affected = psProd.executeUpdate();
            if (affected == 0) throw new SQLException("No se pudo insertar el producto");

            ResultSet rs = psProd.getGeneratedKeys();
            if (rs.next()) {
                int idProducto = rs.getInt(1);
                // Insertar en inventario con stock 0
                String sqlInv = "INSERT INTO inventario(id_producto, stock_actual) VALUES(?,0)";
                PreparedStatement psInv = con.prepareStatement(sqlInv);
                psInv.setInt(1, idProducto);
                psInv.executeUpdate();
            } else {
                throw new SQLException("No se pudo obtener el ID del producto");
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            System.out.println("Error insertar producto: " + e.getMessage());
            return false;
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) {}
        }
    }

    @Override
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE activo = 1";
        try (Connection con = ConnectionMySQL.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Producto p = new Producto();
                p.setIdproducto(rs.getInt("id_producto"));
                p.setNombreProducto(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPrecio(rs.getDouble("precio"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error listar producto: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean actualizar(Producto p) {
        String sql = "UPDATE producto SET nombre=?, descripcion=?, precio=? WHERE id_producto=?";
        try (Connection con = ConnectionMySQL.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombreProducto());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getIdproducto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizar producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int idProducto) {
        String sql = "UPDATE producto SET activo = 0 WHERE id_producto = ?";
        try (Connection con = ConnectionMySQL.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminar producto: " + e.getMessage());
            return false;
        }
    }
}