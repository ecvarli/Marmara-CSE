import numpy as np

def read_file(file_path):
    with open(file_path, 'r') as f:
        return f.read().strip()

# Method 1: Direct Comparison
def direct_compare(A, B, i, j):
    t = 0
    while i + t < len(A) and j + t < len(B) and A[i + t] == B[j + t]:
        t += 1
    return t

# Build Generalized Suffix Array
def build_suffix_array(s):
    suffixes = [(s[i:], i) for i in range(len(s))]
    suffixes.sort()
    sa = [suffix[1] for suffix in suffixes]
    return sa

# Build LCP Array
def build_lcp(s, sa):
    n = len(s)
    rank = [0] * n
    lcp = [0] * n
    for i in range(n):
        rank[sa[i]] = i
    h = 0
    for i in range(n):
        if rank[i] > 0:
            j = sa[rank[i] - 1]
            while i + h < n and j + h < n and s[i + h] == s[j + h]:
                h += 1
            lcp[rank[i]] = h
            if h > 0:
                h -= 1
    return lcp

# RMQ using Sparse Table
class RMQ:
    def __init__(self, array):
        self.n = len(array)
        self.log = [0] * (self.n + 1)
        for i in range(2, self.n + 1):
            self.log[i] = self.log[i // 2] + 1
        self.sparse_table = [[0] * (self.log[self.n] + 1) for _ in range(self.n)]
        for i in range(self.n):
            self.sparse_table[i][0] = array[i]
        j = 1
        while (1 << j) <= self.n:
            i = 0
            while i + (1 << j) <= self.n:
                self.sparse_table[i][j] = min(self.sparse_table[i][j - 1], self.sparse_table[i + (1 << (j - 1))][j - 1])
                i += 1
            j += 1

    def query(self, l, r):
        j = self.log[r - l + 1]
        return min(self.sparse_table[l][j], self.sparse_table[r - (1 << j) + 1][j])

# Method 2: Generalized Suffix Array + LCP + RMQ
def generalized_lce(A, B, i, j):
    separator = chr(1)  # Unique separator
    combined = A + separator + B
    sa = build_suffix_array(combined)
    lcp = build_lcp(combined, sa)
    rmq = RMQ(lcp)

    n = len(A)
    idx_i = sa.index(i)
    idx_j = sa.index(n + 1 + j)  # Offset by separator and A's length

    if idx_i > idx_j:
        idx_i, idx_j = idx_j, idx_i
    return rmq.query(idx_i + 1, idx_j)

# Main Program
if __name__ == "__main__":
    # Example usage
    file_a = "file_a.txt"
    file_b = "file_b.txt"
    A = read_file(file_a)
    B = read_file(file_b)

    # Method 1
    print("Direct Comparison LCE:", direct_compare(A, B, 3, 6))  # Example query

    # Method 2
    print("Generalized Suffix Array LCE:", generalized_lce(A, B, 1, 7))  # Example query
