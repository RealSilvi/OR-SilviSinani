NAME example04.mps
OBJSENSE
 MAX
ROWS
 N  obj     
 L  c1
 G  c2
 G  c3
COLUMNS
    x1        obj                  1   c1                    2
    x1        c2                   1   c3                    3
    x2        obj                  1   c1                    4
    x2        c2                   6   c3                    1
RHS
    rhs       c1                   8   c2                    6
    rhs       c3                   3
ENDATA
