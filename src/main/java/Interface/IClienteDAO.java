/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Modelo.Cliente;
import java.util.List;

public interface IClienteDAO {
    boolean insertar(Cliente c);
    boolean actualizar(Cliente c);
    boolean eliminar(int idCliente);
    List<Cliente> listar();
    Cliente buscarPorRUC(String dni);
}
