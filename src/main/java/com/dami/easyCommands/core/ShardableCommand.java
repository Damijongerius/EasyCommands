package com.dami.easyCommands.core;

import com.dami.easyCommands.annotations.SubCommand;
import com.dami.easyCommands.annotations.SubCommandTab;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public abstract class ShardableCommand extends BaseCommand {

    protected final List<String> registeredSubCommandClasses = new ArrayList<>();

    protected ShardableCommand() {
        super();
    }

    public void RegisterSubCommandClass(ICommand command){
        if(registeredSubCommandClasses.contains(command.getClass().getName())){
            return;
        }
        registeredSubCommandClasses.add(command.getClass().getName());
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
        ).filter(s -> s != null && !s.isEmpty()).toArray(String[]::new);

        try {
            Method mainMethod = commandClass.getClass().getMethod("mainCommand", CommandSender.class, String[].class);
            insertCommand(basePath, mainMethod, subCommandClassContext, commandClass);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The class " + commandClass.getClass().getName() + " has no mainCommand method", e);
        }

        for (Method method : commandClass.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubCommand.class)) {
                SubCommand sub = method.getAnnotation(SubCommand.class);
                String[] path = Stream.concat(
                        Stream.concat(Arrays.stream(basePath), Arrays.stream(sub.commandPath())),
                        Stream.of(sub.name())
                ).filter(s -> s != null && !s.isEmpty()).toArray(String[]::new);
                if (path.length > 0) {
                    insertCommand(path, method, sub, commandClass);
                }
            }
        }
    }

    protected void collectSubClassTabCompleteMethods(ICommand commandClass) {
        SubCommand subCommandClassContext = commandClass.getClass().getAnnotation(SubCommand.class);
        String[] basePath = Stream.concat(
                Arrays.stream(subCommandClassContext.commandPath()),
                Stream.of(subCommandClassContext.name())
        ).filter(s -> s != null && !s.isEmpty()).toArray(String[]::new);

        try {
            Method tabComplete = commandClass.getClass().getMethod("tabComplete", CommandSender.class, String[].class);
            insertTabComplete(basePath, tabComplete, commandClass, subCommandClassContext.permission(), 100);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The class " + commandClass.getClass().getName() + " has no tabComplete method", e);
        }

        for (Method method : commandClass.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubCommandTab.class)) {
                SubCommandTab tab = method.getAnnotation(SubCommandTab.class);
                String[] path = Stream.concat(
                        Stream.concat(Arrays.stream(basePath), Arrays.stream(tab.commandPath())),
                        Stream.of(tab.name())
                ).filter(s -> s != null && !s.isEmpty()).toArray(String[]::new);
                insertTabComplete(path, method, tab, commandClass);
            }
        }
    }
}
