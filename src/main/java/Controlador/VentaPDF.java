package Controlador;


import Modelo.Cliente;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.image.ImageDataFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import Vista.Sistema;

/**
 *
 * @author edison
 */
public class VentaPDF {
    
    private Sistema vista;
    private String fechaActual = "";
    private String nombreArchivoPDFVenta = "";  

    public VentaPDF(Sistema vista) {
        this.vista = vista;
    }
    
    
    
    //metodo para generar la factura de venta
    public void generarFacturaPDF(Cliente cliente) {
        // 1. Preparar fecha y nombre de archivo
        fechaActual = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String fechaNueva = fechaActual.replace("/", "_"); // más simple
        nombreArchivoPDFVenta = "Venta_" + cliente.getNombres() + "_" + fechaNueva + ".pdf";
        File file = new File("src/pdf/" + nombreArchivoPDFVenta);
        file.getParentFile().mkdirs(); // asegurar que la carpeta exista

        // 2. Crear el documento con iText 7
        try (FileOutputStream fos = new FileOutputStream(file);
                PdfWriter writer = new PdfWriter(fos); 
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document doc = new Document(pdfDoc, PageSize.A4)) {

            // Fuente base (Times-Roman) y negrita
            PdfFont normalFont = PdfFontFactory.createFont("Times-Roman");
            PdfFont boldFont = PdfFontFactory.createFont("Times-Bold");

            // --- ENCABEZADO (tabla 4 columnas) ---
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{20, 30, 70, 40}));
            headerTable.setWidth(UnitValue.createPercentValue(100));

            // Imagen
            /*Image img = new Image(ImageDataFactory.create("src/img/logopdf.png"));
            img.setAutoScale(true);
            Cell imgCell = new Cell().add(img).setBorder(Border.NO_BORDER);
            headerTable.addCell(imgCell);*/

            // Celda vacía
            headerTable.addCell(new Cell().setBorder(Border.NO_BORDER));

            // Datos de la empresa
            String empresaInfo = "RUC: 20605513825\n"
                    + "NOMBRE: Davsol Eco Systems\n"
                    + "TELEFONO: 976 601 735\n"
                    + "DIRECCION: Jr. Ramon Castillo 1451 San Roman - San Roman - Puno\n"
                    + "RAZON SOCIAL: DAVSOL ECO SYSTEMS PERU SRL";
            Paragraph empresaPar = new Paragraph(empresaInfo).setFont(normalFont).setFontSize(10);
            Cell empresaCell = new Cell().add(empresaPar).setBorder(Border.NO_BORDER);
            headerTable.addCell(empresaCell);

            // Fecha y número de factura
            String facturaInfo = "Factura: 001\nFecha: " + fechaActual;
            Paragraph fechaPar = new Paragraph(facturaInfo).setFont(normalFont).setFontSize(10).setTextAlignment(TextAlignment.RIGHT);
            Cell fechaCell = new Cell().add(fechaPar).setBorder(Border.NO_BORDER);
            headerTable.addCell(fechaCell);

            doc.add(headerTable);

            // --- DATOS DEL CLIENTE ---
            doc.add(new Paragraph("Datos del cliente:").setFont(boldFont).setFontSize(12).setMarginTop(10));

            Table clientTable = new Table(UnitValue.createPercentArray(new float[]{25, 45, 30, 40}));
            clientTable.setWidth(UnitValue.createPercentValue(100));

            // Encabezados (en negrita) sin bordes
            String[] labels = {"Cedula/RUC:", "Nombre:", "Teléfono:", "Dirección:"};
            for (String label : labels) {
                Cell c = new Cell().add(new Paragraph(label).setFont(boldFont).setFontSize(10));
                c.setBorder(Border.NO_BORDER);
                clientTable.addCell(c);
            }
            // Valores
            String[] values = {cliente.getDniRUC(), cliente.getNombres(), cliente.getTelefono(), cliente.getCorreo()};
            for (String val : values) {
                Cell c = new Cell().add(new Paragraph(val).setFont(normalFont).setFontSize(10));
                c.setBorder(Border.NO_BORDER);
                clientTable.addCell(c);
            }
            doc.add(clientTable);

            // Espacio
            doc.add(new Paragraph("\n"));

            // --- TABLA DE PRODUCTOS ---
            Table productTable = new Table(UnitValue.createPercentArray(new float[]{15, 50, 15, 20}));
            productTable.setWidth(UnitValue.createPercentValue(100));

            // Encabezados con fondo gris
            String[] prodLabels = {"Cantidad", "Producto", "Precio Unit.", "Subtotal"};
            for (String label : prodLabels) {
                Cell c = new Cell().add(new Paragraph(label).setFont(boldFont).setFontSize(10));
                c.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                c.setBorder(Border.NO_BORDER);
                productTable.addCell(c);
            }

            // Datos de la tabla de la interfaz (JTable)
            
            for (int i = 0; i < vista.t_regVent.getRowCount(); i++) {
                // Se toman las columnas según el índice original: 1=producto, 2=cantidad, 3=precio, 7=total
                String producto = vista.t_regVent.getValueAt(i, 1).toString();
                String cantidad = vista.t_regVent.getValueAt(i, 0).toString();
                String precio = vista.t_regVent.getValueAt(i, 2).toString();
                String total = vista.t_regVent.getValueAt(i, 3).toString();

                productTable.addCell(new Cell().add(new Paragraph(cantidad).setFont(normalFont).setFontSize(10)).setBorder(Border.NO_BORDER));
                productTable.addCell(new Cell().add(new Paragraph(producto).setFont(normalFont).setFontSize(10)).setBorder(Border.NO_BORDER));
                productTable.addCell(new Cell().add(new Paragraph(precio).setFont(normalFont).setFontSize(10)).setBorder(Border.NO_BORDER));
                productTable.addCell(new Cell().add(new Paragraph(total).setFont(normalFont).setFontSize(10)).setBorder(Border.NO_BORDER));
            }
            doc.add(productTable);

            // --- TOTAL ---
            String totalPagar = vista.txtF_total.getText();
            Paragraph totalPar = new Paragraph("Total a pagar: " + totalPagar)
                    .setFont(boldFont).setFontSize(12)
                    .setTextAlignment(TextAlignment.RIGHT);
            doc.add(totalPar);

            // El documento se cierra automáticamente por el try-with-resources
        } catch (Exception e) {
            System.err.println("Error al generar el PDF: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. Abrir el PDF automáticamente
        try {
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            }
        } catch (IOException e) {
            System.err.println("No se pudo abrir el archivo: " + e.getMessage());
        }
    }

}
