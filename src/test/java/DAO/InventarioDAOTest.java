package DAO;

import Modelo.Inventario;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.util.List;
import util.ConnectionMySQL;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventarioDAOTest {

    private static InventarioDAO inventarioDAO;
    private static final int ID_PRODUCTO_PRUEBA = 1; // "Terma Solar Eco 80L" de tu BD real

    @BeforeAll
    static void setUp() {
        inventarioDAO = new InventarioDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Listar inventario (Verificar registros vinculados a productos)")
    void test1_Listar() {
        List<Inventario> lista = inventarioDAO.listar();

        assertNotNull(lista, "La lista de inventario no debe ser nula");
        assertFalse(lista.isEmpty(), "Debe haber registros de inventario en la BD");
        
        System.out.println("Total de registros de inventario recuperados: " + lista.size());
    }

    @Test
    @Order(2)
    @DisplayName("2. Obtener stock actual de un producto existente (ID: 1)")
    void test2_ObtenerStockActual() {
        int stock = inventarioDAO.obtenerStockActual(ID_PRODUCTO_PRUEBA);
        
        // El stock inicial creado por ProductoDAO al insertar es 0 o el valor actual en la BD
        assertTrue(stock >= 0, "El stock actual debe ser un valor numérico válido mayor o igual a 0");
        System.out.println("Stock actual del producto ID " + ID_PRODUCTO_PRUEBA + ": " + stock);
    }

    @Test
    @Order(3)
    @DisplayName("3. Obtener objeto Inventario por ID de producto")
    void test3_ObtenerPorProducto() {
        Inventario inv = inventarioDAO.obtenerPorProducto(ID_PRODUCTO_PRUEBA);

        assertNotNull(inv, "El inventario para el producto ID " + ID_PRODUCTO_PRUEBA + " debe existir");
        assertTrue(inv.getIdinventario() > 0, "El ID de inventario debe ser mayor a 0");
        assertNotNull(inv.getProducto(), "El objeto producto dentro del inventario no debe ser nulo");
        assertEquals(ID_PRODUCTO_PRUEBA, inv.getProducto().getIdproducto());
    }

    @Test
    @Order(4)
    @DisplayName("4. Actualizar stock usando conexión externa y transacción")
    void test4_ActualizarStockConexion() {
        int stockOriginal = inventarioDAO.obtenerStockActual(ID_PRODUCTO_PRUEBA);
        int stockModificado = stockOriginal + 25; // Cantidad de prueba

        try (Connection con = ConnectionMySQL.getConexion()) {
            con.setAutoCommit(false);
            
            // Obtenemos el ID de inventario usando la conexión
            int idInventario = inventarioDAO.obtenerIdInventario(con, ID_PRODUCTO_PRUEBA);
            assertTrue(idInventario > 0, "Debe existir un ID de inventario asociado");

            // Actualizamos stock con el método transaccional
            inventarioDAO.actualizarStock(con, idInventario, stockModificado);
            con.commit();

            // Verificamos que se haya aplicado el cambio
            int stockActualizado = inventarioDAO.obtenerStockActual(ID_PRODUCTO_PRUEBA);
            assertEquals(stockModificado, stockActualizado, "El stock debe haberse actualizado correctamente");

            // Restauramos el stock original para no alterar los datos reales del negocio
            con.setAutoCommit(false);
            inventarioDAO.actualizarStock(con, idInventario, stockOriginal);
            con.commit();

        } catch (Exception e) {
            fail("La prueba falló por una excepción SQL: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("5. Probar métodos específicos con conexión (obtenerPorProductoConConexion y actualizarStockRV)")
    void test5_MetodosConConexionExplicita() {
        try (Connection con = ConnectionMySQL.getConexion()) {
            con.setAutoCommit(false);

            // Probar obtención con conexión activa
            Inventario inv = inventarioDAO.obtenerPorProductoConConexion(con, ID_PRODUCTO_PRUEBA);
            assertNotNull(inv, "Debe retornar el inventario utilizando la conexión provista");

            // Probar actualización rápida de stock (actualizarStockRV)
            int stockPruebaRV = 40;
            inventarioDAO.actualizarStockRV(con, ID_PRODUCTO_PRUEBA, stockPruebaRV);
            con.commit();

            // Verificación
            assertEquals(stockPruebaRV, inventarioDAO.obtenerStockActual(ID_PRODUCTO_PRUEBA));

            // Limpieza: restablecemos el stock a 0 o dejamos el estado original
            con.setAutoCommit(false);
            inventarioDAO.actualizarStockRV(con, ID_PRODUCTO_PRUEBA, 0);
            con.commit();

        } catch (Exception e) {
            fail("Falló el test de métodos con conexión explícita: " + e.getMessage());
        }
    }
}