package com.ssmm.util;
	import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import com.ssmm.model.lxcUser;

	public class MutiStyleExcelWrite {
	    public void createExcel(OutputStream os,List<lxcUser> list) throws WriteException,IOException {
	        //����������
	        WritableWorkbook workbook = Workbook.createWorkbook(os);
	        //�����µ�һҳ
	        WritableSheet sheet = workbook.createSheet("First Sheet", 0);
	        //�����ͷ
	        sheet.mergeCells(0, 0, 4, 0);//��Ӻϲ���Ԫ�񣬵�һ����������ʼ�У��ڶ�����������ʼ�У���������������ֹ�У����ĸ���������ֹ��
	        WritableFont bold = new WritableFont(WritableFont.ARIAL,10,WritableFont.BOLD);//������������ͺ�����ʾ,����ΪArial,�ֺŴ�СΪ10,���ú�����ʾ
	        WritableCellFormat titleFormate = new WritableCellFormat(bold);//����һ����Ԫ����ʽ���ƶ���
	        titleFormate.setAlignment(jxl.format.Alignment.CENTRE);//��Ԫ���е�����ˮƽ�������
	        titleFormate.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//��Ԫ������ݴ�ֱ�������
	        Label title = new Label(0,0,"�ҳ�ע���û���ϸ",titleFormate);
	        sheet.setRowView(0, 600, false);//���õ�һ�еĸ߶�
	        sheet.addCell(title);
	        
	        //����Ҫ��ʾ�ľ�������
	        WritableFont color = new WritableFont(WritableFont.ARIAL);//ѡ������
	        color.setColour(Colour.GOLD);//����������ɫΪ���ɫ
	        WritableCellFormat colorFormat = new WritableCellFormat(color);

	       //���ø�������������д�˸�
	        String[] negativeTitle = { "ID", "role", "phonenumber", "activetime"};
	        for (int i = 0; i < negativeTitle.length; i++) {  
	            Label lable = new Label(i, 1, negativeTitle[i],colorFormat);  
	            sheet.addCell(lable);  
	        }  
	        //������д��
	        for (int i = 0; i < list.size(); i++) {  

	        	Number id = new Number(0,i+2,list.get(i).getId());
	        	sheet.addCell(id); 
	        	
	        	Number role = new Number(1,i+2,list.get(i).getRole());
	        	sheet.addCell(role);
	        	
	        	Label getPhonenumber = new Label(2, i+2, list.get(i).getPhonenumber());  
	        		sheet.addCell(getPhonenumber);
	        		
	        	 Label Activetime = new Label(3, i+2, list.get(i).getActivetime());	
	        	 sheet.addCell(Activetime);
	        }  

	        //�Ѵ���������д�뵽������У����ر������
	        workbook.write();
	        workbook.close();
	        os.close();
	        
	    }

		
	
}
