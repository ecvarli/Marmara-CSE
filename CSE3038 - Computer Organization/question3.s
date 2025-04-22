.data
prompt:       .asciiz "Please enter a password: "
weak_msg:     .asciiz "Your password is not strong enough. Consider using: \""
strong_msg:   .asciiz "Your password is strong."
closing_quote:.asciiz "\""
buffer:       .space 100    # Buffer for input password
new_password: .space 100    # Buffer for suggested password

.text
main:
    # Display prompt
    li $v0, 4
    la $a0, prompt
    syscall
    
    # Read the password
    li $v0, 8
    la $a0, buffer
    li $a1, 100
    syscall
    
    # Remove the newline character
    la $a0, buffer
    jal remove_newline
    
    # Check if the password is strong
    la $a0, buffer
    jal check_password
    
    # $v0 will be 1 if password is strong, 0 otherwise
    beq $v0, 1, password_strong
    
    # Password is weak, create a suggestion
    la $a0, buffer
    la $a1, new_password
    jal suggest_password
    
    # Print weak message
    li $v0, 4
    la $a0, weak_msg
    syscall
    
    # Print suggested password
    li $v0, 4
    la $a0, new_password
    syscall
    
    # Print closing quote
    li $v0, 4
    la $a0, closing_quote
    syscall
    
    j exit
    
password_strong:
    # Print strong message
    li $v0, 4
    la $a0, strong_msg
    syscall
    
exit:
    # Exit program
    li $v0, 10
    syscall
    
# Procedure: remove_newline
# Purpose: Removes the newline character from input string
# Arguments: $a0 = address of string
# Returns: None
# Side effects: Replaces newline character with null terminator
remove_newline:
    # Preserve $a0 as required
    addi $sp, $sp, -4
    sw $a0, 0($sp)
    
    move $t0, $a0       # Load buffer address
    
remove_loop:
    lb $t1, 0($t0)       # Load character
    beq $t1, 0, remove_done  # If null terminator, we're done
    beq $t1, 10, replace_newline  # If newline, replace it
    addi $t0, $t0, 1     # Move to next character
    j remove_loop
    
replace_newline:
    sb $zero, 0($t0)     # Replace with null terminator
    
remove_done:
    # Restore $a0
    lw $a0, 0($sp)
    addi $sp, $sp, 4
    jr $ra
    
# Procedure: check_password
# Purpose: Checks if password meets strength requirements
# Arguments: $a0 = address of password string
# Returns: $v0 = 1 if strong, 0 if weak
# Side effects: None
check_password:
    # Save return address and registers
    addi $sp, $sp, -16
    sw $ra, 0($sp)
    sw $s0, 4($sp)
    sw $s1, 8($sp)
    sw $a0, 12($sp)    # Preserve $a0 as required
    
    # Save password address
    move $s0, $a0
    
    # Check length > 6
    jal string_length
    move $s1, $v0       # Save length
    li $t0, 6
    ble $s1, $t0, password_weak  # If length <= 6, password is weak
    
    # Check for lowercase letter
    move $a0, $s0
    li $a1, 97    # 'a' ASCII
    li $a2, 122   # 'z' ASCII
    jal contains_char_range
    beq $v0, 0, password_weak  # If no lowercase, password is weak
    
    # Check for uppercase letter
    move $a0, $s0
    li $a1, 65    # 'A' ASCII
    li $a2, 90    # 'Z' ASCII
    jal contains_char_range
    beq $v0, 0, password_weak  # If no uppercase, password is weak
    
    # Check for digit
    move $a0, $s0
    li $a1, 48    # '0' ASCII
    li $a2, 57    # '9' ASCII
    jal contains_char_range
    beq $v0, 0, password_weak  # If no digit, password is weak
    
    # Check for '*' character
    move $a0, $s0
    li $a1, 42    # '*' ASCII
    jal contains_char
    beq $v0, 0, password_weak  # If no '*', password is weak
    
    # Check for '+' character
    move $a0, $s0
    li $a1, 43    # '+' ASCII
    jal contains_char
    beq $v0, 0, password_weak  # If no '+', password is weak
    
    # Check for repeating characters
    move $a0, $s0
    jal has_repeating_chars
    beq $v0, 1, password_weak  # If has repeating, password is weak
    
    # If we've made it this far, password is strong
    li $v0, 1
    j check_done
    
password_weak:
    li $v0, 0
    
