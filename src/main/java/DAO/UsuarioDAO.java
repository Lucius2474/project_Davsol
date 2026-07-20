/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Interface.IUsuarioDAO;
import Modelo.Usuario;
import util.ConnectionMySQL;
import org.mindrot.jbcrypt.BCrypt; // Necesitas agregar la dependencia: https://mvnrepository.com/artifact/org.mindrot/jbcrypt
import java.sql.*;
/**
 *
 * @author aalex
 */


public class UsuarioDAO implements IUsuarioDAO {

    @Override
    public Usuario autenticar(String username, String password) {
        String sql = "SELECT * FROM usuario WHERE username = ? AND activo = 1";
        try (Connection con = ConnectionMySQL.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                // Verificar la contraseña contra el hash
                if (BCrypt.checkpw(password, hash)) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setUsername(rs.getString("username"));
                    u.setRol(rs.getString("rol"));
                    u.setActivo(rs.getBoolean("activo"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error autenticar: " + e.getMessage());
        }
        return null;
    }
}
