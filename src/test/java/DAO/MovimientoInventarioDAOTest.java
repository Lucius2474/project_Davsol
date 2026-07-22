package DAO;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.ConnectionMySQL;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MovimientoInventarioDAOTest {

    private static MovimientoInventarioDAO movimientoDAO;
    private static InventarioDAO inventarioDAO;

    // Referencias para obtener un id_inventario real existente
    private static final int ID_PRODUCTO_PRUEBA = 1; // "Terma Solar Eco 80L"
    private static int idInventarioPrueba = -1;

    @BeforeAll
    static void setUp() {
        movimientoDAO = new MovimientoInventarioDAO();
        inventarioDAO = new InventarioDAO();

        // Recuperamos un id_inventario válido de la base de datos para la prueba
        try (Connection con = ConnectionMySQL.getConexion()) {
            idInventarioPrueba = inventarioDAO.obtenerIdInventario(con, ID_PRODUCTO_PRUEBA);
        } catch (SQLException e) {
            fail("No se pudo obtener el id_inventario inicial: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. Insertar movimiento de ENTRADA de stock en transacción")
    void test1_InsertarMovimientoEntrada() {
        assertTrue(idInventarioPrueba > 0, "Se requiere un ID de inventario válido asociado al producto");

        try (Connection con = ConnectionMySQL.getConexion()) {
            con.setAutoCommit(false); // Iniciar transacción explícita

            String tipo = "ENTRADA";
            int cantidad = 10;

            // Ejecutar la inserción
            assertDoesNotThrow(() -> movimientoDAO.insertar(con, idInventarioPrueba, tipo, cantidad),
                    "La inserción del movimiento no debe lanzar excepciones SQL");

            // Verificar que el registro se guardó correctamente en la tabla movimiento_inventario
            String sqlVerificar = "SELECT tipo_movimiento, cantidad FROM movimiento_inventario WHERE id_inventario = ? AND tipo_movimiento = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlVerificar)) {
                ps.setInt(1, idInventarioPrueba);
                ps.setString(2, tipo);
                ResultSet rs = ps.executeQuery();

                assertTrue(rs.next(), "Debe existir al menos un registro con el tipo de movimiento especificado");
                assertEquals(tipo, rs.getString("tipo_movimiento"));
            }

            con.commit(); // Confirmar la transacción
            System.out.println("Movimiento 'ENTRADA' registrado y verificado correctamente.");

        } catch (SQLException e) {
            fail("Falló la prueba de inserción de movimiento ENTRADA: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. Insertar movimiento de SALIDA de stock")
    void test2_InsertarMovimientoSalida() {
        assertTrue(idInventarioPrueba > 0, "Se requiere un ID de inventario válido");

        try (Connection con = ConnectionMySQL.getConexion()) {
            con.setAutoCommit(false);

            String tipo = "SALIDA";
            int cantidad = 3;

            assertDoesNotThrow(() -> movimientoDAO.insertar(con, idInventarioPrueba, tipo, cantidad));

            con.commit();
            System.out.println("Movimiento 'SALIDA' registrado con éxito.");

        } catch (SQLException e) {
            fail("Falló la prueba de inserción de movimiento SALIDA: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("3. Validar fallo por Foreign Key cuando id_inventario no existe")
    void test3_InsertarIdInventarioInexistente() {
        try (Connection con = ConnectionMySQL.getConexion()) {
            con.setAutoCommit(false);

            int idInventarioInvalido = -9999;

            // Debe lanzar SQLException al violar la restricción de llave foránea (FK)
            assertThrows(SQLException.class, () -> {
                movimientoDAO.insertar(con, idInventarioInvalido, "ENTRADA", 5);
            }, "Debe arrojar SQLException por violar la Foreign Key con id_inventario inexistente");

            con.rollback(); // Deshacer cambios pendientes

        } catch (SQLException e) {
            fail("Excepción inesperada al probar Foreign Key inválida: " + e.getMessage());
        }
    }
}