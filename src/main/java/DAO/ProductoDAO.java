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
        String sql = "INSERT INTO producto(nombre, descripcion, precio) VALUES(?,?,?)";
        try (Connection con = ConnectionMySQL.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombreProducto());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getPrecio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insertar producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto";
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
        String sql = "DELETE FROM producto WHERE id_producto=?";
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
