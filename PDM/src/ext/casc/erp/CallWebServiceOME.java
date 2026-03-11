package ext.casc.erp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.activation.DataHandler;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

import ext.casc.erp.md5.Md5;

public class CallWebServiceOME {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		callWebServiceUploadFile("");

	}

	public static void callWebServiceUploadFile(String filePath) {
		try {
			String webServiceUrl = "http://10.125.192.4/ArchivesFile/Services/DataInterfaceService.asmx?wsdl";
			String namespaceName = "http://www.shsasc.com/Archives";
			String methodName = "UploadDIMonitorFile";
			File file = new File(filePath);
			String md5 = Md5.fileMD5(filePath);
			long fileSize = 0;
			long blockOffset = 0;
			long blockSize = 65536; // 64*1024

			fileSize = file.length();

			InputStream inputStream  = new FileInputStream(file);
			inputStream.skip(blockOffset);
			while (blockOffset != fileSize) {
				// 计算字节块大小
				if (blockOffset + blockSize > fileSize) {
					blockSize = fileSize - blockOffset;
				}

				// 构造字节数组
				byte[] fileBlock = new byte[(int) blockSize];
				inputStream.read(fileBlock, 0, fileBlock.length);

				// 是否是最后一个文件块
				boolean lastBlock = ((blockOffset + blockSize) == fileSize) ? true
						: false;

				OMFactory factory = OMAbstractFactory.getOMFactory();
			    OMNamespace namespace = factory.createOMNamespace(namespaceName, "");
			    OMElement method = factory.createOMElement(methodName, namespace);

			    OMElement fileName = factory.createOMElement("fileName", namespace);
			    fileName.setText(file.getName());
	            method.addChild(fileName);

			    OMElement originalFileDir = factory.createOMElement("originalFileDir", namespace);
			    originalFileDir.setText(String.valueOf(""));
	            method.addChild(originalFileDir);

	            ByteArrayDataSource dataSource= new ByteArrayDataSource(fileBlock);
	            DataHandler fileHandler= new DataHandler(dataSource);
	            OMText textData = factory.createOMText(fileHandler, true);

	            OMElement buffer = factory.createOMElement("buffer", namespace);
	            buffer.addChild(textData);
	            method.addChild(buffer);

	            OMElement offset = factory.createOMElement("offset", namespace);
	            offset.setText(blockOffset+"");
	            method.addChild(offset);

	            OMElement isLastBlock = factory.createOMElement("isLastBlock", namespace);
	            isLastBlock.setText(lastBlock+"");
	            method.addChild(isLastBlock);



	            OMElement fileMd5 = factory.createOMElement("fileMd5", namespace);
	            fileMd5.setText(String.valueOf(md5));
	            method.addChild(fileMd5);


	            //Build Options
	            EndpointReference endpointRef = new EndpointReference(webServiceUrl);
	            Options options = new Options();
				options.setTo(endpointRef);
				options.setSoapVersionURI(org.apache.axiom.soap.SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
				options.setAction(namespaceName + "/" + methodName);

				ServiceClient sender = new ServiceClient();
			    sender.setOptions(options);

			    OMElement result = sender.sendReceive(method);
			    System.out.println(result);
				blockOffset = blockOffset + blockSize;
			}
			inputStream.close();

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
