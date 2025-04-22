import math

#Fischer heun structure
class FischerHeunRMQ:
    def __init__(self, arr):
        self.arr = arr
        self.n = len(arr)
        self.block_size = max(1, math.floor(math.log2(self.n)))  
        self.num_blocks = (self.n + self.block_size - 1) // self.block_size  
        self.block_min = []  
        self.lookup_tables = []  
        self.sparse_table = []  

        self.preprocess()

    def preprocess(self):
        #compute block minimums and block lookup tables
        for block_start in range(0, self.n, self.block_size):
            block_end = min(block_start + self.block_size, self.n)
            block = self.arr[block_start:block_end]
            self.block_min.append(min(block))
            self.lookup_tables.append(self.build_lookup_table(block))

        #build sparse table for block minimums
        self.sparse_table = self.build_sparse_table(self.block_min)

    def build_lookup_table(self, block):
        #precompute mins for all ranges within block
        m = len(block)
        lookup = [[0] * m for _ in range(m)]
        for i in range(m):
            lookup[i][i] = block[i]
            for j in range(i + 1, m):
                lookup[i][j] = min(lookup[i][j - 1], block[j])
        return lookup

    def build_sparse_table(self, block_min):
         #build sparse table for block minimums
        n = len(block_min)
        log = math.floor(math.log2(n)) + 1
        sparse_table = [[0] * log for _ in range(n)]

        for i in range(n):
            sparse_table[i][0] = block_min[i]

        j = 1
        while (1 << j) <= n: 
            for i in range(n - (1 << j) + 1):
                sparse_table[i][j] = min(sparse_table[i][j - 1], sparse_table[i + (1 << (j - 1))][j - 1])
            j += 1

        return sparse_table

    def query_sparse_table(self, left, right):
        j = math.floor(math.log2(right - left + 1))
        return min(self.sparse_table[left][j], self.sparse_table[right - (1 << j) + 1][j])

    def query(self, left, right):
        left_block = left // self.block_size
        right_block = right // self.block_size

        if left_block == right_block:
            #query within the same block
            return self.lookup_tables[left_block][left % self.block_size][right % self.block_size]

        #query between multiple blocks
        left_min = self.lookup_tables[left_block][left % self.block_size][self.block_size - 1]
        right_min = self.lookup_tables[right_block][0][right % self.block_size]
        if left_block + 1 <= right_block - 1:
            mid_min = self.query_sparse_table(left_block + 1, right_block - 1)
        else:
            mid_min = float('inf')

        return min(left_min, mid_min, right_min)

#read files and generate suffixes
def read_file_and_generate_suffixes(file_name):
    try:
        with open(file_name, 'r') as file:
            string = file.read().strip()
            suffixes = [string[i:] for i in range(len(string))]
            return suffixes
    except FileNotFoundError:
        return []

#calculate the LCP between two strings (first method)
def longest_common_prefix(str1, str2):
    lcp_length = 0
    for c1, c2 in zip(str1, str2):
        if c1 == c2:
            lcp_length += 1
        else:
            break
    return lcp_length

#read text files and generate suffixes
suffixes_of_a = read_file_and_generate_suffixes('A.txt')
suffixes_of_b = read_file_and_generate_suffixes('B.txt')

#sorting suffixes of A and B alphabetically
suffix_array = sorted(suffixes_of_a + suffixes_of_b)

#generate LCP Array (add 0 as the first element)
lcp_array = [0]
for i in range(len(suffix_array) - 1):
    lcp = longest_common_prefix(suffix_array[i], suffix_array[i + 1])
    lcp_array.append(lcp)

#fischer-Heun RMQ for LCP Array
rmq = FischerHeunRMQ(lcp_array)

#allowing multiple queries
try:
    while True:
        user_input = input("\nEnter i,j values (e.g., '2,3') or 'done' to finish: ").strip()
        if user_input.lower() == 'done':
            break
        
        try:
            i, j = map(int, user_input.split(','))
            i -= 1
            j -= 1
            
            #get strings from Suffixes of A and Suffixes of B arrays
            if 0 <= i < len(suffixes_of_a) and 0 <= j < len(suffixes_of_b):
                a_string = suffixes_of_a[i]
                b_string = suffixes_of_b[j]
                
                # First method
                lcp_value = longest_common_prefix(a_string, b_string)
                print(f"LCP({i+1},{j+1}): {lcp_value}")
                
                # Second method using Fischer-Heun 
                a_suffix_index = suffix_array.index(a_string) + 1
                b_suffix_index = suffix_array.index(b_string) + 1
                
                smaller_index = min(a_suffix_index, b_suffix_index)
                larger_index = max(a_suffix_index, b_suffix_index)
                
                # Query Fischer-Heun structure
                min_lcp_value = rmq.query(smaller_index - 1, larger_index - 1)
                print(f"RMQlcp({smaller_index}, {larger_index}): {min_lcp_value}")
            else:
                print("Invalid indices entered.")
        except ValueError:
            print("Please enter a valid input (e.g., '2,3').")
except ValueError:
    print("An error occurred.")
