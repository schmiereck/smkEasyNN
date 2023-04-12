


Ident:
* values between 0.0 an 1.0
* i(r,g,b) sum is always 1.0
* global value of all cells of a individuum

State:
* values between 0.0 an 1.0
* s(r,g,b)

Target:
* State == Ident

Input:
* me: i0, s0
* neighbours: i1, s1, i2, s2, i3, s3,

Output:
* transfer +/- x % of differenz between s0 and s1, s2, s3
* transfer +/- y % of differenz between s0 and i1, i2, i3
  * if i of neighbour cell is equal to net ident then cell is part of the net

Architektur:
* Node: Netz für Zelle mit Nachbarn
* Meta-Node: 

```
|-|-|-| |i| |-|-|-|
|-|-|-|  I  |-|-|-|   m
|-|-|  I  |-|-|-|-|   m
|-|-|-|-|  I  |-|-|   m
|-|-|    I    |-|-|   mm

           i0
          /  \
         i1   i2
        /  \ /  \
       i         i
```
