/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import java.sql.Connection;
import java.sql.SQLException;

public interface IMovimientoInventarioDAO {
    void insertar(Connection con, int idInventario, String tipo, int cantidad) throws SQLException;
}
