import java.util.*;
import java.io.*;
import java.lang.*;

/** 
*<h1>FoodforLess Grocery </h1>
*<p>
*Allows the user to perform several functions, based on the current inventory of Food for Less Grocery--which is read in from the stock.txt file--, including:
*<p> 
*<ol><li>listing the current inventory</li> <li>what is not in stock</li> <li>the most expensive item in stock</li> <li>the total value of the current inventory</li> <li>allowing the user to create a custom order</li></ol>
*@author Berhane-Hiwet N. La Rose
*@since 28-Nov-15
*@version 1.7
*/
public class FoodForLess{
    /**
     * Initiates the various variables utilised throughout this program, and makes calls to {@link #greeting()} and {@link #pickOption()}. Also allows for the program to be launched in test mode, via use of the "t" command line argument
     * @exception IOException due to chained call to {@link #populateArrays()}
     */
        public static void main(String[] args) throws IOException{
        ArrayList<String> productCodeArray = new ArrayList<String>();
        ArrayList<String> descriptionArray = new ArrayList<String>();
        ArrayList<Integer> stockArray = new ArrayList<Integer>();
        ArrayList<Double> priceArray = new ArrayList<Double>();
        ArrayList<String> ordProdCode = new ArrayList<String>();
        ArrayList<Integer> ordQuant = new ArrayList<Integer>();
        int size = 0;
        double ordValue = 0.0;
        boolean changes = false;
        boolean test_mode= false;
        String input= "";
        
        size= populateArrays(productCodeArray, descriptionArray, stockArray, priceArray, size);

        if (args.length>0){
            input= args[0].toLowerCase();
            char mode= input.charAt(0);
            if (mode=='t'){
                test_mode=true;
                runTests(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
            }         
        }
        
        greeting();
        pickOption(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
    }

    /**
    * Responsible for all screen printing, both formatted and unformatted
    *@param mode Specifies the procedure's mode; mode 1 facilitates the printing of unformatted text from an ArrayList, while mode 2 allows for the printing of formatted text and also gets its variables from an ArrayList
    *@param output The output to be printed to the screen
    */
    public static void printOutput (int mode, ArrayList<String> output){
        if (mode==1){
            for (int i=0; i<output.size(); i++){
                System.out.printf(output.get(i));
            }
        } else {
            for (int i=0; i<output.size(); i+=2){
                System.out.printf(output.get(i), output.get(i+1));
            }
        }
    }

    /**
    * Responsible for capturing all input for the application. The user prompt is taken as a variable, and the user input is returned to the caller as a String, which is later formatted to the required type
    *@param prompt The text prompt to be printed to the screen
    *@return input The user input as a String; this will be formatted (as necessary), by the calling procedure 
    */
    public static String getUserInput(String prompt){
        String input="";
        Scanner keyboard = new Scanner(System.in);
        System.out.printf(prompt);
        input= keyboard.next();
        return input;
    }

    /**
    * Attempts to open the stock.txt file. The function will throw an exception if the file doesn't exist, and returns the file if it does
    *@param fileName The name of the file to be opened
    *@return The opened file
    *@exception FileNotFoundException If the specified file is not found
    */ 
    public static Scanner openFile( String fileName) throws FileNotFoundException{
        Scanner fileInput = null;
        try{
            fileInput = new Scanner(new File(fileName));
        }
        catch ( FileNotFoundException fnfex){
            ArrayList<String> output = new ArrayList<String>(Arrays.asList("The specified file (%s) cannot be opened.%n", fileName));
            printOutput(2, output);
        }
        return fileInput;
    }

    /**
    * Takes the stock list, and populates the productCode, description, Stock, and price arrays from it. The size of the arrays is also captured for use in loops.
    *@return size of ArrayLists
    *@exception IndexOutOfBoundsException If the data in the specified file does not match the program-specified format
    *@exception FileNotFoundException If the specified file is not found
    */ 
    public static int populateArrays(ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size) throws IndexOutOfBoundsException, FileNotFoundException{
        Scanner fileInput= openFile("stock.txt");
        while (fileInput.hasNextLine()){
            String line = fileInput.nextLine();
            String[] segments = line.split(" ");
            try{
                productCodeArray.add(segments[0]);
                descriptionArray.add(segments[1]);
                stockArray.add(Integer.valueOf(segments[2]));
                priceArray.add(Double.valueOf(segments[3]));
            } catch (IndexOutOfBoundsException idex){
                throw new RuntimeException(String.format("Incomplete data detected! Please check inventory file."));
            }      
        }
        size = priceArray.size();
        return size;
    }

    /**
    * Displays a greeting message, identifying the company
    */ 
    public static void greeting(){
        ArrayList<String> greetings1= new ArrayList<String>(Arrays.asList("%50s%n", "Welcome to Foods for Less Grocery.", "%56s%n%n", "Proudly serving Point Fortin for over 40 years."));
        printOutput(2,greetings1);    
    }

    /**
    * Presents the user with a listing of the program's features. Subsequent to this, the user is allowed to specify their desired option.
    */ 
    public static void giveOptions(){
        ArrayList<String> prompt= new ArrayList<String>(Arrays.asList("%nKindly select an option from the list below:%n", "1. Display current stock levels and values%n", "2. Display all out of stock items%n", "3. Display total value of current stock%n", "4. Identify most expensive food item%n", "5. Create an order from current inventory%n"));
        printOutput(1, prompt);
    }

    /**
    * Allows the user to select their desired option from the list displayed by {@link #giveOptions()}
    *@param productCodeArray a String ArrayList containing the product codes of all items in the inventory 
    *@param descriptionArray a String ArrayList containing the description of all items in the inventory
    *@param stockArray an Integer ArrayList containing the quantity in stock of all items in the inventory
    *@param priceArray a Double ArrayList containing the price of all items in the inventory
    *@param size the size of the inventory (Derived from the ArrayList sizes)
    *@param ordProdCode a String ArrayList containing the product codes of items contained in a custom order (Used in Option 5)
    *@param ordQuant an Integer ArrayList containing the desired quantity of items in a custom order (Used in Option 5)
    *@param ordValue a Double containing the total value of the custom order housed in ordProdCode and ordQuant (Used in Option 5)
    *@param changes a Boolean indicating whether or not stock values have changed. Used to trigger {@link #updateStockFile()} upon program termination when necessary
    *@exception InputMismatchException Thrown if user enters non-numerical input
    *@exception IOException IOException Thrown on account of chained calls to {@link #updateStockFile()}
    *@exception NumberFormatException This exception is thrown if the user input cannot be parsed to an Integer
    */ 
    public static void pickOption(ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes, boolean test_mode) throws InputMismatchException, IOException, NumberFormatException{
        int option = 0;
        giveOptions();        
        while (option<1 || option>6){
            String response= getUserInput("Which would you like to do?: ");
            try{
                option = Integer.valueOf(response);
            }catch (InputMismatchException|NumberFormatException e){
                ArrayList<String> badInput= new ArrayList<String>(Arrays.asList("That's definitely not an option on the list.%n"));
                printOutput(1, badInput);
                pickOption(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
            }            
        }
         switch(option){
            case 1: giveStock(1, productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
                break;
            case 2: getWhatsOut(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
                break;
            case 3: getTotalValue(1, productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
                break;
            case 4: getMostExpensive(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
                break;
            case 5: getOrderSize(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
                break;
            default: pickOption(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
                break;
        }                      
    }

    /**
    * Allows the user to perform another operation, or end the program. If the user inputs "yes", {@link #pickOption()} is called. If the user input is "no", the program terminates, with {@link #updateStockFile()} updating stock.txt if any orders were placed
    *@param productCodeArray a String ArrayList containing the product codes of all items in the inventory 
    *@param descriptionArray a String ArrayList containing the description of all items in the inventory
    *@param stockArray an Integer ArrayList containing the quantity in stock of all items in the inventory
    *@param priceArray a Double ArrayList containing the price of all items in the inventory
    *@param size the size of the inventory (Derived from the ArrayList sizes)
    *@param ordProdCode a String ArrayList containing the product codes of items contained in a custom order (Used in Option 5)
    *@param ordQuant an Integer ArrayList containing the desired quantity of items in a custom order (Used in Option 5)
    *@param ordValue a Double containing the total value of the custom order housed in ordProdCode and ordQuant (Used in Option 5)
    *@param changes a Boolean indicating whether or not stock values have changed. Used to trigger {@link #updateStockFile()} upon program termination when necessary
    *@exception IOException Thrown on account of chained calls to {@link #updateStockFile()}
    */ 
    public static void getAnother(ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes, boolean test_mode) throws IOException{
        String response = "";
        response = getUserInput("%nWould you like to perform another operation? [Yes/No]: ");
        switch (response.toLowerCase()){
            case "y": case "yes": pickOption(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
                break;
            case "n": case "no": ArrayList<String> farewell= new ArrayList<String>(Arrays.asList("Goodbye!\n"));
                printOutput(1, farewell);
                updateStockFile(productCodeArray, descriptionArray, stockArray, priceArray, changes, size);
                break;
            default: ArrayList<String> valid= new ArrayList<String>(Arrays.asList("Please enter a valid response.\n"));
                printOutput(1, valid);
                getAnother(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
        }
    }

    /**
    * Displays the full stock list, inclusive of out of stock items; or displays the current custom order, depended on the mode selected.
    *@param option Specifies the mode (and relative function) of the procedure. Mode 1 displays the full stock list. Mode 2 displays the details of a custom order
    *@param productCodeArray a String ArrayList containing the product codes of all items in the inventory 
    *@param descriptionArray a String ArrayList containing the description of all items in the inventory
    *@param stockArray an Integer ArrayList containing the quantity in stock of all items in the inventory
    *@param priceArray a Double ArrayList containing the price of all items in the inventory
    *@param size the size of the inventory (Derived from the ArrayList sizes)
    *@param ordProdCode a String ArrayList containing the product codes of items contained in a custom order (Used in Option 5)
    *@param ordQuant an Integer ArrayList containing the desired quantity of items in a custom order (Used in Option 5)
    *@param ordValue a Double containing the total value of the custom order housed in ordProdCode and ordQuant (Used in Option 5)
    *@param changes a Boolean indicating whether or not stock values have changed. Used to trigger {@link #updateStockFile()} upon program termination when necessary 
    *@exception IOException Thrown on account of chained calls to {@link #updateStockFile()}
    */ 
    public static void giveStock(int option, ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes, boolean test_mode) throws IOException{
        if (option==1){ 
            ArrayList<String> header= new ArrayList<String>(Arrays.asList("%55s\n", "INVENTORY AND CURRENT STOCK LEVELS:", "%-16s", "Item", "%-20s", "Description", "%-12s", "Quantity", "%-16s", "Unit Price", "%-15s%n", "Stock Total"));
            printOutput(2, header);
            for (int i=0; i<size; i++){
                String productCode= productCodeArray.get(i);
                String description = descriptionArray.get(i);
                int stock = stockArray.get(i);
                double price= priceArray.get(i);
                double totals= stockArray.get(i)*priceArray.get(i);
                ArrayList<String> listing = new ArrayList<String>(Arrays.asList("%-16s", productCode, "%-20s", description, "%-12s", Integer.toString(stock), "%10s", String.format("%10.2f", price), "%17s%n", String.format("%13.2f", totals)));
                printOutput(2, listing);
            }
            getAnother(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
        }else{
            ArrayList<String> header2= new ArrayList<String>(Arrays.asList("%35s\n", "YOUR ORDER:","%-20s", "Description", "%-12s", "Quantity", "%-16s", "Unit Price", "%-15s%n", "Item Total"));
            printOutput(2, header2);
            for (int i=0; i<ordQuant.size(); i++){
                String description= descriptionArray.get(productCodeArray.indexOf(ordProdCode.get(i)));
                int quant = ordQuant.get(i);
                double price= priceArray.get(productCodeArray.indexOf(ordProdCode.get(i)));
                double itemTotal= ordQuant.get(i)*priceArray.get(productCodeArray.indexOf(ordProdCode.get(i)));
                ArrayList<String> invoice = new ArrayList<String>(Arrays.asList("%-15s", description, "%8s", Integer.toString(quant), "%7s", "", "%12s", String.format("%10.2f", price),"%16s%n", String.format("%13.2f", itemTotal)));
                printOutput(2, invoice);
            }
            ArrayList<String> ordTotal= new ArrayList<String>(Arrays.asList("%n%42s", "Total","%4s", "", "%14s%n", String.format("%10.2f%n", ordValue)));
            printOutput(2, ordTotal);
            int x= adjustInventory(ordProdCode,ordQuant, productCodeArray, stockArray);
            getAnother(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
        }
    }

    /**
    * Displays those items that are currently out of stock
    *@param productCodeArray a String ArrayList containing the product codes of all items in the inventory 
    *@param descriptionArray a String ArrayList containing the description of all items in the inventory
    *@param stockArray an Integer ArrayList containing the quantity in stock of all items in the inventory
    *@param priceArray a Double ArrayList containing the price of all items in the inventory
    *@param size the size of the inventory (Derived from the ArrayList sizes)
    *@param ordProdCode a String ArrayList containing the product codes of items contained in a custom order (Used in Option 5)
    *@param ordQuant an Integer ArrayList containing the desired quantity of items in a custom order (Used in Option 5)
    *@param ordValue a Double containing the total value of the custom order housed in ordProdCode and ordQuant (Used in Option 5)
    *@param changes a Boolean indicating whether or not stock values have changed. Used to trigger {@link #updateStockFile()} upon program termination when necessary
    *@exception IOException Thrown on account of chained calls to {@link #updateStockFile()}
    */ 
    public static void getWhatsOut(ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes, boolean test_mode)throws IOException{
        ArrayList<String> header= new ArrayList<String>(Arrays.asList("%n***********OUT OF STOCK ITEMS***********%n"));
        printOutput(1, header);
        for (int i =0; i<size; i++){
            if (stockArray.get(i)==0){
                String item = descriptionArray.get(i);
                ArrayList<String> out = new ArrayList<String>(Arrays.asList("%s%n", item));
                printOutput(2, out);
            }
        }
        if (test_mode==false){
            getAnother(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
        }
    }

    /**
    * Displays the total value of the current inventory, or custom order
    *@param option Specifies what to tally total value for. Option 1 tallies current inventory. Option 2 tallies a custom order.
    *@param productCodeArray a String ArrayList containing the product codes of all items in the inventory 
    *@param descriptionArray a String ArrayList containing the description of all items in the inventory
    *@param stockArray an Integer ArrayList containing the quantity in stock of all items in the inventory
    *@param priceArray a Double ArrayList containing the price of all items in the inventory
    *@param size the size of the inventory (Derived from the ArrayList sizes)
    *@param ordProdCode a String ArrayList containing the product codes of items contained in a custom order (Used in Option 5)
    *@param ordQuant an Integer ArrayList containing the desired quantity of items in a custom order (Used in Option 5)
    *@param ordValue a Double containing the total value of the custom order housed in ordProdCode and ordQuant (Used in Option 5)
    *@param changes a Boolean indicating whether or not stock values have changed. Used to trigger {@link #updateStockFile()} upon program termination when necessary
    *@exception IOException Thrown on account of chained calls to {@link #updateStockFile()}
    */ 
    public static void getTotalValue(int option, ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes, boolean test_mode) throws IOException{
        double count = 0;
        if (option == 1){
            for (int i=0; i<size; i++){
                count+=(stockArray.get(i)*priceArray.get(i));
            }
            ArrayList<String> total= new ArrayList<String>(Arrays.asList("%nThe total value of the current stock is $%s%n", String.format("%4.2f", count)));
            printOutput(2, total);
            if (test_mode==false){
                getAnother(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
            }
        }else{
            for (int j=0; j<ordQuant.size(); j++){
                count+=(ordQuant.get(j)*priceArray.get(productCodeArray.indexOf(ordProdCode.get(j))));
            }
            ordValue= count;
            giveStock(2, productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
        }
    }

    /**
    * Displays the most expensive item in the inventory
    *@param productCodeArray a String ArrayList containing the product codes of all items in the inventory 
    *@param descriptionArray a String ArrayList containing the description of all items in the inventory
    *@param stockArray an Integer ArrayList containing the quantity in stock of all items in the inventory
    *@param priceArray a Double ArrayList containing the price of all items in the inventory
    *@param size the size of the inventory (Derived from the ArrayList sizes)
    *@param ordProdCode a String ArrayList containing the product codes of items contained in a custom order (Used in Option 5)
    *@param ordQuant an Integer ArrayList containing the desired quantity of items in a custom order (Used in Option 5)
    *@param ordValue a Double containing the total value of the custom order housed in ordProdCode and ordQuant (Used in Option 5)
    *@param changes a Boolean indicating whether or not stock values have changed. Used to trigger {@link #updateStockFile()} upon program termination when necessary
    *@exception IOException Thrown on account of chained calls to {@link #updateStockFile()}
    */ 
    public static void getMostExpensive(ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes, boolean test_mode) throws IOException{
        double big = priceArray.get(0);
        ArrayList<Integer> upper= new ArrayList<Integer>();
        for (int i=1; i<size; i++){
            if (priceArray.get(i)>big){
                upper.clear();
                upper.add(i);
                big=priceArray.get(i);
            }
            else if (priceArray.get(i)==big){
                upper.add(i);
            }
        }
        ArrayList<String> header = new ArrayList<String>(Arrays.asList("%n%42s%n", "***********MOST EXPENSIVE ITEMS***********", "%-20s", "Description", "%10s%n", "Price"));
        printOutput(2, header);
        int upperSize= upper.size();
        for (int j=0; j<upperSize; j++){
            String desc= descriptionArray.get(upper.get(j));
            double price= priceArray.get(upper.get(j));
            ArrayList<String> pricey= new ArrayList<String>(Arrays.asList("%-20s", desc, "%10s%n", String.format("%4.2f", price)));
            printOutput(2, pricey);
        }
        if (test_mode==false){
            getAnother(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
        }
    }

    /**
    * Allows the user to specify the size of the custom order
    *@param productCodeArray a String ArrayList containing the product codes of all items in the inventory 
    *@param descriptionArray a String ArrayList containing the description of all items in the inventory
    *@param stockArray an Integer ArrayList containing the quantity in stock of all items in the inventory
    *@param priceArray a Double ArrayList containing the price of all items in the inventory
    *@param size the size of the inventory (Derived from the ArrayList sizes)
    *@param ordProdCode a String ArrayList containing the product codes of items contained in a custom order (Used in Option 5)
    *@param ordQuant an Integer ArrayList containing the desired quantity of items in a custom order (Used in Option 5)
    *@param ordValue a Double containing the total value of the custom order housed in ordProdCode and ordQuant (Used in Option 5)
    *@param changes a Boolean indicating whether or not stock values have changed. Used to trigger {@link #updateStockFile()} upon program termination when necessary
    *@exception InputMismatchException Thrown if user enters non-numerical input
    *@exception IOException Thrown on account of chained calls to {@link #updateStockFile()}
    *@exception NumberFormatException This exception is thrown if the user input cannot be parsed to an Integer
    */ 
    public static void getOrderSize(ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes, boolean test_mode) throws InputMismatchException, IOException, NumberFormatException{
        try{
            int orderSize= Integer.valueOf(getUserInput("\nHow many items would you like to order?: "));
            placeOrders(orderSize, 1, productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
        } catch (InputMismatchException|NumberFormatException wType) {
            ArrayList<String> valNum= new ArrayList<String>(Arrays.asList("\nPlease enter a valid number size.\n"));
            printOutput(1, valNum);
            getOrderSize(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
        }        
    }

    /**
    * Allows the user to order each specific item that makes up their order. Subsequent to ordering, changes made are noted by the boolean, changes, and 
    *{@link #getTotalValue} and {@link #giveStock} are called.
    *@param orderSize The number of items to be ordered, specified in {@link #getOrderSize()}
    *@param mode Allows for the order loop to be broken by {@link #optOut()} if the user so chooses
    *@param productCodeArray a String ArrayList containing the product codes of all items in the inventory 
    *@param descriptionArray a String ArrayList containing the description of all items in the inventory
    *@param stockArray an Integer ArrayList containing the quantity in stock of all items in the inventory
    *@param priceArray a Double ArrayList containing the price of all items in the inventory
    *@param size the size of the inventory (Derived from the ArrayList sizes)
    *@param ordProdCode a String ArrayList containing the product codes of items contained in a custom order (Used in Option 5)
    *@param ordQuant an Integer ArrayList containing the desired quantity of items in a custom order (Used in Option 5)
    *@param ordValue a Double containing the total value of the custom order housed in ordProdCode and ordQuant (Used in Option 5)
    *@param changes a Boolean indicating whether or not stock values have changed. Used to trigger {@link #updateStockFile()} upon program termination when necessary
    *@exception IOException Thrown on account of chained calls to {@link #updateStockFile()}
    */ 
    public static void placeOrders(int orderSize, int mode, ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes, boolean test_mode) throws IOException{
        orders:{
            for(int i=0; i<orderSize; i++){
                verifyProdCode(i, productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes);
                if (i<orderSize-1){
                    int choice= optOut();
                    if (choice==1){
                        break orders;
                    }
                }
            }
        }
        changes=true;                                
        getTotalValue(2, productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
    }

    /**
    * Lists the product codes of the current inventory. Used in tandem with {@link #verifyProductCode()}
    *@param productCodeArray a String ArrayList containing the product codes of all items in the inventory 
    *@param descriptionArray a String ArrayList containing the description of all items in the inventory
    *@param stockArray an Integer ArrayList containing the quantity in stock of all items in the inventory
    *@param size the size of the inventory (Derived from the ArrayList sizes)
    */ 
    public static void giveProdCodes(ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, int size){
        ArrayList<String> listing= new ArrayList<String>(Arrays.asList("\nThese are the product codes of the items currently in stock: %n"));
        printOutput(1, listing);
        for (int i=0; i<size; i++){
            if (stockArray.get(i)>0){
                String code= productCodeArray.get(i);
                String desc= descriptionArray.get(i);
                ArrayList<String> items= new ArrayList<String>(Arrays.asList("%-10s ", code, "%s%n", desc));
                printOutput(2, items);
            }
        }
    }

    /**
    * Verifies that the product code entered corresponds to an item in the inventory
    *@param i The loop count (and item number, if increased by 1). Used in this procedure to help the user keep track of the current item's position in the intended order
    *@param productCodeArray a String ArrayList containing the product codes of all items in the inventory 
    *@param descriptionArray a String ArrayList containing the description of all items in the inventory
    *@param stockArray an Integer ArrayList containing the quantity in stock of all items in the inventory
    *@param priceArray a Double ArrayList containing the price of all items in the inventory
    *@param size the size of the inventory (Derived from the ArrayList sizes)
    *@param ordProdCode a String ArrayList containing the product codes of items contained in a custom order (Used in Option 5)
    *@param ordQuant an Integer ArrayList containing the desired quantity of items in a custom order (Used in Option 5)
    *@param ordValue a Double containing the total value of the custom order housed in ordProdCode and ordQuant (Used in Option 5)
    *@param changes a Boolean indicating whether or not stock values have changed. Used to trigger {@link #updateStockFile()} upon program termination when necessary
    */ 
    public static void verifyProdCode(int i, ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes){
        giveProdCodes(productCodeArray, descriptionArray, stockArray, size);
        int itemNo= i+1;
        String prompt= "Please enter the product code for item "+Integer.toString(itemNo)+": ";
        String ordProductCode = getUserInput(prompt);
        ordProductCode = ordProductCode.toUpperCase();
        int position = productCodeArray.indexOf(ordProductCode);
        if (position>=0){
            verifyQuantity(position, i, productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes);
        }else{
            ArrayList<String> error= new ArrayList<String>(Arrays.asList("I'm sorry, but that product code does not exist in our inventory.%n"));
            printOutput(1, error);
            verifyProdCode(i, productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes);
        }
    }

    /**
    * Verifies that the desired quantity of the selected product can be purchased. If it can, the desired product and quantity are added to the custom order. If it cannot, the procedure informs the user, and then calls itself.
    *@param position The position of the selected product's data in the main program arrays
    *@param i The loop count/item number from the loop initiated in {@link #getOrderSize()}
    *@param productCodeArray a String ArrayList containing the product codes of all items in the inventory 
    *@param descriptionArray a String ArrayList containing the description of all items in the inventory
    *@param stockArray an Integer ArrayList containing the quantity in stock of all items in the inventory
    *@param priceArray a Double ArrayList containing the price of all items in the inventory
    *@param size the size of the inventory (Derived from the ArrayList sizes)
    *@param ordProdCode a String ArrayList containing the product codes of items contained in a custom order (Used in Option 5)
    *@param ordQuant an Integer ArrayList containing the desired quantity of items in a custom order (Used in Option 5)
    *@param ordValue a Double containing the total value of the custom order housed in ordProdCode and ordQuant (Used in Option 5)
    *@param changes a Boolean indicating whether or not stock values have changed. Used to trigger {@link #updateStockFile()} upon program termination when necessary
    *@exception NumberFormatException This exception is thrown if the user input cannot be parsed to an Integer
    */ 
    public static void verifyQuantity(int position, int i,  ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes) throws NumberFormatException{
        String avail= Integer.toString(stockArray.get(position));
        String desc= descriptionArray.get(position);
        String prompt= "%nWe have " + avail + " "+ desc + " in stock. How many would you like?: ";
        try{
            int quant = Integer.valueOf(getUserInput(prompt));
            if (quant<=stockArray.get(position)){
                addToOrder(position, quant, i, productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes);
            }else{
                ArrayList<String> tooMany= new ArrayList<String>(Arrays.asList("I'm sorry, but you can order a maximum of "+ avail +" " + desc));
                verifyQuantity(position, i, productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes);
            }
        } catch (NumberFormatException inval){
            ArrayList<String> wType= new ArrayList<String>(Arrays.asList("I'm pretty sure that's not even a number..."));
            printOutput(1, wType);
            verifyQuantity(position, i, productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes);
        }
    }

    /**
    * Adds the specified product and quantity to the custom order
    *@param position The position of the selected product's data in the main program arrays. Obtained in {@link #verifyQuantity}
    *@param quant The desired quantity of the selected product. Also obtained in {@link #verifyQuantity}
    *@param productCodeArray a String ArrayList containing the product codes of all items in the inventory 
    *@param descriptionArray a String ArrayList containing the description of all items in the inventory
    *@param stockArray an Integer ArrayList containing the quantity in stock of all items in the inventory
    *@param priceArray a Double ArrayList containing the price of all items in the inventory
    *@param size the size of the inventory (Derived from the ArrayList sizes)
    *@param ordProdCode a String ArrayList containing the product codes of items contained in a custom order (Used in Option 5)
    *@param ordQuant an Integer ArrayList containing the desired quantity of items in a custom order (Used in Option 5)
    *@param ordValue a Double containing the total value of the custom order housed in ordProdCode and ordQuant (Used in Option 5)
    *@param changes a Boolean indicating whether or not stock values have changed. Used to trigger {@link #updateStockFile()} upon program termination when necessary
    *@param i The loop count/item number from the loop initiated in {@link #getOrderSize()}
    */ 
    public static void addToOrder(int position, int quant, int i, ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes){
        ordProdCode.add(productCodeArray.get(position));
        ordQuant.add(quant);
        String message= Integer.toString(quant)+ " "+ descriptionArray.get(position)+ " have been added to your order.%n%n";
        ArrayList<String> confirmation= new ArrayList<String>(Arrays.asList(message));
        printOutput(1, confirmation);
    }

    /**
    * Allows the user to end the ordering process by inputting "n" or "no"
    *@return prompt Specifies whether the user wishes to end the ordering process (facilitated by {@link #placeOrders}).
    */   
    public static int optOut(){
        ArrayList<String> negatives= new ArrayList<String>(Arrays.asList("no", "n"));
        String response = getUserInput("Type 'N' or 'No' to stop ordering [No/N]: ");
        response = response.toLowerCase();
        int prompt= 0;
        if (negatives.contains(response)){
            prompt=1;
        }
        return prompt;
    }

    /**
    * Updates current stock levels after an order is completed
    *@param ordProdCode a String ArrayList containing the product codes of items contained in a custom order (Used in Option 5)
    *@param ordQuant an Integer ArrayList containing the desired quantity of items in a custom order (Used in Option 5)
    *@param productCodeArray a String ArrayList containing the product codes of all items in the inventory 
    *@param stockArray an Integer ArrayList containing the quantity in stock of all items in the inventory
    *@return prevStockLvl in order to return updated values to calling procedure
    */ 
    public static int adjustInventory(ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, ArrayList<String> productCodeArray, ArrayList<Integer> stockArray){
        int prevStockLvl= 0;
        int reduction = 0;
        int genPos = 0;
        for (int i=0; i<ordProdCode.size(); i++){
            String item= ordProdCode.get(i);
            genPos = productCodeArray.indexOf(item);
            prevStockLvl= stockArray.get(genPos);
            reduction= ordQuant.get(ordProdCode.indexOf(item));
            stockArray.set(genPos, (prevStockLvl-reduction));
        }
        return prevStockLvl;
    }

    /**
    * Updates the stock.txt file with current stock levels at program termination if any custom orders have been placed (Currently set to write to a different file than the input file for testing purposes)
    *@param productCodeArray a String ArrayList containing the product codes of all items in the inventory 
    *@param descriptionArray a String ArrayList containing the description of all items in the inventory
    *@param stockArray an Integer ArrayList containing the quantity in stock of all items in the inventory
    *@param priceArray a Double ArrayList containing the price of all items in the inventory
    *@param size the size of the inventory (Derived from the ArrayList sizes)
    *@param changes a Boolean indicating whether or not stock values have changed. Used to trigger {@link #updateStockFile()} upon program termination when necessary
    *@exception IOException Required declaration of IOException. If the file doesn't exist, it's created; the exception isn't actually thrown
    */ 
    public static void updateStockFile(ArrayList<String> productCodeArray, ArrayList<String> descriptionArray,  ArrayList<Integer> stockArray, ArrayList<Double> priceArray, boolean changes, int size)throws IOException{
        if (changes){
            String file = "stock3.txt";
            String line, code, label, count, cost= "";
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (int i=0; i<size; i++){
                code= productCodeArray.get(i)+ " ";
                label= descriptionArray.get(i)+" ";
                count= Integer.toString(stockArray.get(i))+" ";
                cost= Double.toString(priceArray.get(i));
                line= code+label+count+cost;
                try{
                    writer.write(line);
                    writer.newLine();            
                } catch (IOException x) {
                    System.err.format("IOException: %s%n", x);
                }
            }
            writer.close();        
        }
    }

    public static void runTests(ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes, boolean test_mode) throws IOException{
        test_mode= true;
        testOut(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
        testTotals(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
        testMax(productCodeArray, descriptionArray, stockArray, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
    }

    public static void testOut(ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes, boolean test_mode) throws IOException{
        ArrayList<Integer> quant1= new ArrayList<Integer>(Arrays.asList(5, 7, 9));
        ArrayList<String> products1= new ArrayList<String>(Arrays.asList("Product A", "Product B", "Product C"));
        ArrayList<String> results1= new ArrayList<String>(Arrays.asList("%nNo items out of stock"));
        printOutput(1, results1);
        size= 3;
        getWhatsOut(productCodeArray, products1, quant1, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);

        ArrayList<Integer> quant2= new ArrayList<Integer>(Arrays.asList(5, 7, 0, 9, 0, 0));
        ArrayList<String> products2= new ArrayList<String>(Arrays.asList("Product A", "Product B", "Product C", "Product D", "Product E", "Product F"));
        ArrayList<String> results2= new ArrayList<String>(Arrays.asList("%nProducts C, E, and F out of stock"));
        printOutput(1, results2);
        size= 6;
        getWhatsOut(productCodeArray, products2, quant2, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);

        ArrayList<Integer> quant3= new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0));
        ArrayList<String> products3= new ArrayList<String>(Arrays.asList("Product A", "Product B", "Product C", "Product D", "Product E"));
        ArrayList<String> results3= new ArrayList<String>(Arrays.asList("%nAll items (A-E) out of stock"));
        printOutput(1, results3);
        size= 5;
        getWhatsOut(productCodeArray, products3, quant3, priceArray, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
    }

    public static void testTotals(ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes, boolean test_mode) throws IOException{
        ArrayList<Double> prices1= new ArrayList<Double>(Arrays.asList());
        ArrayList<Integer> vol1= new ArrayList<Integer>(Arrays.asList());
        ArrayList<String> results1= new ArrayList<String>(Arrays.asList("%nTotal is $0.00"));
        printOutput(1, results1);
        size= 0;
        getTotalValue(1, productCodeArray, descriptionArray, vol1, prices1, size, ordProdCode, ordQuant, ordValue, changes, test_mode);

        ArrayList<Double> prices2= new ArrayList<Double>(Arrays.asList(5.32, 7.96, 15.03));
        ArrayList<Integer> vol2= new ArrayList<Integer>(Arrays.asList(1, 3, 2));
        ArrayList<String> results2= new ArrayList<String>(Arrays.asList("%nTotal is $59.26"));
        printOutput(1, results2);
        size= 3;
        getTotalValue(1, productCodeArray, descriptionArray, vol2, prices2, size, ordProdCode, ordQuant, ordValue, changes, test_mode);

        ArrayList<Double> prices3= new ArrayList<Double>(Arrays.asList(5.32, 7.96, 15.03, 55.78, 0.01, 1.00, 3.50, 12.50));
        ArrayList<Integer> vol3= new ArrayList<Integer>(Arrays.asList(2, 2, 2, 2, 2, 2, 2, 2));
        ArrayList<String> results3= new ArrayList<String>(Arrays.asList("%nTotal is $202.20"));
        printOutput(1, results3);
        size= 8;
        getTotalValue(1, productCodeArray, descriptionArray, vol3, prices3, size, ordProdCode, ordQuant, ordValue, changes, test_mode);

        ArrayList<Double> prices4= new ArrayList<Double>(Arrays.asList(5.32, 7.96, 15.03, 55.78, 0.01, 1.00, 3.50, 12.50, 500.00, 250.0, 0.30, 7.25, 33.33));
        ArrayList<Integer> vol4= new ArrayList<Integer>(Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
        ArrayList<String> results4= new ArrayList<String>(Arrays.asList("%nTotal is $891.98"));
        printOutput(1, results4);
        size= 13;
        getTotalValue(1, productCodeArray, descriptionArray, vol4, prices4, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
    }

    public static void testMax(ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size, ArrayList<String> ordProdCode, ArrayList<Integer> ordQuant, double ordValue, boolean changes, boolean test_mode) throws IOException{
        ArrayList<Double> prices1= new ArrayList<Double>(Arrays.asList(2.00, 2.00, 2.00, 2.00, 2.00));
        ArrayList<String> desc1= new ArrayList<String>(Arrays.asList("Product A", "Product B", "Product C", "Product D", "Product E"));
        ArrayList<String> results1= new ArrayList<String>(Arrays.asList("%nAll items same price (Products A-E listed)"));
        printOutput(1, results1);
        size= 5;
        getMostExpensive(productCodeArray, desc1, stockArray, prices1, size, ordProdCode, ordQuant, ordValue, changes, test_mode);

        ArrayList<Double> prices2= new ArrayList<Double>(Arrays.asList(2.00, 2.00, 2.00, 8.00, 2.00));
        ArrayList<String> desc2= new ArrayList<String>(Arrays.asList("Product A", "Product B", "Product C", "Product D", "Product E"));
        ArrayList<String> results2= new ArrayList<String>(Arrays.asList("%nProduct D is most expensive at $8.00"));
        printOutput(1, results2);
        size= 5;
        getMostExpensive(productCodeArray, desc2, stockArray, prices2, size, ordProdCode, ordQuant, ordValue, changes, test_mode);

        ArrayList<Double> prices3= new ArrayList<Double>(Arrays.asList(2.00, 12.00, 2.00, 2.00, 12.00));
        ArrayList<String> desc3= new ArrayList<String>(Arrays.asList("Product A", "Product B", "Product C", "Product D", "Product E"));
        ArrayList<String> results3= new ArrayList<String>(Arrays.asList("%nProducts B and E are the most expensive, and cost $12.00"));
        printOutput(1, results3);
        size= 5;
        getMostExpensive(productCodeArray, desc3, stockArray, prices3, size, ordProdCode, ordQuant, ordValue, changes, test_mode);
    }      
}   