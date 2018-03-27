Hello and thanks again for considering my application for the Software Engineer internship at Surescripts.
For my code sample I have decided to submit my custom assembler program. I originally wrote a similar program in c
for my computer architechture class, but after discussing with a softerware engineer at surscripts and learning
that his team primarily uses Java, I have decided to we write that assembler in Java and to make it more object oriented.

This program reads instruction text files consisting of UST3400 assembly instructions, and then converts thos instructions to machine code. UST3400 uses a limited instruction set consisiting of eight instructions:

add: Add the contents of RegisterA with the contents of RegisterB, store the results in destReg.
nand: Bitwise Nand contents of Register A with the contents of RegisterB, each bit is treaded independently
lw: Load RegisterA from memory. memory adress is formed by adding offset(immediate) feild with the contents of Register B
sw: Store register A into memory. Memory address is formed by adding offsetField with the contents of registerB
beq: If the contents of RegisterA and RegisterB are the same, then branch to the adress (ProgramCounter + 1 + offsetField), where programCounter is the address of the beq instruction.
Jalr: First store PC + 1 into regA, where PC is the address of the jalr instruction. then branch to the address contained in RegB.
halt: Increment the program counter then halt the machine.
noop Do nothing except incrementing the PC.

Additionially each istruction has a specific type that determines how each bit of the instruction is used.

R-Type instructions(add,nand):
bits 24-22 opcode
bits 21-19 regA
bits 18-16 regB
bits 15-3  unused
bits 2-0   destReg

J-Type instructions(jalr):
bits 24-22 opcode
bits 21-19 regA
bits 18-16 regB
bits 15-0 unused

I-Type instructions(lw,sw,beq):
bits 24-22 opcode
bits 21-19 regA
bits 18-16 rebB
bits 15-0  offset_field

O-Type instructions(halt, noop):
bits 24-22 opcode
bits 21-0  unused


An example UST3400 assembly code file would be:

	lw 1 0 five load reg1 with 5 (symbolic address)
	lw 2 1 3 load reg2 with -1 (numeric address)
start   add 1 2 1 decrement reg1
	beq 0 1 2 goto end of program when reg1==0
	beq 0 0 start go back to the beginning of the loop
	noop
done    halt end of program
five    .fill 5
neg1    .fill -1
stAdd   .fill start will contain the address of start (2)

and the resulting machine code after being processed by the Assembler would be:
8912903
9502723
1114113
16842754
16842749
29360128
25165824
5
-1
2
