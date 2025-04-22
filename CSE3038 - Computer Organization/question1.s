.data
    prompt:         .asciiz "Enter a string: "
    output_msg:     .asciiz "Palindrome: "
    added_msg:      .asciiz "\nCharacters added: "
    input_str:      .space 100    # Buffer for input string
    palindrome:     .space 200    # Buffer for the palindrome result

.text
.globl main

main:
    # Display prompt and read input string
    li $v0, 4
    la $a0, prompt
    syscall
    
    li $v0, 8
    la $a0, input_str
    li $a1, 100
    syscall
    
    # Remove newline character
    jal remove_newline
    
    # Get length of input string
    la $a0, input_str
    jal strlen
    move $s0, $v0        # $s0 = length of input string
    
    # Check if input is already a palindrome
    la $a0, input_str
    jal is_palindrome
    beq $v0, 1, already_palindrome
    
    # Find the shortest palindromic prefix
    la $a0, input_str
    move $a1, $s0        # Length of input string
    jal find_shortest_palindromic_prefix
    move $s1, $v0        # $s1 = length of shortest palindromic prefix
    
    # Calculate number of characters to add (original length - shortest palindromic prefix)
    sub $s2, $s0, $s1    # $s2 = number of characters to add
    
    # Create palindrome
    la $s3, palindrome   # Destination for palindrome
    
    # Copy characters that need to be added (in reverse order)
    la $t0, input_str    # Source string
    add $t1, $t0, $s0    # Point to end of string
    addi $t1, $t1, -1    # Adjust for 0-indexed
    
    move $t2, $s3        # Start of palindrome buffer
    move $t3, $s2        # Counter for characters to add
    
add_chars_loop:
    beqz $t3, copy_original
    lb $t4, 0($t1)       # Load character from end of string
    sb $t4, 0($t2)       # Store in palindrome buffer
    addi $t1, $t1, -1    # Move backwards in source
    addi $t2, $t2, 1     # Move forward in destination
    addi $t3, $t3, -1    # Decrement counter
    j add_chars_loop
    
copy_original:
    # Copy the original string
    la $t0, input_str    # Source string
    move $t3, $s0        # Counter for original string length
    
copy_loop:
    beqz $t3, finish_palindrome
    lb $t4, 0($t0)       # Load character from input
    sb $t4, 0($t2)       # Store in palindrome buffer
    addi $t0, $t0, 1     # Next char in input
    addi $t2, $t2, 1     # Next position in palindrome
    addi $t3, $t3, -1    # Decrement counter
    j copy_loop
    
finish_palindrome:
    sb $zero, 0($t2)     # Null-terminate the palindrome
    j print_result
    
already_palindrome:
    # If already a palindrome, just copy input string to palindrome buffer
    la $t0, input_str
    la $t1, palindrome
    move $t2, $s0
    addi $t2, $t2, 1     # Include null terminator
    
copy_palindrome_loop:
    beqz $t2, set_zero_added
    lb $t3, 0($t0)
    sb $t3, 0($t1)
    addi $t0, $t0, 1
    addi $t1, $t1, 1
    addi $t2, $t2, -1
    j copy_palindrome_loop
    
set_zero_added:
    li $s2, 0            # No characters added
    
print_result:
    # Print the resulting palindrome
    li $v0, 4
    la $a0, output_msg
    syscall
    
    li $v0, 4
    la $a0, palindrome
    syscall
    
    # Print number of characters added
    li $v0, 4
    la $a0, added_msg
    syscall
    
    li $v0, 1
    move $a0, $s2
    syscall
    
    # Exit program
    li $v0, 10
    syscall

# Function to check if a string is a palindrome
# Parameters:
#   $a0 = address of string
# Returns:
#   $v0 = 1 if palindrome, 0 if not
is_palindrome:
    # Save registers that will be modified
    addi $sp, $sp, -8
    sw $a0, 0($sp)    # Save $a0 as required by specifications
    sw $ra, 4($sp)    # Save return address
    
    move $t0, $a0        # Start of string
    
    # Find end of string
    move $t1, $a0
