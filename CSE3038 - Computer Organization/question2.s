.data
    prompt_array:    .asciiz "Please enter an integer array: "
    prompt_n:        .asciiz "Please enter the value of N: "
    output_msg:      .asciiz "Output: "
    output_st:       .asciiz "1st maximum number is "
    output_nd:       .asciiz "2nd maximum number is "
    output_rd:       .asciiz "3rd maximum number is "
    output_th:       .asciiz "th maximum number is "
    output_no:       .asciiz "There is no "
    output_max:      .asciiz " number, maximum number is "
    dot:             .asciiz ".\n"

    .align 2          # Align next data on word boundary (4 bytes)
    array:           .space 400      # Space for 100 integers (4 bytes each)

    .align 2
    unique_array:    .space 400      # Space for unique values

    .align 2
    array_size:      .word 0         # Number of elements in array

    .align 2
    unique_size:     .word 0         # Number of unique elements

    .align 2
    buffer:          .space 1000     # Input buffer for array values (byte-level)

    
.text
.globl main

main:
    # Prompt for array input
    li $v0, 4
    la $a0, prompt_array
    syscall
    
    # Read entire line into buffer
    li $v0, 8
    la $a0, buffer
    li $a1, 1000
    syscall
    
    # Parse the buffer into integers
    la $t0, buffer     # Input buffer address
    la $t1, array      # Array to store values
    li $t2, 0          # Count of numbers read
    
parse_loop:
    # Skip whitespace
    lb $t3, 0($t0)
    beqz $t3, end_parse  # End of string
    bne $t3, 32, not_space   # Not a space
    addi $t0, $t0, 1     # Skip space
    j parse_loop
    
not_space:
    # Check if newline or end of string
    beq $t3, 10, end_parse  # Newline
    beqz $t3, end_parse     # End of string
    
    # Parse a number
    li $t4, 0              # Current number
    
parse_number:
    lb $t3, 0($t0)
    beqz $t3, store_number # End of string
    beq $t3, 10, store_number # Newline
    beq $t3, 32, store_number # Space
    
    # Convert from ASCII and add to current number
    addi $t3, $t3, -48    # ASCII to int
    mul $t4, $t4, 10      # Shift decimal place
    add $t4, $t4, $t3     # Add digit
    
    addi $t0, $t0, 1      # Next character
    j parse_number
    
store_number:
    # Store number in array
    sw $t4, 0($t1)
    addi $t1, $t1, 4      # Next array position
    addi $t2, $t2, 1      # Increment count
    
    # Check if at end
    beqz $t3, end_parse   # End of string
    beq $t3, 10, end_parse # Newline
    
    # Skip to next character
    addi $t0, $t0, 1
    j parse_loop
    
end_parse:
    # Store array size
    sw $t2, array_size
    
    # Prompt for N
    li $v0, 4
    la $a0, prompt_n
    syscall
    
    # Read N
    li $v0, 5
    syscall
    move $s0, $v0       # $s0 = N
    
    # Sort the array in descending order
    # Pass array size in $a0
    lw $a0, array_size
    la $a1, array        # Pass array address in $a1
    jal sort_array
    
    # Remove duplicates and store in unique_array
    # Pass necessary parameters
    la $a0, array
    lw $a1, array_size
    la $a2, unique_array
    jal remove_duplicates
    
    # Find Nth maximum
    # Pass necessary parameters
    move $a0, $s0        # N
    la $a1, unique_array
    lw $a2, unique_size
    jal find_nth_max
    move $s1, $v0        # $s1 = Nth maximum (or max if N is invalid)
    move $s2, $v1        # $s2 = 1 if N valid, 0 if invalid
    
    # Print beginning of output message
    li $v0, 4
    la $a0, output_msg
    syscall
    
    # Check if N is valid
    beqz $s2, not_valid_n
    
    # Determine suffix (st, nd, rd, th)
    li $t1, 1
    beq $s0, $t1, print_1st
    
    li $t1, 2
    beq $s0, $t1, print_2nd
    
    li $t1, 3
    beq $s0, $t1, print_3rd
    
    # Default is "th"
    li $v0, 1
    move $a0, $s0
    syscall
    
    li $v0, 4
    la $a0, output_th
    syscall
    j print_value
    
print_1st:
    li $v0, 4
    la $a0, output_st
    syscall
    j print_value
    
print_2nd:
    li $v0, 4
    la $a0, output_nd
    syscall
    j print_value
    
print_3rd:
    li $v0, 4
    la $a0, output_rd
    syscall
    j print_value
    
print_value:
    # Print the value
    li $v0, 1
    move $a0, $s1
    syscall
    
    # Print dot
    li $v0, 4
    la $a0, dot
    syscall
    j program_exit
    
not_valid_n:
    # Print "There is no N'th number" message
    li $v0, 4
    la $a0, output_no
    syscall
    
    # Print N
    li $v0, 1
    move $a0, $s0
    syscall
    
    # Print "number, maximum number is"
    li $v0, 4
    la $a0, output_max
    syscall
    
    # Print the maximum value
    li $v0, 1
    move $a0, $s1
    syscall
      
    # Print dot
    li $v0, 4
    la $a0, dot
    syscall
    
program_exit:
    # Exit program
    li $v0, 10
    syscall
    
#############################################
# Sort array in descending order using bubble sort
# Parameters:
#   $a0 = array size
#   $a1 = array address
# Returns: None (modifies array in place)
#############################################
sort_array:
    # Save registers
    addi $sp, $sp, -24
    sw $ra, 0($sp)
    sw $s0, 4($sp)
    sw $s1, 8($sp)
    sw $a0, 12($sp)    # Preserve $a0
    sw $a1, 16($sp)    # Preserve $a1
    sw $a2, 20($sp)    # Preserve $a2
    
    move $s0, $zero    # i = 0
    move $t8, $a1      # Store array address in $t8
    
