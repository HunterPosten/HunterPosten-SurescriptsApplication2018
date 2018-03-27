
/*

Hunter Posten
Mar 26, 2018


Class is an assembler that assembles machine code from the assembly instruction set
as described in the readme file.

Outer Class:
    NewAssembler:
        this class reads an inputted instruction file and then uses that file to create
        individual instruction objects for each instruction in a file. Once it does this
        than the assembler calls the process() method which processes each instruction into
        runnable machine code which is than outputted to a file.

        Class variables:
            instructionsToEncode
                instructionsToEncode is an arrayList that contains all of the instruction
                objects so that they may be easily accessed when they are processed.

            labels
                since each instructionline may contain a label it is neccassary to keep track
                of each label and their associated line number. this is done throught the labels
                hashmap. the hashmap uses the label as a key and its associated line number as the
                value.

            srcFile
                file variable that stores the source instruction file.

            destFile
                file variable that stores the output file which the resulting machine code is
                written to. if the user does not specify a specific file than a default file of
                AssemblerOut.txt is stored


       Class Methods:
            
            NewAssembler(File instructionFile)
                constructor method that initializes the srcFile and destFile variables. it is this constructor
                that runs if the user does not specify a destination file.

           NewAssembler(File instructionFile, File destinationFile)
                constructor method that initializes the srcFile and destFile variables. this constructor is
                ran when the user specifies a destination file.

            load()
                the load method opens the given instructionFile and for each line in the file it creates a new
                instruction object which is then added to the instructionsToEncode ArrayList.

            labelIndex()
                this labelIndex method performs the first pass through the list of instructions. during this pass
                the method checks to see if any instructions have a label. if they do then they store the label along
                with its associated instruction line number in the labels Hashmap.

            process()
                this is the method that processes the instructions into machine code first after declaring method 
                variable process calls the load() and labelIndex() methods. after doing this it performs another pass
                through the instructions. this pass it encodes its instruction through bitshifting into its type appropriate structure and
                adds the resulting machine code as a new line to the ret variable. after the pass through is complete than process() writes
                the ret variable out to a file.
Inner Class:
    Instruction
        this private inner class is the opjects that is created from each line in the instruction source file.

        Class Variables:
            this class contains individual instance variables for each part of the instruction including:
            RegA, RegB, and destReg to hold the varisus register values that could be used for an instruction.

            Type
                This clas also contains a char Type variable wich is set in accordance to the particular insturction. for example
                if an instruction is a add instruction than type will be set to 'r'. this type declaration is needed in order for process()
            
            Immediate
                in addition to register values 'i' type instructions can utilize a 16 bit immediate value. which is stored in the Immediate
                instance variable.

           Opcode
                this variable stores the instructions opcode for example if an instruction is an "add" instruction than the opcode is 0

            Label 
                this variable stores the string value of any labels used in the instruction.
            
            instrline
                this variable stores the instruction line that that particular instruction object is created from.
            
       Class Methods:
            
            Instructions(String instrLine)
                this is the constructor method for the class. it takes an inputted instruction line string and then splits it into it individual feilds
                additionally if the first field is not empty than that means that the instuction contains a label. if this is the case than the constructor
                initializes the class label variable as this value. it then assignes the split instruction line as the value of the instrLine variable.

           convertOpcode(String op)
                this method converts the inputted opcode to its numerical value.

            labelCheckAndReplace(String s)
                this method checks if the inputted value is a label in the outerclasses labels hashmap. if it is than the numerical value associated with that label is returned
                if it is not than the numerical value of the string will be returned.

            instructionEncode()
                this method first sets the opcode and type of the instruction and then reads through the rest of the instruction line and
                sets the rest of the instruction feilds.

            getLabel()
                accessor method for the instructions label variable

            getType()
                acessor method for the instructions type variable
           
            getRegA()
                acessor method for the instructions RegA variable

            getRegB()
                acessor method for the instructions RegB variable

            getImmediate()
                accessor method for the instructions immediate value

            getDestReg()
                acessor method for the instructions destination register


*/







