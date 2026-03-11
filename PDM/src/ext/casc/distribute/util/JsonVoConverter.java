package ext.casc.distribute.util;

import org.json.JSONArray;
import org.json.JSONObject;
import ext.casc.distribute.vo.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonVoConverter {

    public static String serialize(Object object) {
        return serializeToJsonObject(object).toString(4);
    }

    private static JSONObject serializeToJsonObject(Object object) {
        JSONObject json = new JSONObject();
        if (object == null) return json;

        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = field.get(object);

                if (value == null) {
                    //json.put(fieldName, JSONObject.NULL); //不显示空值属性
                    continue;
                }

                if (value instanceof List) {
                    json.put(fieldName, serializeList((List<?>) value));
                } else if (isVoClass(value.getClass())) {
                    json.put(fieldName, serializeToJsonObject(value));
                } else {
                    json.put(fieldName, value);
                }
            } catch (Exception e) {
                throw new RuntimeException("序列化字段[" + field.getName() + "]失败", e);
            }
        }
        return json;
    }

    private static JSONArray serializeList(List<?> list) {
        JSONArray array = new JSONArray();
        for (Object item : list) {
            if (isVoClass(item.getClass())) {
                array.put(serializeToJsonObject(item));
            } else {
                array.put(item);
            }
        }
        return array;
    }

    public static <T> T deserialize(String jsonStr, Class<T> targetClass) {
        JSONObject json = new JSONObject(jsonStr);
        return deserializeJsonObject(json, targetClass);
    }

    private static <T> T deserializeJsonObject(JSONObject json, Class<T> targetClass) {
        try {
            T instance = targetClass.getDeclaredConstructor().newInstance();

            for (Field field : targetClass.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (!json.has(fieldName)) continue;

                Object jsonValue = json.get(fieldName);
                Object fieldValue = convertJsonValueToFieldValue(jsonValue, field);

                field.set(instance, fieldValue);
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("反序列化[" + targetClass.getName() + "]失败", e);
        }
    }

    /**
     * 核心转换：JSON值 → 字段类型值（修复Java8兼容问题）
     */
    private static Object convertJsonValueToFieldValue(Object jsonValue, Field field) throws Exception {
        Class<?> fieldType = field.getType();

        if (jsonValue == JSONObject.NULL) return null;

        // 处理列表类型（关键修复：使用ParameterizedType获取泛型）
        if (List.class.isAssignableFrom(fieldType)) {
            return deserializeList((JSONArray) jsonValue, field);
        }

        // 处理嵌套VO类型
        if (isVoClass(fieldType)) {
            return deserializeJsonObject((JSONObject) jsonValue, fieldType);
        }

        // 处理基本类型
        return convertBasicType(jsonValue, fieldType);
    }

    private static List<?> deserializeList(JSONArray jsonArray, Field field) throws Exception {
        // 获取字段的泛型类型（如List<DWNodeVo>的泛型参数）
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            return new ArrayList<>(); // 非参数化类型（如List<Object>）返回空列表
        }

        // 获取泛型参数的实际类型（第一个参数）
        Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
        Class<?> elementType = (Class<?>) typeArguments[0];

        List<Object> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object itemJson = jsonArray.get(i);
            if (isVoClass(elementType)) {
                list.add(deserializeJsonObject((JSONObject) itemJson, elementType));
            } else {
                list.add(convertBasicType(itemJson, elementType));
            }
        }
        return list;
    }

    /**
     * 基本类型转换（增强类型覆盖）
     */
    private static Object convertBasicType(Object jsonValue, Class<?> targetType) {
        if (targetType == String.class) {
            return jsonValue.toString();
        } else if (targetType == int.class || targetType == Integer.class) {
            return jsonValue instanceof Number ? ((Number) jsonValue).intValue() : Integer.parseInt(jsonValue.toString());
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return jsonValue instanceof Boolean ? jsonValue : Boolean.parseBoolean(jsonValue.toString());
        } else if (targetType == double.class || targetType == Double.class) {
            return jsonValue instanceof Number ? ((Number) jsonValue).doubleValue() : Double.parseDouble(jsonValue.toString());
        } else if (targetType == long.class || targetType == Long.class) {
            return jsonValue instanceof Number ? ((Number) jsonValue).longValue() : Long.parseLong(jsonValue.toString());
        } else if (targetType == float.class || targetType == Float.class) {
            return jsonValue instanceof Number ? ((Number) jsonValue).floatValue() : Float.parseFloat(jsonValue.toString());
        }
        return jsonValue; // 未知类型直接返回原始值
    }

    private static boolean isVoClass(Class<?> clazz) {
        return clazz.getName().startsWith("ext.casc.distribute.vo");
    }

    public static void main(String[] args) {
        // 1. 构建测试对象
        DWGraphVo originalVo = new DWGraphVo();
        // ... 填充nodes和edges数据 ...

        // 2. 序列化为JSON
        String jsonStr = JsonVoConverter.serialize(originalVo);
        System.out.println("序列化结果：\n" + jsonStr);

        // 3. 反序列化为对象
        DWGraphVo restoredVo = JsonVoConverter.deserialize(jsonStr, DWGraphVo.class);
        System.out.println("反序列化完成，类型：" + restoredVo.getClass().getName());
    }
}
