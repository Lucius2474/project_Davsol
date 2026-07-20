/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Interface.IUsuarioDAO;
import Modelo.Usuario;
import Vista.Login;
import Vista.Sistema;
import javax.swing.JOptionPane;

/**
 *
 * @author aalex
 */
public class LoginController {
    private Login vista;
    private IUsuarioDAO usuarioDAO;

    public LoginController(Login vista, IUsuarioDAO usuarioDAO) {
        this.vista = vista;
        this.usuarioDAO = usuarioDAO;
    }

    public void autenticar() {
        String user = vista.getUsername();
        String pass = vista.getPassword();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(vista,"Ingrese usuario y contraseña.", "Advertencia",JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuario = usuarioDAO.autenticar(user, pass);
        if (usuario != null) {
            // Cerrar login y abrir sistema principal
            vista.cerrarVentana();
            Sistema sistema = new Sistema();
            // Pasar el usuario autenticado para control de permisos
            sistema.setUsuarioAutenticado(usuario);
            sistema.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(vista,"Credenciales incorrectas o usuario inactivo.", "Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}
