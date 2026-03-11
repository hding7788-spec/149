package com.glaway.mpm.mpmresource.gznumber.number;

import com.glaway.mpm.mpmresource.gznumber.bean.PropertiesBean;


public class Sequence {
	private String value;
	private int iValue;
	
	//若申请一个编号的，优先选用作废的编号；若申请一组编号，从最大的编号开始，累进。
	public Sequence(PropertiesBean pb, FormatedNumber number, int volumn){
		String seqFormat = pb.getSequenceFormat();
		int seqBeginAt = pb.getSequenceBeginAt();

		String seqClassPath = number.getClassification().getObjectclasspath();
		
		int iValue = -1;
		
		//if just apply for one number,it will prefer getting number from cacelled number pool;otherwise, create new number
		if(volumn == 1){
			iValue = getCancelledSequence(seqClassPath);
		}
		//if have no canceled free number or apply for more than one number
		if(iValue == -1){
			//get the max number with the given classpath
			iValue= getGeneratedSequence(seqClassPath);
			//if have no number with the given classpath
			if(iValue == -1||iValue<seqBeginAt){
				iValue = seqBeginAt;
			}else{
				iValue++;
			}
		}
		//If the value is not bigger than beginAt, set it to beginAt value
//		if(iValue < seqBeginAt) iValue = seqBeginAt;
		this.iValue = iValue;
	//	this.value = formatSequence(iValue,seqFormat);
		this.value = iValue+"";

	}
	
	/**
	 * Get the Min Sequence Number of the Cancelled Numbers
	 * @param classPath
	 * @return
	 */
	private int getCancelledSequence(String classPath){
		return GZNumberManager.getMaxSequence(classPath, GZNumberManager.CANCELLED_FLAG);
	}
/*	private int getCancelledSequence(String classPath){
		int result = 0;
		ArrayList aNumbers = NumberManager.getNumbersByClassPath(classPath, NumberManager.CANCELLED_FLAG, NumberManager.ASC);
		if(aNumbers.size() > 0){
			GeneratedNumber gNumber = (GeneratedNumber)aNumbers.get(0);
			result = gNumber.getSeq();
		}else{
			result = -1;
		}
		return result;
	}*/
	
	/**
	 * Get the Max Sequence Number of All Generated Numbers
	 * @param classPath
	 * @return
	 */
	
	private int getGeneratedSequence(String classPath){
		return GZNumberManager.getMaxSequence(classPath, GZNumberManager.ALL_FLAG);
	}
/*	private int getGeneratedSequence(String classPath){
		int result = 0;
		ArrayList aNumbers = NumberManager.getNumbersByClassPath(classPath, NumberManager.ALL_FLAG, NumberManager.DESC);
		if(aNumbers.size() > 0){
			GeneratedNumber gNumber = (GeneratedNumber)aNumbers.get(0);
			result = gNumber.getSeq();
		}else{
			result = -1;
		}
		return result;
	}*/
	
	/**
	 * Format the int value of sequence to specified '###' which is defined on properties file
	 * @param value
	 * @param seqFormat
	 * @return
	 */
	private String formatSequence(int value, String seqFormat){
		String result;
		
		int seqFormatLength = seqFormat.length();
		String seqlValue = String.valueOf(value);
		
		seqFormat = seqFormat.replace('#', '0');
		
		result = right((seqFormat + seqlValue),seqFormatLength);
		
		return result;
	}
	
	  private static String right(String s,int pos){   
          String dido = new StringBuffer(s).reverse().substring(0,pos);   
          StringBuffer sb = new StringBuffer(dido).reverse();   
          String didoleo = new String(sb);   
          return didoleo;   
	  }

	public int getIValue() {
		return iValue;
	}

	public String getValue() throws Exception{
		//得到序列号，不足四位补足四位
		String strSeq = "";
		int iseq = Integer.parseInt(value);	
		if(iseq <0){
			System.out.println("~~~编号序列号为负");
			strSeq = "1";
		}else if(iseq <10){
			strSeq += "000"+ iseq;
		}else if(iseq <100){
			strSeq += "00"+ iseq;
		}else if(iseq <1000){
			strSeq = "0" + iseq;
		}else if(iseq <10000){
			strSeq = "" + iseq;
		}else{
			throw new Exception("该分类号已满，请与标准化管理员联系！");
		}
		return strSeq;
	}   

}