outer_loop:
    bge $s0, $a0, sort_done
    
    li $s1, 0          # j = 0
    sub $t0, $a0, $s0  # $t0 = n - i - 1
    addi $t0, $t0, -1
    
inner_loop:
    bge $s1, $t0, outer_loop_inc
    
    # Calculate array address for j and j+1
    sll $t1, $s1, 2    # $t1 = j * 4
    add $t2, $t8, $t1  # $t2 = &array[j]
    
    addi $t3, $t2, 4   # $t3 = &array[j+1]
    
    # Compare array[j] and array[j+1]
    lw $t4, 0($t2)     # $t4 = array[j]
    lw $t5, 0($t3)     # $t5 = array[j+1]
    
    # If array[j] < array[j+1], swap them
    bge $t4, $t5, no_swap
    
    # Swap values
    sw $t5, 0($t2)
    sw $t4, 0($t3)
    
no_swap:
    addi $s1, $s1, 1   # j++
    j inner_loop
    
outer_loop_inc:
    addi $s0, $s0, 1   # i++
    j outer_loop
    
sort_done:
    # Restore registers
    lw $ra, 0($sp)
    lw $s0, 4($sp)
    lw $s1, 8($sp)
    lw $a0, 12($sp)    # Restore $a0
    lw $a1, 16($sp)    # Restore $a1
    lw $a2, 20($sp)    # Restore $a2
    addi $sp, $sp, 24
    
    jr $ra
    
#############################################
# Remove duplicates from sorted array
# Parameters:
#   $a0 = source array address
#   $a1 = source array size
#   $a2 = destination array address
# Returns: None (modifies destination array and unique_size)
#############################################
remove_duplicates:
    # Save registers
    addi $sp, $sp, -24
    sw $ra, 0($sp)
    sw $s0, 4($sp)
    sw $s1, 8($sp)
    sw $a0, 12($sp)    # Preserve $a0
    sw $a1, 16($sp)    # Preserve $a1
    sw $a2, 20($sp)    # Preserve $a2
    
    move $t0, $a0      # Source array
    move $t1, $a2      # Destination array
    move $t2, $a1      # Original array size
    li $t3, 0          # Counter for unique elements
    li $t4, 0          # Current index in original array
    
    # Handle empty array case
    beqz $t2, dup_done
    
    # Always add first element
    lw $t5, 0($t0)
    sw $t5, 0($t1)
    addi $t3, $t3, 1
    addi $t4, $t4, 1
    
dup_loop:
    bge $t4, $t2, dup_done
    
    # Get current element
    sll $t6, $t4, 2
    add $t6, $t0, $t6
    lw $t5, 0($t6)     # Current element
    
    # Get last unique element
    addi $t7, $t3, -1
    sll $t7, $t7, 2
    add $t7, $t1, $t7
    lw $t8, 0($t7)     # Last unique element
    
    # If different, add to unique array
    beq $t5, $t8, skip_dup
    
    sll $t7, $t3, 2
    add $t7, $t1, $t7
    sw $t5, 0($t7)     # Add to unique array
    addi $t3, $t3, 1   # Increment unique count
    
skip_dup:
    addi $t4, $t4, 1   # Next element
    j dup_loop
    
dup_done:
    # Store unique array size
    sw $t3, unique_size
    
    # Restore registers
    lw $ra, 0($sp)
    lw $s0, 4($sp)
    lw $s1, 8($sp)
    lw $a0, 12($sp)    # Restore $a0
    lw $a1, 16($sp)    # Restore $a1
    lw $a2, 20($sp)    # Restore $a2
    addi $sp, $sp, 24
    
    jr $ra
    
#############################################
# Find Nth maximum number
# Parameters:
#   $a0 = N
#   $a1 = unique array address
#   $a2 = unique array size
# Returns:
#   $v0 = Nth maximum (or max if N is invalid)
#   $v1 = 1 if N valid, 0 if invalid
#############################################
find_nth_max:
    # Save registers
    addi $sp, $sp, -24
    sw $ra, 0($sp)
    sw $s0, 4($sp)
    sw $s1, 8($sp)
    sw $a0, 12($sp)    # Preserve $a0
    sw $a1, 16($sp)    # Preserve $a1
    sw $a2, 20($sp)    # Preserve $a2
    
    move $t0, $a2      # Number of unique elements
    move $t1, $a0      # N
    move $t2, $a1      # Unique array address
    
    # If N > unique_size, return first element (max) and invalid flag
    ble $t1, $t0, valid_n
    
    lw $v0, 0($t2)     # Return max value
    li $v1, 0          # Invalid N
    j find_nth_done
    
valid_n:
    # Calculate position of Nth max (N-1 because array is 0-indexed)
    addi $t1, $t1, -1
    sll $t1, $t1, 2    # Multiply by 4 to get byte offset
    add $t2, $t2, $t1  # Address of Nth maximum
    lw $v0, 0($t2)     # Load Nth maximum
    li $v1, 1          # Valid N
    
find_nth_done:
    # Restore registers
    lw $ra, 0($sp)
    lw $s0, 4($sp)
    lw $s1, 8($sp)
    lw $a0, 12($sp)    # Restore $a0
    lw $a1, 16($sp)    # Restore $a1
    lw $a2, 20($sp)    # Restore $a2
    addi $sp, $sp, 24
    
    jr $ra
