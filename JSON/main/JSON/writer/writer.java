package JSON.writer;

import JSON.structures.*;
import references.references.*;

import static JSON.StringElementMaps.StringElementMaps.*;
import static JSON.elementTypeEnum.elementTypeEnum.*;
import static JSON.lengthComputer.lengthComputer.*;
import static java.lang.Math.*;
import static nnumbers.NumberToString.NumberToString.*;
import static references.references.references.*;

public class writer {
    public static char[] WriteJSON(Element element) {
        char[] result;
        double length;
        NumberReference index;

        length = ComputeJSONStringLength(element);
        result = new char[(int)length];
        index = CreateNumberReference(0d);

        if(ElementTypeEnumEquals(element.type.name, "object".toCharArray())){
            WriteObject(element, result, index);
        }else if(ElementTypeEnumEquals(element.type.name, "string".toCharArray())){
            WriteString(element, result, index);
        }else if(ElementTypeEnumEquals(element.type.name, "array".toCharArray())){
            WriteArray(element, result, index);
        }else if(ElementTypeEnumEquals(element.type.name, "number".toCharArray())){
            WriteNumber(element, result, index);
        }else if(ElementTypeEnumEquals(element.type.name, "nullValue".toCharArray())){
            WriteStringToStingStream(result, index, "null".toCharArray());
        }else if(ElementTypeEnumEquals(element.type.name, "booleanValue".toCharArray())){
            WriteBooleanValue(element, result, index);
        }

        return result;
    }

    public static void WriteBooleanValue(Element element, char[] result, NumberReference index) {

        if(element.booleanValue){
            WriteStringToStingStream(result, index, "true".toCharArray());
        }else{
            WriteStringToStingStream(result, index, "false".toCharArray());
        }
    }

    public static void WriteNumber(Element element, char[] result, NumberReference index) {
        char[] numberString;

        if(abs(element.number) >= pow(10d, 15d) || abs(element.number) <= pow(10d, -15d)){
            numberString = nCreateStringScientificNotationDecimalFromNumber(element.number);
        }else{
            numberString = nCreateStringDecimalFromNumber(element.number);
        }

        WriteStringToStingStream(result, index, numberString);

        // delete(numberString); // TODO: Start using = null instead,
    }

    public static void WriteArray(Element element, char[] result, NumberReference index) {
        char [] s;
        Element arrayElement;
        double i;

        WriteStringToStingStream(result, index, "[".toCharArray());

        for(i = 0; i < element.array.length; i = i + 1d){
            arrayElement = element.array[(int)i];

            s = WriteJSON(arrayElement);
            WriteStringToStingStream(result, index, s);

            if(i + 1d != element.array.length){
                WriteStringToStingStream(result, index, ",".toCharArray());
            }
        }

        WriteStringToStingStream(result, index, "]".toCharArray());
    }

    public static void WriteString(Element element, char[] result, NumberReference index) {
        WriteStringToStingStream(result, index, "\"".toCharArray());
        element.string = JSONEscapeString(element.string);
        WriteStringToStingStream(result, index, element.string);
        WriteStringToStingStream(result, index, "\"".toCharArray());
    }

    public static char[] JSONEscapeString(char[] string) {
        double i, length;
        NumberReference index, lettersReference;
        char ns [], escaped [];

        length = JSONEscapedStringLength(string);

        ns = new char [(int)length];
        index = CreateNumberReference(0d);
        lettersReference = CreateNumberReference( 0d);

        for(i = 0d; i < string.length; i = i + 1d){
            if(JSONMustBeEscaped(string[(int)i], lettersReference)){
                escaped = JSONEscapeCharacter(string[(int)i]);
                WriteStringToStingStream(ns, index, escaped);
            }else{
                WriteCharacterToStingStream(ns, index, string[(int)i]);
            }
        }

        return ns;
    }

    public static double JSONEscapedStringLength(char[] string) {
        NumberReference lettersReference;
        double i, length;

        lettersReference = CreateNumberReference(0d);
        length = 0d;

        for(i = 0d; i < string.length; i = i + 1d){
            if(JSONMustBeEscaped(string[(int)i], lettersReference)){
                length = length + lettersReference.numberValue;
            }else{
                length = length + 1d;
            }
        }
        return length;
    }

