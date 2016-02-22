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
*@version 1.5
*/
public class FoodForLess{
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

            size= populateArrays(productCodeArray, descriptionArray, stockArray, priceArray, size);
            greeting();
            pickOption(productCodeArray, descriptionArray, stockArray, priceArray, size);
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
        //pickOption();     
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
    *@exception InputMismatchException Thrown if user enters non-numerical input
    *@exception IOException IOException Thrown on account of chained calls to {@link #updateStockFile()}
    */ 
    public static void pickOption(ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray, int size) throws InputMismatchException, IOException, NumberFormatException{
        int option = 0;        
        giveOptions();        
        while (option<1 || option>5){
            String response= getUserInput("Which would you like to do?: ");
            try{
                option = Integer.valueOf(response);
                //System.out.printf("%d %d %n", option, option+200);                
            }catch (InputMismatchException|NumberFormatException e){
                ArrayList<String> badInput= new ArrayList<String>(Arrays.asList("That's definitely not an option on the list.%n"));
                printOutput(1, badInput);
                pickOption(productCodeArray, descriptionArray, stockArray, priceArray, size);
            }            
        }
         switch(option){
            case 1: giveStock(1, size, productCodeArray, descriptionArray, stockArray, priceArray);
                break;
            /*case 2: getWhatsOut();
                break;
            case 3: getTotalValue(1);
                break;
            case 4: getMostExpensive();
                break;
            case 5: getOrderSize();
                break;*/
            default: pickOption(productCodeArray, descriptionArray, stockArray, priceArray, size);
                break;
        }                      
    }

    /**
    * Allows the user to perform another operation, or end the program. If the user inputs "yes", {@link #pickOption()} is called. If the user input is "no", the program terminates, with {@link #updateStockFile()} updating stock.txt if any orders were placed
    *@exception IOException Thrown on account of chained calls to {@link #updateStockFile()}
    */ 
    /*public static void getAnother() throws IOException{
        String response = "";
        response = getUserInput("%nWould you like to perform another operation? [Yes/No]: ");
        switch (response.toLowerCase()){
            case "y": case "yes": pickOption();
                break;
            case "n": case "no": ArrayList<String> farewell= new ArrayList<String>(Arrays.asList("Goodbye!\n"));
                printOutput(1, farewell);
                updateStockFile();
                break;
            default: ArrayList<String> valid= new ArrayList<String>(Arrays.asList("Please enter a valid response.\n"));
                printOutput(1, valid);
                getAnother();
        }
    }*/

    /**
    * Displays the full stock list, inclusive of out of stock items; or displays the current custom order, depended on the mode selected.
    *@param option Specifies the mode (and relative function) of the procedure. Mode 1 displays the full stock list. Mode 2 displays the details of a custom order 
    *@exception IOException Thrown on account of chained calls to {@link #updateStockFile()}
    */ 
    public static void giveStock(int option, int size, ArrayList<String> productCodeArray, ArrayList<String> descriptionArray, ArrayList<Integer> stockArray, ArrayList<Double> priceArray) throws IOException{
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
            /*getAnother();
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
            //adjustInventory();
            //getAnother();*/
        }
    }

