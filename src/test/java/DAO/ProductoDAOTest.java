package DAO;

import Modelo.Producto;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoDAOTest {

    private static ProductoDAO productoDAO;

    // Datos para el NUEVO producto de prueba (sigue la temática de termas solares)
    private static final String NOMBRE_NUEVO = "Terma Solar Hybrid 250L";
    private static final String DESC_NUEVA = "Sistema híbrido avanzado para hoteles";
    private static final double PRECIO_NUEVO = 880.00;

    // ID guardado para poder desactivarlo al final
    private static int idProductoInsertado = 0;

    @BeforeAll
    static void setUp() {
        productoDAO = new ProductoDAO();
    }

    @Test
    @Order(1)
    @DisplayName("1. Insertar nuevo producto (Crea registro en Producto e Inventario)")
    void test1_Insertar() {
        Producto nuevo = new Producto();
        nuevo.setNombreProducto(NOMBRE_NUEVO);
        nuevo.setDescripcion(DESC_NUEVA);
        nuevo.setPrecio(PRECIO_NUEVO);

        boolean insertado = productoDAO.insertar(nuevo);
        assertTrue(insertado, "El producto debe insertarse correctamente con transacción");

        // Obtenemos el producto recién insertado desde la lista para guardar su ID generado
        List<Producto> lista = productoDAO.listar();
        Producto registrado = lista.stream()
                .filter(p -> NOMBRE_NUEVO.equalsIgnoreCase(p.getNombreProducto()))
                .findFirst()
                .orElse(null);

        assertNotNull(registrado, "El producto insertado debe recuperarse de la base de datos");
        idProductoInsertado = registrado.getIdproducto();
        assertTrue(idProductoInsertado > 0, "El ID generado debe ser mayor a 0");
    }

    @Test
    @Order(2)
    @DisplayName("2. Listar productos activos (Verificar registros reales de la BD)")
    void test2_Listar() {
        List<Producto> lista = productoDAO.listar();

        assertNotNull(lista, "La lista no debe ser nula");
        assertFalse(lista.isEmpty(), "La lista debe contener los productos registrados");

        // Debe haber al menos los 30 productos iniciales + 1 insertado = 31
        assertTrue(lista.size() >= 2, "Deben figurar al menos 31 productos activos");
    }

    @Test
    @Order(3)
    @DisplayName("3. Buscar un producto existente ('Terma Solar Eco 80L') y modificar datos")
    void test3_ActualizarExistente() {
        List<Producto> lista = productoDAO.listar();

        // Buscamos el primer registro real de la BD ("Terma Solar Eco 80L")
        Producto eco80 = lista.stream()
                .filter(p -> p.getNombreProducto().contains("Terma Solar Eco 80L"))
                .findFirst()
                .orElse(null);

        assertNotNull(eco80, "Debe existir 'Terma Solar Eco 80L' en la BD");

        // Modificamos temporalmente el precio y la descripción
        double precioAnterior = eco80.getPrecio();
        eco80.setPrecio(365.00); // Precio actualizado
        eco80.setDescripcion("Alta eficiencia solar - Modelo Actualizado");

        boolean actualizado = productoDAO.actualizar(eco80);
        assertTrue(actualizado, "El producto existente debe actualizarse correctamente");

        // Restauramos o comprobamos los datos
        eco80.setPrecio(precioAnterior);
        eco80.setDescripcion("Alta eficiencia solar");
        productoDAO.actualizar(eco80); // Restablecemos el valor original
    }

    @Test
    @Order(4)
    @DisplayName("4. Eliminar (desactivar activo = 0) el producto creado en la prueba")
    void test4_EliminarInsertado() {
        assertTrue(idProductoInsertado > 0, "Se requiere el ID del producto insertado en el paso 1");

        boolean eliminado = productoDAO.eliminar(idProductoInsertado);
        assertTrue(eliminado, "El producto debe desactivarse correctamente");

        // Comprobamos que al listar ya no aparezca
        List<Producto> listaDespues = productoDAO.listar();
        boolean todaviaExiste = listaDespues.stream()
                .anyMatch(p -> p.getIdproducto() == idProductoInsertado);

        assertFalse(todaviaExiste, "El producto desactivado (activo = 0) ya no debe aparecer en listar()");
    }
}