find_end:
    lb $t2, 0($t1)
    beqz $t2, found_end
    addi $t1, $t1, 1
    j find_end
    
found_end:
    addi $t1, $t1, -1    # Point to last character
    
check_loop:
    # If pointers cross, it's a palindrome
    bge $t0, $t1, is_pal_true
    
    # Compare characters
    lb $t2, 0($t0)
    lb $t3, 0($t1)
    bne $t2, $t3, is_pal_false
    
    # Move pointers
    addi $t0, $t0, 1
    addi $t1, $t1, -1
    j check_loop
    
is_pal_true:
    li $v0, 1
    # Restore registers
    lw $a0, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra
    
is_pal_false:
    li $v0, 0
    # Restore registers
    lw $a0, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

# Function to find the shortest palindromic prefix
# Parameters:
#   $a0 = address of string
#   $a1 = length of string
# Returns:
#   $v0 = length of shortest palindromic prefix
find_shortest_palindromic_prefix:
    # Save registers that will be modified
    addi $sp, $sp, -12
    sw $a0, 0($sp)    # Save $a0 as required by specifications
    sw $a1, 4($sp)    # Save $a1 as required by specifications
    sw $ra, 8($sp)    # Save return address
    
    move $t0, $a1        # Start with full string
    
check_prefix_loop:
    beqz $t0, prefix_not_found
    
    # Check if prefix of length $t0 is a palindrome
    move $t1, $a0        # Start of string
    add $t2, $a0, $t0    # End of prefix
    addi $t2, $t2, -1    # Adjust for 0-indexed
    
    # Check if substring is palindrome
    li $t3, 1            # Assume is palindrome
    
check_prefix_pal:
    bge $t1, $t2, prefix_found
    lb $t4, 0($t1)
    lb $t5, 0($t2)
    bne $t4, $t5, not_palindrome
    addi $t1, $t1, 1
    addi $t2, $t2, -1
    j check_prefix_pal
    
not_palindrome:
    li $t3, 0            # Not a palindrome
    
prefix_found:
    beq $t3, 1, return_prefix
    addi $t0, $t0, -1    # Try shorter prefix
    j check_prefix_loop
    
prefix_not_found:
    li $v0, 1            # Default to length 1 if no palindromic prefix found
    j finish_prefix
    
return_prefix:
    move $v0, $t0        # Return length of palindromic prefix
    
finish_prefix:
    # Restore registers
    lw $a0, 0($sp)
    lw $a1, 4($sp)
    lw $ra, 8($sp)
    addi $sp, $sp, 12
    
    jr $ra

# Function to get string length
# Parameters:
#   $a0 = address of string
# Returns:
#   $v0 = length of string (excluding null)
strlen:
    # Save registers that will be modified
    addi $sp, $sp, -8
    sw $a0, 0($sp)    # Save $a0 as required by specifications
    sw $ra, 4($sp)    # Save return address
    
    move $t0, $a0        # Copy string address
    li $v0, 0            # Initialize length to 0
    
strlen_loop:
    lb $t1, 0($t0)       # Load byte
    beqz $t1, strlen_done# If null terminator, we're done
    addi $v0, $v0, 1     # Increment length
    addi $t0, $t0, 1     # Move to next character
    j strlen_loop
    
strlen_done:
    # Restore registers
    lw $a0, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

# Function to remove newline character
# Parameters:
#   None (operates on input_str)
# Returns:
#   None
remove_newline:
    # Save return address
    addi $sp, $sp, -4
    sw $ra, 0($sp)
    
    la $t0, input_str
    
remove_loop:
    lb $t1, 0($t0)
    beqz $t1, remove_done
    beq $t1, 10, replace_newline  # ASCII 10 = newline
    addi $t0, $t0, 1
    j remove_loop
    
replace_newline:
    sb $zero, 0($t0)      # Replace with null terminator
    
remove_done:
    # Restore return address
    lw $ra, 0($sp)
    addi $sp, $sp, 4
    jr $ra
