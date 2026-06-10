/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;


import Modelo.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import util.ConnectionMySQL;

/**
 *
 * @author aalex
 */
public class ClienteDAO implements Interface.IClienteDAO{
    
    @Override
    public boolean insertar(Cliente c) {
        String sql = "INSERT INTO cliente(dni, nombre, telefono, correo) VALUES(?,?,?,?)";
        try (Connection con = ConnectionMySQL.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getDniRUC());
            ps.setString(2, c.getNombres());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getCorreo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insertar cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Cliente c) {
        String sql = "UPDATE cliente SET dni=?, nombre=?, telefono=?, correo=? WHERE id_cliente=?";
        try (Connection con = ConnectionMySQL.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getDniRUC());
            ps.setString(2, c.getNombres());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getCorreo());
            ps.setInt(5, c.getIdcliente());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int idCliente) {
        String sql = "DELETE FROM cliente WHERE id_cliente=?";
        try (Connection con = ConnectionMySQL.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminar cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente";
        try (Connection con = ConnectionMySQL.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdcliente(rs.getInt("id_cliente"));
                c.setDniRUC(rs.getString("dni"));
                c.setNombres(rs.getString("nombre"));
                c.setTelefono(rs.getString("telefono"));
                c.setCorreo(rs.getString("correo"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error listar cliente: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Cliente buscarPorRUC(String dni) {
        String sql = "SELECT * FROM cliente WHERE dni = ?";
        try (Connection con = ConnectionMySQL.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Cliente c = new Cliente();
                c.setIdcliente(rs.getInt("id_cliente"));
                c.setDniRUC(rs.getString("dni"));
                c.setNombres(rs.getString("nombre"));
                c.setTelefono(rs.getString("telefono"));
                c.setCorreo(rs.getString("correo"));
                return c;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error buscar cliente: " + e.getMessage(), "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
        return null;
    }
}