    public static char[] JSONEscapeCharacter(char c) {
        double code;
        char escaped [];
        StringReference hexNumber;
        double q, rs, s, b, f, n, r, t;

        code = c;

        q = 34d;
        rs = 92d;
        s = 47d;
        b = 8d;
        f = 12d;
        n = 10d;
        r = 13d;
        t = 9d;

        hexNumber = new StringReference();

        if(code == q){
            escaped = new char [2];
            escaped[0] = '\\';
            escaped[1] = '\"';
        }else if(code == rs){
            escaped = new char [2];
            escaped[0] = '\\';
            escaped[1] = '\\';
        }else if(code == s){
            escaped = new char [2];
            escaped[0] = '\\';
            escaped[1] = '/';
        }else if(code == b){
            escaped = new char [2];
            escaped[0] = '\\';
            escaped[1] = 'b';
        }else if(code == f){
            escaped = new char [2];
            escaped[0] = '\\';
            escaped[1] = 'f';
        }else if(code == n){
            escaped = new char [2];
            escaped[0] = '\\';
            escaped[1] = 'n';
        }else if(code == r){
            escaped = new char [2];
            escaped[0] = '\\';
            escaped[1] = 'r';
        }else if(code == t){
            escaped = new char [2];
            escaped[0] = '\\';
            escaped[1] = 't';
        }else if(code >= 0d && code <= 31d){
            escaped = new char [6];
            escaped[0] = '\\';
            escaped[1] = 'u';
            escaped[2] = '0';
            escaped[3] = '0';

            nCreateStringFromNumberWithCheck(code, 16d, hexNumber);

            if(hexNumber.string.length == 1d){
                escaped[4] = '0';
                escaped[5] = hexNumber.string[0];
            }else if(hexNumber.string.length == 2d){
                escaped[4] = hexNumber.string[0];
                escaped[5] = hexNumber.string[1];
            }
        }else{
            escaped = new char [1];
            escaped[0] = c;
        }

        return escaped;
    }

    public static boolean JSONMustBeEscaped(char c, NumberReference letters) {
        double code;
        boolean mustBeEscaped;
        double q, rs, s, b, f, n, r, t;

        code = c;
        mustBeEscaped = false;

        q = 34d;
        rs = 92d;
        s = 47d;
        b = 8d;
        f = 12d;
        n = 10d;
        r = 13d;
        t = 9d;

        if(code == q || code == rs || code == s || code == b || code == f || code == n || code == r || code == t){
            mustBeEscaped = true;
            letters.numberValue = 2d;
        }else if(code >= 0d && code <= 31d){
            mustBeEscaped = true;
            letters.numberValue = 6d;
        }else if(code >= pow(2d, 16d)){
            mustBeEscaped = true;
            letters.numberValue = 6d;
        }

        return mustBeEscaped;
    }

    public static void WriteObject(Element element, char[] result, NumberReference index) {
        char [] s, key;
        double i;
        StringArrayReference keys;
        Element objectElement;

        WriteStringToStingStream(result, index, "{".toCharArray());

        keys = GetStringElementMapKeySet(element.object);
        for(i = 0; i < keys.stringArray.length; i = i + 1d){
            key = keys.stringArray[(int)i].string;
            key = JSONEscapeString(key);
            objectElement = GetObjectValue(element.object, key);

            WriteStringToStingStream(result, index, "\"".toCharArray());
            WriteStringToStingStream(result, index, key);
            WriteStringToStingStream(result, index, "\"".toCharArray());
            WriteStringToStingStream(result, index, ":".toCharArray());

            s = WriteJSON(objectElement);
            WriteStringToStingStream(result, index, s);

            if(i + 1d != keys.stringArray.length){
                WriteStringToStingStream(result, index, ",".toCharArray());
            }
        }

        WriteStringToStingStream(result, index, "}".toCharArray());
    }

    public static void WriteStringToStingStream(char [] stream, NumberReference index, char [] src) {
        double i;
        for(i = 0; i < src.length; i = i + 1d){
            stream[(int)(index.numberValue + i)] = src[(int)i];
        }
        index.numberValue = index.numberValue + src.length;
    }

    public static void WriteCharacterToStingStream(char [] stream, NumberReference index, char src) {
        stream[(int)(index.numberValue)] = src;
        index.numberValue = index.numberValue + 1d;
    }
}
