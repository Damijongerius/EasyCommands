package com.dami.easyCommands.Command;

import com.dami.easyCommands.Annotations.SubCommand;
import com.dami.easyCommands.Annotations.SubCommandTab;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public abstract class ShardableCommand extends BaseCommand {

    //this list contains the  class name of registered subcommand classes
    protected final List<String> registeredSubCommandClasses = new ArrayList<>();

    protected ShardableCommand(Plugin plugin) {
        super(plugin);
    }

    public void RegisterSubCommandClass(ICommand command){

        if(registeredSubCommandClasses.contains(command.getClass().getName())){
            System.out.println("ignoring already registered subcommand class: " + command.getClass().getName()); //todo implement logger
            return;
        }

        registeredSubCommandClasses.add(command.getClass().getName());

        // inject the subs

        if(command.getClass().isAnnotationPresent(SubCommand.class)){
            collectSubClassCommands(command);
            collectSubClassTabCompleteMethods(command);
        } else {
            throw new IllegalArgumentException("The class " + command.getClass().getName() + " is not annotated with @SubCommand");
        }
    }

    protected void collectSubClassCommands(ICommand commandClass) {

        SubCommand subCommandClassContext = commandClass.getClass().getAnnotation(SubCommand.class);

        String[] basePath = Stream.concat(
                Arrays.stream(subCommandClassContext.commandPath()),
                Stream.of(subCommandClassContext.name())
        ).toArray(String[]::new);

        Method mainMethod = null;
        for (Method declaredMethod : commandClass.getClass().getDeclaredMethods()) {
            if(declaredMethod.getName().equals("mainCommand")){
                mainMethod = declaredMethod;

                insertCommand(basePath, mainMethod, subCommandClassContext, commandClass);
                break;
            }
        }

        if(mainMethod == null){
            throw new IllegalArgumentException("The class " + commandClass.getClass().getName() + " has no main method");
        }


        //get all methods in the class
        for (Method method : commandClass.getClass().getDeclaredMethods()) {
            //the method must have the SubCommand annotation
            if (method.isAnnotationPresent(SubCommand.class)) {

                SubCommand sub = method.getAnnotation(SubCommand.class);

                //the path of the subcommand is now being computed an array to give it the position it needs to have in the hash
                String[] path = Stream.concat(
                        Stream.concat(Arrays.stream(basePath), Arrays.stream(sub.commandPath())),
                        Stream.of(sub.name())
                ).toArray(String[]::new);

                insertCommand(path, method, sub, commandClass);
            }
        }
    }

    protected void collectSubClassTabCompleteMethods(ICommand commandClass) {

        SubCommand subCommandClassContext = commandClass.getClass().getAnnotation(SubCommand.class);

        String[] basePath = Stream.concat(
                Arrays.stream(subCommandClassContext.commandPath()),
                Stream.of(subCommandClassContext.name())
        ).toArray(String[]::new);

        Method tabComplete = null;

        for(Method declaredMethod : commandClass.getClass().getDeclaredMethods()){
            if(declaredMethod.getName().equals("tabComplete")){
                tabComplete = declaredMethod;
                insertTabComplete(basePath, tabComplete, commandClass, subCommandClassContext.permission(), 100);
                break;
            }
        }

        if(tabComplete == null){
            throw new IllegalArgumentException("The class " + commandClass.getClass().getName() + " has no tabComplete method");
        }

        //get all methods in the class
        for (Method method : commandClass.getClass().getDeclaredMethods()) {
            //the method must have the SubCommandTab annotation
            if (method.isAnnotationPresent(SubCommandTab.class)) {

                SubCommandTab tab = method.getAnnotation(SubCommandTab.class);

                //the path of the tab complete is now being computed an array to give it the position it needs to have in the hash
                String[] path = Stream.concat(
                        Stream.concat(Arrays.stream(basePath), Arrays.stream(tab.commandPath())),
                        Stream.of(tab.name())
                ).toArray(String[]::new);

                insertTabComplete(path, method, tab, commandClass);
            }
        }
    }
}
