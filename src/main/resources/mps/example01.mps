NAME example01
OBJSENSE
 MAX
ROWS
 N  obj     
 L  c1      
 G  c2
COLUMNS
    x1        obj                  1
    x1        c1                   5   c2                  5
    x2        obj                  1
    x2        c1                   3   c2                 -3
RHS
    rhs       c1                  15   c2                  0
BOUNDS
 LO bnd       x2                  0.5
ENDATA
