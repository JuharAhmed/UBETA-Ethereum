import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;

public class FilterBidsAndOffers {
    static ArrayList<String []> bidData= new ArrayList<>();
    static ArrayList<String []> offerData= new ArrayList<>();

   public static void main(String[] args) {

       try {
           readData();
       } catch (IOException e) {
           e.printStackTrace();
       }
       writeToFile();

    }

    private static void readData() throws IOException {
        String powerDataFile = "C:/Users/s3753266/RMIT-C/web3j/PowerData/Western Australia/Merged2.csv"; // This is only for PM_Bidding
         String line=null;
        BufferedReader csvReader = null;
        try {
            csvReader = new BufferedReader(new FileReader(powerDataFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

            while ((line = csvReader.readLine()) != null) {
            String[] data = line.split(",");
            String[] rowData = new String[7];
            rowData[0] = data[0];
            rowData[1] = data[1];
            rowData[2] = data[2];
            rowData[3] = data[3];
            rowData[4] = data[4];
            rowData[5] = data[6];
            rowData[6] = data[5];
            if(rowData[4].equals("Bid")){
            bidData.add(rowData);
            }
            else {
                offerData.add(rowData);
            }
        }

        csvReader.close();

    }

    public static void writeToFile(){
        String[] columns = {"Trading Date", "Trading Interval", "Trading Interval", "Participant Code", "Quantity", "Price"};
        Workbook bidDataWorkBook = new XSSFWorkbook();
        Sheet bidDataSheet = bidDataWorkBook.createSheet();
        Row bidDataHeaderRow = bidDataSheet.createRow(0);
        Font bidDataHeaderFont = bidDataWorkBook.createFont();
        bidDataHeaderFont.setBold(true);
        bidDataHeaderFont.setFontHeightInPoints((short) 14);
        bidDataHeaderFont.setColor(IndexedColors.BLACK.getIndex());
        CellStyle headerCellStyle = bidDataWorkBook.createCellStyle();
        headerCellStyle.setFont(bidDataHeaderFont);
        for(int i = 0; i < columns.length; i++) {
            Cell cell = bidDataHeaderRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        Workbook offerDataWorkBook = new XSSFWorkbook();
        Sheet offerDataSheet = offerDataWorkBook.createSheet();
        Row offerDataHeaderRow = offerDataSheet.createRow(0);
        Font offerDataheaderFont = offerDataWorkBook.createFont();
        offerDataheaderFont.setBold(true);
        offerDataheaderFont.setFontHeightInPoints((short) 14);
        offerDataheaderFont.setColor(IndexedColors.BLACK.getIndex());
        CellStyle headerCellStyle2 = offerDataWorkBook.createCellStyle();
        headerCellStyle2.setFont(offerDataheaderFont);
        for(int i = 0; i < columns.length; i++) {
            Cell cell = offerDataHeaderRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNumber=0;
        for(String[] rowData: bidData){
            rowNumber++;
            Row row= bidDataSheet.createRow(rowNumber);
            for(int col=0;col<columns.length;col++){
                Cell cell = row.createCell(col);
                switch(col) {
                    case 0: cell.setCellValue(rowData[0]);
                        break;
                    case 1: cell.setCellValue(rowData[1]);
                        break;
                    case 2:  cell.setCellValue(rowData[2]);
                        break;
                    case 3:  cell.setCellValue(rowData[3]);
                        break;
                    case 4:  cell.setCellValue(rowData[4]);
                        break;
                    case 5:  cell.setCellValue(rowData[6]);
                        break;
                    case 6:  cell.setCellValue(rowData[7]);
                        break;
                    default: cell.setCellValue("-");
                        break;
                }
            }
        }

        rowNumber=0;
        for(String[] rowData: offerData){
            rowNumber++;
            Row row= offerDataSheet.createRow(rowNumber);
            for(int col=0;col<columns.length;col++){
                Cell cell = row.createCell(col);
                switch(col) {
                    case 0: cell.setCellValue(rowData[0]);
                        break;
                    case 1: cell.setCellValue(rowData[1]);
                        break;
                    case 2:  cell.setCellValue(rowData[2]);
                        break;
                    case 3:  cell.setCellValue(rowData[3]);
                        break;
                    case 4:  cell.setCellValue(rowData[4]);
                        break;
                    case 5:  cell.setCellValue(rowData[6]);
                        break;
                    case 6:  cell.setCellValue(rowData[7]);
                        break;
                    default: cell.setCellValue("-");
                        break;
                }
            }
        }

        // Write the output to a file
        FileOutputStream bidDataOutputStream = null;
        try {
            bidDataOutputStream = new FileOutputStream("C:/Users/s3753266/RMIT-C/web3j/PowerData/Western Australia/WesAus_PoolMarketBidData.xlsx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            bidDataWorkBook.write(bidDataOutputStream);
            bidDataOutputStream.close();
            bidDataWorkBook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write the output to a file
        FileOutputStream offerDataStream = null;
        try {
            offerDataStream = new FileOutputStream("C:/Users/s3753266/RMIT-C/web3j/PowerData/Western Australia/WesAus_PoolMarketOfferData.xlsx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            bidDataWorkBook.write(offerDataStream);
            offerDataStream.close();
            bidDataWorkBook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
