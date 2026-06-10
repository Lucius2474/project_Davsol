/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;


import java.sql.*;
import Modelo.Inventario;
import Modelo.Producto;
import java.util.ArrayList;
import java.util.List;
import util.*;


/**
 *
 * @author aalex
 */
public class InventarioDAO implements Interface.IInventarioDAO{
    
    @Override
    public int obtenerStockActual(Connection con, int idProducto) throws SQLException {
        String sql = "SELECT stock_actual FROM inventario WHERE id_producto=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("stock_actual") : 0;
        }
    }

    @Override
    public void actualizarStock(Connection con, int idInventario, int nuevoStock) throws SQLException {
        String sql = "UPDATE inventario SET stock_actual=? WHERE id_inventario=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idInventario);
            ps.executeUpdate();
        }
    }

    @Override
    public Inventario obtenerPorProducto(int idProducto) {
        String sql = "SELECT * FROM inventario WHERE id_producto=?";
        try (Connection con = ConnectionMySQL.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Inventario inv = new Inventario();
                inv.setIdinventario(rs.getInt("id_inventario"));
                inv.setStockActual(rs.getInt("stock_actual"));
                Producto p = new Producto();
                p.setIdproducto(rs.getInt("id_producto"));
                inv.setProducto(p);
                return inv;
            }
        } catch (SQLException e) {
            System.out.println("Error obtener inventario: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Inventario obtenerPorProductoConConexion(Connection con, int idProducto) throws SQLException {
        String sql = "SELECT id_inventario, stock_actual FROM inventario WHERE id_producto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Inventario inv = new Inventario();
                inv.setIdinventario(rs.getInt("id_inventario"));
                inv.setStockActual(rs.getInt("stock_actual"));
                return inv;
            }
        }
        return null;
    }

    @Override
    public List<Inventario> listar() {
        List<Inventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM inventario";
        try (Connection con = ConnectionMySQL.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Inventario i = new Inventario();
                Producto p = new Producto();
                p.setIdproducto(rs.getInt("id_producto"));
                i.setProducto(p);
                i.setStockActual(rs.getInt("stock_actual"));
                lista.add(i);
            }
        } catch (SQLException e) {
            System.out.println("Error listar inventario: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public int obtenerIdInventario(Connection con, int idProducto) throws SQLException {
        String sql = "SELECT id_inventario FROM inventario WHERE id_producto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("id_inventario") : -1;
        }
    }

    @Override
    public void actualizarStockRV(Connection con, int idProducto, int nuevaCantidad) throws SQLException {
        String sql = "UPDATE inventario SET stock_actual = ? WHERE id_producto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevaCantidad);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }
}