check_done:
    # Restore registers and return address
    lw $ra, 0($sp)
    lw $s0, 4($sp)
    lw $s1, 8($sp)
    lw $a0, 12($sp)    # Restore $a0
    addi $sp, $sp, 16
    jr $ra
    
# Procedure: string_length
# Purpose: Calculates the length of a null-terminated string
# Arguments: $a0 = string address
# Returns: $v0 = length of string
# Side effects: None
string_length:
    # Preserve $a0 as required
    addi $sp, $sp, -4
    sw $a0, 0($sp)
    
    li $v0, 0          # Initialize length counter
    move $t0, $a0      # Copy string address to $t0
    
length_loop:
    lb $t1, 0($t0)     # Load character
    beq $t1, 0, length_done  # If null terminator, we're done
    addi $v0, $v0, 1   # Increment length
    addi $t0, $t0, 1   # Move to next character
    j length_loop
    
length_done:
    # Restore $a0
    lw $a0, 0($sp)
    addi $sp, $sp, 4
    jr $ra
    
# Procedure: contains_char_range
# Purpose: Checks if string contains a character in the given ASCII range
# Arguments: $a0 = string address, $a1 = min ASCII value, $a2 = max ASCII value
# Returns: $v0 = 1 if contains, 0 otherwise
# Side effects: None
contains_char_range:
    # Preserve argument registers
    addi $sp, $sp, -12
    sw $a0, 0($sp)
    sw $a1, 4($sp)
    sw $a2, 8($sp)
    
    li $v0, 0          # Initialize result to false
    move $t0, $a0      # Copy string address to $t0
    
range_loop:
    lb $t1, 0($t0)     # Load character
    beq $t1, 0, range_done  # If null terminator, we're done
    
    # Check if character is in range
    blt $t1, $a1, range_next  # If char < min, check next
    bgt $t1, $a2, range_next  # If char > max, check next
    
    # Character is in range
    li $v0, 1          # Set result to true
    j range_done
    
range_next:
    addi $t0, $t0, 1   # Move to next character
    j range_loop
    
range_done:
    # Restore argument registers
    lw $a0, 0($sp)
    lw $a1, 4($sp)
    lw $a2, 8($sp)
    addi $sp, $sp, 12
    jr $ra
    
# Procedure: contains_char
# Purpose: Checks if string contains a specific character
# Arguments: $a0 = string address, $a1 = character to find
# Returns: $v0 = 1 if contains, 0 otherwise
# Side effects: None
contains_char:
    # Preserve argument registers
    addi $sp, $sp, -8
    sw $a0, 0($sp)
    sw $a1, 4($sp)
    
    li $v0, 0          # Initialize result to false
    move $t0, $a0      # Copy string address to $t0
    
char_loop:
    lb $t1, 0($t0)     # Load character
    beq $t1, 0, char_done  # If null terminator, we're done
    
    # Check if character matches
    beq $t1, $a1, char_found  # If matches, set result to true
    
    addi $t0, $t0, 1   # Move to next character
    j char_loop
    
char_found:
    li $v0, 1          # Set result to true
    
char_done:
    # Restore argument registers
    lw $a0, 0($sp)
    lw $a1, 4($sp)
    addi $sp, $sp, 8
    jr $ra
    
# Procedure: has_repeating_chars
# Purpose: Checks if string has repeating characters in a row
# Arguments: $a0 = string address
# Returns: $v0 = 1 if has repeating, 0 otherwise
# Side effects: None
has_repeating_chars:
    # Preserve $a0
    addi $sp, $sp, -4
    sw $a0, 0($sp)
    
    li $v0, 0          # Initialize result to false
    move $t0, $a0      # Copy string address to $t0
    
    lb $t1, 0($t0)     # Load first character
    beq $t1, 0, repeat_done  # If null terminator, we're done
    
repeat_loop:
    addi $t0, $t0, 1   # Move to next character
    lb $t2, 0($t0)     # Load next character
    beq $t2, 0, repeat_done  # If null terminator, we're done
    
    # Check if characters are the same
    beq $t1, $t2, repeat_found  # If same, set result to true
    
    move $t1, $t2      # Update previous character
    j repeat_loop
    
repeat_found:
    li $v0, 1          # Set result to true
    
repeat_done:
    # Restore $a0
    lw $a0, 0($sp)
    addi $sp, $sp, 4
    jr $ra
    
