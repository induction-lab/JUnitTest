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
        System.out.println("Read parametres from file...");
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
        // определено что во входном файле могут быть любые значения полей 
        // в том числе и не подходящие под условия
        assertNotNull("Invalid operand 1", operand1);
        assertNotNull("Invalid operand 2", operand2);
        assertNotNull("Invalid result", result);
        assertTrue("Invalid operation", operation != Operation.INVALID);        
        // В зависимости от оператора производим вычисления и сверяем результат
        // с результатом входного файла
        switch (operation) {
            case SUM: {
                // Проверяем что result у нас целое число иначе при
                // преобразовании сторки в целое число получиться 10.2 = 10
                // А сумма целых чисел не может быть дробным числом
                assertTrue("Result not match sum", (result - result.intValue()) == 0);
                // Сравниваем...                
                assertTrue("Result not match sum", (operand1 + operand2) == result.intValue());
                break;
            }
            case SUB: {
                // Проверяем что result у нас целое число иначе при
                // преобразовании сторки в целое число получиться 10.2 = 10
                // А разность целых чисел не может быть дробным числом
                assertTrue("Result not match sub", (result - result.intValue()) == 0);
                // Сравниваем...                
                assertTrue("Result not match sub", (operand1 - operand2) == result.intValue());                
                break;
            }
            case MULT: {
                // Проверяем что result у нас целое число иначе при
                // преобразовании сторки в целое число получиться 10.2 = 10
                // А сумма произведение чисел не может быть дробным числом                
                assertTrue("Result not match mult", (result - result.intValue()) == 0);
                // Сравниваем...                
                assertTrue("Result not match mult", (operand1 * operand2) == result.intValue());                
                break;
            }
            case DIV: {
                // Проверяем что второй операнд не 0, на ноль делить не можно
                assertTrue("Division by zero", operand2 != 0);
                // Считаем реакльный результат вычислений
                float calcResult = (float)operand1 / (float)operand2;
                // Считаем длинну цифр после запятой в конечном результате,
                // так как 4 / 3 = 1.333333333... а в файле может быть записано
                // 1.33 что, в принципе, верно, и условиями задачи не оговорено
                int length = String.valueOf(result).split("\\.")[1].length();
                // Оставляем в вычисленном результае столько же цифр после
                // запятой сколько и в конечном
                int pow = (int) Math.pow(10, length);
                calcResult = (float)((int)(calcResult * pow) / (float) pow);
                // Сравниваем...
                assertTrue("Result not match div", calcResult == result);                
                break;
            }                    
        }
    }
}
