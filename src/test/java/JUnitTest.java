/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Администратор
 */

@RunWith(Parameterized.class)
public class JUnitTest {

    private final TestParameters parameters;
    public JUnitTest(TestParameters parameters) {
        this.parameters = parameters;
    }    
    
    // Возможные типы операций из условия
    private enum Operation {
        SUM,
        SUB,
        MULT,
        DIV,
        INVALID,
    };    
    
    // Класс хранящий массив строк параметров (распарсиная по символу ";" строка
    // входного файла) и предоставляющий доступ к параметрам (операндам, операции
    // и результату) в соответствии с указанным в условии типом.
    public static class TestParameters {
        private final String[] parametres;
        public TestParameters(String line) {
            parametres = line.split(";");
        }
        public Integer GetOperand1() {
            try {            
                return Integer.valueOf(parametres[0]);                 
            } catch (NumberFormatException e) {
                return null;
            }
        }
        public Integer GetOperand2() {
            try {            
                return Integer.valueOf(parametres[1]);                 
            } catch (NumberFormatException e) {
                return null;
            }
        }
        public Operation GetOperation() {
            switch (parametres[2]) {
                case "+": return Operation.SUM;
                case "-": return Operation.SUB;
                case "*": return Operation.MULT;
                case "/": return Operation.DIV;                    
                default:  return Operation.INVALID;
            }
        }
        public Float GetResult() {
            try {            
                return Float.valueOf(parametres[3]);                 
            } catch (NumberFormatException e) {
                return null;
            }            
        }
    }

    // Считываем из входного файла строки и создаем для каждой строки 
    // контейнер с параметрами вычислений    
    @Parameters
    public static Iterable<Object[]> generateParameters() throws IOException {
        List<Object[]> parametersProvided = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(JUnitTest.class.getClass().getResourceAsStream("/data.csv")))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                TestParameters testContainer = new TestParameters(line);
                parametersProvided.add(new Object[] { testContainer });
            }
	}        
        return parametersProvided;
    }
    
    // Собственно, сам тест для каждой строки...
    @Test
    public void Test() {
        // Разбираем параметры по переменным
        Integer operand1    = parameters.GetOperand1();
        Integer operand2    = parameters.GetOperand2();        
        Operation operation = parameters.GetOperation();
        Float result        = parameters.GetResult();        
        // Проверяем параметры на валидность поскольку условием задачи
        // определено что во входном файле могут быть "любые значения полей"
        // в том числе и не подходящие под условия
        assertNotNull("Неверно задан первый операнд", operand1);
        assertNotNull("Неверно задан второй операнд", operand2);
        assertNotNull("Неверно задан результат вычислений", result);
        assertTrue("Неверно задана операция", operation != Operation.INVALID);        
        // В зависимости от оператора производим вычисления и сверяем результат
        // с результатом входного файла
        switch (operation) {
            case SUM: {
                // Проверяем что result у нас целое число, даже если оно float
                // иначе при преобразовании сторки в целое число получиться,
                // например 10.2 = 10 и 5 + 5 = 10 окажеться верным, а сумма
                // целых чисел (из условия) не может быть дробным числом
                // и сравниваем с результатом из входного файла
                assertTrue("Сумма операндов не соответствует резульату", ((result - result.intValue()) == 0) && (operand1 + operand2) == result.intValue());
                // Сравниваем...                
                break;
            }
            case SUB: {
                // Так же как и написано выше, только для вычитания...
                assertTrue("Разность операндов не соответсвует результату", ((result - result.intValue()) == 0) && (operand1 - operand2) == result.intValue());
                break;
            }
            case MULT: {
                // Так же как и написано выше, только для умножения...
                assertTrue("Произведение операндов не соответсвует результату", ((result - result.intValue()) == 0) && (operand1 * operand2) == result.intValue());
                break;
            }
            case DIV: {
                // Проверяем что второй операнд не 0, на ноль делить очень не хорошо!
                assertTrue("Попытка разделить на второй операнд равный нулю", operand2 != 0);
                // Считаем реакльный результат вычислений
                float calcResult = (float)operand1 / (float)operand2;
                // Считаем длинну цифр после запятой в конечном результате,
                // так как, к примеру, 4 разделить на 3 = 1.333333333... а в
                // файле может быть записано 1.33 что, в принципе, верно, и
                // условиями задачи не оговорено
                int length = String.valueOf(result).split("\\.")[1].length();
                // Оставляем в вычисленном результае столько же цифр после
                // запятой сколько и в конечном
                int pow = (int) Math.pow(10, length); // 10 в степени...
                calcResult = (float)((int)(calcResult * pow) / (float) pow);
                // Сравниваем...
                assertTrue("Деление операндов не соответсвует результату", calcResult == result);                
                break;
            }                    
        }
    }
}