# Procedure: suggest_password
# Purpose: Creates a suggestion for a strong password
# Arguments: $a0 = address of original password, $a1 = address to store suggested password
# Returns: None
# Side effects: Writes a suggested strong password to the address in $a1
suggest_password:
    # Save return address and registers
    addi $sp, $sp, -24
    sw $ra, 0($sp)
    sw $s0, 4($sp)
    sw $s1, 8($sp)
    sw $s2, 12($sp)
    sw $a0, 16($sp)    # Preserve $a0
    sw $a1, 20($sp)    # Preserve $a1
    
    # Save addresses
    move $s0, $a0      # Original password
    move $s1, $a1      # New password
    
    # Start by copying original password without repeating characters
    move $t0, $s0      # Source address
    move $t1, $s1      # Destination address
    
    lb $t2, 0($t0)     # First character
    beq $t2, 0, copy_done  # If null terminator, we're done
    
    # Store first character
    sb $t2, 0($t1)
    addi $t1, $t1, 1
    addi $t0, $t0, 1
    
copy_loop:
    lb $t3, 0($t0)     # Current character
    beq $t3, 0, copy_done  # If null terminator, we're done
    
    # Check if characters are the same
    beq $t2, $t3, skip_char  # If same, skip this character
    
    # Store character and update previous
    sb $t3, 0($t1)
    addi $t1, $t1, 1
    move $t2, $t3
    
skip_char:
    addi $t0, $t0, 1   # Move to next source character
    j copy_loop
    
copy_done:
    # Store the end position of current password
    move $s2, $t1
    
    # Now we have a password without repeating characters
    # Check what's missing and add at the end
    
    # Check for lowercase letter
    move $a0, $s1      # New password address
    li $a1, 97    # 'a' ASCII
    li $a2, 122   # 'z' ASCII
    jal contains_char_range
    bne $v0, 0, check_uppercase  # If present, check next
    
    # Add lowercase 'z'
    li $t0, 122   # 'z' ASCII
    sb $t0, 0($s2)
    addi $s2, $s2, 1
    
check_uppercase:
    # Check for uppercase letter
    move $a0, $s1
    li $a1, 65    # 'A' ASCII
    li $a2, 90    # 'Z' ASCII
    jal contains_char_range
    bne $v0, 0, check_digit  # If present, check next
    
    # Add uppercase 'Z'
    li $t0, 90    # 'Z' ASCII
    sb $t0, 0($s2)
    addi $s2, $s2, 1
    
check_digit:
    # Check for digit
    move $a0, $s1
    li $a1, 48    # '0' ASCII
    li $a2, 57    # '9' ASCII
    jal contains_char_range
    bne $v0, 0, check_asterisk  # If present, check next
    
    # Add digit '9'
    li $t0, 57    # '9' ASCII
    sb $t0, 0($s2)
    addi $s2, $s2, 1
    
check_asterisk:
    # Check for '*' character
    move $a0, $s1
    li $a1, 42    # '*' ASCII
    jal contains_char
    bne $v0, 0, check_plus  # If present, check next
    
    # Add '*'
    li $t0, 42    # '*' ASCII
    sb $t0, 0($s2)
    addi $s2, $s2, 1
    
check_plus:
    # Check for '+' character
    move $a0, $s1
    li $a1, 43    # '+' ASCII
    jal contains_char
    bne $v0, 0, check_length  # If present, check next
    
    # Add '+'
    li $t0, 43    # '+' ASCII
    sb $t0, 0($s2)
    addi $s2, $s2, 1
    
check_length:
    # Calculate current length
    move $a0, $s1
    jal string_length
    
    # Check if length > 6
    li $t0, 6
    bgt $v0, $t0, add_null  # If length > 6, we're done
    
    # Add characters until length > 6
    add_length_loop:
        move $a0, $s1
        jal string_length
        
        li $t0, 6
        bgt $v0, $t0, add_null  # If length > 6, we're done
        
        # Add lowercase 'a'
        li $t0, 97    # 'a' ASCII
        sb $t0, 0($s2)
        addi $s2, $s2, 1
        
        j add_length_loop
    
add_null:
    # Add null terminator
    sb $zero, 0($s2)
    
    # Restore registers and return address
    lw $ra, 0($sp)
    lw $s0, 4($sp)
    lw $s1, 8($sp)
    lw $s2, 12($sp)
    lw $a0, 16($sp)    # Restore $a0
    lw $a1, 20($sp)    # Restore $a1
    addi $sp, $sp, 24
    jr $ra
