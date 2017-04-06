package org.projectdiaries.utility;

import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfGenerator {

	public String getPDfReport(ResultSet rs) throws SQLException {
		String fileName = UUID.randomUUID()+".pdf";
	Document document = new Document();
    try
    {
    	
    	
    	 PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(MyDbConnectionListener.PDF_PATH+fileName));
    	 document.open();
    	 PdfPTable table = new PdfPTable(2); // 3 columns.
         table.setWidthPercentage(100); //Width 100%
         table.setSpacingBefore(10f); //Space before table
         table.setSpacingAfter(10f); //Space after table
         float[] columnWidths = {1f, 1f};
         table.setWidths(columnWidths);
        
         PdfPCell cell1 = new PdfPCell(new Paragraph("Project Name"));
         cell1.setPaddingLeft(10);
         cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
  
         PdfPCell cell2 = new PdfPCell(new Paragraph("Bill ($)"));
         cell2.setPaddingLeft(10);
         cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
  
         table.addCell(cell1);
         table.addCell(cell2);
         
         while(rs.next()){
        	 
        	 try {
				PdfPCell cell11 = new PdfPCell(new Paragraph(rs.getString(2)));
			
				cell11.setPaddingLeft(10);
				cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
      
             PdfPCell cell22 = new PdfPCell(new Paragraph(rs.getString(7)));
             cell22.setPaddingLeft(10);
             cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
             cell22.setVerticalAlignment(Element.ALIGN_MIDDLE);
             
             table.addCell(cell11);
             table.addCell(cell22);
        	 
        	 } catch (SQLException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
         }
      
      
       document.add(table);
       document.close();
       writer.close();
    } catch (Exception e1)
    {
       e1.printStackTrace();
    }
    return fileName;
}
}
