import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class Excel {

    public void createExcelFile(HashMap<String, Integer> data, String metricType, String projectName) {
        String outputFileName = "results/results-" + metricType + "-" + projectName + ".xls";
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Results - " + metricType + " - " + projectName);

        HSSFRow rowhead = sheet.createRow((short)0);
        rowhead.createCell(0).setCellValue("File Name");
        rowhead.createCell(1).setCellValue(metricType);

        int rowCount = 0;
        for (String fileName : data.keySet()) {
            HSSFRow row = sheet.createRow((short)rowCount);
            rowCount++;
            row.createCell(0).setCellValue(fileName);
            row.createCell(1).setCellValue(data.get(fileName));
        }

        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(outputFileName);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
