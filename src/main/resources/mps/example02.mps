NAME example02.mps
ROWS
 N  obj     
 L  c1      
 G  c2
 G  c3
COLUMNS
    x1        obj                  1   c1                  1
    x1        c2                   1   c3                  8
    x2        obj                  2   c1                  1
    x2        c2                   4   c3                 -8
RHS
    rhs       c1                   4   c2                  6
    rhs       c3                   3
ENDATA
