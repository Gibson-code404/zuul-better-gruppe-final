import java.util.HashMap;
import java.util.Map;

/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

public class Game {
    private Parser parser;
    private Room currentRoom;
    private HashMap<String, Items> inventory;
    
    public static void main(String[] args) {
        Game game = new Game();
        game.play();
    }
    
    public Game() {
        parser = new Parser();
        createRooms();
        inventory = new HashMap<>();
    }

    private void createRooms() {
        Room outside, commonRoom, pub, redRoom, office, toilette;

        // Räume erstellen
        outside = new Room("outside the main entrance of the university");
        commonRoom = new Room("in a common room that everyone can chill");
        pub = new Room("in the pub");
        redRoom = new Room("in a room with red light");
        office = new Room("in the managers office");
        toilette = new Room("no description needed its the wc");
        
        
        
        // Exits zwischen den Räumen setzen
        outside.setExit("east", commonRoom);
        outside.setExit("south", redRoom);
        outside.setExit("west", pub);

        commonRoom.setExit("west", outside);
        commonRoom.setExit("south", office);

        pub.setExit("east", outside);
        pub.setExit("south", toilette);

        redRoom.setExit("north", outside);
        redRoom.setExit("east", office);
        redRoom.setExit("west", toilette);

        office.setExit("west", redRoom);
        office.setExit("north", commonRoom);

        toilette.setExit("north", pub);
        toilette.setExit("east", redRoom);

        // Setze den Startraum
        currentRoom = outside;
        
        //Setze die Items
        outside.setItem("keys", "A keyset lying on the ground.");
        commonRoom.setItem("Glasses", "A pair of glasses on the seat.");
        pub.setItem("Bottle", "A bottle of water on the table.");
        redRoom.setItem("Phone", "A phone on the desk.");
        office.setItem("Pen", "A pen on the desk.");
        toilette.setItem("Towel", "A towel hanging on the wall.");

        // Zeige die Beschreibung des Startraums an
        System.out.println(currentRoom.getLongDescription());
    }


    public void play() {
        printWelcome();

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    private void printWelcome() {
        System.out.println();
        System.out.println("You just woke up and have no idea what's going on.");
        System.out.println("Looks like you've lost all your items ... maybe it's a good idea to go look for them.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }
    
    private void look() {
      System.out.println(currentRoom.getLongDescription());
    }


    private boolean processCommand(Command command) {
        boolean wantToQuit = false;

        if (command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        } else if (commandWord.equals("go")) {
            goRoom(command);
        } else if (commandWord.equals("check")) {
            checkRoom(command);
        } else if (commandWord.equals("items")) {
            showItems();
        } else if (commandWord.equals("pick")) {
            pickItem(command);
        } else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        } else if (commandWord.equals("look")) {
            look();}
        return wantToQuit;
    }

    private void printHelp() {
        System.out.println("You were drunk last night.Lost so many things.");
        System.out.println("U need to find them.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    private void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        } else {
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
        }
    }

    private void checkRoom(Command command) {
        if (command.hasSecondWord()) {
            System.out.println("Check what?");
            return;
        }
    
        String itemsInRoom = currentRoom.getItems(); // Alle Items im Raum holen
    
        if (itemsInRoom.equals("No items in this room.")) {
            System.out.println("There are no items in this room.");
        } else {
            System.out.println(itemsInRoom); // Alle Items im Raum anzeigen
        }
    }

    private void showItems() {
        if (inventory.isEmpty()) {
            System.out.println("You have no items.");
        } else {
            System.out.println("Your items:");
            for (String key : inventory.keySet()) {
                Items item = inventory.get(key);
                System.out.println(item.getName() + ": " + item.getDescription());
            }
        }
    }

    private void pickItem(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Pick up what?");
            return;
        }
        
        String itemName = command.getSecondWord();
        String itemDescription = currentRoom.getItemDescription(itemName);
        boolean hasAllItems = false; 
        
        if (itemDescription == null) {
            System.out.println("There is no such item in the room.");
        } else {
            Items item = new Items(itemName, itemDescription);
            inventory.put(itemName, item);
            currentRoom.removeItem(itemName);
            System.out.println("You picked up: " + itemName);
            if (inventory.size() == 6){hasAllItems = true;
            }
        }
        
        if (hasAllItems) {
            System.out.println("Congratulations! With that you have gathered all your belongings!");
            System.out.println("Your memories slowly come back...");
            System.out.println("Seems like you partied a little too hard.");
            System.out.println("Better be careful next time!");
        }
        
    }

    private boolean quit(Command command) {
        if (command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        } else {
            return true;
        }
    }
}