import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class NewAssembler implements Runnable {
    private static ArrayList<instruction> instructionsToEncode = new ArrayList<instruction>();
    private HashMap<String, Integer> labels = new HashMap<String, Integer>();
    private File srcFile;
    private File destFile;
    
    public NewAssembler(File instructionFile){
        File a = new File("AssemblerOut.txt");
        this.destFile = a;
        this.srcFile = instructionFile;
        
    }
    
    public NewAssembler(File instructionFile, File destinationFile){
        this.srcFile = instructionFile;
        this.destFile = destinationFile;
        
     
    }
    
    public void process(){
        String ret;
        int machineCode;
        instruction instr;
        int opcode;
        int regA;
        int regB;
        int destReg;
        int immediate;  
        
        ret = ""; 
        
        try {load();}
        catch(Exception e){System.out.println(e.getMessage());}
        LabelIndex();
               
        for(int i = 0; i < instructionsToEncode.size(); i++){//loop runs the instruction encode method on every instruction
            machineCode = 0;
            instr = instructionsToEncode.get(i);
            instr.instructionEncode();
            
            if(instr.getType() == 'f'){//.fill
                immediate = instr.getImmediate();
                machineCode = immediate;
                
            }
            
            else if(instr.getType() == 'o'){//otype 
                opcode = instr.getOpcode();
                machineCode = opcode << 22;
                
            }
            
            else if(instr.getType() == 'r'){//rtype
                opcode = instr.getOpcode() << 22;
                regA = instr.getRegA() << 19;
                regB = instr.getRegB() << 16;
                destReg = instr.getDestReg();
                machineCode = opcode | regA | regB | destReg;
               
            }
            
            else if(instr.getType() == 'j'){//jtype
                opcode = instr.getOpcode() << 22;
                regA = instr.getRegA() << 19;
                regB = instr.getRegB() << 16;
                machineCode = opcode | regA | regB;
                
            }
            
            else if(instr.getType() == 'i'){//itype
                opcode = instr.getOpcode() << 22;
                regA = instr.getRegA() << 19;
                regB = instr.getRegB() << 16;
                immediate = instr.getImmediate();
                machineCode = opcode | regA | regB | immediate;
            }
           
            ret = ret + machineCode + "\n";   
        }//loop
        
        //output
        try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.destFile),"ust-8"))){
            writer.write(ret);
        }catch(Exception e){
            System.out.println("Assembler process: " + e.getMessage());
        }
    }//process
    
    
    
    private void load() throws Exception{
       String line;
       try(BufferedReader br = new BufferedReader(new FileReader(this.srcFile))){
           while((line = br.readLine()) != null){
               instruction a = new instruction(line);
               instructionsToEncode.add(a);
           }
        }catch (Exception e) {
            Logger.getLogger(NewAssembler.class.getName()).log(Level.SEVERE, null, e);
        }
    }//load

  
    private void LabelIndex() throws IllegalArgumentException{//method that performs a pass through the instructions to find any labels if a lebel is found then it records it location and label
        instruction a;
        for(int i = 0; i < instructionsToEncode.size(); i++){
            a = instructionsToEncode.get(i);
            if(a.Label != null){
                System.out.println("label found");
                if(labels.containsKey(a.Label)){
                    throw new IllegalArgumentException("LabelIndex: instruction has a duplicate label");
                }
                else{
                    labels.put(a.Label, i);
                }
                
            }
        }
        
    }//LabelIndex aka first pass.
    
    public void run(){
        process();
    }
    
    
    
    
    private class instruction{
        private char Type;
        private int  RegA;
        private int  RegB;
        private int  opcode;
        private int  immediate;
        private int  destReg;
        private String  Label = null;
        private String[] instrLine;
      
        
        public instruction(String instrLine){
            String[] parts;
            parts = instrLine.split(" |\t");
            if(parts[0].length() > 0){
                this.Label = parts[0];
            }
            this.instrLine = parts;
        }//constructor
        
        public void instructionEncode(){
            boolean RegAFlag = false;
            boolean RegBFlag = false;
            boolean opcodeFlag = false;
            boolean immediateFlag = false;
            for(int i = 1; i < instrLine.length; i++){//sets the opcode and the type
                if(instrLine[i].length() > 0 && opcodeFlag != true){//sets the opcode and the type
                    try{
                        this.convertOpcode(instrLine[i]);
                    }catch(Exception e){
                        System.out.println(e.getMessage());
                    }
                    
                    opcodeFlag = true;
                }
                else if(instrLine[i].length() > 0 && this.Type == 'f'){//if the instruction is a .fill than only the immediate is needed
                    this.immediate = labelCheckAndReplace(instrLine[i]);
                    break;
                }
                    
                    
                else if(instrLine[i].length() > 0 && RegAFlag != true){//sets Register A
                    this.RegA = Integer.parseInt(instrLine[i]);
                    RegAFlag = true;
                }
                else if(instrLine[i].length() > 0 && RegBFlag != true){//sets Register B
                    this.RegB = Integer.parseInt(instrLine[i]);
                    RegBFlag = true;
                }
                
                else if(instrLine[i].length() > 0 && immediateFlag != true && this.Type == 'i'){//Sets immediate
                    this.immediate = labelCheckAndReplace(instrLine[i]);
                    immediateFlag = true;
                }
                else if(instrLine[i].length() > 0 && immediateFlag != true && this.Type == 'r'){//sets destReg  
                    this.destReg = labelCheckAndReplace(instrLine[i]);
                    immediateFlag = true;
                }

            }
        }//instructionEncode
        
        
        private int labelCheckAndReplace(String s){
            
            if(labels.get(s) != null){
                return labels.get(s);
            }
            else return Integer.parseInt(s);
        }//labelCheckAndReplace
        
        
        private void convertOpcode(String op){//converts instruction into the opcode
            switch(op){
                case ".fill":
                    this.Type = 'f';
                    this.opcode = 0;
                    break;
                case "add":
                    this.Type = 'r';
                    this.opcode = 0;
                    break;
                case "nand":
                    this.Type = 'r';
                    this.opcode = 1;
                    break;
                case "lw":
                    this.Type = 'i';
                    this.opcode = 2;
                    break;
                case "sw":
                    this.Type = 'i';
                    this.opcode = 3;
                    break;
                case "beq":
                    this.Type = 'i';
                    this.opcode = 4;
                    break;
                case "jalr":
                    this.Type = 'j';
                    this.opcode = 5;
                    break;
                case "halt":
                    this.Type = 'o';
                    this.opcode = 6;
                    break;
                case "noop":
                    this.Type = 'o';
                    this.opcode = 7;
                default:
                    throw new IllegalArgumentException(" instruction: "+ instrLine.toString() + " unrecognized opcode");
                    
            }
        }//convertOpcode()
        
        private char getType(){
            return this.Type;
        }
        
        private int getRegA(){
            return this.RegA;
        }
        
        private int getRegB(){
            return this.RegB;
        }
        
        private int getOpcode(){
            return this.opcode;
        }
        private int getImmediate(){
            return this.immediate;
        }
        private String getLabel(){
            return this.Label;
        }
        
        public int getDestReg(){
            return this.destReg;
        }
    }//instruction inner class
    

}//NewAssembler
