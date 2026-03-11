package ext.sast.common.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 兼容 Apache Commons CSV 1.4，支持动态属性列的 CSV 读取工具。
 * 前三列固定为：对象类型、工序编号、工序名称，之后为动态属性列。
 */
public class CSVUtil {

	public static void main(String[] args) {
		try {
			List<Data> list = readCSV("data.csv");
			System.out.println("共读取 " + list.size() + " 条数据");
			for (Data d : list) {
				System.out.println(d);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class Data {
		private String dataType;
		private String number;
		private String name;
		private Map<String, String> attributes;

		public String getDataType() {
			return dataType;
		}

		public void setDataType(String dataType) {
			this.dataType = dataType;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Map<String, String> getAttributes() {
			return attributes;
		}

		public void setAttributes(Map<String, String> attributes) {
			this.attributes = attributes;
		}

		@Override
		public String toString() {
			return "Data [dataType=" + dataType + ", number=" + number + ", name=" + name + ", attributes=" + attributes + "]";
		}
	}

	public static List<Data> readCSV(String filePath) throws IOException {
		List<Data> resultList = new ArrayList<>();
		Reader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filePath)), StandardCharsets.UTF_8));
		CSVFormat format = CSVFormat.DEFAULT.withHeader(); // 第一行为表头
		CSVParser parser = new CSVParser(reader, format);

		// 取出所有表头（注意：顺序可能不稳定）
		Map<String, Integer> headerMap = parser.getHeaderMap();
		List<String> headers = new ArrayList<>(headerMap.keySet());

		for (CSVRecord record : parser) {
			if (record.size() < 1) continue; // 跳过不合法行

			Data data = new Data();
			data.setDataType(record.get(0));
			data.setNumber(record.get(1));
			data.setName(record.get(2));

			Map<String, String> attributes = new HashMap<>();
			for (int i = 3; i < headers.size(); i++) {
				String key = headers.get(i);
				String value = i < record.size() ? record.get(i) : "";
				attributes.put(key, value);
			}

			data.setAttributes(attributes);
			resultList.add(data);
		}

		parser.close();
		reader.close();

		return resultList;
	}

	// 示例主函数

}
