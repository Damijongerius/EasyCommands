import Commands.AmazingBaseCommand;
import Helpers.ExampleComand;
import Helpers.ExampleSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;


public class TestBasicCommands {

    PrintStream originalOut;
    ByteArrayOutputStream baos;
    AmazingBaseCommand command;

    @BeforeEach
    public void setup() {
        // capture console output
        originalOut = System.out;
        baos = new ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(baos));

        command = new AmazingBaseCommand(null);
    }


    @Test
    public void testRunMainOnInvalidEntry() {

        try {
            command.onCommand(new ExampleSender(), new ExampleComand("amazing"), "amazing", new String[]{});
        } finally {
            System.setOut(originalOut);
        }

        String output = baos.toString();

        System.out.println(output);

        assert output.contains("Amazing Command Executed");
    }

    @Test
    public void testEndpoint() {

        try {
            command.onCommand(new ExampleSender(), new ExampleComand("amazing"), "amazing", new String[]{"subexample"});
        } finally {
            System.setOut(originalOut);
        }

        String output = baos.toString();

        System.out.println(output);

        List<String> result = command.onTabComplete(new ExampleSender(), new ExampleComand("amazing"), "amazing", new String[]{"subexample", ""});

        System.out.println(result);
        assert result != null && result.size() == 3;
    }

    @Test
    public void testSerialization() {

        System.setOut(originalOut);

        System.out.println(command.ConvertToObject());

        assert command.ConvertToObject() != null;
    }
}
