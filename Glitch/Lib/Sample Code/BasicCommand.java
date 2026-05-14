import static edu.wpi.first.wpilibj2.command.Commands.parallel;
import static edu.wpi.first.wpilibj2.command.Commands.sequence;
import static edu.wpi.first.wpilibj2.command.Commands.waitSeconds;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.Subsystem;

// An important thing to understand when working with commands is that there are multiple types.
// The type you will most likely end up using is SequentialCommandGroup, which, as the name suggests, is a group of commands that all run in sequence.
// You can also use the base type Command if you just want it to do one thing, but, generally speaking, if you're doing that then you probably don't need to make a whole Command.
// I will show just a basic Command extension here for reasons I will elaborate on later but everthing in the class definition also applies to SequentialCommandGroup.
public class BasicCommand extends Command{

    // The thing that sets commands apart from most class types is the fact that you really only care about the class definition itself.
    // As in, the thing that actually runs when you make a new instance of that class.
    // You probably won't use a generic Subsystem parameter, more likely you will use the specific type of Subsystem that the command is actually built around using,
    // like ShooterIntake or ElevatorMotorGroup.
    Subsystem subsystem1;
    public BasicCommand(Subsystem subsystem, Subsystem subsystem2) {
        subsystem1 = subsystem;
        // This framework adds commands to the CommandScheduler, which I imagine is important if you don't want two completely different things trying to run at the same time.
        addCommands(
            // Parallel command groups run everything in them at once.
            parallel(
                // You're going to see a lot of the basic Subsystem.run(() -> {}) structure.
                // It turns something that is normally not considered a command and pretends like it is.
                subsystem.run(
                    // Basically this funny little pair of parentheses followed by a lambda is considered a Runnable function, which run() takes and presents like a Command
                    // to parallel so it doesn't have to think too hard about why you didn't give it anything along the lines of new Command().
                    () -> {
                        subsytem.method(); 
                        subsystem.method2();
                    }
                ),
                subsystem2.run(
                    () -> {
                        subsystem2.method();
                    }
                )
            ).finallyDo(() -> {subsystem2.method2();}),
            // finallyDo is something called a WrapperCommand that runs whatever is inside it only once whatever its been appended to has ended.
            // It functions similar to run() so you give it a Runnable instead of a Command.
            // finallyDo also accepts BooleanConsumer, but that doesn't come up as much.

            // Sequential command groups run everything in the order they are given, only starting the next Command when the last one ends. 
            // Usually used when you need a wait between commands.
            sequence(
                subsystem.run(
                    () -> {
                        subsystem.method();
                    }
                ),
                waitSeconds(2),
                subsystem2.run(
                    () -> {
                        subsystem2.method();
                    }
                ))
            // Important! You can run sequential or parallel command groups inside of each other!
            );
    }

    // Once last thing!
    // This is still technically a class, so you can put more stuff outside of the class definition.
    // For instance, you could make an enum that you provide to the command as a parameter in order to more easily change its behavior.
    // But perhaps more importantly, as an extension of Command, BasicCommand can alter some of its inherent methods.
    // You cannot do this specific method alteration with SequentialCommandGroup, as its methods are all labelled as final.

    @Override
    public void end(boolean interrupted) {
        sequence(
            waitSeconds(1),
            subsystem1.method()
            );
    }
    // Normally, when a Command ends, it just stops all of its Runnables.
    // But what we have added here means that it will wait a second and then call another method before it finally stops.
    // This is really cool but also far from all that you can do with these methods. I implore you to explore all of them in your own time.

    // This sets what the command does on startup
    @Override
    public void initialize() {}
    
    // And this sets what the command will do when it actually runs, alongside all the other stuff we set before.
    @Override
    public void execute() {}
}
