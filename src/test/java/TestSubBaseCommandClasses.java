import Commands.AmazingBaseCommand;
import Commands.Subs.ExampleSubCommand;
import Helpers.ExampleComand;
import Helpers.ExampleSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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


    }

    @Test
    public void testSubCommandClasses() {

        try {
            command.onCommand(new ExampleSender(), new ExampleComand("amazing"), "amazing", new String[]{"subexample"});
        } finally {
            System.setOut(originalOut);
        }

        String output = baos.toString();

        System.out.println(output);

        assert output.contains("Subexample Command Executed");
    }
}
