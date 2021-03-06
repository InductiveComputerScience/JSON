package com.progsbase.libraries.JSON;

import JSON.StringElementMaps.StringElementMap;
import JSON.structures.Element;
import JSON.structures.ElementReference;
import references.references.StringArrayReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static JSON.StringElementMaps.StringElementMaps.GetStringElementMapNumberOfKeys;
import static JSON.parser.parser.ReadJSON;
import static references.references.references.CreateStringArrayReferenceLengthValue;

/*
 This class reads JSON into a Java Object with the following actual classes:
  - {} -> Map<String, Object>
  - [] -> List<Object>
  - number -> Double
  - "" -> String
  - null -> null
  - boolean -> Boolean
 */
public class JSONObjectReader {
    public static Object readJSON(String json){
        ElementReference elementReference;
        StringArrayReference errorMessages;
        Object object;

        object = null;
        elementReference = new ElementReference();
        errorMessages = CreateStringArrayReferenceLengthValue(0d, "".toCharArray());

        boolean success = ReadJSON(json.toCharArray(), elementReference, errorMessages);

        if(success){
            object = javaifyJSONValue(elementReference.element);
        }

        return object;
    }

    public static Object readJSONExceptionOnFailure(String json) throws JSONException {
        ElementReference elementReference;
        StringArrayReference errorMessages;
        Object object;

        elementReference = new ElementReference();
        errorMessages = CreateStringArrayReferenceLengthValue(0d, "".toCharArray());

        boolean success = ReadJSON(json.toCharArray(), elementReference, errorMessages);

        if(success){
            object = javaifyJSONValue(elementReference.element);
        }else{
            String errorMessage = joinErrorMessages(errorMessages);

            throw new JSONException(errorMessage);
        }

        return object;
    }

    public static String joinErrorMessages(StringArrayReference errorMessages) {
        StringBuilder errorMessage = new StringBuilder();

        for (int i = 0; i < errorMessages.stringArray.length; i++) {
            errorMessage.append(i + 1);
            errorMessage.append(". ");
            errorMessage.append(new String(errorMessages.stringArray[i].string));
            errorMessage.append(" ");
        }

        return errorMessage.toString();
    }

    public static JSONReturn readJSONWithCheck(String json) {
        ElementReference elementReference;
        StringArrayReference errorMessages;
        Object object;

        object = null;
        JSONReturn jsonReturn = new JSONReturn();
        elementReference = new ElementReference();
        errorMessages = CreateStringArrayReferenceLengthValue(0d, "".toCharArray());

        boolean success = ReadJSON(json.toCharArray(), elementReference, errorMessages);

        if(success){
            object = javaifyJSONValue(elementReference.element);
        }else{
            jsonReturn.errorMessage = joinErrorMessages(errorMessages);
        }

        jsonReturn.object = object;
        jsonReturn.success = success;

        return jsonReturn;
    }

    public static Object javaifyJSONValue(Element element) {
        Object o;

        o = null;

        String type = new String(element.type);

        if(type.equals("object")){
            o = javaifyJSONObject(element.object);
        }else if(type.equals("array")){
            o = javaifyJSONArray(element.array);
        }else if(type.equals("string")){
            o = new String(element.string);
        }else if(type.equals("number")){
            o = element.number;
        }else if(type.equals("boolean")){
            o = element.booleanValue;
        }else if(type.equals("null")){
            o = null;
        }

        return o;
    }

    public static Object javaifyJSONObject(StringElementMap object) {
        Map<String, Object> resultObject = new HashMap<>();

        for(int i = 0; i < GetStringElementMapNumberOfKeys(object); i++){
            resultObject.put(new String(object.stringListRef.stringArray[i].string), javaifyJSONValue(object.elementListRef.array[i]));
        }

        return resultObject;
    }

    public static Object javaifyJSONArray(Element[] array) {
        List<Object> resultArray = new ArrayList<>();

        for(int i = 0; i < array.length; i++){
            resultArray.add(javaifyJSONValue(array[i]));
        }

        return resultArray;
    }
}