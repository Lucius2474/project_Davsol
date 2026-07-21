/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Modelo.Usuario;

/**
 *
 * @author aalex
 */
public interface IUsuarioDAO {
    // Buscar usuario por username (para login)
    Usuario autenticar(String username, String password);
    
}