    /**
    * Displays those items that are currently out of stock
    *@exception IOException Thrown on account of chained calls to {@link #updateStockFile()}
    */ 
    /*public static void getWhatsOut()throws IOException{
        ArrayList<String> header= new ArrayList<String>(Arrays.asList("%n***********OUT OF STOCK ITEMS***********%n"));
        printOutput(1, header);
        for (int i =0; i<size; i++){
            if (stockArray.get(i)==0){
                String item = descriptionArray.get(i);
                ArrayList<String> out = new ArrayList<String>(Arrays.asList("%s%n", item));
                printOutput(2, out);
            }
        }
        getAnother();
    }

    /**
    * Displays the total value of the current inventory, or custom order
    *@param option Specifies what to tally total value for. Option 1 tallies current inventory. Option 2 tallies a custom order.
    *@exception IOException Thrown on account of chained calls to {@link #updateStockFile()}
    */ 
    /*public static void getTotalValue(int option) throws IOException{
        double count = 0;
        if (option == 1){
            for (int i=0; i<size; i++){
                count+=(stockArray.get(i)*priceArray.get(i));
            }
            ArrayList<String> total= new ArrayList<String>(Arrays.asList("%nThe total value of the current stock is $%s%n", String.format("%4.2f", count)));
            printOutput(2, total);
            getAnother();
        }else{
            for (int j=0; j<ordQuant.size(); j++){
                count+=(ordQuant.get(j)*priceArray.get(productCodeArray.indexOf(ordProdCode.get(j))));
            }
            ordValue= count;
        }
    }

    /**
    * Displays the most expensive item in the inventory
    *@exception IOException Thrown on account of chained calls to {@link #updateStockFile()}
    */ 
    /*public static void getMostExpensive() throws IOException{
        double big = priceArray.get(0);
        ArrayList<Integer> upper= new ArrayList<Integer>();
        //int position = 0;
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
        getAnother();
    }

    /**
    * Allows the user to specify the size of the custom order
    *@exception InputMismatchException Thrown if user enters non-numerical input
    *@exception IOException Thrown on account of chained calls to {@link #updateStockFile()}
    */ 
    /*public static void getOrderSize() throws InputMismatchException, IOException, NumberFormatException{
        try{
            int orderSize= Integer.valueOf(getUserInput("\nHow many items would you like to order?: "));
            placeOrders(orderSize, 1);
        } catch (InputMismatchException|NumberFormatException wType) {
            ArrayList<String> valNum= new ArrayList<String>(Arrays.asList("\nPlease enter a valid number size.\n"));
            printOutput(1, valNum);
            getOrderSize();
        }        
    }

    /**
    * Allows the user to order each specific item that makes up their order. Subsequent to ordering, changes made are noted by the boolean, changes, and 
    *{@link #getTotalValue} and {@link #giveStock} are called.
    *@param orderSize The number of items to be ordered, specified in {@link #getOrderSize()}
    *@param mode Allows for the order loop to be broken by {@link #optOut()} if the user so chooses
    *@exception IOException Thrown on account of chained calls to {@link #updateStockFile()}
    */ 
    /*public static void placeOrders(int orderSize, int mode) throws IOException{
        orders:{
            for(int i=0; i<orderSize; i++){
                verifyProdCode(i);
                if (i<orderSize-1){
                    int choice= optOut();
                    if (choice==1){
                        break orders;
                    }
                }
            }
        }
        changes=true;                                
        getTotalValue(2);
        giveStock(2);
    }

    /**
    * Lists the product codes of the current inventory. Used in tandem with {@link #verifyProductCode()}
    */ 
    /*public static void giveProdCodes(){
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
    */ 
    /*public static void verifyProdCode(int i){
        giveProdCodes();
        int itemNo= i+1;
        String prompt= "Please enter the product code for item"+Integer.toString(itemNo)+": ";
        String ordProductCode = getUserInput(prompt);
        ordProductCode = ordProductCode.toUpperCase();
        int position = productCodeArray.indexOf(ordProductCode);
        if (position>=0){
            verifyQuantity(position, i);
        }else{
            ArrayList<String> error= new ArrayList<String>(Arrays.asList("I'm sorry, but that product code does not exist in our inventory.%n"));
            printOutput(1, error);
            verifyProdCode(i);
        }
    }

    /**
    * Verifies that the desired quantity of the selected product can be purchased. If it can, the desired product and quantity are added to the custom order. If it cannot, the procedure informs the user, and then calls itself.
    *@param position The position of the selected product's data in the main program arrays
    *@param i The loop count/item number from the loop initiated in {@link #getOrderSize()}
    */ 
    /*public static void verifyQuantity(int position, int i) throws NumberFormatException{
        String avail= Integer.toString(stockArray.get(position));
        String desc= descriptionArray.get(position);
        String prompt= "%nWe have " + avail + " "+ desc + " in stock. How many would you like?: ";
        try{
            int quant = Integer.valueOf(getUserInput(prompt));
            if (quant<=stockArray.get(position)){
                addToOrder(position, quant, i);
            }else{
                ArrayList<String> tooMany= new ArrayList<String>(Arrays.asList("I'm sorry, but you can order a maximum of "+ avail +" " + desc));
                verifyQuantity(position, i);
            }
        } catch (NumberFormatException inval){
            ArrayList<String> wType= new ArrayList<String>(Arrays.asList("I'm pretty sure that's not even a number..."));
            printOutput(1, wType);
            verifyQuantity(position, i);
        }
    }

    /**
    * Adds the specified product and quantity to the custom order
    *@param position The position of the selected product's data in the main program arrays. Obtained in {@link #verifyQuantity}
    *@param quant The desired quantity of the selected product. Also obtained in {@link #verifyQuantity}
    *@param i The loop count/item number from the loop initiated in {@link #getOrderSize()}
    */ 
    /*public static void addToOrder(int position, int quant, int i){
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
    /*public static int optOut(){
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
    */ 
    /*public static void adjustInventory(){
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
    }

    /**
    * Updates the stock.txt file with current stock levels at program termination if any custom orders have been placed
    *@exception IOException Required declaration of IOException. If the file doesn't exist, it's created; the exception isn't actually thrown
    */ 
    /*public static void updateStockFile()throws IOException{
        if (changes){
            String file = "stock3.txt";
            String line= "";
            String code, label, count, cost="";
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
    }*/      
}   