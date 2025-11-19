import Commands.AmazingBaseCommand;
import Commands.Subs.ExampleSubCommand;
import Helpers.ExampleComand;
import Helpers.ExampleSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class TestSubBaseCommandClasses {

    PrintStream originalOut;
    ByteArrayOutputStream baos;
    AmazingBaseCommand command;
    ExampleSubCommand subCommand;

    @BeforeEach
    public void setup() {
        // capture console output
        originalOut = System.out;
        baos = new ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(baos));

        command = new AmazingBaseCommand(null);

        subCommand = new ExampleSubCommand();

        command.RegisterSubCommandClass(subCommand);
    }

    @Test
    public void testSubCommandClasses() {

        try {
            command.onCommand(new ExampleSender(), new ExampleComand("amazing"), "amazing", new String[]{"admin","unalive"});
        } finally {
            System.setOut(originalOut);
        }

        String output = baos.toString();

        System.out.println("the output is:");
        System.out.println(output);

        assert output.contains("Example Sub Command Executed");
    }

    @Test
    public void testSubCommandClassesSubTab() {

        System.setOut(originalOut);

        List<String> result = command.onTabComplete(new ExampleSender(), new ExampleComand("amazing"), "amazing", new String[]{"admin", ""});

        System.out.println(result);
        assert result != null && result.size() == 1;
    }

    @Test
    public void testSerialization() {

        System.setOut(originalOut);

        System.out.println(command.ConvertToObject());

        assert command.ConvertToObject() != null;
    }
}
